using EnvDTE;
using System;
using System.Collections;
using System.Collections.Specialized;
using System.IO;
using System.Xml;
using System.Text;
using System.Globalization;
using System.Data;
using Ini;
using ComposestarVSAddin;

namespace BuildConfiguration
{
	/// <summary>
	/// Summary description for Configuration. This class stores all the Configuration settings and saves this file to Xml format.
	/// </summary>
	/// <remarks>Not completly type safe. When ported to .NET 2, use generics for the lists.</remarks>
	public class BuildConfigurationManager
	{

		#region Privates

		private ComposestarVSAddin.SupportedLanguages languages = null;
		private string mFilthFilterOrderSpecification = "";
		private ArrayList mUsedCompilers;
		private string _tempFolder = "";
		#endregion

		#region Singleton access
		static readonly BuildConfigurationManager instance=new BuildConfigurationManager();

		// Explicit static constructor to tell C# compiler
		// not to mark type as beforefieldinit
		static BuildConfigurationManager()
		{
		}

		BuildConfigurationManager()
		{
		}

		public static BuildConfigurationManager Instance
		{
			get
			{
				return instance;
			}
		}
		#endregion

		#region Properties

		private System.Collections.ArrayList _projects = new System.Collections.ArrayList();

		public System.Collections.ArrayList Projects
		{
			get
			{
				return _projects;
			}
			set
			{
				_projects = value;
			}
		}
  
		public Project GetProjectByName(string name)
		{
			foreach (Project p in _projects)
			{
				if (p.Name.Equals(name))
					return p;
			}
			return null;
		}

		private Platform _dotNetPlatform;


		public Platform DotNetPlatform
		{
			get
			{
				return _dotNetPlatform;
			}
			set
			{
				_dotNetPlatform = value;
			}
		}

		private System.Collections.ArrayList _concernSources = new System.Collections.ArrayList();
		public System.Collections.ArrayList ConcernSources
		{
			get
			{
				return _concernSources;
			}
			set
			{
				_concernSources = value;
			}
		}

		private Settings _settings = new Settings();
		public Settings Settings
		{
			get
			{
				return _settings;
			}
			set
			{
				_settings = value;
			}
		}

		private ComposestarVSAddin.DebugModes _runDebugLevel = ComposestarVSAddin.DebugModes.NotSet   ;
		public ComposestarVSAddin.DebugModes RunDebugLevel
		{
			get
			{
				return _runDebugLevel;
			}
			set
			{
				_runDebugLevel = value;
			}
		}

		private string _executable = "";


		public string Executable
		{
			get
			{
				return _executable;
			}
			set
			{
				_executable = value;
			}
		}

		private System.Collections.ArrayList _customFilters = new ArrayList ();
 
		public System.Collections.ArrayList CustomFilters
		{
			get
			{
				return _customFilters;
			}
			set
			{
				_customFilters = value;
			}
		}

		private string _outputPath = "";
	
		public string OutputPath
		{
			get
			{
				return _outputPath;
			}
			set
			{
				_outputPath = value;
			}
		}

		private string _applicationStart="";

		public string ApplicationStart
		{
			get
			{
				return _applicationStart;
			}
			set
			{
				_applicationStart = value;
			}
		}

		#endregion

		#region Functions

		#region Save to Xml 

		/// <summary>
		/// Formats the path to forward slashes.
		/// </summary>
		/// <param name="pathName"></param>
		/// <returns></returns>
		private string FormatPath(string pathName)
		{
			// check if it is a path or a file. With a file, do not add last slash.
			if (!Path.HasExtension(pathName)) 
			{ 
				if (!pathName.EndsWith("\\") & !pathName.EndsWith("/") )
					pathName= string.Concat(pathName, "\\"); 
			}
			return pathName.Replace("\\", "/");
		}

