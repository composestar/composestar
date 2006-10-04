using System;
using System.Collections;
using System.Collections.Specialized;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;

using Microsoft.Build.BuildEngine;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.ILAnalyzer; 
using Composestar.Repository.LanguageModel;  

namespace Composestar.StarLight.MSBuild.Tasks
{
    public class AnalyzerTask : Task
    {
        #region Properties for MSBuild

        private ITaskItem[] _assemblyFiles;

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

        private IILAnalyzer analyzer;

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:AnalyzerTask"/> class.
        /// </summary>
        public AnalyzerTask()
            : base(Properties.Resources.ResourceManager)
        {
            analyzer = new CecilILAnalyzer();
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
            // TODO must perform a cleanup before executing the analyzer.
            // Otherelse the repository will be dirty

            // TODO add text to resource file
            // FIXME If a reference (thus an assembly) is removed from the project, it might still be in the 
            // Yap database, so it has to be cleaned up.

            Log.LogMessage("Analyzing the IL files using the Cecil IL Analyzer");

            // Read registry
            RegistrySettings rs = new RegistrySettings();
            if (!rs.ReadSettings())
            {
                Log.LogErrorFromResources("CouldNotReadRegistryValues");  
                return false;
            }

            NameValueCollection config = new NameValueCollection();
            config.Add("RepositoryFilename", RepositoryFilename);
            config.Add("CacheFolder", rs.InstallFolder);
             
            foreach (ITaskItem item in AssemblyFiles)
            {
                try
                {
                    Log.LogMessage("Analyzing file {0}", item.ToString());

                    analyzer.Initialize(config);
                    IlAnalyzerResults result = analyzer.ExtractTypeElements(item.ToString());

                    if (result == IlAnalyzerResults.FROM_ASSEMBLY)
                    {
                        Log.LogMessage("File analysis summary: {0} types found in {2:0.0000} seconds. ({1} types not resolved)", analyzer.ResolvedTypes.Count, analyzer.UnresolvedTypes.Count, analyzer.LastDuration.TotalSeconds);
                    }
                    else
                    {
                        Log.LogMessage("File has not been modified, analysis skipped. Removed weaving information from repository in {0:0.0000} seconds.", analyzer.LastDuration.TotalSeconds);
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

            }

            // Try to resolve types from the cache
            if (analyzer.UnresolvedTypes.Count > 0)
            {
                Log.LogMessage("Accessing cache for {0} unresolved types", analyzer.UnresolvedTypes.Count);
                int unresolvedCount = analyzer.UnresolvedTypes.Count;
                analyzer.ProcessUnresolvedTypes();
                Log.LogMessage("Cache lookup summary: {0} out of {1} types found in {2:0.0000} seconds.", unresolvedCount - analyzer.UnresolvedTypes.Count, unresolvedCount, analyzer.LastDuration.TotalSeconds);

                if (analyzer.UnresolvedTypes.Count > 0)
                {                    
                    foreach (String type in analyzer.UnresolvedTypes)
                    {
                        Log.LogError("Cannot resolve type {0}", type);
                    }
                    Log.LogError("Unable to resolve {0} types.", analyzer.UnresolvedTypes.Count);
                }
            }
           
            // Close the analyzer
            analyzer.Close();
 
            return !Log.HasLoggedErrors;

        }
    }
}
