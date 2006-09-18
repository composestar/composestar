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

            foreach (ITaskItem item in AssemblyFiles)
            {
                filename = item.ToString();
                Log.LogMessage("Weaving file {0}", filename);

                // Preparing config
                NameValueCollection config = new NameValueCollection();
                config.Add("RepositoryFilename", RepositoryFilename);
                config.Add("OutputImagePath", Path.GetDirectoryName(filename));
                config.Add("ShouldSignAssembly", "false");
                config.Add("OutputImageSNK", "");

                try
                {
                    // Initialize
                    weaver.Initialize(filename, config);

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
                    weaver.Close();
                }

            }

            return true;
        }


    }
}
