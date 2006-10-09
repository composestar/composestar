#region Using directives
using System;
using System.Collections;
using System.Collections.Specialized;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.ComponentModel.Design;  
using System.Text;
using System.Xml;

using Microsoft.Build.BuildEngine;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.StarLight.ILAnalyzer; 
using Composestar.Repository.LanguageModel;  
using Composestar.StarLight.CoreServices;  
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
            ICpsParser cfp = null;
            cfp = DIHelper.CreateObject<CpsFileParser>(CreateContainer());
                     
            foreach (ITaskItem item in ConcernFiles)
            {
                String concernFile = item.ToString();

                Log.LogMessage("Parsing concern file '{0}'...", concernFile);
                
                List<string> refTypes = cfp.ParseFileForReferencedTypes(concernFile);

                int index = 0;
                ReferencedTypes = new ITaskItem[refTypes.Count];
                foreach (String type in refTypes)
                {
                    ReferencedTypes[index] = new TaskItem(type);
                    index++;
                    Log.LogMessage("  found referenced type '{0}'", type);
                } // foreach  (type)
            } // foreach  (item)
            
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
