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

        private const string JavaExecutable = "java.exe";

        /// <summary>
        /// Initializes a new instance of the <see cref="T:MasterCallerTask"/> class.
        /// </summary>
        public MasterCallerTask() : base(Properties.Resources.ResourceManager)
        {
            
        }

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

        private RepositoryAccess _repositoryAccess;

        const int ERROR_FILE_NOT_FOUND = 2;
        const int ERROR_ACCESS_DENIED = 5;

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
                    Log.LogMessagesFromStream(p.StandardOutput, MessageImportance.Normal) ;
                }
                if (p.ExitCode == 0)
                {
                    _repositoryAccess.CloseDatabase(); 
                    return true;
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
    }
}
