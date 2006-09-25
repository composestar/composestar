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
using Composestar.StarLight.ILWeaver;
using Composestar.Repository;

namespace Composestar.StarLight.MSBuild.Tasks
{

    /// <summary>
    /// Responsible for the actual weaving of the aspects. Calls the weaving library to perform weaving at IL level.
    /// </summary>
    public class ILWeaverTask : Task
    {
        ServiceContainer svcContainer = new ServiceContainer();

        #region Properties for MSBuild

        private ITaskItem[] _assemblyFiles;

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

        #endregion



        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverTask"/> class.
        /// </summary>
        public ILWeaverTask()
        {
            svcContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new IlWeaverBuilderConfigurator());
            svcContainer.AddService(typeof(CecilWeaverConfiguration), CecilWeaverConfiguration.CreateDefaultConfiguration("TestTarget.exe"));
            svcContainer.AddService(typeof(ILanguageModelAccessor), new RepositoryAccess());

        }

        /// <summary>
        /// When overridden in a derived class, executes the task.
        /// </summary>
        /// <returns>
        /// true if the task successfully executed; otherwise, false.
        /// </returns>
        public override bool Execute()
        {
            Log.LogMessage("Weaving the filter code using the Cecil IL Weaver");

            String filename;
            CecilILWeaver weaver = null;
            ILanguageModelAccessor langModelAccessor = new RepositoryAccess(RepositoryFilename);

            foreach (ITaskItem item in AssemblyFiles)
            {
                filename = item.ToString();
                Log.LogMessage("Weaving file {0}", filename);

                // Preparing config
                CecilWeaverConfiguration configuration = new CecilWeaverConfiguration(Path.GetDirectoryName(filename), false, "", filename, false);

                try
                {
                    weaver = new CecilILWeaver(configuration, langModelAccessor);
                    
                    // Perform weaving
                    weaver.DoWeave();

                    Log.LogMessage("Weaving completed in {0} seconds.", weaver.LastDuration.TotalSeconds);
                }
                catch (ILWeaverException ex)
                {
                    Log.LogErrorFromException(ex, true);
                }
                catch (ArgumentException ex)
                {
                    Log.LogErrorFromException(ex, true);
                }
                finally
                {
                    // Close the weaver, so it closes the database, performs cleanups etc
                    if (weaver != null) 
                        weaver.Close();
                }

            }

            return true;
        }


    }
}
