#region Using directives
using System;
using System.Collections.Generic;
using System.ComponentModel.Design;  

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.CpsParser; 
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

            try
            {
                sw.Start();

                ICpsParser cfp = null;
                cfp = DIHelper.CreateObject<CpsFileParser>(CreateContainer());

                // Parse all concern files
                foreach (ITaskItem item in ConcernFiles)
                {
                    String concernFile = item.ToString();

                    Log.LogMessageFromResources("ParsingConcernFile", concernFile);

                    refTypes = cfp.ParseFileForReferencedTypes(concernFile);
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
