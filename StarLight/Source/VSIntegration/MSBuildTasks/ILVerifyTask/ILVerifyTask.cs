using System;
using System.IO;
using System.ComponentModel;
using System.Diagnostics;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.LanguageModel;
using Composestar.StarLight.Configuration;
using Composestar.Repository;

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
        private string _repositoryFilename;

        [Required()]
        public string RepositoryFilename
        {
            get { return _repositoryFilename; }
            set { _repositoryFilename = value; }
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
            string extension;

            Stopwatch sw = new Stopwatch();
            sw.Start();

            // Get the location of PEVerify
            PEVerifyLocation = RegistrySettings.GetNETSDKLocation();

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
            process.StartInfo.RedirectStandardError = true;

            IEntitiesAccessor entitiesAccessor = new EntitiesAccessor();

            ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFilename);

            // Execute PEVerify for each file
            foreach (AssemblyConfig assembly in configContainer.Assemblies)
            {
                try
                {

                    Log.LogMessageFromResources("VerifyingAssembly", assembly.Filename);

                    process.StartInfo.Arguments = String.Format("{0} /IL /MD /NOLOGO", assembly.Filename);

                    process.Start();
                    while (!process.HasExited)
                    {

                    }
                    if (process.ExitCode == 0)
                    {
                        Log.LogMessageFromResources("VerifySuccess", assembly.Filename);
                    }
                    else
                    {
                        while (!process.StandardOutput.EndOfStream)
                        {
                            ParseOutput(process.StandardOutput.ReadLine(), assembly.Filename);
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

            Log.LogMessageFromResources("VerificationCompleted", configContainer.Assemblies.Count, sw.Elapsed.TotalSeconds);

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
                catch (Exception)
                {
                    Log.LogError(message);
                }

            }

        }

    }
}
