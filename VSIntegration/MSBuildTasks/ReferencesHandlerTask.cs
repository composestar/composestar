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


namespace Composestar.StarLight.MSBuild.Tasks
{
    public class ReferencesHandlerTask : Task
    {
        private string _projectFile;

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

            

            foreach (String rp in ReferencedProjects.Split(new char() { ";" }))
            {
                
            }

            return true;
        }


    }
}
