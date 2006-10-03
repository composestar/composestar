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

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Execute the PEVerify application to check if the generated IL code is valid.
    /// </summary>
    public class ILVerifyTask : Task
    {

        const int ErrorFileNotFound = 2;
        const int ErrorAccessDenied = 5;

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

            // Get the location of PEVerify
            PEVerifyLocation = RegistrySettings.GetNETSDKLocation();
 
            if (String.IsNullOrEmpty(PEVerifyLocation) || !File.Exists(Path.Combine(PEVerifyLocation, PEVerifyExecutable)) )
            {
                Log.LogWarningFromResources("PEVerifyExecutableNotFound", "PEVerify.exe");  
                return true;
            }
            
            // Setup process
            System.Diagnostics.Process p = new System.Diagnostics.Process();

            // Determine filename
            p.StartInfo.FileName = Path.Combine(PEVerifyLocation, PEVerifyExecutable);
               
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.UseShellExecute = false;
            p.StartInfo.RedirectStandardError = true;

            // Execute PEVerify for each file
            foreach (ITaskItem item in AssemblyFiles)
            {
                try
                {
                    filename = item.ToString();
                    Log.LogMessage("Verifying file {0}", filename);

                    p.StartInfo.Arguments = String.Format("{0} /IL /MD /NOLOGO", filename);

                    p.Start();
                    while (!p.HasExited)
                    {
                        
                    }
                    if (p.ExitCode == 0)
                    {
                        Log.LogMessageFromResources("VerifySuccess", filename);                         
                    }
                    else
                    {
                        while (!p.StandardOutput.EndOfStream)
                        {
                            ParseOutput(p.StandardOutput.ReadLine(), filename);
                        }                        
                    }

                }
                catch (Win32Exception e)
                {
                    if (e.NativeErrorCode == ErrorFileNotFound)
                    {
                        Log.LogWarningFromResources("PEVerifyExecutableNotFound", p.StartInfo.FileName);                        
                        return true;
                    }
                    else if (e.NativeErrorCode == ErrorAccessDenied)
                    {
                        Log.LogWarningFromResources("PEVerifyExecutableAccessDenied", p.StartInfo.FileName);                        
                    return true;
                    }
                    else
                    {
                        Log.LogErrorFromResources("PEVerifyExecutionException", e.ToString());
                        return false;
                    }
                    
                }
                catch (Exception ex)
                {
                    Log.LogErrorFromException(ex, true);
                }

                 
            }

            return !Log.HasLoggedErrors;

        }

        /// <summary>
        /// Parses the output.
        /// </summary>
        /// <remarks>
        ///     [IL]: Error: [C:\ComposeStar\StarLight\Examples\Testing\Concerns\bin\Debug\B
        /// asicTests.exe : BasicTests.FilterTests::doOutsideVisit][offset 0x0000000A] Unabl
        /// e to resolve token.
        /// </remarks> 
        /// <param name="message">The message.</param>
        /// <param name="filename">The filename.</param>
        private void ParseOutput(String message, string filename)
        {
            if (!message.Contains("Errors Verifying") & !message.Contains("Error Verifying"))
            {
                try
                {
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
