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

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Calls the Master to perform the actual analyzing of concerns by composestar.
    /// </summary>
    [LoadInSeparateAppDomain()]
    public class MasterCallerTask : AppDomainIsolatedTask 
    {
        
        #region Constructor
        /// <summary>
        /// Initializes a new instance of the <see cref="T:MasterCallerTask"/> class.
        /// </summary>
        public MasterCallerTask() : base(Properties.Resources.ResourceManager)      {    }
        
        #endregion

        #region Properties

        private string _repositoryFileName;

        /// <summary>
        /// Gets or sets the repository filename.
        /// </summary>
        /// <value>The repository filename.</value>
        [Required()]
        public string RepositoryFileName
        {
            get { return _repositoryFileName; }
            set { _repositoryFileName = value; }
        }

        private ITaskItem[] _concernFiles;

        /// <summary>
        /// Gets or sets the concern files.
        /// </summary>
        /// <value>The concern files.</value>
        [Required()]
        public ITaskItem[] ConcernFiles
        {
            get { return _concernFiles; }
            set { _concernFiles = value; }
        }

        private string debugLevel;

        /// <summary>
        /// Gets or sets the debug level.
        /// </summary>
        /// <value>The debug level.</value>
        public string DebugLevel
        {
            get { return debugLevel; }
            set { debugLevel = value; }
        }

        private String _intermediateOutputPath;

        /// <summary>
        /// Gets or sets the intermediate output path.
        /// </summary>
        /// <value>The intermediate output path.</value>
        [Required()]
        public String IntermediateOutputPath
        {
            get { return _intermediateOutputPath; }
            set { _intermediateOutputPath = value; }
        }        
	
        #endregion

        #region Declarations
        private const string JavaExecutable = "java.exe";
        
        private bool BuildErrorsEncountered;
        private DebugMode CurrentDebugMode;
   
        private const int ErrorFileNotFound = 2;
        private const int ErrorAccessDenied = 5;

        public enum DebugMode
        {
            NotSet = -1,
            Error = 0,
            Crucial = 1,
            Warning = 2,
            Information = 3,
            Debug = 4
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
            System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
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

            // Place debuglevel
            Log.LogMessageFromResources("StoreDebugLevel", DebugLevel);

            // Set the Common Configuration
            short debugLevelValue;
            if (!short.TryParse(DebugLevel, out debugLevelValue))
            {
                Log.LogErrorFromResources("CouldNotConvertDebugLevel", DebugLevel);
                return false;
            }

            CurrentDebugMode = (DebugMode)debugLevelValue;

            configContainer.CompiletimeDebugLevel = debugLevelValue;
            configContainer.IntermediateOutputPath = IntermediateOutputPath;
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
            System.Diagnostics.Process process = new System.Diagnostics.Process();

            // Determine filename
            if (!string.IsNullOrEmpty(rs.JavaLocation) )
            {
                process.StartInfo.FileName = Path.Combine(rs.JavaLocation, JavaExecutable);
            }
            else
                process.StartInfo.FileName = JavaExecutable; // In path
            
            process.StartInfo.Arguments = String.Format(CultureInfo.InvariantCulture,  "{0} -cp \"{1}\" {2} \"{3}\"", rs.JVMOptions, rs.ClassPath, rs.MainClass, RepositoryFileName);
            Log.LogMessageFromResources("JavaStartMessage", process.StartInfo.Arguments ) ;
            
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
                string warninglevel = parsed[1];
                string filename = parsed[2];
                string line = parsed[3];
                string msg = parsed[4];
                DebugMode mode;

                try
                {
                    mode = (DebugMode)DebugMode.Parse(typeof(DebugMode), warninglevel, true);
                }
                catch (ArgumentException)
                {
                    mode = DebugMode.Warning;
                }

                int linenumber = 0;
                try
                {
                    linenumber = Convert.ToInt32(line, CultureInfo.CurrentCulture);
                }
                catch (ArgumentException)
                { 
                    linenumber = 0; 
                }

                if (this.BuildErrorsEncountered && String.IsNullOrEmpty(message))
                {
                    this.BuildErrorsEncountered = false;
                }

                // Check for compilation errors
                if (msg != null && msg.StartsWith("RECOMACOMERROR:"))
                    this.BuildErrorsEncountered = true;
                else
                    this.BuildErrorsEncountered = false;

                // Update task list with compilation errors
                if (this.BuildErrorsEncountered)
                {
                    try
                    {
                        string comError = "RECOMACOMERROR:";
                        string file = msg.Substring(comError.Length, msg.IndexOf("(") - comError.Length).Replace("/", "\\");
                        string rest = msg.Substring(file.Length + 1 + comError.Length);
                        int lineRecoma = 0;

                        lineRecoma = int.Parse((rest.Substring(0, rest.IndexOf(","))), CultureInfo.CurrentCulture);

                        rest = rest.Substring(rest.IndexOf(")") + 2).TrimStart();
                        DebugMode dm;
                        if (rest.StartsWith("warning"))
                            dm = DebugMode.Warning;
                        else
                            dm = DebugMode.Error;
                        this.LogMessage(dm, "RECOMA", rest, file, lineRecoma);
                    }
                    catch (ArgumentException)
                    {
                        Log.LogError(msg);
                    }
                }
                else
                    this.LogMessage(mode, module, msg, filename, linenumber);

            }
            else
            {
                this.LogMessage(message);
            }
        }

        private void LogMessage(DebugMode debugMode, string module, string message, string filename, int line)
        {
            if (CurrentDebugMode >= debugMode)
            {
                String modeDescription = "";

                switch (debugMode)
                {
                    case DebugMode.Warning:                     
                        modeDescription = "warning";
                        Log.LogWarning(module, "", "", filename, line, 0, line + 1, 0, message);
                        break;
                    case DebugMode.Information:
                        modeDescription = "info";
                        Log.LogMessage(MessageImportance.Normal, String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module, modeDescription, message));
                        break;
                    case DebugMode.Debug:
                        modeDescription = "debug";
                        Log.LogMessage(MessageImportance.Normal, String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module,modeDescription, message));
                        break;
                    case DebugMode.Crucial:
                        modeDescription = "crucial";
                        Log.LogMessage(MessageImportance.Normal, String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module,modeDescription, message));
                        break;
                    case DebugMode.Error:
                        modeDescription = "error";
                        Log.LogError(module, "", "", filename, line, 0, line + 1, 0, message);
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

        #endregion
    }
}
