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
		private string m_clientLanguage;
		private string[] m_sources;
		private string[] m_concerns;
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
		public string ClientLanguage
		{
			set { m_clientLanguage = value; }
		}

		[Required]
		public string[] Sources
		{
			set { m_sources = value; }
		}

		[Required]
		public string[] Concerns
		{
			set { m_concerns = value; }
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
			project.language = m_clientLanguage;
			project.basePath = m_projectPath;

			// concerns
			foreach (string concernPath in m_concerns)
			{
				string absolutePath = m_projectPath + concernPath;
				config.concernSources.Add(absolutePath);
			}

			// sources
			foreach (string sourcePath in m_sources)
			{
				string absolutePath = m_projectPath + sourcePath;
				project.sources.Add(absolutePath);
			}

			// dependencies
			foreach (ITaskItem dep in m_dependencies)
				project.deps.Add(dep.GetMetadata("FullPath"));

			// module settings
			config.AddModuleSetting("INCRE", "config", "INCRE-DotNET20.xml");
			config.AddModuleSetting("SECRET", "mode", "2");
			config.AddModuleSetting("ILICIT", "verifyAssemblies", "false");

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
