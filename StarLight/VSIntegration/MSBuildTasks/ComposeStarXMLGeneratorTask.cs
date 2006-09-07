using System;
using System.IO;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;
using ComposeStar.MSBuild.Tasks.Compiler;
using ComposeStar.MSBuild.Tasks.BuildConfiguration;
using System.Xml;
using System.Text;
using System.Collections;

namespace ComposeStar.MSBuild.Tasks
{
    public class ComposeStarXMLGeneratorTask : Task
    {
        #region Private members

        private ICompiler compiler = null;
        private string Executable = "";
        private string ApplicationStart = "";
        private string OutputPath = "";
        private string BuildFolder = "";
        private ArrayList _projects = new ArrayList();
        private ArrayList ConcernSources = new ArrayList();

        #endregion


        #region Constructors
        /// <summary>
		/// Constructor. This is the constructor that will be used
		/// when the task run.
		/// </summary>
		public ComposeStarXMLGeneratorTask()
		{
		}

		/// <summary>
		/// Constructor. The goal of this constructor is to make
		/// it easy to test the task.
		/// </summary>
        public ComposeStarXMLGeneratorTask(ICompiler compilerToUse)
		{
			compiler = compilerToUse;
		}
		#endregion

		#region Public Properties and related Fields
		private string[] sourceFiles;
		/// <summary>
		/// List of Python source files that should be compiled into the assembly
		/// </summary>
		[Required()]
		public string[] SourceFiles
		{
			get { return sourceFiles; }
			set { sourceFiles = value; }
		}

		private string outputAssembly;
		/// <summary>
		/// Output Assembly (including extension)
		/// </summary>
		[Required()]
		public string OutputAssembly
		{
			get { return outputAssembly; }
			set { outputAssembly = value; }
		}

		private ITaskItem[] referencedAssemblies = new ITaskItem[0];
		/// <summary>
		/// List of dependent assemblies
		/// </summary>
		public ITaskItem[] ReferencedAssemblies
		{
			get { return referencedAssemblies; }
			set
			{
				if (value != null)
				{
					referencedAssemblies = value;
				}
				else
				{
					referencedAssemblies = new ITaskItem[0];
				}

			}
		}

        private ITaskItem[] resources = new ITaskItem[0];
        /// <summary>
        /// List of linked resources
        /// </summary>
        [Required()]
        public ITaskItem[] Resources
        {
            get { return Resources; }
            set
            {
                if (value != null)
                {
                    Resources = value;
                }
                else
                {
                    Resources = new ITaskItem[0];
                }

            }
        }

		private ITaskItem[] resourceFiles = new ITaskItem[0];
		/// <summary>
		/// List of resource files
		/// </summary>
		public ITaskItem[] ResourceFiles
		{
			get { return resourceFiles; }
			set
			{
				if (value != null)
				{
					resourceFiles = value;
				}
				else
				{
					resourceFiles = new ITaskItem[0];
				}

			}
		}

		private string mainFile;
		/// <summary>
		/// For applications, which file is the entry point
		/// </summary>
		[Required()]
		public string MainFile
		{
			get { return mainFile; }
			set { mainFile = value; }
		}

		private string targetKind;
		/// <summary>
		/// Target type (exe, winexe, library)
		/// These will be mapped to System.Reflection.Emit.PEFileKinds
		/// </summary>
		public string TargetKind
		{
			get { return targetKind; }
			set { targetKind = value.ToLower(CultureInfo.InvariantCulture); }
		}
		private bool includeDebugInformation = true;
		/// <summary>
		/// Generate debug information
		/// </summary>
		public bool IncludeDebugInformation
		{
			get { return includeDebugInformation; }
			set { includeDebugInformation = value; }
		}
		private string projectPath = null;
		/// <summary>
		/// This should be set to $(MSBuildProjectDirectory)
		/// </summary>
		public string ProjectPath
		{
			get { return projectPath; }
			set { projectPath = value; }
		}

        private Platform _platform = null;
        public Platform DotNETPlatform
        {
            get { return _platform; }
            set { _platform = value; }
        }

		#endregion

		/// <summary>
		/// Main entry point for the task
		/// </summary>
		/// <returns></returns>
		public override bool Execute()
		{
			Log.LogMessage(MessageImportance.Normal, "Generating Compose* build configuration file...");

			// Create the compiler if it does not already exist
			CompilerErrorSink errorSink = new CompilerErrorSink(this.Log);
			errorSink.ProjectDirectory = ProjectPath;
			if (compiler == null)
			{
				compiler = new Compiler.Compiler(this.SourceFiles, this.OutputAssembly);
			}

			if (!InitializeCompiler())
				return false;

            this.searchComposeStarIniFile();
            this.ReadIniFile();
            this.ReadDotNetPlatform();

            this.SaveToXml();

			// Call the compiler and report errors and warnings
			//compiler.Compile();

			return errorSink.BuildSucceeded;
		}

		/// <summary>
		/// Initialize compiler options based on task parameters
		/// </summary>
		/// <returns>false if failed</returns>
		private bool InitializeCompiler()
		{
			return true;
        }



