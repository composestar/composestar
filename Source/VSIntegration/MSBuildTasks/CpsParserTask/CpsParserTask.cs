#region Using directives
using System;
using System.IO;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Diagnostics;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CpsParser;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;   
using Composestar.Repository;

#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Task to parse the Cps files.
    /// </summary>
    public class CpsParserTask : Task
    {

        #region Constructor
        
        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsParserTask"/> class.
        /// </summary>
        public CpsParserTask() : base(Properties.Resources.ResourceManager)
        {

        }
        #endregion

        #region Properties

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

        private ITaskItem[] _concernFiles;

        /// <summary>
        /// Gets or sets the concern files.
        /// </summary>
        /// <value>The concern files.</value>
        [Required()]
        public ITaskItem[] ConcernFiles
        {
            get { return _concernFiles; }
            set { _concernFiles = value; }
        }

        private ITaskItem[] _referencedTypes;

        /// <summary>
        /// Gets or sets the referenced types.
        /// </summary>
        /// <value>The referenced types.</value>
        [Output()]
        public ITaskItem[] ReferencedTypes
        {
            get { return _referencedTypes; }
            set { _referencedTypes = value; }
        }

        private bool _hasOutputFilters;

        [Output()]
        public bool HasOutputFilters
        {
            get { return _hasOutputFilters; }
            set { _hasOutputFilters = value; }
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
            List<string> refTypes = new List<string>();
            Stopwatch sw = new Stopwatch();                        
       
            try
            {
                sw.Start();

                ICpsParser cfp = null;
                
                // Open DB
                Log.LogMessageFromResources(MessageImportance.Low, "OpenDatabase", RepositoryFilename);
                IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance; 

                ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFilename);               

                // Clear all existing concerns
                configContainer.Concerns.Clear();  

                // Parse all concern files and add to the database
                foreach (ITaskItem item in ConcernFiles)
                {
                    String concernFile = item.ToString();

                    Log.LogMessageFromResources("ParsingConcernFile", concernFile);

                    CpsParserConfiguration config = CpsParserConfiguration.CreateDefaultConfiguration(concernFile);

                    cfp = DIHelper.CreateObject<CpsFileParser>(CreateContainer(config));

                    cfp.Parse();

                    refTypes.AddRange(cfp.ReferencedTypes);

                    // If this concern has output filters, then enable (do not override a previously set true value)
                    HasOutputFilters = HasOutputFilters | cfp.HasOutputFilters;

                    string path = Path.GetDirectoryName(concernFile);
                    string filename = Path.GetFileName(concernFile);

                    ConcernElement ce = new ConcernElement();
                    ce.PathName = path;
                    ce.FileName = filename;
                    ce.Timestamp = File.GetLastWriteTime(concernFile).Ticks; 
                  
                    Log.LogMessageFromResources("AddingConcernFile", filename);
                    configContainer.Concerns.Add(ce);
                }

                sw.Stop();

                Log.LogMessageFromResources("FoundReferenceType", refTypes.Count, ConcernFiles.Length, sw.Elapsed.TotalSeconds);

                // Pass all the referenced types back to msbuild
                if (refTypes != null && refTypes.Count > 0)
                {
                    int index = 0;
                    ReferencedTypes = new ITaskItem[refTypes.Count];
                    foreach (String type in refTypes)
                    {
                        ReferencedTypes[index] = new TaskItem(type);
                        index++;
                    } // foreach  (type)

                } // foreach  (item)

                // Save the configContainer
                entitiesAccessor.SaveConfiguration(RepositoryFilename, configContainer); 
                
            }
            catch (CpsParserException ex)
            {
                Log.LogErrorFromException(ex, false);
            }
            catch (System.IO.FileNotFoundException ex)
            {
                Log.LogErrorFromException(ex, false);
            }
                
            return !Log.HasLoggedErrors;

        } // Execute()

        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <returns></returns>
        internal IServiceProvider CreateContainer(CpsParserConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(CpsParserConfiguration), configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new CpsParserBuilderConfigurator());

            return serviceContainer;
        }
    }
}
