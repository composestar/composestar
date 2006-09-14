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

        private IILAnalyzer analyzer;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:AnalyzerTask"/> class.
        /// </summary>
        public AnalyzerTask()
        {
            analyzer = new CecilILAnalyzer();
        }
        
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

            Log.LogMessage("Analyzing the IL files using the Cecil IL Analyzer");

            NameValueCollection config = new NameValueCollection();
            config.Add("RepositoryFilename", RepositoryFilename);
             
            foreach (ITaskItem item in AssemblyFiles)
            {
                Log.LogMessage("Analyzing file {0}", item.ToString()); 

                analyzer.Initialize(item.ToString(), config);
                IList<TypeElement> ret = analyzer.ExtractTypeElements();

                Log.LogMessage("{0} types found in {1} seconds.", ret.Count, analyzer.LastDuration.TotalSeconds);                       
            }
           
            return true;

        }


    }
}
