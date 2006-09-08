using EnvDTE;
using System;
using System.Collections;
using System.Collections.Specialized;
using System.IO;
using Ini;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for ConfigManager.
	/// </summary>
	public class ConfigManager : AbstractManager
	{
		private SupportedLanguages languages = null;
		private Hashtable mSources;
		private ArrayList mUsedCompilers;
		private string mFilthFilterOrderSpecification = "";

		public ConfigManager(IniFile inifile) : base (inifile)
		{
			languages = new SupportedLanguages();
            mSources = new Hashtable(languages.GetCount());
			mSources.Add("ConcernSources", new ArrayList()); // ConcernSources tag should always be in the project.ini

			mUsedCompilers = new ArrayList(languages.GetCount());
		}

		private void ProcessProjectFiles(string path, ProjectItems projectitems) 
		{
			foreach (ProjectItem projectitem in projectitems)
			{
				if (projectitem.ProjectItems.Count > 0) 
				{
					ProcessProjectFiles(path + projectitem.Name + "/", projectitem.ProjectItems);
				}
				else 
				{
					Language l = languages.GetLanguage(projectitem.Name);

					string sourcename = "";
					if (l != null) 
					{
						sourcename = l.SourceTag;

						if ( l.isCompiler() && !this.mUsedCompilers.Contains(l.Name) ) 
						{
							this.mUsedCompilers.Add(l.Name);
						}
					}

					if (!sourcename.Equals(""))
					{
						if (mSources.ContainsKey(sourcename))
						{
							((ArrayList)mSources[sourcename]).Add("" + path + projectitem.Name + "");
						}
						else 
						{
							ArrayList list = new ArrayList();
							list.Add("" + path + projectitem.Name + "");
							mSources.Add(sourcename, list);
						}
					}

					// Check for FILTH filter order specification file
					if (projectitem.Name.Equals("filterorderspecification.xml"))
					{
						this.mFilthFilterOrderSpecification = path + "filterorderspecification.xml";
					}
				}
				//System.Windows.Forms.MessageBox.Show(projectitem.Name + ": " + projectitem.ProjectItems.Count  );
			}
		}

		private string GetProjectOutputPath(Project project) 
		{
			return getProperty(project.ConfigurationManager.ActiveConfiguration.Properties, "OutputPath").ToString();
		}

		public override void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
		{
			mApplicationObject = applicationObject;
			ArrayList outputFileNames = new ArrayList();
			ArrayList startupObjects = new ArrayList();

			foreach (Project project in mApplicationObject.Solution.Projects) 
			{
				if( project != null  && project.Properties != null)
				{
					if( getProperty(project.Properties, "OutputType") != null )
					{
						if (getProperty(project.Properties, "OutputType").ToString().Equals("1")) 
						{
							// Add the executable to the list, all dll's are automatically included with *.dll
							outputFileNames.Add(getProperty(project.Properties, "LocalPath").ToString().Replace("\\", "/") + getProperty(project.Properties, "OutputFileName").ToString());
						}
					}
					outputFileNames.Add((getProperty(project.Properties, "LocalPath").ToString() + GetProjectOutputPath(project) + "*.dll").Replace("\\", "/"));
					ProcessProjectFiles(getProperty(project.Properties, "LocalPath").ToString().Replace("\\", "/"), project.ProjectItems);
			
					if (!getProperty(project.Properties, "StartupObject").ToString().Equals(""))
					{
						startupObjects.Add(getProperty(project.Properties, "StartupObject").ToString());
					}
				}
			}
			
			IEnumerator enumSources = mSources.GetEnumerator();
			while (enumSources.MoveNext())
			{
				DictionaryEntry source = (DictionaryEntry)enumSources.Current;
				
				this.writeIniValue("Sources", source.Key.ToString(), ((ArrayList)source.Value).Count.ToString());
				for (int sourceCounter=0; sourceCounter < ((ArrayList)source.Value).Count; sourceCounter++)
				{
					this.writeIniValue("Sources", source.Key.ToString().Substring(0, source.Key.ToString().Length-1) + sourceCounter, ((ArrayList)source.Value)[sourceCounter].ToString());
				}
			}

			string usedCompilers = String.Join(",", (string[])(mUsedCompilers).ToArray(typeof(string)));
			this.writeIniValue("Common", "Compilers", usedCompilers);

			this.writeIniValue("Common", "OutputPath", "" + this.readIniValue("Common", "TempFolder").Replace("\\", "/") + "bin/");

			//string targetsString = String.Join(",", (string[])(outputFileNames.ToArray(typeof(string))));
			this.writeIniValue("ILICIT", "BuiltAssemblies", "");

			// TYM configuration
			this.writeIniValue("TYM", "BuildPath", "" + this.readIniValue("Common", "TempFolder").Replace("\\", "/") + "obj/");
		//	StringCollection dependencies = DependencyHarvester.Collect(mApplicationObject.Solution.Projects, false);
//			this.writeIniValue("TYM", "Dependencies", dependencies.Count.ToString());
//			for (int dependencyCounter=0; dependencyCounter < dependencies.Count; dependencyCounter++)
//			{
//				this.writeIniValue("TYM", "Dependency" + dependencyCounter, dependencies[dependencyCounter]);
//			}

			// FILTH configuration
			this.writeIniValue("FILTH", "FILTH_INPUT", this.mFilthFilterOrderSpecification);

			// CONE configuration
			//string startup = "";
			if (startupObjects.Count > 0) {
				//startup = startupObjects[0].ToString();// NOT SUPPORTED YET! String.Join(",", (string[])(startupObjects.ToArray(typeof(string))));
				this.writeIniValue("CONE", "ApplicationStart", string.Join(",", (string[])startupObjects.ToArray(typeof(string))));
			}
			
			// Include Composestar.ini and overwrite values with Project.ini, if it exists
			StreamReader composestarIni = null;
			composestarIni = File.OpenText(this.readIniValue("Common", "ComposestarIniFile"));
			processIniFile( composestarIni );
			composestarIni.Close();

			if ( File.Exists(readIniValue("Common", "TempFolder") + "project.ini") ) 
			{
				composestarIni = File.OpenText(this.readIniValue("Common", "TempFolder") + "project.ini");
				processIniFile( composestarIni );
				composestarIni.Close();
			}
			
			
		}

		private void processIniFile( System.IO.StreamReader composestarIni ) 
		{
			string section = "Global Composestar configuration";
			string line;
			while ( (line = composestarIni.ReadLine()) != null ) 
			{
				if ( line.StartsWith("[") && line.EndsWith("]") ) 
				{
					section = line.Substring(1, line.Length - 2);
				}
				else if ( (!line.StartsWith("#")) && (line.IndexOf("=") > 0) )
				{
					string key = line.Substring(0, line.IndexOf("="));


					string new_value = line.Substring(line.IndexOf("=") + 1, line.Length - line.IndexOf("=") - 1);
					this.writeIniValue(section, key, new_value);

				}
			}
		}
	}
}
