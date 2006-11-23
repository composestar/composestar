using System;
using System.Globalization;
using System.IO;
using System.ComponentModel;
using System.Diagnostics.CodeAnalysis;
  
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.CoreServices;  
using Composestar.Repository;
using System.Diagnostics;

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// Calls the Master to perform the actual analyzing of concerns by ComposeStar.
	/// </summary>
	[LoadInSeparateAppDomain]
	public class MasterCallerTask : AppDomainIsolatedTask 
	{
		private const string JAVA_EXE = "java.exe";
		private const string STARLIGHT_JAR = "Compiletime\\StarLight.jar";

		private const int ERROR_FILE_NOT_FOUND = 2;
		private const int ERROR_ACCESS_DENIED = 5;
		
		private string _repositoryFileName;
		private ITaskItem[] _concernFiles;
		private string _debugLevel;
		private String _intermediateOutputPath;

		private bool _buildErrorsEncountered;
		private DebugMode _currentDebugMode;

		#region Constructor
		/// <summary>
		/// Initializes a new instance of the <see cref="T:MasterCallerTask"/> class.
		/// </summary>
		public MasterCallerTask() 
			: base(Properties.Resources.ResourceManager)
		{
		}
		
		#endregion

		#region Properties

		/// <summary>
		/// Gets or sets the repository filename.
		/// </summary>
		/// <value>The repository filename.</value>
		[Required]
		public string RepositoryFileName
		{
			get { return _repositoryFileName; }
			set { _repositoryFileName = value; }
		}

		/// <summary>
		/// Gets or sets the concern files.
		/// </summary>
		/// <value>The concern files.</value>
		[Required]
		public ITaskItem[] ConcernFiles
		{
			get { return _concernFiles; }
			set { _concernFiles = value; }
		}

		/// <summary>
		/// Gets or sets the debug level.
		/// </summary>
		/// <value>The debug level.</value>
		public string DebugLevel
		{
			get { return _debugLevel; }
			set { _debugLevel = value; }
		}

		/// <summary>
		/// Gets or sets the intermediate output path.
		/// </summary>
		/// <value>The intermediate output path.</value>
		[Required]
		public String IntermediateOutputPath
		{
			get { return _intermediateOutputPath; }
			set { _intermediateOutputPath = value; }
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
			Stopwatch sw = new System.Diagnostics.Stopwatch();
			sw.Start();

			// Open DB
			Log.LogMessageFromResources(MessageImportance.Low, "OpenDatabase", RepositoryFileName);
			IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;
			ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFileName);

			if (configContainer.Concerns.Count == 0)
			{
				Log.LogMessageFromResources("MasterSkipNoConcerns");             
				return !Log.HasLoggedErrors;
			}

			Log.LogMessageFromResources("MasterStartText");

			// Get registry settings
			RegistrySettings rs = GetRegistrySettings();
			if (rs == null)
				return !Log.HasLoggedErrors; 

			// Set debuglevel
			Log.LogMessageFromResources("StoreDebugLevel", DebugLevel);

			// Set the Common Configuration
			short debugLevelValue;
			if (!short.TryParse(DebugLevel, out debugLevelValue))
			{
				Log.LogErrorFromResources("CouldNotConvertDebugLevel", DebugLevel);
				return false;
			}

			_currentDebugMode = (DebugMode)debugLevelValue;

			configContainer.CompiletimeDebugLevel = debugLevelValue;
			configContainer.IntermediateOutputPath = _intermediateOutputPath;
			configContainer.InstallFolder = rs.InstallFolder;

			// Set FILTH Specification
			String filthFile = "FILTH.xml";
			if (!File.Exists(filthFile))
			{
				filthFile = Path.Combine(rs.InstallFolder, filthFile);
			}
			configContainer.SpecificationFILTH = filthFile;
			
			// Save common config
			entitiesAccessor.SaveConfiguration(RepositoryFileName, configContainer);
																 
			// Start java                  
			Process process = new Process();

			// Determine executable filename
			if (!string.IsNullOrEmpty(rs.JavaLocation))
			{
				// TODO: get from HKLM\Software\JavaSoft\JRE\JavaHome?
				process.StartInfo.FileName = Path.Combine(rs.JavaLocation, JAVA_EXE);
			}
			else
				process.StartInfo.FileName = JAVA_EXE; // In path
			
			string jar = Path.Combine(rs.InstallFolder, STARLIGHT_JAR);
			process.StartInfo.Arguments = String.Format("{0} -jar \"{1}\" \"{2}\"", rs.JVMOptions, jar, RepositoryFileName);
			Log.LogMessageFromResources("JavaStartMessage", process.StartInfo.Arguments);
			
			process.StartInfo.CreateNoWindow = true;
			process.StartInfo.RedirectStandardOutput = true;
			process.StartInfo.UseShellExecute = false;
			process.StartInfo.RedirectStandardError = true; 
			
			try
			{
				process.Start();

				StreamReader outputReader = process.StandardOutput;
				while (!outputReader.EndOfStream)
				{
					ParseMasterOutput(outputReader.ReadLine());                    
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
				if (e.NativeErrorCode == ERROR_FILE_NOT_FOUND)
				{
					Log.LogErrorFromResources("JavaExecutableNotFound", process.StartInfo.FileName);
				}
				else if (e.NativeErrorCode == ERROR_ACCESS_DENIED)
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

		/// <summary>
		/// Gets the registry settings.
		/// </summary>
		/// <returns></returns>
		private RegistrySettings GetRegistrySettings()
		{
			RegistrySettings rs = new RegistrySettings();
			if (!rs.ReadSettings())
			{
				Log.LogErrorFromResources("CouldNotReadRegistryValues");
				return null;
			}
			return rs;
		}

		#region Parse Master Output and Logger helper functions

		/// <summary>
		/// Parses the master output.
		/// </summary>
		/// <param name="message">The message.</param>
		private void ParseMasterOutput(string message)
		{
			if (message == null) return;

			// Parse the message
			string[] parsed = message.Split("~".ToCharArray(), 5);
			if (parsed.Length == 5)
			{
				string module = parsed[0];
				string level = parsed[1];
				string filename = parsed[2];
				string line = parsed[3];
				string msg = parsed[4];
				
				DebugMode mode;
				switch (level)
				{
					case "error": mode = DebugMode.Error; break;
					case "crucial": mode = DebugMode.Crucial; break;
					case "warning": mode = DebugMode.Warning; break;
					case "information": mode = DebugMode.Information; break;
					case "debug": mode = DebugMode.Debug; break;
					default: mode = DebugMode.NotSet; break;
				} 
				
				int linenumber = 0;
				int.TryParse(line, out linenumber);

				if (_buildErrorsEncountered && String.IsNullOrEmpty(message))
				{
					_buildErrorsEncountered = false;
				}

				this.LogMessage(mode, module, msg, filename, linenumber);
			}
			else
			{
				this.LogMessage(message);
			}
		}

		private void LogMessage(DebugMode debugMode, string module, string message, string filename, int line)
		{
			if (_currentDebugMode >= debugMode)
			{
				string modeDescription = getModeDescription(debugMode);
				string fm = String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module, modeDescription, message);

				switch (debugMode)
				{
					case DebugMode.Error:
						Log.LogError(module, "", "", filename, line, 0, line + 1, 0, message);
						break;
					case DebugMode.Crucial:
						Log.LogMessage(MessageImportance.Normal, fm);
						break;
					case DebugMode.Warning:                     
						Log.LogWarning(module, "", "", filename, line, 0, line + 1, 0, message);
						break;
					case DebugMode.Information:
						Log.LogMessage(MessageImportance.Normal, fm);
						break;
					case DebugMode.Debug:
						Log.LogMessage(MessageImportance.Normal, fm);
						break;
				}
			}
		}

		private string getModeDescription(DebugMode mode)
		{
			switch (mode)
			{
				case DebugMode.Error: return "error";
				case DebugMode.Crucial: return "crucial";
				case DebugMode.Warning: return "warning";
				case DebugMode.Information: return "info";
				case DebugMode.Debug: return "debug";
				default: return "unknown";
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

		#endregion
	}

	public enum DebugMode
	{
		NotSet = -1,
		Error = 0,
		Crucial = 1,
		Warning = 2,
		Information = 3,
		Debug = 4
	}
}
