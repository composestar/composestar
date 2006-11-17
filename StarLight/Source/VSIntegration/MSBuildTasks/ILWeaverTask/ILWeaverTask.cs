using System;
using System.IO;
using System.ComponentModel.Design;
using System.Globalization;
using System.Diagnostics.CodeAnalysis;  

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
    //[LoadInSeparateAppDomain()]
    public class ILWeaverTask : Task //AppDomainIsolatedTask 
    {
        private const string ContextInfoFileName = "Composestar.StarLight.ContextInfo.dll";

        #region Properties for MSBuild

        private string _repositoryFileName;

        [Required()]
        public string RepositoryFileName
        {
            get { return _repositoryFileName; }
            set { _repositoryFileName = value; }
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

        private bool _concernsDirty;

        /// <summary>
        /// Gets or sets a value indicating whether [concerns dirty].
        /// </summary>
        /// <value><c>true</c> if [concerns dirty]; otherwise, <c>false</c>.</value>        
        public bool ConcernsDirty
        {
            get { return _concernsDirty; }
            set { _concernsDirty = value; }
        }

        private string _weaveDebug = "none";

        /// <summary>
        /// Gets or sets the weave debug.
        /// </summary>
        /// <value>The weave debug.</value>
        public string WeaveDebug
        {
            get { return _weaveDebug; }
            set { _weaveDebug = value; }
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
            ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFileName);

            // Set the weave debug level
            CecilWeaverConfiguration.WeaveDebug weaveDebugLevel;
            if (string.IsNullOrEmpty(_weaveDebug))
                weaveDebugLevel = CecilWeaverConfiguration.WeaveDebug.None;
            else
            {
                try
                {
                    weaveDebugLevel = (CecilWeaverConfiguration.WeaveDebug) CecilWeaverConfiguration.WeaveDebug.Parse(typeof(CecilWeaverConfiguration.WeaveDebug), _weaveDebug, true);             
                }
                catch (ArgumentException)
                {
                    Log.LogErrorFromResources("CouldNotParseWeaveDebugLevel", _weaveDebug);
                    return false;
                }                
            }
            
            try
            {
                // For each assembly in the config
                foreach (AssemblyConfig assembly in configContainer.Assemblies)
                {

                    // Exclude StarLight ContextInfo assembly from the weaving process
                    if (assembly.FileName.EndsWith(ContextInfoFileName)) 
                        continue;

                    // Exclude references
                    if (assembly.IsReference)
                        continue;

                    // If there is no weaving spec file, then skip
                    if (String.IsNullOrEmpty(assembly.WeaveSpecificationFile))
                    {
                        Log.LogMessageFromResources("SkippedWeavingFile", assembly.FileName);
                        continue; 
                    }

                    // Check for modification
                    if (!ConcernsDirty && File.GetLastWriteTime(assembly.FileName).Ticks <= assembly.Timestamp)
                    {
                        // we beter copy the backuped file
                        String backupWeavefile = string.Format(CultureInfo.InvariantCulture, "{0}.weaved", assembly.FileName);
                        if (File.Exists(backupWeavefile))
                        {
                            File.Copy(backupWeavefile, assembly.FileName, true);
                            Log.LogMessageFromResources("UsingBackupWeaveFile", assembly.FileName);
                            continue; 
                        }
                    }

                    Log.LogMessageFromResources("WeavingFile", assembly.FileName);

                    // Preparing config
                    CecilWeaverConfiguration configuration = new CecilWeaverConfiguration(assembly, configContainer, weaveDebugLevel);

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
                        Log.LogMessageFromResources("WeavingCompleted", weaveStats.TotalWeaveTime.TotalSeconds);
                        switch (configuration.WeaveDebugLevel)
                        {
                            case CecilWeaverConfiguration.WeaveDebug.None:
                                break;
                            case CecilWeaverConfiguration.WeaveDebug.Statistics:
                                ShowWeavingStats(assembly, weaveStats);
                                break;
                            case CecilWeaverConfiguration.WeaveDebug.Detailed:
                                ShowWeavingStats(assembly, weaveStats);

                                // Save instruction log
                                string logFilename = assembly.FileName + ".weavelog.txt";
                                string timingFilename = assembly.FileName + ".weavetiming.txt";
                                
                                weaveStats.SaveInstructionsLog(logFilename);
                                weaveStats.SaveTimingLog(timingFilename);
  
                                Log.LogMessageFromResources("WeavingInstructionsLogSaved", logFilename);
                                Log.LogMessageFromResources("WeavingTimingLogSaved", timingFilename); 
                                break;
                            default:
                                break;
                        }                        

                    }
                    catch (ILWeaverException ex)
                    {
                        //Log.LogErrorFromException(ex, true); 
                        string errorMessage = ex.Message;
                        string stackTrace = ex.StackTrace;

                        Exception innerException = ex.InnerException;
                        while (innerException != null)
                        {
                            errorMessage = String.Format(CultureInfo.CurrentCulture, "{0}; {1}", errorMessage, innerException.Message);
                            innerException = innerException.InnerException; 
                        }
                        Log.LogErrorFromResources("WeaverException", errorMessage);

                        // Only show stacktrace when debugging is enabled
                        if (weaveDebugLevel != CecilWeaverConfiguration.WeaveDebug.None)
                            Log.LogErrorFromResources("WeaverExceptionStackTrace", stackTrace); 
                    }
                    catch (ArgumentException ex)
                    {
                        Log.LogErrorFromException(ex, true);
                    }
                    catch (BadImageFormatException ex)
                    {
                        Log.LogErrorFromException(ex, false);
                    }
                }
            }
            finally
            {
                // Close the weaver, so it closes the database, performs cleanups etc
                if (weaver != null)
                    weaver.Dispose();
            }

            return !Log.HasLoggedErrors;
        }

        /// <summary>
        /// Shows the weaving statistics.
        /// </summary>
        /// <param name="assembly">The assembly.</param>
        /// <param name="weaveStats">The weave stats.</param>
        private void ShowWeavingStats(AssemblyConfig assembly, WeaveStatistics weaveStats)
        {
            Log.LogMessageFromResources("WeavingStats", weaveStats.AverageWeaveTimePerMethod.TotalSeconds, weaveStats.AverageWeaveTimePerType.TotalSeconds,
                                                                                weaveStats.MaxWeaveTimePerMethod.TotalSeconds, weaveStats.MaxWeaveTimePerType.TotalSeconds,
                                                                                weaveStats.TotalMethodWeaveTime.TotalSeconds, weaveStats.TotalTypeWeaveTime.TotalSeconds,
                                                                                weaveStats.MethodsProcessed, weaveStats.TypesProcessed,
                                                                                assembly.FileName,
                                                                                weaveStats.InternalsAdded, weaveStats.ExternalsAdded,
                                                                                weaveStats.InputFiltersAdded, weaveStats.OutputFiltersAdded,
                                                                                weaveStats.TotalWeaveTime.TotalSeconds);
        }
        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <param name="languageModel">The language model.</param>
        /// <param name="configuration">The configuration.</param>
        /// <returns></returns>
        internal static IServiceProvider CreateContainer(IEntitiesAccessor languageModel, CecilWeaverConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(IEntitiesAccessor), languageModel);
            serviceContainer.AddService(typeof(CecilWeaverConfiguration), configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new ILWeaverBuilderConfigurator());

            return serviceContainer;
        }

    }
}
