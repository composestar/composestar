using EnvDTE;
using Ini;
using System;
using System.Collections;
using System.Threading;
using BuildConfiguration;
using System.IO;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for MasterManager.
	/// </summary>
	public class MasterManager : AbstractManager
	{
		private bool mSuccess = false;
		private string moduleName = "Compose* Master; {0}";

		public MasterManager() : base ()
		{

		}

		public bool CompletedSuccessfully() 
		{
			return mSuccess;
		}

		public override void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
		{
			this.mApplicationObject = applicationObject;

			System.Diagnostics.Process p = new System.Diagnostics.Process();
			if (BuildConfigurationManager.Instance.Settings.Paths["JavaBin"] != null)
			{
				p.StartInfo.FileName = Path.Combine(BuildConfigurationManager.Instance.Settings.Paths["JavaBin"], "java.exe");
			}
			else
				p.StartInfo.FileName = "java";
			
			string composestarJar = BuildConfigurationManager.Instance.Settings.Paths["Composestar"];   
			composestarJar = composestarJar.Replace("\"", "");
			composestarJar = "\"" + composestarJar + "Composestar.jar\"";

			string classPath = BuildConfigurationManager.Instance.DotNetPlatform.ClassPath; 
			string mainClass = BuildConfigurationManager.Instance.DotNetPlatform.MainClass;  
			string jvmOptions = BuildConfigurationManager.Instance.DotNetPlatform.Options;  

			string projectIni = BuildConfigurationManager.Instance.Settings.Paths["Base"];
			projectIni = projectIni.Replace("\"", "");
			projectIni = "\"" + projectIni + "BuildConfiguration.xml\"";

			//p.StartInfo.Arguments = "-cp " + composestarJar + " Composestar.CTCommon.Master.Master " + projectIni;
			//p.StartInfo.Arguments = "-cp \"" + classPath + "\" " + mainClass + " " + projectIni;
			
			p.StartInfo.Arguments = jvmOptions + " -cp \"" + classPath + "\" " + mainClass + " " + projectIni;
			p.StartInfo.CreateNoWindow = true;
			p.StartInfo.RedirectStandardOutput = true;
			p.StartInfo.UseShellExecute = false;
			p.Start();

			while (!p.HasExited)
			{
				Debug.Instance.ParseLog(p.StandardOutput.ReadLine());
			}
			//
			//			string line = null;
			//			while ((line = p.StandardOutput.ReadLine()) != null)
			//			{
			//				this.printMessage(line);
			//			}

			//			this.printMessage(p.StandardOutput.ReadToEnd());
			//
			//			string output =  p.StandardOutput.ReadToEnd();
			//			p.WaitForExit();
			//
			//			this.printMessage(output);

			if (p.ExitCode == 0) 
			{
				mSuccess = true;
			}
			else
			{
				Debug.Instance.Log(String.Format("Master run failure reported by process. Exit code is {0}.", p.ExitCode) );
				Debug.Instance.AddTaskItem(string.Format(moduleName, "Master run failed."), vsTaskPriority.vsTaskPriorityHigh , vsTaskIcon.vsTaskIconCompile  );
			}
		
		}

	}
}
