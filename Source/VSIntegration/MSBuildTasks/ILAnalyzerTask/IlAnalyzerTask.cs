using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel.Design;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;

using Microsoft.Build.BuildEngine;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.Repository.LanguageModel;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILAnalyzer;
using Composestar.Repository.Db4oContainers;
using Composestar.Repository; 

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

        private CecilILAnalyzer analyzer;

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
            
            // TODO add text to resource file
            // FIXME If a reference (thus an assembly) is removed from the project, it might still be in the 
            // Yap database, so it has to be cleaned up.

            Log.LogMessage("Analyzing the IL files using the Cecil IL Analyzer");

            IILAnalyzer analyzer = null;
            CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration("", RepositoryFilename); 
            ILanguageModelAccessor langModelAccessor = new RepositoryAccess(Db4oRepositoryContainer.Instance, RepositoryFilename);
       
            // Create a list to store the retrieved assemblies in            
            List<AssemblyElement> assemblies = new List<AssemblyElement>();

           // RepositoryAccess repository = new RepositoryAccess(Db4oRepositoryContainer.Instance, RepositoryFilename);
            
            // Create the analyzer using the object builder
            analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(langModelAccessor, configuration));

            // TODO: this aint the best approach, clearing everything...
            langModelAccessor.DeleteWeavingInstructions();

            // Create a list of all the referenced assemblies (complete list is supplied by the msbuild file)
            List<String> assemblyFileList = new List<string>();
            foreach (ITaskItem item in AssemblyFiles)
            {
                assemblyFileList.Add(item.ToString());
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

            // Add all the unresolved types (used in the concern files) to the analyser
            Log.LogMessage("Referenced types to resolve: {0}", ReferencedTypes.Length);
            foreach (ITaskItem item in ReferencedTypes)
            {
                analyzer.UnresolvedTypes.Add(item.ToString());
            }

            // Analyze all assemblies
            foreach (String item in assemblyFileList)
            {
                try
                {
                    AssemblyElement assembly = null;
                    Log.LogMessage("Analyzing file '{0}'...", item);

                    // Try to get the assembly information from the database
                    assembly = langModelAccessor.GetAssemblyElementByFileName(item);
                    if (assembly != null)
                    {
                        if (assembly.Timestamp == File.GetLastWriteTime(item).Ticks)
                        {
                            // Assembly has not been modified, skipping analysis
                            Log.LogMessage("File analysis summary: assembly has not been modified, skipping analysis.");
                            continue;
                        }
                        else
                        {
                            // Assembly has been modified, removing all existing types from database
                            langModelAccessor.DeleteTypeElements(item);
                        }
                    }

                    assembly = analyzer.ExtractAllTypes(item);
                    assemblies.Add(assembly);
                    
                    Log.LogMessage("File analysis summary: {0} types found in {2:0.0000} seconds. ({1} types not resolved)", assembly.TypeElements.Length, analyzer.UnresolvedTypes.Count, analyzer.LastDuration.TotalSeconds);
                }
                catch (ILAnalyzerException ex)
                {
                    Log.LogErrorFromException(ex, true); 
                }
                catch (ArgumentException ex)
                {
                    Log.LogErrorFromException(ex, true); 
                }

            }

            // Analyze the non-local copied assemblies for missing types
            if (analyzer.UnresolvedTypes.Count > 0)
            {
                // Step 1: Check if local database already contain the type
                System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                Log.LogMessage("Searching local database for {0} unresolved types...", analyzer.UnresolvedTypes.Count);

                sw.Start();
                
                int numberOfResolvedTypes = 0;
                List<String> unresolvedTypes = new List<string>(analyzer.UnresolvedTypes);
                foreach (String type in unresolvedTypes)
                {
                    TypeElement te = langModelAccessor.GetTypeElement(type);
                    if (te != null)
                    {
                        analyzer.UnresolvedTypes.Remove(type);
                        numberOfResolvedTypes++;
                    }
                }
                
                sw.Stop();

                Log.LogMessage("Found {0} types in local database in {1:0.0000} seconds.", numberOfResolvedTypes, sw.Elapsed.TotalSeconds);
            }
            if (analyzer.UnresolvedTypes.Count > 0)
            {
                // Step 2: Analyze all referenced assemblies in the hope we find the unresolved types
                System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                Log.LogMessage("Analyzing {0} referenced assemblies for {1} unresolved types...", refAssemblies.Count, analyzer.UnresolvedTypes.Count);

                sw.Start();

                assemblies.AddRange(analyzer.ProcessUnresolvedTypes(refAssemblies));

                sw.Stop();

                Log.LogMessage("Referenced assemblies analyzed in {1:0.0000} seconds, {0} unresolved types remaining.", analyzer.UnresolvedTypes.Count, sw.Elapsed.TotalSeconds);
            }
            if (analyzer.UnresolvedTypes.Count > 0)
            {
                // Step 3: Unable to resolve some types, throw an error
                foreach (String type in analyzer.UnresolvedTypes)
                {
                    Log.LogError("Unable to resolve type '{0}', are you missing an assembly reference?", type);
                }
            }

            // Storing types in database
            if (assemblies.Count > 0)
            {
                System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                Log.LogMessage("Storing type information for {0} assemblies in database...", assemblies.Count);

                sw.Start();

                // TODO: checken of assembly bestaat en dan 'updaten', bv. nieuwe types toevoegen aan system assembly

                langModelAccessor.AddAssemblies(assemblies, analyzer.ResolvedTypes);

                sw.Stop();

                Log.LogMessage("Storage summary: {0} assemblies with a total of {1} types stored in {2:0.0000} seconds.", assemblies.Count, analyzer.ResolvedTypes.Count, sw.Elapsed.TotalSeconds);
            }
            
            //// Try to resolve types from the cache
            //if (analyzer.UnresolvedTypes.Count > 0)
            //{
            //    Log.LogMessage("Accessing cache for {0} unresolved types", analyzer.UnresolvedTypes.Count);
            //    int unresolvedCount = analyzer.UnresolvedTypes.Count;
            //    analyzer.ProcessUnresolvedTypes();
            //    Log.LogMessage("Cache lookup summary: {0} out of {1} types found in {2:0.0000} seconds.", unresolvedCount - analyzer.UnresolvedTypes.Count, unresolvedCount, analyzer.LastDuration.TotalSeconds);

            //    if (analyzer.UnresolvedTypes.Count > 0)
            //    {
            //        Log.LogError("Unable to resolve {0} types, detailed overview below:", analyzer.UnresolvedTypes.Count);
            //        foreach (String type in analyzer.UnresolvedTypes)
            //        {
            //            Log.LogError("  {0}", type);
            //        }
            //    }
            //}
           
            // Close the analyzer
            analyzer.Close();
            langModelAccessor.Close();
 
            return !Log.HasLoggedErrors;

        }
    
        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <param name="languageModel">The language model.</param>
        /// <param name="configuration">The configuration.</param>
        /// <returns></returns>
        internal IServiceProvider CreateContainer(ILanguageModelAccessor languageModel, CecilAnalyzerConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(ILanguageModelAccessor), languageModel);
            serviceContainer.AddService(typeof(CecilAnalyzerConfiguration), configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new IlAnalyzerBuilderConfigurator());

            return serviceContainer;
        }

    }

}
