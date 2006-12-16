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
using Composestar.StarLight.CoreServices.Weaver;
using Composestar.StarLight.CoreServices.Logger;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.ILWeaver;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;
using System;
using System.ComponentModel.Design;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;
using System.IO;
#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{

	/// <summary>
	/// Responsible for the actual weaving of the aspects. Calls the weaving library to perform weaving at IL level.
	/// </summary>	
	/// <summary>
	/// ILWeaver task
	/// </summary>	
	public class ILWeaverTask : Task 
	{
		private const string ContextInfoFileName = "Composestar.StarLight.ContextInfo.dll";

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

		/// <summary>
		/// _bin folder
		/// </summary>
		private string _binFolder;

		/// <summary>
		/// Gets or sets the bin folder.
		/// </summary>
		/// <value>The bin folder.</value>
		public string BinFolder
		{
			get { return _binFolder; }
			set { _binFolder = value; }
		}

		/// <summary>
		/// _concerns dirty
		/// </summary>
		private bool _concernsDirty;

		/// <summary>
		/// Gets or sets a value indicating whether [concerns dirty].
		/// </summary>
		/// <value><c>true</c> if [concerns dirty]; otherwise, <c>false</c>.</value>        
		public bool ConcernsDirty
		{
			get { return _concernsDirty; }
			set { _concernsDirty = value; }
		}

		/// <summary>
		/// _weave debug
		/// </summary>
		private string _weaveDebug = "none";

		/// <summary>
		/// Gets or sets the weave debug.
		/// </summary>
		/// <value>The weave debug.</value>
		public string WeaveDebug
		{
			get { return _weaveDebug; }
			set { _weaveDebug = value; }
		}

		#endregion

		#region ctor

		public ILWeaverTask()
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
			Log.LogMessageFromResources("WeavingStartText");

			// Get the configuration container
			IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;
			ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFileName);

			// Set the weave debug level
			CecilWeaverConfiguration.WeaveDebug weaveDebugLevel;
			if (string.IsNullOrEmpty(_weaveDebug))
				weaveDebugLevel = CecilWeaverConfiguration.WeaveDebug.None;
			else
			{
				try
				{
					weaveDebugLevel = (CecilWeaverConfiguration.WeaveDebug)CecilWeaverConfiguration.WeaveDebug.Parse(typeof(CecilWeaverConfiguration.WeaveDebug), _weaveDebug, true);
				}
				catch (ArgumentException)
				{
					Log.LogErrorFromResources("CouldNotParseWeaveDebugLevel", _weaveDebug);
					return false;
				}
			}

			// For each assembly in the config
			foreach (AssemblyConfig assembly in configContainer.Assemblies)
			{
				// Exclude StarLight ContextInfo assembly from the weaving process
				if (assembly.FileName.EndsWith(ContextInfoFileName))
					continue;

				// Exclude references
				if (assembly.IsReference)
					continue;

				// If there is no weaving spec file, then skip
				if (string.IsNullOrEmpty(assembly.WeaveSpecificationFile))
				{
					Log.LogMessageFromResources("SkippedWeavingFile", assembly.FileName);
					continue;
				}

				// Check for modification
				if (!ConcernsDirty && File.GetLastWriteTime(assembly.FileName).Ticks <= assembly.Timestamp)
				{
					// we beter copy the backuped file
					string backupWeavefile = string.Concat(assembly.FileName, ".weaved");
					if (File.Exists(backupWeavefile))
					{
						File.Copy(backupWeavefile, assembly.FileName, true);
						Log.LogMessageFromResources("UsingBackupWeaveFile", assembly.FileName);
						continue;
					}
				}

				Log.LogMessageFromResources("WeavingFile", assembly.FileName);

				// Preparing config
				CecilWeaverConfiguration configuration = new CecilWeaverConfiguration(assembly, configContainer, weaveDebugLevel);

				if (!String.IsNullOrEmpty(BinFolder))
				{
					configuration.BinFolder = BinFolder;
				}

				try
				{
					// Retrieve a weaver instance from the ObjectManager
					using (IILWeaver weaver = DIHelper.CreateObject<CecilILWeaver>(CreateContainer(entitiesAccessor, configuration)))
					{
						// Perform weaving
						IWeaveResults weaveResults = weaver.DoWeave();

						// Output the logitems
						foreach (LogItem item in weaveResults.Log.LogItems)
						{
							Log.LogMessageFromText(item.ToString(), MessageImportance.Normal);
						}

						// Show information about weaving
						Log.LogMessageFromResources("WeavingCompleted", weaveResults.WeaveStatistics.TotalWeaveTime.TotalSeconds);
						switch (configuration.WeaveDebugLevel)
						{
							case CecilWeaverConfiguration.WeaveDebug.None:
								break;
							case CecilWeaverConfiguration.WeaveDebug.Statistics:
								ShowWeavingStats(assembly, weaveResults.WeaveStatistics);
								break;
							case CecilWeaverConfiguration.WeaveDebug.Detailed:
								ShowWeavingStats(assembly, weaveResults.WeaveStatistics);

								// Save instruction log
								string logFilename = assembly.FileName + ".weavelog.txt";
								string timingFilename = assembly.FileName + ".weavetiming.txt";

								weaveResults.WeaveStatistics.SaveInstructionsLog(logFilename);
								weaveResults.WeaveStatistics.SaveTimingLog(timingFilename);

								Log.LogMessageFromResources("WeavingInstructionsLogSaved", logFilename);
								Log.LogMessageFromResources("WeavingTimingLogSaved", timingFilename);
								break;
							default:
								break;
						}
					}
				}
				catch (ILWeaverException ex)
				{
					//Log.LogErrorFromException(ex, true); 
					string errorMessage = ex.Message;
					string stackTrace = ex.StackTrace;

					Exception innerException = ex.InnerException;
					while (innerException != null)
					{
						errorMessage = string.Concat(errorMessage, "; ", innerException.Message);
						innerException = innerException.InnerException;
					}
					Log.LogErrorFromResources("WeaverException", errorMessage);

					// Only show stacktrace when debugging is enabled
					if (weaveDebugLevel != CecilWeaverConfiguration.WeaveDebug.None)
						Log.LogErrorFromResources("WeaverExceptionStackTrace", stackTrace);
				}
				catch (ArgumentException ex)
				{
					Log.LogErrorFromException(ex, true);
				}
				catch (BadImageFormatException ex)
				{
					Log.LogErrorFromException(ex, false);
				}
			}

			return !Log.HasLoggedErrors;
		}

		/// <summary>
		/// Shows the weaving statistics.
		/// </summary>
		/// <param name="assembly">The assembly.</param>
		/// <param name="weaveStats">The weave stats.</param>
		private void ShowWeavingStats(AssemblyConfig assembly, WeaveStatistics weaveStats)
		{
			Log.LogMessageFromResources("WeavingStats", weaveStats.AverageWeaveTimePerMethod.TotalSeconds, weaveStats.AverageWeaveTimePerType.TotalSeconds,
																				weaveStats.MaxWeaveTimePerMethod.TotalSeconds, weaveStats.MaxWeaveTimePerType.TotalSeconds,
																				weaveStats.TotalMethodWeaveTime.TotalSeconds, weaveStats.TotalTypeWeaveTime.TotalSeconds,
																				weaveStats.MethodsProcessed, weaveStats.TypesProcessed,
																				assembly.FileName,
																				weaveStats.InternalsAdded, weaveStats.ExternalsAdded,
																				weaveStats.InputFiltersAdded, weaveStats.OutputFiltersAdded,
																				weaveStats.TotalWeaveTime.TotalSeconds);
		}
		/// <summary>
		/// Creates the services container.
		/// </summary>
		/// <param name="languageModel">The language model.</param>
		/// <param name="configuration">The configuration.</param>
		/// <returns></returns>
		internal static IServiceProvider CreateContainer(IEntitiesAccessor languageModel, CecilWeaverConfiguration configuration)
		{
			ServiceContainer serviceContainer = new ServiceContainer();
			serviceContainer.AddService(typeof(IEntitiesAccessor), languageModel);
			serviceContainer.AddService(typeof(CecilWeaverConfiguration), configuration);
			serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new ILWeaverBuilderConfigurator());

			return serviceContainer;
		}
	}
}
