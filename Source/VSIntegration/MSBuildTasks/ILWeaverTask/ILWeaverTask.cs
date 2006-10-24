using System;
using System.IO;
using System.ComponentModel.Design;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.ILWeaver; 
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.ILWeaver;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration;
using Composestar.Repository;


namespace Composestar.StarLight.MSBuild.Tasks
{

    /// <summary>
    /// Responsible for the actual weaving of the aspects. Calls the weaving library to perform weaving at IL level.
    /// </summary>
    public class ILWeaverTask : Task
    {
        private const string ContextInfoFileName = "Composestar.StarLight.ContextInfo.dll";

        #region Properties for MSBuild

        private string _repositoryFilename;

        [Required()]
        public string RepositoryFilename
        {
            get { return _repositoryFilename; }
            set { _repositoryFilename = value; }
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

        #endregion

        #region ctor

        public ILWeaverTask()
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
            Log.LogMessageFromResources("WeavingStartText");

            IILWeaver weaver = null;
            IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;

            // Get the configuration container
            ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFilename);

            try
            {
                // For each assembly in the config
                foreach (AssemblyConfig assembly in configContainer.Assemblies)
                {

                    // Exclude StarLight ContextInfo assembly from the weaving process
                    if (assembly.Filename.EndsWith(ContextInfoFileName)) continue;

                    if (assembly.IsReference)
                        continue;

                    // If there is no weaving spec file, then skip
                    if (String.IsNullOrEmpty(assembly.WeaveSpecificationFile))
                    {
                        Log.LogMessageFromResources("SkippedWeavingFile", assembly.Filename);
                        continue; 
                    } // if

                    Log.LogMessageFromResources("WeavingFile", assembly.Filename);

                    // Preparing config
                    CecilWeaverConfiguration configuration = CecilWeaverConfiguration.CreateDefaultConfiguration(assembly, configContainer);

                    if (!String.IsNullOrEmpty(BinFolder))
                    {
                        configuration.BinFolder = BinFolder;
                    }

                    try
                    {
                        // Retrieve a weaver instance from the ObjectManager
                        weaver = DIHelper.CreateObject<CecilILWeaver>(CreateContainer(entitiesAccessor, configuration));

                        // Perform weaving
                        WeaveStatistics weaveStats = weaver.DoWeave();                                            

                        // Show information about weaving
                        Log.LogMessageFromResources("WeavingCompleted", weaveStats.InternalsAdded, weaveStats.ExternalsAdded, weaveStats.InputFiltersAdded, weaveStats.OutputFiltersAdded, weaveStats.TotalWeaveTime.TotalSeconds);
                        Log.LogMessageFromResources("WeavingStats", weaveStats.AverageWeaveTimePerMethod.TotalSeconds, weaveStats.AverageWeaveTimePerType.TotalSeconds,
                                                                    weaveStats.MaxWeaveTimePerMethod.TotalSeconds, weaveStats.MaxWeaveTimePerType.TotalSeconds,
                                                                    weaveStats.TotalMethodWeaveTime.TotalSeconds, weaveStats.TotalTypeWeaveTime.TotalSeconds, 
                                                                    weaveStats.MethodsProcessed, weaveStats.TypesProcessed,
                                                                    assembly.Filename); 
                    }
                    catch (ILWeaverException ex)
                    {
                        Log.LogErrorFromException(ex, false);
                        if (ex.InnerException != null)
                            Log.LogErrorFromException(ex.InnerException, true);
                    }
                    catch (ArgumentException ex)
                    {
                        Log.LogErrorFromException(ex, true);
                    }
                    catch (BadImageFormatException ex)
                    {
                        Log.LogErrorFromException(ex, true);
                    }

                }
            }
            finally
            {
                // Close the weaver, so it closes the database, performs cleanups etc
                if (weaver != null)
                    weaver.Close();
            }

            return !Log.HasLoggedErrors;
        }

        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <param name="languageModel">The language model.</param>
        /// <param name="configuration">The configuration.</param>
        /// <returns></returns>
        internal IServiceProvider CreateContainer(IEntitiesAccessor languageModel, CecilWeaverConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(IEntitiesAccessor), languageModel);
            serviceContainer.AddService(typeof(CecilWeaverConfiguration), configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new ILWeaverBuilderConfigurator());

            return serviceContainer;
        }

    }
}
