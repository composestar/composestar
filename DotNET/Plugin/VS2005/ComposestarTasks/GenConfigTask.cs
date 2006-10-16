using System;
using System.Text;
using System.Collections.Generic;

using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;

namespace Trese.ComposestarTasks
{
	public class GenConfigTask : Task
	{
		private string m_projectName;
		private string m_projectPath;
		private string[] m_sourceFiles;
		private ITaskItem[] m_dependencies;
		private string m_outputPath;
		private string m_startupObject;
		private string m_frameworkPath;
		private string m_frameworkSdkPath;

		public GenConfigTask()
		{
		}

		[Required]
		public string ProjectName
		{
			set { m_projectName = value; }
		}

		[Required]
		public string ProjectPath
		{
			set { m_projectPath = FixPath(value); }
		}

		[Required]
		public string[] SourceFiles
		{
			set { m_sourceFiles = value; }
		}

		[Required]
		public ITaskItem[] Dependencies
		{
			set { m_dependencies = value; }
		}

		[Required]
		public string OutputPath
		{
			set { m_outputPath = value; }
		}

		[Required]
		public string StartupObject
		{
			set { m_startupObject = value; }
		}

		[Required]
		public string FrameworkPath
		{
			set { m_frameworkPath = value; }
		}

		[Required]
		public string FrameworkSdkPath
		{
			set { m_frameworkSdkPath = value; }
		}

		public override bool Execute()
		{
			WriteConfiguration();
			return true;
		}

		private void WriteConfiguration()
		{
			Config config = new Config();
			config.buildDebugLevel = "4";
			config.runDebugLevel = "1";
			config.outputPath = m_outputPath;
			config.applicationStart = m_startupObject;

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
			foreach (ITaskItem dep in m_dependencies)
				project.deps.Add(dep.GetMetadata("FullPath"));

			// module settings
			ModuleSettings filth = config.AddModule("FILTH");
			ModuleSettings coder = config.AddModule("CODER");
			ModuleSettings secret = config.AddModule("SECRET");
			ModuleSettings ilicit = config.AddModule("ILICIT");

			secret["mode"] = "2";
			ilicit["verifyAssemblies"] = "False";

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

		private bool IsConcernSource(string filename)
		{
			return filename.EndsWith(".cps");
		}

		private String FixPath(String path)
		{
			// argh, ugly hack :(
			return (path.EndsWith(@"\") ? path : path + @"\");
		}
	}
}
