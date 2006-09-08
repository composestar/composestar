using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;
using System.ComponentModel;

using Microsoft.Build.BuildEngine;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.MSBuild.Tasks.BuildConfiguration;

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Calls the Master to perform the actual weaving of composestar.
    /// </summary>
    public class MasterCallerTask : Task
    {

        private string _projectFile = "";

        [Required()]
        public string ProjectFile
        {
            get { return _projectFile; }
            set { _projectFile = value; }
        }

        const int ERROR_FILE_NOT_FOUND = 2;
        const int ERROR_ACCESS_DENIED = 5;

        public override bool Execute()
        {
            if (Utilities.searchComposeStarIniFile(Log) == false)
            {
                return false;
            }

            Utilities.ReadIniFile();
            Utilities.ReadDotNetPlatform();

            if (Settings.Paths["Base"] == null)
            {
                String ObjFolder = String.Concat(Path.GetDirectoryName(ProjectFile), "\\obj\\");
                Settings.Paths.Add("Base", ObjFolder);
            }

            System.Diagnostics.Process p = new System.Diagnostics.Process();
            if (Settings.Paths["JavaBin"] != null)
            {
                p.StartInfo.FileName = Path.Combine(Settings.Paths["JavaBin"], "java.exe");
            }
            else
                p.StartInfo.FileName = "java";

            if (Settings.Paths["Composestar"] == null)
            {
                Log.LogError("Could not find ComposeStar folder location.") ;
                return false;
            }

            string composestarJar = Settings.Paths["Composestar"];
            composestarJar = composestarJar.Replace("\"", "");
            composestarJar = "\"" + composestarJar + "Composestar.jar\"";

            string classPath = "";
            string mainClass = "";
            string jvmOptions = "";

            classPath = BuildConfigurationManager.Instance.DotNetPlatform.ClassPath;
            mainClass = BuildConfigurationManager.Instance.DotNetPlatform.MainClass;
            jvmOptions = BuildConfigurationManager.Instance.DotNetPlatform.Options;
           
            string projectIni;
            projectIni = "\"" + Settings.Paths["Base"] + "BuildConfiguration.xml\"";

            //p.StartInfo.Arguments = "-cp " + composestarJar + " Composestar.CTCommon.Master.Master " + projectIni;
            //p.StartInfo.Arguments = "-cp \"" + classPath + "\" " + mainClass + " " + projectIni;

            p.StartInfo.Arguments = jvmOptions + " -cp \"" + classPath + "\" " + mainClass + " " + projectIni;
            Log.LogMessage("Arguments are: {0}", p.StartInfo.Arguments ) ;
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.UseShellExecute = false;
            try
            {
                p.Start();
                while (!p.HasExited)
                {
                    //Debug.Instance.ParseLog(p.StandardOutput.ReadLine());
                    Log.LogMessagesFromStream(p.StandardOutput, MessageImportance.Normal) ;
                }
                if (p.ExitCode == 0)
                {
                    return true;
                }
                else
                {
                    Log.LogError("Master", 1000, "", "", 0, 0, 0, 0, "Master run failure reported by process. Exit code is {0}.", p.ExitCode);
                    return false;
                }
            }
            catch (Win32Exception e)
            {
                if (e.NativeErrorCode == ERROR_FILE_NOT_FOUND)
                {
                    //Console.WriteLine(e.Message + ". Check the path.");
                    //Debug.Instance.AddTaskItem("The java execuatble, "+p.StartInfo.FileName+", is not found, please add the Java executable to your path!", vsTaskPriority.vsTaskPriorityHigh , vsTaskIcon.vsTaskIconCompile  );
                    //Debug.Instance.ActivateTaskListWindow();
                    Log.LogError("The java executable, " + p.StartInfo.FileName + ", is not found, please add the Java executable to your path!");
                }

                else if (e.NativeErrorCode == ERROR_ACCESS_DENIED)
                {
                    // Note that if your word processor might generate exceptions
                    // such as this, which are handled first.
                    //Debug.Instance.AddTaskItem("The java execuatble, "+p.StartInfo.FileName+", can not be accessed!", vsTaskPriority.vsTaskPriorityHigh , vsTaskIcon.vsTaskIconCompile  );
                    //Debug.Instance.ActivateTaskListWindow();
                    Log.LogError("The java executable, " + p.StartInfo.FileName + ", can not be accessed!");
                }
                return false;
            }
        }

    }
}
