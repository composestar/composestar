using EnvDTE;
using Ini;
using System;
using System.Collections;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for MasterManager.
	/// </summary>
	public class PeWeaverManager : AbstractManager
	{
		private bool mSuccess = false;

		public PeWeaverManager(IniFile inifile) : base (inifile)
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
			p.StartInfo.FileName = this.readIniValue("Global Composestar configuration", "ComposestarPath") + "bin/peweaver.exe";

			p.StartInfo.Arguments = "/nologo /quiet";

			if (this.readIniValue("Global Composestar configuration", "DebugLevel").Equals("3"))
			{
				p.StartInfo.Arguments += " /debug";
			}

			if (this.readIniValue("Global Composestar configuration", "VerifyAssemblies").Equals("true"))
			{
				p.StartInfo.Arguments += " /verify";
			}

			p.StartInfo.Arguments += "/ws " + this.readIniValue("Common", "TempFolder") + "weavespec.xml";

			string targets = String.Join("\",\"", this.readIniValue("Generated assemblies", "AssembliesToWeave").Split(','));
			p.StartInfo.Arguments += " " + targets;

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
			}
		}
	}
}
