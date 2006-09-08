using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;

using Microsoft.Build.BuildEngine;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.MSBuild.Tasks.BuildConfiguration;

namespace Composestar.StarLight.MSBuild.Tasks
{
    public class XMLGeneratorTask : Task
    {

        #region Private members

        private string Executable = "";
        private string ApplicationStart = "";
        private string OutputPath = "";
        private string BuildFolder = "";
        private List<Composestar.StarLight.MSBuild.Tasks.BuildConfiguration.Project> _projects = new List<Composestar.StarLight.MSBuild.Tasks.BuildConfiguration.Project>();
    
        #endregion

        #region Constructors
        /// <summary>
        /// Constructor. This is the constructor that will be used
        /// when the task run.
        /// </summary>
        public XMLGeneratorTask()
        {
        }

        #endregion

        #region Public Properties and related Fields
        private string[] sourceFiles;
        /// <summary>
        /// List of source files that should be compiled into the assembly
        /// </summary>
        [Required()]
        public string[] SourceFiles
        {
            get { return sourceFiles; }
            set { sourceFiles = value; }
        }

        private string _projectFile = "";

        [Required()]
        public string ProjectFile
        {
            get { return _projectFile; }
            set { _projectFile = value; }
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
                    resources = value;
                }
                else
                {
                    resources = new ITaskItem[0];
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


            //Load the project.
            Microsoft.Build.BuildEngine.Engine engine = Engine.GlobalEngine;

            if (engine == null)
            {
                Log.LogError("Could not find Engine. Aborting.");
                return false;
            }
            Log.LogMessage("Project file is {0}", ProjectFile);

            Microsoft.Build.BuildEngine.Project project = engine.GetLoadedProject(ProjectFile);
            if (project == null)
            {
                Log.LogError("Could not find project. Aborting.");
                return false;
            }

            // Get the references and print them.
            BuildItemGroup references = project.GetEvaluatedItemsByName("Reference");

            if (references == null)
            {
                Log.LogError("Could not find references. Aborting.");
                return false;
            }

            foreach (BuildItem bi in references)
            {
               // Log.LogMessage(MessageImportance.Normal, "References: " + bi.Include);
            }
         
            if (Utilities.searchComposeStarIniFile(Log) == false)
            {
                return false;
            }

            Utilities.ReadIniFile();
            Utilities.ReadDotNetPlatform();

            String ObjFolder = String.Concat(Path.GetDirectoryName(ProjectFile), "\\obj\\");

            Settings.Paths.Add("Base", ObjFolder);

            try
            {             
                SaveToXml(Path.Combine(ObjFolder, "BuildConfiguration.Xml"));
                return true;
            }
            catch (Exception ex)
            {
                Log.LogErrorFromException(ex, true);     
                return false;
            }
                        
        }


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
                foreach (Composestar.StarLight.MSBuild.Tasks.BuildConfiguration.Project p in this._projects)
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
