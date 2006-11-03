using System;
using System.Globalization;
using System.IO;
using System.ComponentModel;
using System.Threading;
 
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.CoreServices;  
using Composestar.Repository;

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Not implemented further. Could be used to start a Java server is a special thread.
    /// </summary>
    public class JavaServerTask : Task
    {
        
        #region Constructor
        /// <summary>
        /// Initializes a new instance of the <see cref="T:MasterCallerTask"/> class.
        /// </summary>
        public JavaServerTask() : base(Properties.Resources.ResourceManager) { }
        
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
        
        private bool BuildErrorsEncountered = false;
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
               JavaServer.Instance.StartServer();
               return true;
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

    public class JavaServer
    {
        #region Singleton Instance

        private static readonly JavaServer m_Instance = new JavaServer();

        // Explicit static constructor to tell C# compiler
        // not to mark type as beforefieldinit
        static JavaServer()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:EntitiesAccessor"/> class.
        /// </summary>
        private JavaServer()
        {
        }

        /// <summary>
        /// Gets the instance.
        /// </summary>
        /// <value>The instance.</value>
        public static JavaServer Instance
        {
            get
            {
                return m_Instance;
            }
        }

        #endregion

        public void StartServer()
        {
            Console.WriteLine("Created singleton");
            ThreadStart threadDelegate = new ThreadStart(JavaServer.Instance.ServerThreadStart);
            Thread t = new Thread(threadDelegate);
            t.Start(); 
        }

        public void ServerThreadStart()
        {
               RegistrySettings rs = new RegistrySettings();
            if (!rs.ReadSettings())
            {
                Console.WriteLine("CouldNotReadRegistryValues");                 
                return;
            }
                                                                             
            // Start java                  
            System.Diagnostics.Process process = new System.Diagnostics.Process();
            String JavaExecutable = "java";
            // Determine filename
            if (!string.IsNullOrEmpty(rs.JavaLocation) )
            {
                process.StartInfo.FileName = Path.Combine(rs.JavaLocation, JavaExecutable);
            }
            else
                process.StartInfo.FileName = JavaExecutable; // In path

            process.StartInfo.Arguments = String.Format("{0} -cp \"{1}\" {2} \"{3}\"", rs.JVMOptions, rs.ClassPath, "Composestar.DotNET.MASTER.StarLightServer", "");
            //Log.LogMessageFromResources("JavaStartMessage", process.StartInfo.Arguments ) ;
            
            process.StartInfo.CreateNoWindow = false;
            process.StartInfo.RedirectStandardOutput = false;
            process.StartInfo.UseShellExecute = false;
            process.StartInfo.RedirectStandardError = false; 
            
            try
            {
                
                process.Start();
                while (!process.HasExited)
                {
                    Thread.Sleep(100);
                }
            }
            catch (Win32Exception e)
            {
                //if (e.NativeErrorCode == ErrorFileNotFound)
                //{
                //    Log.LogErrorFromResources("JavaExecutableNotFound", process.StartInfo.FileName);
                //}
                //else if (e.NativeErrorCode == ErrorAccessDenied)
                //{
                //    Log.LogErrorFromResources("JavaExecutableAccessDenied", process.StartInfo.FileName);
                //}
                //else
                //{
                //    Log.LogErrorFromResources("ExecutionException", e.ToString());
                //}             
                //return false;
            }
            finally
            {
                
            }
                    
        }
    }

}
