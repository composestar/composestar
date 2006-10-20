using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.IO;
using System.Diagnostics; 

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILAnalyzer;
using Composestar.StarLight.LanguageModel;  
using Composestar.StarLight.Configuration; 

using Composestar.Repository;
using Composestar.StarLight.CoreServices.Exceptions;

namespace Composestar.StarLight.MSBuild.Tasks
{

    /// <summary>
    /// MSBuild tasks to start the analyzer.
    /// </summary>
    public class IlAnalyzerTask : Task
    {

        #region Properties for MSBuild

        private ITaskItem[] _assemblyFiles;
        private ITaskItem[] _referencedAssemblies;
        private ITaskItem[] _referencedTypes;

        /// <summary>
        /// Gets or sets the assembly files to analyze.
        /// </summary>
        /// <value>The assembly files.</value>
        [Required()]
        public ITaskItem[] AssemblyFiles
        {
            get { return _assemblyFiles; }
            set { _assemblyFiles = value; }
        }

        private string _repositoryFilename;

        /// <summary>
        /// Gets or sets the repository filename.
        /// </summary>
        /// <value>The repository filename.</value>
        [Required()]
        public string RepositoryFilename
        {
            get { return _repositoryFilename; }
            set { _repositoryFilename = value; }
        }

        [Required()]
        public ITaskItem[] ReferencedAssemblies
        {
            get { return _referencedAssemblies; }
            set { _referencedAssemblies = value; }
        }

        [Required()]
        public ITaskItem[] ReferencedTypes
        {
            get { return _referencedTypes; }
            set { _referencedTypes = value; }
        }

        private string _binFolder;

        /// <summary>
        /// Gets or sets the bin folder.
        /// </summary>
        /// <value>The bin folder.</value>
        public string BinFolder
        {
            get { return _binFolder; }
            set { _binFolder = value; }
        }

        private String _intermediateOutputPath;

        /// <summary>
        /// Gets or sets the intermediate output path.
        /// </summary>
        /// <value>The intermediate output path.</value>
        [Required()]
        public String IntermediateOutputPath
        {
            get { return _intermediateOutputPath; }
            set { _intermediateOutputPath = value; }
        }

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:AnalyzerTask"/> class.
        /// </summary>
        public IlAnalyzerTask()
            : base(Properties.Resources.ResourceManager)
        {

        }

        #endregion

        /// <summary>
        /// When overridden in a derived class, executes the task.
        /// </summary>
        /// <returns>
        /// true if the task successfully executed; otherwise, false.
        /// </returns>
        public override bool Execute()
        {                   
            Log.LogMessageFromResources("AnalyzerStartText");

            IILAnalyzer analyzer = null;
            CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration(RepositoryFilename);
            IEntitiesAccessor entitiesAccessor = new EntitiesAccessor();
            
            // Set configuration settings
            configuration.BinFolder = BinFolder;

            // Create a list to store the retrieved assemblies in            
            List<AssemblyElement> assemblies = new List<AssemblyElement>();

            // Create a list to store the FilterTypes in
            List<FilterTypeElement> filterTypes = new List<FilterTypeElement>();

            // Create a list to store the FilterActions in
            List<FilterActionElement> filterActions = new List<FilterActionElement>();
                   
            // Create the analyzer using the object builder
            analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(entitiesAccessor, configuration));
            
            // Create a list of all the referenced assemblies (complete list is supplied by the msbuild file)
            List<String> assemblyFileList = new List<string>();
            foreach (ITaskItem item in AssemblyFiles)
            {
                // We are only interested in assembly files.
                string extension = Path.GetExtension(item.ToString()).ToLower();
                if (extension.Equals(".dll") || extension.Equals(".exe"))
                {
                    assemblyFileList.Add(item.ToString());
                } // if
            }

            // Create a list of all the referenced assemblies, which are not copied local for complete analysis
            Dictionary<String, String> refAssemblies = new Dictionary<string, string>();
            
            foreach (ITaskItem item in ReferencedAssemblies)
            {
                if (item.GetMetadata("CopyLocal") == "false")
                {
                    refAssemblies.Add(item.GetMetadata("FusionName"), item.GetMetadata("Identity"));
                }
            }

            // Get the configuration
            ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFilename);               

            // Get the assemblies in the config file
            List<AssemblyConfig> assembliesInConfig = configContainer.Assemblies;

            List<AssemblyConfig> assembliesToStore = new List<AssemblyConfig>();  

            // Add all the unresolved types (used in the concern files) to the analyser
            Log.LogMessageFromResources(MessageImportance.Low, "NumberOfReferencesToResolve", ReferencedTypes.Length);
            foreach (ITaskItem item in ReferencedTypes)
            {
                analyzer.UnresolvedTypes.Add(item.ToString());
            }

