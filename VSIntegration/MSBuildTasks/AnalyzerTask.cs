using System;
using System.Collections;
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

namespace Composestar.StarLight.MSBuild.Tasks
{
    public class AnalyzerTask : Task
    {
        private string[] _assemblyFiles;

        [Required()]
        public string[] AssemblyFiles
        {
            get { return _assemblyFiles; }
            set { _assemblyFiles = value; }
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
            foreach (string filename in AssemblyFiles)
            {
                 analyzer.Initialize(filename, null);
                List<MethodElement> methods = analyzer.ExtractMethods(); 

                 Log.LogMessage("{0} methods found in {1} seconds.", methods.Count, analyzer.LastDuration.TotalSeconds);
        
            }
           
            return true;

        }


    }
}
