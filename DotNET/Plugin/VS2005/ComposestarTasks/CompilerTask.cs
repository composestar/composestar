using System;
using System.Text;
using System.Diagnostics;
using System.Collections.Generic;

using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;
using System.ComponentModel;
using System.IO;

namespace Trese.ComposestarTasks
{
	public class ComposestarCompilerTask : Task
	{
		private string m_projectName;
		private string m_projectPath;
		private string m_buildPath;
		private string[] m_sourceFiles;
		private string[] m_assemblies;
		private string m_outputPath;
		private string m_outputAssembly;
		private string m_frameworkPath;
		private string m_frameworkSdkPath;

		public ComposestarCompilerTask()
		{
		}

		[Required]
		public string ProjectName
		{
			get { return m_projectName; }
			set { m_projectName = value; }
		}

		[Required]
		public string ProjectPath
		{
			get { return m_projectPath; }
			set { m_projectPath = FixPath(value); }
		}

		[Required]
		public string BuildPath
		{
			get { return m_buildPath; }
			set { m_buildPath = value; }
		}

		[Required]
		public string[] SourceFiles
		{
			get { return m_sourceFiles; }
			set { m_sourceFiles = value; }
		}

		[Required]
		public string[] ReferencedAssemblies
		{
			get { return m_assemblies; }
			set { m_assemblies = value; }
		}

		[Required]
		public string OutputPath
		{
			get { return m_outputPath; }
			set { m_outputPath = value; }
		}

		[Required]
		public string OutputAssembly
		{
			get { return m_outputAssembly; }
			set { m_outputAssembly = value; }
		}

		[Required]
		public string FrameworkPath
		{
			get { return m_frameworkPath; }
			set { m_frameworkPath = value; }
		}

		[Required]
		public string FrameworkSdkPath
		{
			get { return m_frameworkSdkPath; }
			set { m_frameworkSdkPath = value; }
		}

		public override bool Execute()
		{
			WriteConfiguration();
			InvokeCompiler();

			return false;
		}

		private void InvokeCompiler()
		{
			Process p = new Process();
			p.StartInfo.FileName = "java";
			p.StartInfo.Arguments = GetCompilerArguments();
			p.StartInfo.CreateNoWindow = true;
			p.StartInfo.UseShellExecute = false;
			p.StartInfo.RedirectStandardOutput = true;
//			p.StartInfo.RedirectStandardError = true;

			try
			{
				p.Start();

				while (!p.HasExited)
				{
					string line = p.StandardOutput.ReadLine();
					Debug.Print(line);
					Log.LogError(line);
				}

			//	string stderr = p.StandardError.ReadToEnd();
			//	Debug.Print("stderr=" + stderr);
			}
			catch (Exception e)
			{
				Log.LogErrorFromException(e);
				Debug.Print(e.Message);
			}
		}

		private String GetCompilerArguments()
		{
			String mainjar = @"C:\Program Files\ComposeStar\binaries\ComposestarDotNET.jar";

			StringBuilder sb = new StringBuilder();
			sb.Append("-jar ");
			sb.Append('"').Append(mainjar).Append('"');
			sb.Append(" BuildConfiguration.xml");

			return sb.ToString();
		}

		private void WriteConfiguration()
		{
			Config config = new Config();
			config.executable = "NotSet";
			config.applicationStart = "NotSet";
			config.runDebugLevel = "1";
			config.buildDebugLevel = "3";
			config.outputPath = m_outputPath;

			Project project = config.AddProject();
			project.name = m_projectName;
			project.language = "JSharp";
			project.basePath = m_projectPath;

			// sources
			foreach (string s in m_sourceFiles)
			{
				string absolutePath = m_projectPath + s;
				if (IsConcernSource(s))
					config.concernSources.Add(absolutePath);
				else
					project.sources.Add(absolutePath);
			}

			// dependencies
			foreach (string s in m_assemblies)
				project.deps.Add(s);

			// module settings
			ModuleSettings filth = config.AddModule("FILTH");
			ModuleSettings coder = config.AddModule("CODER");
			ModuleSettings secret = config.AddModule("SECRET");
			ModuleSettings ilicit = config.AddModule("ILICIT");

			filth["output_pattern"] = "./analyses/FILTH_";
			coder["DebuggerType"] = "CodeDebugger";
			secret["mode"] = "2";
			ilicit["verifyAssemblies"] = "False";
		//	ilicit.props["assemblies"] = "NotSet";

			// paths
			config.AddPath("Base", m_projectPath);
			config.AddPath("Composestar", @"C:\Program Files\ComposeStar\");
			config.AddPath("NET", m_frameworkPath);
			config.AddPath("NETSDK", m_frameworkSdkPath);
			config.AddPath("EmbeddedSources", @"embedded\");
			config.AddPath("Dummy", @"dummies\");

			ConfigWriter cw = new ConfigWriter(config);
			cw.write();
		}

		private String FixPath(String path)
		{
			// argh, ugly hack :(
			return (path.EndsWith(@"\") ? path : path + @"\");
		}

		public bool IsConcernSource(string filename)
		{
			return filename.EndsWith(".cps");
		}
	}
}
