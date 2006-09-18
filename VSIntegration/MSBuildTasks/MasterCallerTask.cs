using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;
using System.ComponentModel;
using System.Security.Permissions;  

using Microsoft.Win32;

using Microsoft.Build.BuildEngine;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.ILAnalyzer;
using Composestar.Repository.LanguageModel;
using Composestar.Repository.Configuration;
using Composestar.Repository;

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Calls the Master to perform the actual analyzing of concerns by composestar.
    /// </summary>
    public class MasterCallerTask : Task
    {
        
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
        private string _repositoryFilename;

        /// <summary>
        /// Gets or sets the repository filename.
        /// </summary>
        /// <value>The repository filename.</value>
        [Required()]
        public string RepositoryFilename
        {
            get { return _repositoryFilename; }
            set { _repositoryFilename = value; }
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
        #endregion

        #region Declarations
        private const string JavaExecutable = "java.exe";
        
        private bool BuildErrorsEncountered = false;
        private DebugMode CurrentDebugMode;

        private RepositoryAccess _repositoryAccess;

        const int ERROR_FILE_NOT_FOUND = 2;
        const int ERROR_ACCESS_DENIED = 5;

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
            // Open DB
            Log.LogMessage(MessageImportance.Low, "Opening repository '{0}'.", RepositoryFilename);
            _repositoryAccess = new RepositoryAccess(RepositoryFilename);

            Log.LogMessage("Preparing to start master by collecting data.");

            // Place the concern files in the datastore
            foreach (ITaskItem item in ConcernFiles)
            {
                string fullname = item.ToString();

                string path = Path.GetDirectoryName(fullname);
                string filename = Path.GetFileName(fullname);
 
                ConcernInformation ci = new ConcernInformation(filename, path);

                Log.LogMessage("Adding concern '{0}' to the repository.", filename);
                _repositoryAccess.AddConcern(ci);
            }

            // Place debuglevel
            Log.LogMessage("Debug level is {0}.", DebugLevel);             

            // Set the Common Configuration
            CommonConfiguration cc = _repositoryAccess.GetCommonConfiguration();                
            int debugLevelValue;
            if (!int.TryParse(DebugLevel, out debugLevelValue ))
            {
                Log.LogErrorFromResources("CouldNotConvertDebugLevel", DebugLevel); 
                _repositoryAccess.CloseDatabase(); 
                return false;
            }
            CurrentDebugMode = (DebugMode)debugLevelValue;
            cc.CompiletimeDebugLevel = debugLevelValue;
            _repositoryAccess.SetCommonConfiguration(cc);

            // Prepare to run java
            string classPath = "";
            string mainClass = "";
            string jvmOptions = "";
            string javaLocation = "";

            // Retrieve the settings from the registry
            RegistryPermission keyPermissions = new RegistryPermission(
               RegistryPermissionAccess.Read, @"HKEY_LOCAL_MACHINE\Software\Composestar\StarLight");

            RegistryKey regKey = Registry.LocalMachine.OpenSubKey(@"Software\Composestar\StarLight");

            if (regKey != null)
            {
                classPath = (string)regKey.GetValue("JavaClassPath", "");
                mainClass = (string)regKey.GetValue("JavaMainClass", "Composestar.DotNET.MASTER.StarLightMaster");
                jvmOptions = (string)regKey.GetValue("JavaOptions", "");
                javaLocation = (string)regKey.GetValue("JavaFolder", "");
            }
            else
            {
                Log.LogErrorFromResources("CouldNotReadRegistryValues");
                _repositoryAccess.CloseDatabase();
                return false;
            }

            // Check for empty values
            if (string.IsNullOrEmpty(classPath) | string.IsNullOrEmpty(mainClass))
            {
                Log.LogErrorFromResources("CouldNotReadRegistryValues");
                _repositoryAccess.CloseDatabase();
                return false;
            }
                                              
            // Start java                  
            System.Diagnostics.Process p = new System.Diagnostics.Process();

            // Determine filename
            if (!string.IsNullOrEmpty(javaLocation) )
            {
                p.StartInfo.FileName = Path.Combine(javaLocation, JavaExecutable);
            }
            else
                p.StartInfo.FileName = JavaExecutable; // In path
            
            p.StartInfo.Arguments = String.Format("{0} -cp \"{1}\" {2} \"{3}\"", jvmOptions, classPath, mainClass, RepositoryFilename);
            Log.LogMessage("Java will be called with the arguments: {0}", p.StartInfo.Arguments ) ;
            
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.UseShellExecute = false;
            
            try
            {
                p.Start();
                while (!p.HasExited)
                {            
                    ParseMasterOutput(p.StandardOutput.ReadLine());
                    //Log.LogMessagesFromStream(p.StandardOutput, MessageImportance.Normal) ;
                }
                if (p.ExitCode == 0)
                {
                    _repositoryAccess.CloseDatabase(); 
                    return !Log.HasLoggedErrors;
                }
                else
                {
                    Log.LogErrorFromResources("MasterRunFailed", p.ExitCode);
                    _repositoryAccess.CloseDatabase(); 
                    return false;
                }
            }
            catch (Win32Exception e)
            {
                if (e.NativeErrorCode == ERROR_FILE_NOT_FOUND)
                {
                    Log.LogErrorFromResources("JavaExecutableNotFound", p.StartInfo.FileName);
                }
                else if (e.NativeErrorCode == ERROR_ACCESS_DENIED)
                {
                    Log.LogErrorFromResources("JavaExecutableAccessDenied", p.StartInfo.FileName);
                }
                else
                {
                    Log.LogErrorFromResources("ExecutionException", e.ToString());
                }
                _repositoryAccess.CloseDatabase(); 
                return false;
            }                                              
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
                catch (Exception)
                {
                    mode = DebugMode.Warning;
                }

                int linenumber = 0;
                try
                {
                    linenumber = Convert.ToInt32(parsed[3]);
                }
                catch (Exception) { linenumber = 0; }

                if (this.BuildErrorsEncountered && message.Equals(""))
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

                        lineRecoma = int.Parse((rest.Substring(0, rest.IndexOf(","))));

                        rest = rest.Substring(rest.IndexOf(")") + 2).TrimStart();
                        DebugMode dm;
                        if (rest.StartsWith("warning"))
                            dm = DebugMode.Warning;
                        else
                            dm = DebugMode.Error;
                        this.LogMessage(dm, "RECOMA", rest, file, lineRecoma);
                    }
                    catch (Exception)
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

        /// <summary>
        /// Log the message.
        /// </summary>
        /// <param name="debugMode">Debugmode to use.</param>
        /// <param name="module">Module creating this log message.</param>
        /// <param name="message">The logmessage</param>
        private void LogMessage(DebugMode debugMode, string module, string message)
        {
            this.LogMessage(debugMode, module, message, "", 0);
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
                        modeDescription = "DEBUG";
                        Log.LogMessage(MessageImportance.Normal, String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module,modeDescription, message));
                        break;
                    case DebugMode.Crucial:
                        modeDescription = "";
                        Log.LogMessage(MessageImportance.Normal, String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module,modeDescription, message));
                        break;
                    case DebugMode.Error:
                        modeDescription = "ERROR";
                        Log.LogError(module, "", "", filename, line, 0, line + 1, 0, message);
                        break;
                }
            }
        }

        /// <summary>
        /// Log the message with the current debugmode.
        /// </summary>
        /// <param name="module">Module creating this log message.</param>
        /// <param name="message">The logmessage</param>
        private void LogMessage(string module, string message)
        {
            LogMessage(this.CurrentDebugMode, module, message);
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