		/// <summary>
		/// Save the configuration to Xml
		/// </summary>
		/// <param name="fileName"></param>
		public void SaveToXml(string fileName)
		{

			// Start a new textwriter
			XmlTextWriter writer = null;

			try
			{
				writer = new XmlTextWriter(fileName, Encoding.UTF8);
				writer.Formatting = Formatting.Indented;
					
				// Write the XML declaration. 
				writer.WriteStartDocument();

				// Add comments
				writer.WriteComment(String.Format("This BuildConfiguration file is automaticly generated on {0} by the Composestar VS Addin.", DateTime.Now.ToString () ) );
				
				// Start the root
				writer.WriteStartElement("BuildConfiguration");
				writer.WriteAttributeString("version", "1.00");
	 
				// First the projects
				writer.WriteStartElement("Projects");
				writer.WriteAttributeString("executable", this.Executable);
				writer.WriteAttributeString("applicationStart", this.ApplicationStart);
				writer.WriteAttributeString("runDebugLevel", ((int)this.RunDebugLevel).ToString());
				writer.WriteAttributeString("outputPath", FormatPath(this.OutputPath));
			

				// All the projects
				foreach (Project p in this._projects)
				{
					writer.WriteStartElement("Project");
					writer.WriteAttributeString("name", p.Name );
					writer.WriteAttributeString("language", p.Language  );
					writer.WriteAttributeString("buildPath", FormatPath(p.BuildPath)  );
					writer.WriteAttributeString("basePath", FormatPath(p.BasePath)  );
					writer.WriteAttributeString("outputPath", FormatPath(p.OutputPath)  );
				
					// Sources
					writer.WriteStartElement("Sources");
					foreach (String s in p.Sources)
					{
						writer.WriteStartElement("Source");
						writer.WriteAttributeString("fileName", FormatPath(s));
						writer.WriteEndElement(); 
					}

					// /Sources
					writer.WriteEndElement();

					// Dependencies
					writer.WriteStartElement("Dependencies");
					foreach (String s in p.Dependencies)
					{
						writer.WriteStartElement("Dependency");
						writer.WriteAttributeString("fileName", FormatPath(s));
						writer.WriteEndElement(); 
					}

					// /Dependencies
					writer.WriteEndElement();

					// TypeSources
					writer.WriteStartElement("TypeSources");
					foreach (TypeSource ts in p.TypeSources)
					{
						writer.WriteStartElement("TypeSource");
						writer.WriteAttributeString("name", ts.Name);
						writer.WriteAttributeString("fileName", FormatPath(ts.FileName));
						writer.WriteEndElement(); 
					}

					// /TypeSources
					writer.WriteEndElement();

					// /Project
					writer.WriteEndElement();
				}

				// Write the Concerns
				writer.WriteStartElement("ConcernSources");
				foreach (String s in this.ConcernSources)
				{
					writer.WriteStartElement("ConcernSource");
					writer.WriteAttributeString("fileName", FormatPath(s));
					writer.WriteEndElement(); 
				}

				// /ConcernSources
				writer.WriteEndElement();

				// CustomFilters
				writer.WriteStartElement("CustomFilters");
				foreach (CustomFilter cf in this._customFilters)
				{
					writer.WriteStartElement("Filter");
					writer.WriteAttributeString("filterName", cf.FilterName);
					writer.WriteAttributeString("library", FormatPath(cf.AssemblyName));
					writer.WriteEndElement(); 
				}

				// /CustomFilters
				writer.WriteEndElement();

				// Close the projects
				writer.WriteEndElement();

				// Settings
				writer.WriteStartElement("Settings");
				writer.WriteAttributeString("composestarIni", FormatPath(this.Settings.ComposestarIni ) );
				writer.WriteAttributeString("buildDebugLevel", ((int)this.Settings.BuildDebugLevel).ToString());
				writer.WriteAttributeString("compilePhase", this.Settings.CompilePhase   );
				writer.WriteAttributeString("platform", "dotNET" );
				
				// Modules
				writer.WriteStartElement("Modules");

				foreach (ModuleSetting ms in Settings.Modules.Values)
				{
					writer.WriteStartElement("Module");
					writer.WriteAttributeString("name", ms.Name );

					// Add all the properties
					foreach (String s in ms.Elements.AllKeys  )
					{
						writer.WriteAttributeString(s, ms.Elements[s]);
					}

					writer.WriteEndElement(); 
				}

				// Close the modules
				writer.WriteEndElement(); 

				// Paths
				writer.WriteStartElement("Paths");

				foreach (String s in Settings.Paths.AllKeys )
				{
					writer.WriteStartElement("Path");
					writer.WriteAttributeString("name", s);
					writer.WriteAttributeString("pathName", FormatPath(Settings.Paths[s])) ;
					writer.WriteEndElement(); 
				}

				// Close the Paths
				writer.WriteEndElement(); 

				// Close the settings
				writer.WriteEndElement(); 
				
				// Include the PlatformConfiguration file
				writer.WriteStartElement("Platforms"); 
				string filePlatformConfig = Path.Combine(Settings.Paths["Composestar"] , "PlatformConfigurations.xml");
				XmlTextReader reader = new XmlTextReader(filePlatformConfig) ;
				reader.MoveToContent(); 
				writer.WriteRaw(reader.ReadInnerXml());
				reader.Close();
				writer.WriteEndElement();
	 
				// Close the root
				writer.WriteEndElement(); 

				// Close the document.
				writer.WriteEndDocument();
			}
			catch (Exception ex)
			{
				throw new ApplicationException(String.Format("Could not write the '{0}' file to store the build configuration.", fileName), ex); 
			}
			finally
			{
				if (writer != null)
					// Close the file
					writer.Close();				
			}
					
		}

