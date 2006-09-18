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

using Composestar.StarLight.ILWeaver; 
using Composestar.Repository.LanguageModel;  

namespace Composestar.StarLight.MSBuild.Tasks
{
    public class ILWeaverTask : Task
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

        private IILWeaver weaver;


        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverTask"/> class.
        /// </summary>
        public ILWeaverTask()
        {
            weaver = new CecilILWeaver();
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

            NameValueCollection config = new NameValueCollection();
            config.Add("RepositoryFilename", RepositoryFilename);
             

            //foreach (ITaskItem item in AssemblyFiles)
            //{
            //    filename = item.ToString();
            //    Log.LogMessage("Weaving file {0}", filename); 

            //    weaver.Initialize(filename, config);
            //    weaver.DoWeave(); 
                
            //    Log.LogMessage("Weaving completed in {0} seconds.", weaver.LastDuration.TotalSeconds);                       

            //    weaver.Close(); 
            //}                     

            return true;
        }


    }
}
