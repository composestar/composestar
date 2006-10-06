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

        public override bool Execute()
        {
            CpsParser.CpsFileParser cfp = new Composestar.CpsParser.CpsFileParser();

            foreach (ITaskItem item in ConcernFiles)
            {
                String concernFile = item.ToString();

                Log.LogMessage("Parsing concern file '{0}'...", concernFile);
                
                cfp.ParseFile(concernFile);

                int i = 0;
                ReferencedTypes = new ITaskItem[cfp.ReferencedTypes.Count];
                foreach (String type in cfp.ReferencedTypes)
                {
                    ReferencedTypes[i] = new TaskItem(type);
                    i = i + 1;
                    Log.LogMessage("  found referenced type '{0}'", type);
                }
            }





            return !Log.HasLoggedErrors;
        }
    }


}