		public void SaveToXml()
		{
			SaveToXml(Path.Combine(_tempFolder, "BuildConfiguration.xml"));
		}

	#endregion

		#region Read dotNet platform settings

		public void ReadDotNetPlatform()
		{
			string filePlatformConfig = Path.Combine(Settings.Paths["Composestar"] , "PlatformConfigurations.xml");
			ReadDotNetPlatform(filePlatformConfig);
		}

		public void ReadDotNetPlatform(string fileName)
		{
			Platform p = new Platform();

			XmlTextReader reader = null;
			 
			try
			{
				reader = new XmlTextReader(fileName);
				while (reader.Read()) 
				{
					if (reader.NodeType == XmlNodeType.Element) 
					{
						if (reader.Name.Equals("Platform")) 
						{
							if (reader.GetAttribute("name").Equals("dotNET"))
							{

								p.ClassPath = reader.GetAttribute("classPath");
								p.MainClass = reader.GetAttribute("mainClass");
								p.Options = reader.GetAttribute("options"); 
							

								while (reader.Read()) 
								{
									if (reader.NodeType == XmlNodeType.Element) 
									{
										if (reader.Name.Equals("RequiredFile"))
										{
											p.RequiredFiles.Add(reader.GetAttribute("fileName") ); 
										}
									}
								}
							}
						}  
					}
				}
			}
			catch (Exception ex)
			{
				throw new ApplicationException(String.Format("Could not open the file '{0}' to read the platform settings.", fileName), ex) ;
			}
			finally
			{
				if (reader != null) reader.Close();
			}
			
			this.DotNetPlatform = p;
		}

		#endregion

		public void AddProject(Project p)
		{
			if (p == null)
				return;
			
			_projects.Add(p);
		}

		private Object getProperty(Properties properties, string propertyName)
		{
			if (properties.Item(propertyName) != null)
			{
				return properties.Item(propertyName).Value;
			}

			return null;
		}

		/// <summary>
		/// Clear the config 
		/// </summary>
		public void CleanConfig()
		{

			this._concernSources.Clear();
			this._executable = "";
			this._projects.Clear();
			this._runDebugLevel = DebugModes.Information;
			this._outputPath = "";
			this._customFilters.Clear();
			this._settings = new Settings();
 
		}
        
