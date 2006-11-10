#region Using Directives

using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.IO;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;   

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILAnalyzer;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration;

using Composestar.Repository;
using Composestar.StarLight.CoreServices.Exceptions;

#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{

    /// <summary>
    /// MSBuild tasks to start the analyzer.
    /// </summary>
    [LoadInSeparateAppDomain()]
    public class ILAnalyzerTask : AppDomainIsolatedTask
    {

        // The analyzer removes the Composestar.StarLight dlls from the list
        private const string ComposeStarDlls = "Composestar.StarLight";
        // Except the dlls listed below
        private const string ComposeStarFilterDll = "Composestar.StarLight.Filters;Composestar.StarLight.CustomFilters";

        private ConfigurationContainer configContainer;
        private List<FilterActionElement> filterActions;
        private List<FilterTypeElement> filterTypes;
        private List<AssemblyConfig> assembliesInConfig;
        private List<AssemblyConfig> assembliesToStore;

        #region Properties for MSBuild

        private ITaskItem[] _assemblyFiles;
        private ITaskItem[] _referencedAssemblies;
        private ITaskItem[] _referencedTypes;
        private bool _doMethodCallAnalysis = true;

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

        public bool DoMethodCallAnalysis
        {
            get { return _doMethodCallAnalysis; }
            set { _doMethodCallAnalysis = value; }
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


        private bool _assembliesDirty;

        /// <summary>
        /// Gets or sets a value indicating whether one or more assemblies are dirty.
        /// </summary>
        /// <value><c>true</c> if [assemblies dirty]; otherwise, <c>false</c>.</value>
        [Output]
        public bool AssembliesDirty
        {
            get { return _assembliesDirty; }
            set { _assembliesDirty = value; }
        }

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:AnalyzerTask"/> class.
        /// </summary>
        public ILAnalyzerTask()
            : base(Properties.Resources.ResourceManager)
        {

        }

        #endregion

        #region Supporting functions

        /// <summary>
        /// Determines whether the specified filename is a filter file.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <returns>
        /// 	<c>true</c> if the specified filename is a filter file; otherwise, <c>false</c>.
        /// </returns>
        private static bool IsFilterFile(string filename)
        {           
            foreach (String filterFile in ComposeStarFilterDll.Split(';'))
            {
                if (filename.StartsWith(filterFile))
                {
                    return true;
                }
            }

            return false;
        }

        /// <summary>
        /// Finds the assemblies to analyze.
        /// </summary>
        /// <param name="assemblyFileList">The assembly file list.</param>
        /// <param name="refAssemblies">The ref assemblies.</param>
        private void FindAssembliesToAnalyze(out List<String> assemblyFileList, out Dictionary<String, String> refAssemblies)
        {
            // Create a list of all the referenced assemblies (complete list is supplied by the msbuild file)
            assemblyFileList = new List<string>();

            foreach (ITaskItem item in AssemblyFiles)
            {
                // Skip composestar files
                string filename = Path.GetFileNameWithoutExtension(item.ToString());

                if (filename.StartsWith(ComposeStarDlls) && !IsFilterFile(filename))
                    continue;

                // We are only interested in assembly files.
                string extension = Path.GetExtension(item.ToString()).ToLower(CultureInfo.InvariantCulture);
                if (extension.Equals(".dll") || extension.Equals(".exe"))
                {
                    assemblyFileList.Add(item.ToString());
                } // if
            }

            // Create a list of all the referenced assemblies, which are not copied local for complete analysis
            // We cannot weave on these files, so we only use them for lookup of base types etc.
            refAssemblies = new Dictionary<string, string>();

            foreach (ITaskItem item in ReferencedAssemblies)
            {
                if (item.GetMetadata("CopyLocal") == "false")
                {
                    refAssemblies.Add(item.GetMetadata("FusionName"), item.GetMetadata("Identity"));
                }
            }
         
        }

        /// <summary>
        /// Analyzes all assemblies in output folder.
        /// </summary>
        /// <param name="analyzer">The analyzer.</param>
        /// <param name="assemblies">The assemblies.</param>
        /// <param name="assemblyFileList">The assembly file list.</param>
        /// <returns><see langword="true"/> when one or more assemblies are changed.</returns>
        private bool AnalyzeAllAssembliesInOutputFolder(IILAnalyzer analyzer, List<AssemblyElement> assemblies, List<String> assemblyFileList)
        {
            Boolean assemblyChanged = false;

            // Analyze all assemblies in the output folder
            foreach (String item in assemblyFileList)
            {
                try
                {
                    // See if we already have this assembly in the list
                    AssemblyConfig assConfig = null;

                    assConfig = assembliesInConfig.Find(delegate(AssemblyConfig ac)
                    {
                        return ac.Filename.Equals(item);
                    });

                    if (assConfig != null && File.Exists(assConfig.SerializedFilename))
                    {
                        // Already in the config. Check the last modification date.
                        if (assConfig.Timestamp == File.GetLastWriteTime(item).Ticks)
                        {
                            // Assembly has not been modified, skipping analysis
                            Log.LogMessageFromResources("AssemblyNotModified", assConfig.Name);

                            // We still have to store this assembly, but we do not analyze it
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

                    if (assembly != null && !IsFilterFile(Path.GetFileName(item)))
                    {
                        // Create a new AssemblyConfig object
                        assConfig = new AssemblyConfig();

                        assConfig.Filename = item;
                        assConfig.Name = assembly.Name;
                        assConfig.Timestamp = File.GetLastWriteTime(item).Ticks;
                        assConfig.Assembly = assembly;

                        // Generate a unique filename
                        assConfig.GenerateSerializedFilename(IntermediateOutputPath);

                        assemblyChanged = true;
                        _assembliesDirty = true;

                        assembliesToStore.Add(assConfig);
                        assemblies.Add(assembly);
                    } // if

                    sw.Stop();

                    Log.LogMessageFromResources("AssemblyAnalyzed", assembly.Types.Count, analyzer.UnresolvedAssemblies.Count, sw.Elapsed.TotalSeconds);

                    sw.Reset();

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

            if (analyzer.FilterTypes.Count > 0 && analyzer.FilterActions.Count > 0)
            {
                // Add FilterTypes
                foreach (FilterTypeElement ft in analyzer.FilterTypes)
                {
                    bool canAdd = true;
                    foreach (FilterTypeElement ftConfig in filterTypes)
                    {
                        if (ft.Name.Equals(ftConfig.Name))
                        {
                            canAdd = false;
                            continue;
                        } // if
                    }
                    if (canAdd)
                        filterTypes.Add(ft);
                }

                // Add FilterActions
                foreach (FilterActionElement fa in analyzer.FilterActions)
                {
                    bool canAdd = true;
                    foreach (FilterActionElement faConfig in filterActions)
                    {
                        if (fa.Name.Equals(faConfig.Name))
                        {
                            canAdd = false;
                            continue;
                        } // if
                    }
                    if (canAdd)
                        filterActions.Add(fa);
                }

                // TODO we miss a cleanup of the filtertypes and actions no longer in the assemblies. User has to use a rebuild.

                Log.LogMessageFromResources("FiltersAnalyzed", analyzer.FilterTypes.Count, analyzer.FilterActions.Count);
            }

            return assemblyChanged;
        }

        /// <summary>
        /// Analyzes the referenced assemblies.
        /// </summary>
        /// <param name="analyzer">The analyzer.</param>
        /// <param name="entitiesAccessor">The entities accessor.</param>
        /// <param name="assemblyChanged">if set to <c>true</c> one or more assemblies are changed.</param>
        /// <param name="refAssemblies">The ref assemblies.</param>
        /// <param name="assemblies">The assemblies.</param>
        /// <param name="assemblyFileList">The assembly file list.</param>
        private void AnalyzeReferencedAssemblies(IILAnalyzer analyzer, IEntitiesAccessor entitiesAccessor, bool assemblyChanged, Dictionary<String, String> refAssemblies, List<AssemblyElement> assemblies, List<String> assemblyFileList)
        {
            // Only if we have unresolved types
            if (analyzer.UnresolvedTypes.Count > 0)
            {

                Log.LogMessageFromResources("NumberOfReferencesToResolve", analyzer.UnresolvedTypes.Count);

                assemblyFileList.Clear();

                // The previous step could introduce new assemblies. So add those to the list
                assemblyFileList.AddRange(analyzer.ResolveAssemblyLocations());

                // Create new config
                CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration(RepositoryFilename);

                // Disable some options
                configuration.DoFieldAnalysis = false;
                configuration.DoMethodCallAnalysis = false;
                configuration.ExtractUnresolvedOnly = true;
                configuration.BinFolder = BinFolder;  

                // Store before reinit
                List<string> tempUnresolvedTypes = analyzer.UnresolvedTypes;

                // Create the analyzer using the object builder
                analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(entitiesAccessor, configuration));

                // Set the unresolved types (because we reinit the analyzer)
                analyzer.UnresolvedTypes.AddRange(tempUnresolvedTypes);

                // Add the assemblies to analyze.
                foreach (string al in refAssemblies.Values)
                {
                    if (!assemblyFileList.Contains(al))
                        assemblyFileList.Add(al);
                } // foreach

                // Try to resolve all the references.
                do
                {

                    // Loop through all the referenced assemblies.
                    foreach (String item in assemblyFileList)
                    {
                        try
                        {
                            // See if we already have this assembly in the list
                            AssemblyConfig assConfig = null;

                            assConfig = assembliesInConfig.Find(delegate(AssemblyConfig ac)
                            {
                                return ac.Filename.Equals(item);
                            });

                            // If a source assembly has changed, then new unresolved types can be introduced
                            // So we must rescan the library based on the value of assemblyChanged
                            // TODO can this be optimized?
                            if (!assemblyChanged && assConfig != null && File.Exists(assConfig.SerializedFilename))
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
                                assConfig.IsReference = true;

                                // Generate a unique filename
                                assConfig.GenerateSerializedFilename(IntermediateOutputPath);

                                assembliesToStore.Add(assConfig);
                                assemblies.Add(assembly);
                            } // if

                            sw.Stop();

                            Log.LogMessageFromResources("AssemblyAnalyzed", assembly.Types.Count, analyzer.UnresolvedAssemblies.Count, sw.Elapsed.TotalSeconds);

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
                            } // if
                        } // try
                        catch (ILAnalyzerException ex)
                        {
                            Log.LogErrorFromException(ex, true);
                        } // catch
                        catch (ArgumentException ex)
                        {
                            Log.LogErrorFromException(ex, true);
                        } // catch
                        catch (FileNotFoundException ex)
                        {
                            Log.LogErrorFromException(ex, true);
                        } // catch
                        catch (BadImageFormatException ex)
                        {
                            Log.LogErrorFromException(ex, false);
                        } // catch

                    } // foreach  (item)

                    // Clear the already analyzed assemblies
                    assemblyFileList.Clear();

                    // Get the unresolved
                    assemblyFileList.AddRange(analyzer.ResolveAssemblyLocations());
                }
                while (analyzer.UnresolvedTypes.Count > 0 && assemblyFileList.Count > 0);
            } // if

        }

        /// <summary>
        /// Stores the assemblies to the configuration container and, when needed, to assemblyElements on disk.
        /// </summary>
        /// <param name="analyzer">The analyzer.</param>
        /// <param name="entitiesAccessor">The entities accessor.</param>
        /// <param name="assemblies">The assemblies.</param>
        private void StoreAssemblies(IILAnalyzer analyzer, IEntitiesAccessor entitiesAccessor, List<AssemblyElement> assemblies)
        {
            // Storing types
            if (assemblies.Count > 0 && !Log.HasLoggedErrors)
            {
                Stopwatch sw = new Stopwatch();
                Log.LogMessageFromResources("StoreInDatabase", assemblies.Count);

                sw.Start();

                // Check if we have to save the assembly data
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

                Log.LogMessageFromResources("StoreInDatabaseCompleted", assemblies.Count, analyzer.ResolvedAssemblies.Count, sw.Elapsed.TotalSeconds);
            }
        }

        /// <summary>
        /// Adds all unresolved types from the concern files to the analyzer.
        /// </summary>
        /// <param name="analyzer">The analyzer.</param>
        private void AddAllUnresolvedTypes(IILAnalyzer analyzer)
        {
            if (ReferencedTypes.Length > 0)
            {
                Log.LogMessageFromResources(MessageImportance.Low, "NumberOfReferencesToResolve", ReferencedTypes.Length);
                foreach (ITaskItem item in ReferencedTypes)
                {
                    analyzer.UnresolvedTypes.Add(item.ToString());
                }
            }
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
            // Show start text
            Log.LogMessageFromResources("AnalyzerStartText");

            //
            // Setup of configuration, analyzer and lists
            //
            IILAnalyzer analyzer = null;
            CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration(RepositoryFilename);
            IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;
            Boolean assemblyChanged = false;

            // Set configuration settings
            configuration.BinFolder = BinFolder;
            configuration.DoMethodCallAnalysis = DoMethodCallAnalysis;

            // Create a list to store the retrieved assemblies in            
            List<AssemblyElement> assemblies = new List<AssemblyElement>();

            // Create the analyzer using the object builder
            analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(entitiesAccessor, configuration));

            // Get the configuration
            configContainer = entitiesAccessor.LoadConfiguration(RepositoryFilename);

            filterActions = configContainer.FilterActions;
            filterTypes = configContainer.FilterTypes;

            // Get the assemblies in the config file
            assembliesInConfig = configContainer.Assemblies;

            // Create a new list to store all the assemblies we have to save
            assembliesToStore = new List<AssemblyConfig>();

            //
            // Find the assemblies to analyze
            //
            List<String> assemblyFileList;
            Dictionary<String, String> refAssemblies;

            // Get the lists of the assemblies we have to analyze
            FindAssembliesToAnalyze(out assemblyFileList, out refAssemblies);

            // Add all the unresolved types (used in the concern files) to the analyser
            AddAllUnresolvedTypes(analyzer);

            //
            // Analyze the assemblies in the output folder
            //
            assemblyChanged = AnalyzeAllAssembliesInOutputFolder(analyzer, assemblies, assemblyFileList);
      
            //
            // Analyze the assemblies referenced to this project or subprojects
            //
            AnalyzeReferencedAssemblies(analyzer, entitiesAccessor, assemblyChanged, refAssemblies, assemblies, assemblyFileList);

            //
            // Store the found assemblies in the configuration container
            //
            StoreAssemblies(analyzer, entitiesAccessor, assemblies);

            // Close the analyzer
            analyzer.Dispose();

            return !Log.HasLoggedErrors;
        }

        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <param name="languageModel">The language model.</param>
        /// <param name="configuration">The configuration.</param>
        /// <returns></returns>
        internal static IServiceProvider CreateContainer(IEntitiesAccessor languageModel, CecilAnalyzerConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(IEntitiesAccessor), languageModel);
            serviceContainer.AddService(typeof(CecilAnalyzerConfiguration), configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new ILAnalyzerBuilderConfigurator());

            return serviceContainer;
        }

    }
}
