using EnvDTE;
using Ini;
using System;
using System.Collections;
using System.Threading;
using BuildConfiguration;
using System.Diagnostics;
using System.ComponentModel;
using System.IO;

namespace Composestar.StarLight.VSAddin
{
	/// <summary>
	/// Summary description for MasterManager.
	/// </summary>
	public class MasterManager : AbstractManager
	{
		private bool mSuccess = false;
		private string moduleName = "StarLight Master; {0}";
		const int ERROR_FILE_NOT_FOUND =2;
		const int ERROR_ACCESS_DENIED = 5;

		public MasterManager() : base ()
		{

		}

		public bool CompletedSuccessfully() 
		{
			return mSuccess;
		}

		public override void Run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
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
			try
			{
				p.Start();
				while (!p.HasExited)
				{
					Debug.Instance.ParseLog(p.StandardOutput.ReadLine());
				}
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
			catch (Win32Exception e)
			{
				if(e.NativeErrorCode == ERROR_FILE_NOT_FOUND)
				{
					//Console.WriteLine(e.Message + ". Check the path.");
					Debug.Instance.AddTaskItem("The java execuatble, "+p.StartInfo.FileName+", is not found, please add the Java executable to your path!", vsTaskPriority.vsTaskPriorityHigh , vsTaskIcon.vsTaskIconCompile  );
					Debug.Instance.ActivateTaskListWindow();
				} 

				else if (e.NativeErrorCode == ERROR_ACCESS_DENIED)
				{
					// Note that if your word processor might generate exceptions
					// such as this, which are handled first.
					Debug.Instance.AddTaskItem("The java execuatble, "+p.StartInfo.FileName+", can not be accessed!", vsTaskPriority.vsTaskPriorityHigh , vsTaskIcon.vsTaskIconCompile  );
					Debug.Instance.ActivateTaskListWindow();
				}
			}
		}

	}
}
