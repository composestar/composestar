using System;
using System.Collections.Generic;
using System.Text;

namespace Trese.ComposestarTasks
{
	class Config
	{
		public string executable;
		public string applicationStart;
		public string runDebugLevel;
		public string buildDebugLevel;
		public string outputPath;

		public IList<Project> projects = new List<Project>();
		public IList<string> concernSources = new List<string>();
		public IList<Filter> customFilters = new List<Filter>();
		public IList<ModuleSettings> modules = new List<ModuleSettings>();
		public IList<Path> paths = new List<Path>();

		public Project AddProject()
		{
			Project p = new Project();
			projects.Add(p);
			return p;
		}

		public ModuleSettings AddModule(string name)
		{
			ModuleSettings ms = new ModuleSettings();
			ms.name = name;
			return ms;
		}

		public void AddPath(string name, string pathName)
		{
			Path path = new Path();
			path.name = name;
			path.pathName = pathName;
			paths.Add(path);
		}
	}

	class Project
	{
		public string name;
		public string language;
		public string basePath;
		public string buildPath;
		public string outputPath;
		public string assemblyName;

		public IList<string> sources = new List<string>();
		public IList<string> deps = new List<string>();
	}

	class ModuleSettings
	{
		public string name;
		public IDictionary<string, string> props = new Dictionary<string,string>();

		public string this[string key]
		{
			set { props[key] = value; }
		}
	}

	class Filter
	{
		public string name;
		public string library;
	}

	class Path
	{
		public string name;
		public string pathName;
	}
}
