using EnvDTE;
using System;
using System.Collections;
using ComposeStar.MSBuild.Tasks.BuildConfiguration;

namespace ComposeStar.MSBuild.Tasks.MasterManager
{
	/// <summary>
	/// Summary description for MasterManager.
	/// </summary>
	public class MasterManager
	{
		private bool mSuccess = false;

		public MasterManager(IniFile inifile)
		{

		}

		public bool CompletedSuccessfully() 
		{
			return mSuccess;
		}

		public void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
		{
            /*this.mApplicationObject = applicationObject;

            System.Diagnostics.Process p = new System.Diagnostics.Process();
            p.StartInfo.FileName = "java";

            string composestarJar = readIniValue("Global Composestar configuration", "ComposestarPath");
            composestarJar = composestarJar.Replace("\"", "");
            composestarJar = "\"" + composestarJar + "Composestar.jar\"";

            string projectIni = readIniValue("Common", "TempFolder");
            projectIni = projectIni.Replace("\"", "");
            projectIni = "\"" + projectIni + "project.ini\"";

            p.StartInfo.Arguments = "-cp " + composestarJar + " Composestar.CTCommon.Master.Master " + projectIni;
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.UseShellExecute = false;
			
            p.Start();

            string output =  p.StandardOutput.ReadToEnd();
            p.WaitForExit();

            this.printMessage(output);

            if (p.ExitCode == 0) 
            {
                mSuccess = true;
            }*/
        }
	}
}
