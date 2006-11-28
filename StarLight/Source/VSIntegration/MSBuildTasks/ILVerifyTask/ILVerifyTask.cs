#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CoreServices.Settings;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Globalization;
using System.IO;
#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// Execute the PEVerify application to check if the generated IL code is valid.
	/// </summary>
	public class ILVerifyTask : Task
	{

		private const int ErrorFileNotFound = 2;
		private const int ErrorAccessDenied = 5;

		#region Properties for MSBuild
		/// <summary>
		/// _repository file name
		/// </summary>
		private string _repositoryFileName;

		/// <summary>
		/// Repository file name
		/// </summary>
		/// <returns>String</returns>
		[Required()]
		public string RepositoryFileName
		{
			get { return _repositoryFileName; }
			set { _repositoryFileName = value; }
		}
		#endregion

		#region ctor

		/// <summary>
		/// Initializes a new instance of the <see cref="T:AnalyzerTask"/> class.
		/// </summary>
		public ILVerifyTask()
			: base(Properties.Resources.ResourceManager)
		{

		}

		#endregion

		/// <summary>
		/// When overridden in a derived class, executes the task.
		/// </summary>
		/// <returns>
		/// true if the task successfully executed; otherwise, false.
		/// </returns>
		public override bool Execute()
		{
			string PEVerifyLocation;
			string PEVerifyExecutable = "bin\\PEVerify.exe";

			Stopwatch sw = new Stopwatch();
			sw.Start();

			// Get the location of PEVerify
			PEVerifyLocation = StarLightSettings.Instance.DotNETSDKLocation;

			if (String.IsNullOrEmpty(PEVerifyLocation) || !File.Exists(Path.Combine(PEVerifyLocation, PEVerifyExecutable)))
			{
				Log.LogWarningFromResources("PEVerifyExecutableNotFound", "PEVerify.exe");
				return true;
			}

			// Setup process
			Process process = new Process();

			// Determine filename
			process.StartInfo.FileName = Path.Combine(PEVerifyLocation, PEVerifyExecutable);

			process.StartInfo.CreateNoWindow = true;
			process.StartInfo.RedirectStandardOutput = true;
			process.StartInfo.UseShellExecute = false;
			process.StartInfo.RedirectStandardError = false;

			IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;

			ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFileName);

			UInt16 filesVerified = 0;

			// Execute PEVerify for each file
			foreach (AssemblyConfig assembly in configContainer.Assemblies)
			{
				try
				{
					// Only verify files we weaved on. Skip the rest.
					if (assembly.IsReference || String.IsNullOrEmpty(assembly.WeaveSpecificationFile))
						continue;

					Log.LogMessageFromResources("VerifyingAssembly", assembly.FileName);

					string arguments = String.Format(CultureInfo.InvariantCulture, "{0} /IL /MD /NOLOGO", assembly.FileName);
					process.StartInfo.Arguments = arguments;
					process.Start();

					process.WaitForExit(8000);
					if (process.ExitCode == 0)
					{
						Log.LogMessageFromResources("VerifySuccess", assembly.FileName);
					}
					else
					{
						while (!process.StandardOutput.EndOfStream)
						{
							ParseOutput(process.StandardOutput.ReadLine(), assembly.FileName);
						}
					}
					filesVerified++;

				}
				catch (Win32Exception exception)
				{
					if (exception.NativeErrorCode == ErrorFileNotFound)
					{
						Log.LogWarningFromResources("PEVerifyExecutableNotFound", process.StartInfo.FileName);
						return true;
					}
					else if (exception.NativeErrorCode == ErrorAccessDenied)
					{
						Log.LogWarningFromResources("PEVerifyExecutableAccessDenied", process.StartInfo.FileName);
						return true;
					}
					else
					{
						Log.LogErrorFromResources("PEVerifyExecutionException", exception.ToString());
						return false;
					}
				}
				catch (InvalidOperationException ex)
				{
					Log.LogErrorFromException(ex, true);
				}
				catch (ArgumentException ex)
				{
					Log.LogErrorFromException(ex, true);
				}
			}

			sw.Stop();

			Log.LogMessageFromResources("VerificationCompleted", filesVerified, sw.Elapsed.TotalSeconds);

			return !Log.HasLoggedErrors;

		}

		/// <summary>
		/// Parses the output.
		/// </summary>
		/// <param name="message">The message.</param>
		/// <param name="filename">The filename.</param>
		private void ParseOutput(String message, string filename)
		{
			if (!message.Contains("Errors Verifying") & !message.Contains("Error Verifying"))
			{
				try
				{
					// Try to parse the output
					string method = message.Substring(message.IndexOf(" : ") + 3);
					string offset = method.Substring(method.IndexOf("][") + 2);
					string mes = offset.Substring(offset.IndexOf("]") + 2);
					method = method.Substring(0, method.Length - offset.Length - 2);
					offset = offset.Substring(0, offset.Length - mes.Length - 2);

					Log.LogErrorFromResources("VerificationError", filename, mes, method, offset);
				}
				catch (ArgumentException)
				{
					Log.LogError(message);
				}
			}
		}
	}
}
