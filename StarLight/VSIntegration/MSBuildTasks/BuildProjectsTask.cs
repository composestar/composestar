using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;
using System.ComponentModel;

using Microsoft.Build.BuildEngine;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using ComposeStar.MSBuild.Tasks.BuildConfiguration;

namespace ComposeStar.MSBuild.Tasks
{
    /// <summary>
    /// Makes sure all the referenced projects are build.
    /// </summary>
    public class BuildProjectsTask : Task
    {
        private string _projectFile = "";

        [Required()]
        public string ProjectFile
        {
            get { return _projectFile; }
            set { _projectFile = value; }
        }

         private String referencedProject = "";
        /// <summary>
        /// List of dependent assemblies
        /// </summary>
        public String ReferencedProjects
        {
            get { return referencedProject; }
            set
            {
                if (value != null)
                {
                    referencedProject = value;
                }
                else
                {
                    referencedProject = "";
                }

            }
        }

        public override bool Execute()
        {
              //Load the Engine.
            Microsoft.Build.BuildEngine.Engine engine = Engine.GlobalEngine;

            if (engine == null)
            {
                Log.LogError("Could not find Engine. Aborting.");
                return false;
            }
            // Load the current composestar project
            Microsoft.Build.BuildEngine.Project project = engine.GetLoadedProject(ProjectFile);
            if (project == null)
            {
                Log.LogError("Could not find project. Aborting.");
                return false;
            }

            foreach (String rp in ReferencedProjects.Split(new char() {";"}) )
            {
               ProcessProject(rp, Log);                
            }
            
            return true;
        }

        /// <summary>
        /// Get the referenced project data
        /// </summary>
        /// <param name="bi"></param>
        /// <param name="log"></param>
        private bool ProcessProject(String rp, TaskLoggingHelper log)
        {
              //Load the Engine.
            Microsoft.Build.BuildEngine.Engine engine = Engine.GlobalEngine;

            if (engine == null)
            {
                log.LogError("Could not find Engine. Aborting.");
                return false;
            }
            // Load the current composestar project
            Microsoft.Build.BuildEngine.Project project = engine.GetLoadedProject(rp);
            if (project == null)
            {
                Log.LogError("Could not find project. Aborting.");
                return false;
            }

            Log.LogMessage("Analysing and processing {0}...", rp) ;

            // ....

            Log.LogMessage("Building {0}...", rp) ;

            project.Build(); 

            Log.LogMessage("Building completed."); 

            return true; 
        }

    }
}
