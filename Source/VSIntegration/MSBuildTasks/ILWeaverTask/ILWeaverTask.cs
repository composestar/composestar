using System;
using System.IO;
using System.ComponentModel.Design;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.ILWeaver;
using Composestar.Repository.Db4oContainers;
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

        private ITaskItem[] _assemblyFiles;

        // TODO Weaver should only work on the assemblies in the database. Thus supplied by the analyzer.

        [Required()]
        public ITaskItem[] AssemblyFiles
        {
            get { return _assemblyFiles; }
            set { _assemblyFiles = value; }
        }

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

            String filename;
            String extension;
            IILWeaver weaver = null;
            ILanguageModelAccessor langModelAccessor = new RepositoryAccess(Db4oRepositoryContainer.Instance, RepositoryFilename);
            
            try
            {
                foreach (ITaskItem item in AssemblyFiles)
                {
                    filename = item.ToString();
                    extension = Path.GetExtension(filename).ToLower(); 
                    if (!extension.Equals(".dll") && !extension.Equals(".exe"))
                    {
                        continue;
                    } // foreach  (item)
                

                    // Exclude StarLight ContextInfo assembly from the weaving process
                    if (filename.EndsWith(ContextInfoFileName)) continue;

                    Log.LogMessageFromResources("WeavingFile", filename);

                    // Preparing config
                    //CecilWeaverConfiguration configuration = new CecilWeaverConfiguration(Path.Combine(Path.GetDirectoryName(filename), @"woven\" + Path.GetFileName(filename)), false, "", filename, false);
                    CecilWeaverConfiguration configuration = new CecilWeaverConfiguration(filename, false, "", filename, false);
                    if (!String.IsNullOrEmpty(BinFolder))
                    {
                        configuration.BinFolder = BinFolder;
                    }

                    try
                    {
                        // Retrieve a weaver instance from the ObjectManager
                        weaver = DIHelper.CreateObject<CecilILWeaver>(CreateContainer(langModelAccessor, configuration));

                        // Perform weaving
                        weaver.DoWeave();

                        Log.LogMessageFromResources("WeavingCompleted", weaver.LastDuration.TotalSeconds);
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
                langModelAccessor.Close(); 
            }

            return !Log.HasLoggedErrors;
        }

        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <param name="languageModel">The language model.</param>
        /// <param name="configuration">The configuration.</param>
        /// <returns></returns>
        internal IServiceProvider CreateContainer(ILanguageModelAccessor languageModel, CecilWeaverConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(ILanguageModelAccessor), languageModel);
            serviceContainer.AddService(typeof(CecilWeaverConfiguration), configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new IlWeaverBuilderConfigurator());

            return serviceContainer;
        }

    }
}