		/// <summary>
		/// Build the configuration by looking at all the project information.
		/// </summary>
		/// <param name="applicationObject"></param>
		/// <param name="scope"></param>
		/// <param name="action"></param>
		public void BuildConfig(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
		{
			if (applicationObject == null)
				return;
			
			_DTE mApplicationObject = applicationObject;
			ArrayList outputFileNames = new ArrayList();
			ArrayList startupObjects = new ArrayList();

			if (languages == null)
				languages = new SupportedLanguages();

			if (mUsedCompilers == null)
				mUsedCompilers = new ArrayList(languages.GetCount());

			string solutionfile = applicationObject.Solution.Properties.Item("Path").Value.ToString();
			string tempfolder = solutionfile.Substring(0, solutionfile.LastIndexOf("\\")+1);
			_tempFolder = tempfolder;

			// Find the startupproject and read the executable and outputpath defined in this project.
			EnvDTE.Project startProject = GetStartupProject(applicationObject);
			if (startProject != null)
			{
				this.OutputPath =Path.Combine(Path.GetDirectoryName(startProject.FileName), "bin");	
				this.Executable = getProperty(startProject.Properties, "OutputFileName").ToString();
				string appStart = null;
				appStart = (string)getProperty(startProject.Properties, "StartupObject"); 
				if (appStart != null & appStart.Length  > 0)
				{
					this.ApplicationStart = appStart;
				}
			}
			else
			{
				this.OutputPath = tempfolder;
				this.Executable = "program.exe";
			}
		
			// FILTH configuration
			ModuleSetting filthModule = new ModuleSetting();
			filthModule.Name = "FILTH";
			filthModule.Elements.Add("input", this.mFilthFilterOrderSpecification)  ;
			filthModule.Elements.Add("output_pattern", ".//analyses//FILTH_") ;
			Settings.SetModule(filthModule);
			
			// Include Composestar.ini and overwrite values with Project.ini, if it exists
			ReadIniFile(Settings.ComposestarIni);

			foreach (EnvDTE.Project project in mApplicationObject.Solution.Projects) 
			{
				
				if( project != null  && project.Properties != null)
				{
					BuildConfiguration.Project p = new Project();

					p.Name = project.Name;
					p.BasePath = Path.GetDirectoryName(project.FileName);

					if( getProperty(project.Properties, "OutputType") != null )
					{
						if (getProperty(project.Properties, "OutputType").ToString().Equals("1")) 
						{
							// Add the executable to the list, all dll's are automatically included with *.dll
							outputFileNames.Add(getProperty(project.Properties, "LocalPath").ToString() + getProperty(project.Properties, "OutputFileName").ToString());
						}
					}
					outputFileNames.Add((getProperty(project.Properties, "LocalPath").ToString() + GetProjectOutputPath(project) + "*.dll"));
					ProcessProjectFiles(p, getProperty(project.Properties, "LocalPath").ToString(), project.ProjectItems);
			
					if (!getProperty(project.Properties, "StartupObject").ToString().Equals(""))
					{
						startupObjects.Add(getProperty(project.Properties, "StartupObject").ToString());
					}

					p.OutputPath = tempfolder + "bin\\";
					p.BuildPath = tempfolder + "obj\\";
					StringCollection dependencies = ComposestarVSAddin.DependencyHarvester.Collect(project, false);
					p.Dependencies.AddRange(dependencies) ;

					Projects.Add(p); 

					// Process project.ini
					ReadIniFile(Path.Combine(_tempFolder, "project.ini"));

				}
			}
			
			// Paths
			Settings.Paths.Add("Dummy", "dummies\\") ;		
		}

		private void ReadIniFile(string fileName)
		{
			if (File.Exists(fileName) == false )
				return;

			Org.Mentalis.Files.IniReader ini = new Org.Mentalis.Files.IniReader(fileName); 

			String composeStarPath = ini.ReadString("Global Composestar configuration", "ComposestarPath", "") ;
			if (composeStarPath.Length > 0)
				Settings.Paths.Add("Composestar",composeStarPath ) ;

			String NETPath = ini.ReadString("Global Composestar configuration", ".NETPath", "") ;
			if (NETPath.Length > 0)
				Settings.Paths.Add("NET",NETPath ) ;

			String NETSDKPath = ini.ReadString("Global Composestar configuration", ".NETSDKPath", "") ;
			if (NETSDKPath.Length > 0)
				Settings.Paths.Add("NETSDK",NETSDKPath ) ;

			String EmbeddedSourcesFolder = ini.ReadString("Global Composestar configuration", "EmbeddedSourcesFolder", "") ;
			if (NETSDKPath.Length > 0)
				Settings.Paths.Add("EmbeddedSources",EmbeddedSourcesFolder ) ;

			String SECRETMode = ini.ReadString("Global Composestar configuration", "SECRETMode", "") ;
			if (SECRETMode.Length > 0)
			{
				ModuleSetting SecretModule = new ModuleSetting ();
				SecretModule.Name = "SECRET";
				SecretModule.Elements.Add("mode", SECRETMode)  ;
				Settings.SetModule(SecretModule); 
			}
				
			String RunDebugLevel = ini.ReadString("Common", "RunDebugLevel", "") ;
			if (RunDebugLevel.Length > 0)
				this.RunDebugLevel = (DebugModes)Convert.ToInt32(RunDebugLevel);
				
			String BuildDebugLevel = ini.ReadString("Common", "BuildDebugLevel", "") ;
			if (BuildDebugLevel.Length > 0)
				Settings.BuildDebugLevel  = (DebugModes)Convert.ToInt32(BuildDebugLevel);

			String INCRE_ENABLED = ini.ReadString("Common", "INCRE_ENABLED", "") ;
			if (INCRE_ENABLED.Length > 0)
			{
				ModuleSetting IncreModule = new ModuleSetting ();
				IncreModule.Name = "INCRE";
				IncreModule.Elements.Add("enabled", INCRE_ENABLED)  ;
				Settings.SetModule(IncreModule);
			}

			String VerifyAssemblies = ini.ReadString("Common", "VerifyAssemblies", "") ;
			if (VerifyAssemblies.Length > 0)
			{
				ModuleSetting ilicitModule = new ModuleSetting ();
				ilicitModule.Name = "ILICIT";
				ilicitModule.Elements.Add("verifyAssemblies", VerifyAssemblies)  ;
				Settings.SetModule(ilicitModule);
			}
				
			// Debugger
			String DebuggerType = ini.ReadString("Global Composestar configuration", "DebuggerType", "") ;
			if (DebuggerType.Length > 0)
			{
				ModuleSetting debuggerModule = new ModuleSetting ();
				debuggerModule.Name = "CODER";
				debuggerModule.Elements.Add("DebuggerType", DebuggerType)  ;
				Settings.SetModule(debuggerModule);
			}

			// Custom Filters
			// Custom filters are stored in the CustomFilters section
			String[] customFilters ;
			if (ini.ReadSectionValues("CustomFilters", out customFilters))
			{			
				foreach (String cusFil in customFilters)
				{
					String[] parsed = cusFil.Split('=');
					this._customFilters.Add(new CustomFilter(parsed[0], parsed[1]) );
				}
			}
		}

		private EnvDTE.Project GetStartupProject(_DTE applicationObject)
		{
			
			System.Array projectNames = (System.Array) applicationObject.Solution.SolutionBuild.StartupProjects;
			if ((projectNames != null) && (projectNames.Length > 0))
			{
				string curPrjName = (string) projectNames.GetValue(0);
   
				foreach(EnvDTE.Project project in applicationObject.Solution.Projects ) 
				{
					if (project.UniqueName == curPrjName)
						return project;
				}
			
			}

			return null;
		}

		private void ProcessProjectFiles(BuildConfiguration.Project project, string path, ProjectItems projectitems) 
		{
			foreach (ProjectItem projectitem in projectitems)
			{
				if (projectitem.ProjectItems.Count > 0) 
				{
					ProcessProjectFiles(project, path + projectitem.Name + "\\", projectitem.ProjectItems);
				}
				else 
				{
					ComposestarVSAddin.Language l = languages.GetLanguage(projectitem.Name);


					// Set the language of the project 
					if (project.Language.Length == 0 && l != null) 
					{
						project.Language = l.Name;
					}

					// Add the file if it is the same language 
					string sourcename = "";
					if (l != null) 
					{

						if (l.Name.Equals("CPS") )
						{
							this.ConcernSources.Add(  String.Concat(path, projectitem.Name));
							continue;
						}

						sourcename = l.SourceTag;

						if ( l.isCompiler() && !this.mUsedCompilers.Contains(l.Name) ) 
						{
							this.mUsedCompilers.Add(l.Name);
						}

						if (!sourcename.Equals(""))
						{
							project.Sources.Add(String.Concat(path, projectitem.Name));
						}
					}
					
					// Check for FILTH filter order specification file
					if (projectitem.Name.Equals("filterorderspecification.xml"))
					{
						this.mFilthFilterOrderSpecification = path + "filterorderspecification.xml";
					}
				}

			}
		}

		private string GetProjectOutputPath(EnvDTE.Project project) 
		{
			return getProperty(project.ConfigurationManager.ActiveConfiguration.Properties, "OutputPath").ToString();
		}
		#endregion

	}
}
