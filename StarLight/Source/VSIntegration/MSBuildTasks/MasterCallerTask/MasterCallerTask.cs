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
using Composestar.StarLight.CoreServices.Settings;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CoreServices.Logger;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;
using System.IO;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.CoreServices.Settings.Providers;
using System.Text.RegularExpressions;
#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// Calls the Master to perform the actual analyzing of concerns by composestar.
	/// </summary>
	public class MasterCallerTask : StarlightTask
	{
		#region Constructor
		/// <summary>
		/// Initializes a new instance of the <see cref="T:MasterCallerTask"/> class.
		/// </summary>
		public MasterCallerTask() : base(Properties.Resources.ResourceManager) { }

		#endregion

		#region Task properties

		private string _repositoryFileName;
		private string _debugLevel;
		private string _intermediateOutputPath;
		private string _increConfig;
        private string _bookKeepingMode;
		private bool _filthOutput = true;

		/// <summary>
		/// Sets the repository filename.
		/// </summary>
		/// <value>The repository filename.</value>
		[Required]
		public string RepositoryFileName
		{
			set { _repositoryFileName = value; }
		}

		/// <summary>
		/// Sets the debug level.
		/// </summary>
		/// <value>The debug level.</value>
		public string JavaDebugLevel
		{
			set { _debugLevel = value; }
		}

		/// <summary>
		/// Sets the intermediate output path.
		/// </summary>
		/// <value>The intermediate output path.</value>
		/// <returns>string</returns>
		[Required]
		public string IntermediateOutputPath
		{
			set { _intermediateOutputPath = value; }
		}

		/// <summary>
		/// Sets the name of the configuration file for INCRE.
		/// </summary>
		public string IncreConfig
		{
			set { _increConfig = value; }
		}

		/// <summary>
		/// Sets a value indicating whether FILTH output is enabled.
		/// </summary>
		/// <value><c>true</c> if FILTH output is enabled; otherwise, <c>false</c>.</value>
		public bool FilthOutput
		{
			set { _filthOutput = value; }
		}

        /// <summary>
        /// Sets the resource operating book keeping mode
        /// </summary>
        public string BookKeepingMode
        {
            set { _bookKeepingMode = value; }
        }
		#endregion

		#region Declarations
	
		private const string JavaExecutable = "java.exe";
		private const string StarLightJar = "Compiletime\\StarLight.jar";

		private bool BuildErrorsEncountered;
        private DebugLevel CurrentDebugMode;

		private const int ErrorFileNotFound = 2;
		private const int ErrorAccessDenied = 5;

        public enum DebugLevel
        {
            NotSet = -1,
            Error = 0,
            Crucial = 1,
            Warning = 2,
            Information = 3,
            Debug = 4,
            Trace = 5
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
            base.Execute();
			Stopwatch sw = Stopwatch.StartNew();
		
			// Open DB
			Log.LogMessageFromResources(MessageImportance.Low, "OpenDatabase", _repositoryFileName);
			IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;

			ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(_repositoryFileName);

			if (configContainer.Concerns.Count == 0)
			{
				Log.LogMessageFromResources("MasterSkipNoConcerns");
				return !Log.HasLoggedErrors;
			}

			Log.LogMessageFromResources("MasterStartText");

			// Place debuglevel
			Log.LogMessageFromResources("StoreDebugLevel", _debugLevel);

			// Set the Common Configuration
			string debugLevelValue = "";
			if (!string.IsNullOrEmpty(_debugLevel))
			{
				try
				{
                    CurrentDebugMode = (DebugLevel)Enum.Parse(typeof(DebugLevel), _debugLevel);
                    debugLevelValue = DebugLevelToLog4j(CurrentDebugMode);
				}
				catch (ArgumentException)
				{
					Log.LogErrorFromResources("CouldNotConvertDebugLevel", _debugLevel);
					return false;
				}
			}

            if (!String.IsNullOrEmpty(debugLevelValue))
            {
                configContainer.AddSetting("buildDebugLevel", debugLevelValue);
            }
			configContainer.AddSetting("IntermediateOutputPath", _intermediateOutputPath);
			configContainer.AddSetting("InstallFolder", StarLightSettings.Instance.StarLightInstallFolder);

            //michielh: disabled for now, not used in FILTH
            //configContainer.AddSetting("FILTH.outputEnabled", _filthOutput.ToString(CultureInfo.InvariantCulture));

            if (!String.IsNullOrEmpty(_bookKeepingMode))
            {
                configContainer.AddSetting("INLINE.bookkeeping", _bookKeepingMode);
            }

			// Save common config
			entitiesAccessor.SaveConfiguration(_repositoryFileName, configContainer);

			// Create a process to execute the StarLight Master.
			Process process = new Process();
			process.StartInfo.CreateNoWindow = true;
			process.StartInfo.RedirectStandardOutput = true;
			process.StartInfo.UseShellExecute = false;
			process.StartInfo.RedirectStandardError = true;

			// Determine filename
			if (!string.IsNullOrEmpty(StarLightSettings.Instance.JavaLocation))
			{
				process.StartInfo.FileName = Path.Combine(StarLightSettings.Instance.JavaLocation, JavaExecutable);
			}
			else
				process.StartInfo.FileName = JavaExecutable; // In path

			// Create command line
			string jarFile = Path.Combine(StarLightSettings.Instance.StarLightInstallFolder, StarLightJar);

			IList<string> args = new List<string>();
			args.Add(StarLightSettings.Instance.JavaOptions);
			args.Add("-jar " + Quote(jarFile));
            if (_increConfig != null) args.Add("-DINCRE.config=" + _increConfig);
			args.Add(Quote(_repositoryFileName));

			process.StartInfo.Arguments = Join(args, " ");
			Log.LogMessageFromResources("JavaStartMessage", process.StartInfo.Arguments);

			// Start the process
			try
			{
				process.Start();

				using (StreamReader outputReader = process.StandardOutput)
				{
					while (!outputReader.EndOfStream)
					{
						ParseMasterOutput(outputReader.ReadLine());
					}
				}

				process.WaitForExit();
				if (process.ExitCode == 0)
				{
					sw.Stop();
					Log.LogMessageFromResources("MasterCompleted", sw.Elapsed.TotalSeconds);
					return !Log.HasLoggedErrors;
				}
				else
				{
					Log.LogMessagesFromStream(process.StandardError, MessageImportance.High);
					Log.LogErrorFromResources("MasterRunFailed", process.ExitCode);
					return false;
				}
			}
			catch (Win32Exception e)
			{
				if (e.NativeErrorCode == ErrorFileNotFound)
				{
					Log.LogErrorFromResources("JavaExecutableNotFound", process.StartInfo.FileName);
				}
				else if (e.NativeErrorCode == ErrorAccessDenied)
				{
					Log.LogErrorFromResources("JavaExecutableAccessDenied", process.StartInfo.FileName);
				}
				else
				{
					Log.LogErrorFromResources("ExecutionException", e.ToString());
				}
				return false;
			}
			finally
			{
				if (sw.IsRunning) sw.Stop();
			}
		}

		#region Helper functions

		/// <summary>
		/// Surrounds the specified string with quotes.
		/// </summary>
		private string Quote(string value)
		{
			return '"' + value + '"';
		}

		/// <summary>
		/// Returns a string that contains the values in the specified list of strings
		/// separated by some other string.
		/// </summary>
		/// <param name="items">The list of values.</param>
		/// <param name="glue">The glue that is used to combine the values.</param>
		private string Join(IList<string> items, string glue)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < items.Count; i++)
			{
				sb.Append(items[i]);
				
				if (i < items.Count - 1) 
					sb.Append(glue);
			}
			return sb.ToString();
		}

        // filename(line:colum) priority [module]: description
        Regex pattern = new Regex("^(.*)\\(([0-9]+):([0-9]+)\\) ([\\w]+) \\[([\\w+._]+)\\]: (.*)$");

		/// <summary>
		/// Parses the master output.
		/// </summary>
		/// <param name="message">The message.</param>
		private void ParseMasterOutput(string message)
		{
			if (message == null)
				return;

			// Parse the message

            Match m = pattern.Match(message);
			if (m.Success)
			{
				string module = m.Groups[4].Value;
                string level = m.Groups[3].Value;
                string filename = m.Groups[0].Value;
                string line = m.Groups[1].Value;
                string col = m.Groups[2].Value;
                string msg = m.Groups[5].Value;

                DebugLevel mode;
				switch (level.ToLower())
				{
					case "error":
                        mode = DebugLevel.Error;
						break;
					case "crucial":
                        mode = DebugLevel.Crucial; 
						break;
					case "warning":
					case "warn":
                        mode = DebugLevel.Warning; 
						break;
					case "information":
					case "info":
                        mode = DebugLevel.Information; 
						break;
					case "debug":
                        mode = DebugLevel.Debug; 
						break;
                    case "trace":
                        mode = DebugLevel.Trace;
                        break;
					default:
                        mode = DebugLevel.NotSet; 
						break;
				}

				int linenumber = 0;
				int.TryParse(line, out linenumber);
                int colnumber = 0;
                int.TryParse(col, out colnumber);

				if (BuildErrorsEncountered && string.IsNullOrEmpty(message))
				{
					BuildErrorsEncountered = false;
				}

				this.LogMessage(mode, module, msg, filename, linenumber, colnumber);
			}
			else
			{
				this.LogMessage(message);
			}
		}

		/// <summary>
		/// Logs the message.
		/// </summary>
		/// <param name="debugMode">The debug mode.</param>
		/// <param name="module">The module.</param>
		/// <param name="message">The message.</param>
		/// <param name="filename">The filename.</param>
		/// <param name="line">The line.</param>
        private void LogMessage(DebugLevel debugMode, string module, string message, string filename, int line, int col)
		{
			if (CurrentDebugMode >= debugMode)
			{
                string modeDescription = DebugLevelToLog4j(debugMode);
				string fm = string.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module, modeDescription, message);

				switch (debugMode)
				{
                    case DebugLevel.Error:
						Log.LogError(module, "", "", filename, line, col, 0, 0, message);
						break;
                    case DebugLevel.Warning:
						Log.LogWarning(module, "", "", filename, line, col, 0, 0, message);
						break;
                    case DebugLevel.Crucial:
						Log.LogMessage(MessageImportance.Normal, fm);
						break;
                    case DebugLevel.Information:
						Log.LogMessage(MessageImportance.Normal, fm);
						break;
                    case DebugLevel.Debug:
						Log.LogMessage(MessageImportance.Normal, fm);
						break;
				}

			}
		}

		/// <summary>
		/// This log method allows you to print a complete string to the output pane. So no formatting etc.
		/// </summary>
		/// <param name="message"></param>
		private void LogMessage(string message)
		{
			Log.LogMessage(message);
		}

		/// <summary>
		/// Gets the mode description.
		/// </summary>
		/// <param name="mode">The mode.</param>
		/// <returns></returns>
        private static string DebugLevelToLog4j(DebugLevel mode)
		{
			switch (mode)
			{
                case DebugLevel.Error: 
					return "ERROR";
                case DebugLevel.Warning: 
					return "WARN";
                case DebugLevel.Information: 
					return "INFO";
                case DebugLevel.Debug: 
					return "DEBUG";
                case DebugLevel.Trace:
                    return "TRACE";
                case DebugLevel.Crucial:
                    return "CRUCIAL";
				default: 
					return "unknown";
			}
		}
		#endregion
	}
}
