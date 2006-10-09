using System;
using System.IO;
using System.ComponentModel;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;


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

        private ITaskItem[] _assemblyFiles;

        /// <summary>
        /// Gets or sets the assembly files to analyze.
        /// </summary>
        /// <value>The assembly files.</value>
        [Required()]
        public ITaskItem[] AssemblyFiles
        {
            get { return _assemblyFiles; }
            set { _assemblyFiles = value; }
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
            string filename;

            System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
            sw.Start();

            // Get the location of PEVerify
            PEVerifyLocation = RegistrySettings.GetNETSDKLocation();
 
            if (String.IsNullOrEmpty(PEVerifyLocation) || !File.Exists(Path.Combine(PEVerifyLocation, PEVerifyExecutable)) )
            {
                Log.LogWarningFromResources("PEVerifyExecutableNotFound", "PEVerify.exe");  
                return true;
            }
            
            // Setup process
            System.Diagnostics.Process process = new System.Diagnostics.Process();

            // Determine filename
            process.StartInfo.FileName = Path.Combine(PEVerifyLocation, PEVerifyExecutable);
               
            process.StartInfo.CreateNoWindow = true;
            process.StartInfo.RedirectStandardOutput = true;
            process.StartInfo.UseShellExecute = false;
            process.StartInfo.RedirectStandardError = true;

            // Execute PEVerify for each file
            foreach (ITaskItem item in AssemblyFiles)
            {
                try
                {
                    filename = item.ToString();
                    Log.LogMessage("Verifying assembly '{0}'...", filename);

                    process.StartInfo.Arguments = String.Format("{0} /IL /MD /NOLOGO", filename);

                    process.Start();
                    while (!process.HasExited)
                    {
                        
                    }
                    if (process.ExitCode == 0)
                    {
                        Log.LogMessageFromResources("VerifySuccess", filename);                         
                    }
                    else
                    {
                        while (!process.StandardOutput.EndOfStream)
                        {
                            ParseOutput(process.StandardOutput.ReadLine(), filename);
                        }                        
                    }

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
                catch (Exception ex)
                {
                    Log.LogErrorFromException(ex, true);
                }

                 
            }

            sw.Stop();

            Log.LogMessage("Verification of '{0}' assemblies executed in {1:0.0000} seconds.", AssemblyFiles.Length, sw.Elapsed.TotalSeconds);

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

                    Log.LogError(String.Format("Verification error in '{0}', method '{2}' at {3}: {1}", filename, mes, method, offset));
                }
                catch (Exception)
                {
                    Log.LogError(message);
                }
                
            }

        }

    }
}
