#region Using directives
using System;
using System.IO;
using System.Collections.Generic;
using System.ComponentModel.Design;  

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.CpsParser;
using Composestar.Repository.LanguageModel;
using Composestar.Repository.Configuration;
using Composestar.Repository.Db4oContainers;
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
        private ITaskItem[] _referencedTypes;

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

        #endregion

        /// <summary>
        /// When overridden in a derived class, executes the task.
        /// </summary>
        /// <returns>
        /// true if the task successfully executed; otherwise, false.
        /// </returns>
        public override bool Execute()
        {
            List<string> refTypes = null;
            System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                        
            RepositoryAccess repositoryAccess = null;

            try
            {
                sw.Start();

                ICpsParser cfp = null;
                cfp = DIHelper.CreateObject<CpsFileParser>(CreateContainer());

                // Open DB
                Log.LogMessageFromResources(MessageImportance.Low, "OpenDatabase", RepositoryFilename);
                repositoryAccess = new RepositoryAccess(Db4oRepositoryContainer.Instance, RepositoryFilename);

                // TODO this can be optimized. Save data in the database so we also know if concerns are not changes
                // Do not start master when assemblies and concerns are not changed.

                // Remove all concerns first because the user may have removed a concern from the project
                // after a previous run.
                repositoryAccess.DeleteConcernInformations();

                // Parse all concern files and add to the database
                foreach (ITaskItem item in ConcernFiles)
                {
                    String concernFile = item.ToString();

                    Log.LogMessageFromResources("ParsingConcernFile", concernFile);

                    refTypes = cfp.ParseFileForReferencedTypes(concernFile);

                    string path = Path.GetDirectoryName(concernFile);
                    string filename = Path.GetFileName(concernFile);

                    ConcernInformation ci = new ConcernInformation(filename, path);
                  
                    Log.LogMessageFromResources("AddingConcernFile", filename);
                    repositoryAccess.AddConcern(ci);
                }

                sw.Stop();

                // Pass all the referenced types back to msbuild
                if (refTypes != null && refTypes.Count > 0)
                {
                    Log.LogMessageFromResources("FoundReferenceType", refTypes.Count, ConcernFiles.Length, sw.Elapsed.TotalSeconds);
                    int index = 0;
                    ReferencedTypes = new ITaskItem[refTypes.Count];
                    foreach (String type in refTypes)
                    {
                        ReferencedTypes[index] = new TaskItem(type);
                        index++;
                    } // foreach  (type)

                } // foreach  (item)
            }
            catch (CpsParserException ex)
            {
                Log.LogErrorFromException(ex, false);
            }
            catch (System.IO.FileNotFoundException ex)
            {
                Log.LogErrorFromException(ex, false);
            }
            finally
            {
                if (repositoryAccess != null)   repositoryAccess.Close(); 
            } // finally
           
            return !Log.HasLoggedErrors;

        } // Execute()

        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <returns></returns>
        internal IServiceProvider CreateContainer()
        {
            ServiceContainer serviceContainer = new ServiceContainer();

            return serviceContainer;
        }
    }


}