        #region Process Compose* ini file

        private void searchComposeStarIniFile()
        {
            string path = "";
            string fullname;
            string iniFileName = "Composestar.ini";
            try
            {
                path = Microsoft.Win32.Registry.CurrentUser.OpenSubKey("Software").OpenSubKey("Microsoft").OpenSubKey("VisualStudio").OpenSubKey("7.1").OpenSubKey("Addins").OpenSubKey("ComposestarVSAddin.Connect").GetValue("ComposestarPath").ToString();
                fullname = System.IO.Path.Combine(path, iniFileName);

                if (!System.IO.File.Exists(fullname))
                {
                    Log.LogError("Could not locate ComposeStar.ini file, please reinstall Compose*!");
                }
                else
                {
                    Settings.ComposestarIni = fullname;
                }
            }
            catch (Exception)
            {
                Log.LogError("Could not locate ComposeStar.ini file, please reinstall Compose*!");
            }
        }

        private void ReadIniFile()
        {
            if (File.Exists(Settings.ComposestarIni) == false)
                return;

            IniReader ini = new IniReader(Settings.ComposestarIni); 

            String composeStarPath = ini.ReadString("Global Composestar configuration", "ComposestarPath", "");
            if (composeStarPath.Length > 0)
            {
                Settings.Paths.Set("Composestar", composeStarPath);
                //System.Windows.Forms.MessageBox.Show("Found composestar installation: " + composeStarPath);
            }

            String NETPath = ini.ReadString("Global Composestar configuration", ".NETPath", "");
            if (NETPath.Length > 0)
                Settings.Paths.Add("NET", NETPath);

            String NETSDKPath = ini.ReadString("Global Composestar configuration", ".NETSDKPath", "");
            if (NETSDKPath.Length > 0)
                Settings.Paths.Add("NETSDK", NETSDKPath);

            String EmbeddedSourcesFolder = ini.ReadString("Global Composestar configuration", "EmbeddedSourcesFolder", "");
            if (NETSDKPath.Length > 0)
                Settings.Paths.Add("EmbeddedSources", EmbeddedSourcesFolder);

            String SECRETMode = ini.ReadString("Global Composestar configuration", "SECRETMode", "");
            if (SECRETMode.Length > 0)
            {
                ModuleSetting SecretModule = new ModuleSetting();
                SecretModule.Name = "SECRET";
                SecretModule.Elements.Add("mode", SECRETMode);
                Settings.SetModule(SecretModule);
            }

            String RunDebugLevel = ini.ReadString("Common", "RunDebugLevel", "");
            if (RunDebugLevel.Length > 0)
                Settings.RunDebugLevel = (DebugModes)Convert.ToInt32(RunDebugLevel);

            String BuildDebugLevel = ini.ReadString("Common", "BuildDebugLevel", "");
            if (BuildDebugLevel.Length > 0)
                Settings.BuildDebugLevel = (DebugModes)Convert.ToInt32(BuildDebugLevel);

            String INCRE_ENABLED = ini.ReadString("Common", "INCRE_ENABLED", "");
            if (INCRE_ENABLED.Length > 0)
            {
                ModuleSetting IncreModule = new ModuleSetting();
                IncreModule.Name = "INCRE";
                IncreModule.Elements.Add("enabled", INCRE_ENABLED);
                Settings.SetModule(IncreModule);
            }

            String FilterModuleOrder = ini.ReadString("Global Composestar configuration", "FILTH_INPUT", "");
            if (FilterModuleOrder.Length > 0)
            {
                ModuleSetting filthModule = new ModuleSetting();
                filthModule.Name = "FILTH";
                filthModule.Elements.Add("input", FilterModuleOrder);
                Settings.SetModule(filthModule);
            }

            String VerifyAssemblies = ini.ReadString("Common", "VerifyAssemblies", "");
            if (VerifyAssemblies.Length > 0)
            {
                ModuleSetting ilicitModule = new ModuleSetting();
                ilicitModule.Name = "ILICIT";
                ilicitModule.Elements.Add("verifyAssemblies", VerifyAssemblies);
                Settings.SetModule(ilicitModule);
            }

            // Debugger
            String DebuggerType = ini.ReadString("Global Composestar configuration", "DebuggerType", "");
            if (DebuggerType.Length > 0)
            {
                ModuleSetting debuggerModule = new ModuleSetting();
                debuggerModule.Name = "CODER";
                debuggerModule.Elements.Add("DebuggerType", DebuggerType);
                Settings.SetModule(debuggerModule);
            }

            // Custom Filters
            // Custom filters are stored in the CustomFilters section
            String[] customFilters;
            if (ini.ReadSectionValues("CustomFilters", out customFilters))
            {
                foreach (String cusFil in customFilters)
                {
                    String[] parsed = cusFil.Split('=');
                    Settings.CustomFilters.Add(new CustomFilter(parsed[0], parsed[1]));
                }
            }
        }

        #endregion

        #region Read dotNet platform settings