            // Analyze all assemblies
            foreach (String item in assemblyFileList)
            {
                try
                {
                    // See if we already have this assembly in the list
                    AssemblyConfig assConfig =null;

                    assConfig = assembliesInConfig.Find(delegate(AssemblyConfig ac)
                    {
                        return ac.Filename.Equals(item); 
                    });

                    if (assConfig != null)
                    {
                        // Already in the config. Check the last modification date.
                        if (assConfig.Timestamp == File.GetLastWriteTime(item).Ticks)
                        {
                            // Assembly has not been modified, skipping analysis
                            Log.LogMessageFromResources("AssemblyNotModified", assConfig.Name);

                            assembliesToStore.Add(assConfig); 
                            continue;
                        } // if
                    } // if

                    // Either we could not find the assembly in the config or it was changed.
                   
                    AssemblyElement assembly = null;
                    Stopwatch sw = new Stopwatch();
                    
                    Log.LogMessageFromResources("AnalyzingFile", item);
                    
                    sw.Start();
               
                    assembly = analyzer.ExtractAllTypes(item);

                    if (assembly != null)
                    {
                        // Create a new AssemblyConfig object
                        assConfig = new AssemblyConfig();

                        assConfig.Filename = item;
                        assConfig.Name = assembly.Name;
                        assConfig.Timestamp = File.GetLastWriteTime(item).Ticks;
                        assConfig.Assembly = assembly;

                        // Generate a unique filename
                        assConfig.GenerateSerializedFilename(IntermediateOutputPath);
  
                        assembliesToStore.Add(assConfig);  
                        assemblies.Add(assembly);
                    } // if
                                     
                    sw.Stop();

                    Log.LogMessageFromResources("AssemblyAnalyzed", assembly.Types.Count, analyzer.UnresolvedTypes.Count, sw.Elapsed.TotalSeconds);

                    sw.Reset();

                    sw.Start();

                    // Add FilterTypes
                    filterTypes.AddRange(analyzer.FilterTypes);

                    // Add FilterActions
                    filterActions.AddRange(analyzer.FilterActions);

                    sw.Stop();

                    if (analyzer.FilterTypes.Count > 0 && analyzer.FilterActions.Count > 0)
                    {
                        Log.LogMessageFromResources("FiltersAnalyzed", analyzer.FilterTypes.Count, analyzer.FilterActions.Count, sw.Elapsed.TotalSeconds);
                    }
                }
                catch (ILAnalyzerException ex)
                {
                    Log.LogErrorFromException(ex, true);
                }
                catch (ArgumentException ex)
                {
                    Log.LogErrorFromException(ex, true);
                }
                catch (FileNotFoundException ex)
                {
                    Log.LogErrorFromException(ex, true);
                }
                catch (BadImageFormatException ex)
                {
                    Log.LogErrorFromException(ex, false);
                }

            }

            // Analyze the non-local copied assemblies for missing types
            if (analyzer.UnresolvedTypes.Count > 0)
            {
                // Step 1: Check if local database already contains the type
                Stopwatch sw = new Stopwatch();
                Log.LogMessageFromResources("SearchingInDatabase", analyzer.UnresolvedTypes.Count);

                sw.Start();

                int numberOfResolvedTypes = 0;
                List<String> unresolvedTypes = new List<string>(analyzer.UnresolvedTypes);
                foreach (String type in unresolvedTypes)
                {
                    String typeName = type;
                    if (type.Contains(", ")) typeName = type.Substring(0, type.IndexOf(", "));

                    // TODO dit wordt lastig. Misschien iets te doen met alle assembly elements laden?

                    //TypeElement te = langModelAccessor.GetTypeElement(typeName);
                    //if (te != null)
                    //{
                    //    analyzer.UnresolvedTypes.Remove(type);
                    //    numberOfResolvedTypes++;
                    //}
                }

                sw.Stop();

                Log.LogMessageFromResources("FoundInDatabase", numberOfResolvedTypes, sw.Elapsed.TotalSeconds);
            }
            if (analyzer.UnresolvedTypes.Count > 0)
            {
                // Step 2: Analyze all referenced assemblies in the hope we find the unresolved types
                Stopwatch sw = new Stopwatch();
                Log.LogMessageFromResources("AnalyzingUnresolved", analyzer.UnresolvedTypes.Count);

                try
                {
                    sw.Start();

                    assemblies.AddRange(analyzer.ProcessUnresolvedTypes(refAssemblies));

                    sw.Stop();

                    Log.LogMessageFromResources("AnalyzingUnresolvedCompleted", analyzer.UnresolvedTypes.Count, sw.Elapsed.TotalSeconds);
                }
                catch (ILAnalyzerException ex)
                {
                    Log.LogError(ex.Message);
                }
                finally {
                    if (sw.IsRunning) 
                        sw.Stop();
                }
            }
            if (analyzer.UnresolvedTypes.Count > 0)
            {
                // Step 3: Unable to resolve some types, throw an error
                foreach (String type in analyzer.UnresolvedTypes)
                {
                    Log.LogErrorFromResources("UnableToResolve", type);
                }
            }

            // Storing types 
            if (assemblies.Count > 0 && !Log.HasLoggedErrors)
            {
                Stopwatch sw = new Stopwatch();
                Log.LogMessageFromResources("StoreInDatabase", assemblies.Count);

                sw.Start();
                              
                foreach (AssemblyConfig assembly in assembliesToStore)
                {
                    // save each assembly if needed (there must be an assemblyElement)
                    if (assembly.Assembly != null)
                        entitiesAccessor.SaveAssemblyElement(assembly.SerializedFilename, assembly.Assembly);  
                    
                }

                // Set the assemblies to store
                configContainer.Assemblies = assembliesToStore; 
                
                // Add the filtertypes and actions
                configContainer.FilterTypes = filterTypes;
                configContainer.FilterActions = filterActions;

                // Save the config
                entitiesAccessor.SaveConfiguration(RepositoryFilename, configContainer); 

                sw.Stop();

                Log.LogMessageFromResources("StoreInDatabaseCompleted", assemblies.Count, analyzer.ResolvedTypes.Count, sw.Elapsed.TotalSeconds);
            }
                      
            // Close the analyzer
            analyzer.Close();
      
            return !Log.HasLoggedErrors;
        }

        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <param name="languageModel">The language model.</param>
        /// <param name="configuration">The configuration.</param>
        /// <returns></returns>
        internal IServiceProvider CreateContainer(IEntitiesAccessor languageModel, CecilAnalyzerConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(IEntitiesAccessor), languageModel);
            serviceContainer.AddService(typeof(CecilAnalyzerConfiguration), configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new ILAnalyzerBuilderConfigurator());

            return serviceContainer;
        }

    }

}
