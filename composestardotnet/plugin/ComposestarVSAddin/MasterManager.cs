using EnvDTE;
using Ini;
using System;
using System.Collections;
using System.Threading;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for MasterManager.
	/// </summary>
	public class MasterManager : AbstractManager
	{
		private bool mSuccess = false;
		private string moduleName = "Compose* Master; {0}";

		public MasterManager(IniFile inifile) : base (inifile)
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
			p.StartInfo.FileName = "java";

			string composestarJar = readIniValue("Global Composestar configuration", "ComposestarPath");
			composestarJar = composestarJar.Replace("\"", "");
			composestarJar = "\"" + composestarJar + "Composestar.jar\"";

			string classPath = readIniValue("Global Composestar configuration","ClassPath");
			string mainClass = readIniValue("Global Composestar configuration","MainClass");
			string jvmOptions = readIniValue("JVM","JVMOptions");

			string projectIni = readIniValue("Common", "TempFolder");
			projectIni = projectIni.Replace("\"", "");
			projectIni = "\"" + projectIni + "build.ini\"";

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