        public void ReadDotNetPlatform()
        {
            string filePlatformConfig = Path.Combine(Settings.Paths["Composestar"], "PlatformConfigurations.xml");
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
                                            p.RequiredFiles.Add(reader.GetAttribute("fileName"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                throw new ApplicationException(String.Format("Could not open the file '{0}' to read the platform settings.", fileName), ex);
            }
            finally
            {
                if (reader != null) reader.Close();
            }

            this.DotNETPlatform = p;

            //BuildConfigurationM
        }

        #endregion

        #region Write BuildConfiguration
        public void SaveToXml(string fileName)
        {

            // Start a new textwriter
            XmlTextWriter writer = null;

            try
            {
                writer = new XmlTextWriter(fileName, Encoding.ASCII);
                writer.Formatting = Formatting.Indented;

                // Write the XML declaration. 
                writer.WriteStartDocument();

                // Add comments
                writer.WriteComment(String.Format("This BuildConfiguration file was automaticly generated on {0} by the Composestar VS Addin.", DateTime.Now.ToString()));

                // Start the root
                writer.WriteStartElement("BuildConfiguration");
                writer.WriteAttributeString("version", "1.00");

                // First the projects
                writer.WriteStartElement("Projects");
                writer.WriteAttributeString("executable", this.Executable);
                writer.WriteAttributeString("startupProject", "");
                writer.WriteAttributeString("applicationStart", this.ApplicationStart);
                writer.WriteAttributeString("runDebugLevel", ((int)Settings.RunDebugLevel).ToString());
                writer.WriteAttributeString("outputPath", FormatPath(this.OutputPath));


                // All the projects
                foreach (Project p in this._projects)
                {
                    writer.WriteStartElement("Project");
                    writer.WriteAttributeString("name", p.Name);
                    writer.WriteAttributeString("language", p.Language);
                    writer.WriteAttributeString("buildPath", FormatPath(p.BuildPath));
                    writer.WriteAttributeString("basePath", FormatPath(p.BasePath));
                    writer.WriteAttributeString("outputPath", FormatPath(p.OutputPath));
                    writer.WriteAttributeString("assemblyName", FormatPath(p.AssemblyName));

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
                    System.Windows.Forms.MessageBox.Show("ReferencedAssemblies: " + this.referencedAssemblies.ToString());
                    System.Windows.Forms.MessageBox.Show("Resources: " + this.resources.ToString());
                    foreach (ITaskItem s in ReferencedAssemblies)
                    {
                        System.Windows.Forms.MessageBox.Show("Task spec: " + s.ItemSpec);
                        writer.WriteStartElement("Dependency");
                        writer.WriteAttributeString("fileName", s.GetMetadata("Reference"));
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
                foreach (String s in SourceFiles)
                {
                    string tmp = this.ProjectPath + "/" + s;
                    writer.WriteStartElement("ConcernSource");
                    writer.WriteAttributeString("fileName", FormatPath(tmp));
                    writer.WriteEndElement();
                }

                // /ConcernSources
                writer.WriteEndElement();

                // CustomFilters
                writer.WriteStartElement("CustomFilters");
                foreach (CustomFilter cf in Settings.CustomFilters)
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
                writer.WriteAttributeString("composestarIni", FormatPath(Settings.ComposestarIni));
                writer.WriteAttributeString("buildDebugLevel", ((int)Settings.BuildDebugLevel).ToString());
                writer.WriteAttributeString("platform", "dotNET");

                // Modules
                writer.WriteStartElement("Modules");

                foreach (ModuleSetting ms in Settings.Modules.Values)
                {
                    writer.WriteStartElement("Module");
                    writer.WriteAttributeString("name", ms.Name);

                    // Add all the properties
                    foreach (String s in ms.Elements.AllKeys)
                    {
                        writer.WriteAttributeString(s, ms.Elements[s]);
                    }

                    writer.WriteEndElement();
                }

                // Close the modules
                writer.WriteEndElement();

                // Paths
                writer.WriteStartElement("Paths");

                foreach (String s in Settings.Paths.AllKeys)
                {
                    writer.WriteStartElement("Path");
                    writer.WriteAttributeString("name", s);
                    writer.WriteAttributeString("pathName", FormatPath(Settings.Paths[s]));
                    writer.WriteEndElement();
                }

                // Close the Paths
                writer.WriteEndElement();

                // Close the settings
                writer.WriteEndElement();

                // Include the PlatformConfiguration file
                writer.WriteStartElement("Platforms");
                string filePlatformConfig = Path.Combine(Settings.Paths["Composestar"], "PlatformConfigurations.xml");
                XmlTextReader reader = new XmlTextReader(filePlatformConfig);
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
            SaveToXml(Path.Combine(ProjectPath, "BuildConfiguration.xml"));
        }

        private string FormatPath(string pathName)
        {
            // check if it is a path or a file. With a file, do not add last slash.
            if (!Path.HasExtension(pathName))
            {
                if (!pathName.EndsWith("\\") & !pathName.EndsWith("/"))
                    pathName = string.Concat(pathName, "\\");
            }
            return pathName.Replace("\\", "/");
        }

        #endregion
    }
}
