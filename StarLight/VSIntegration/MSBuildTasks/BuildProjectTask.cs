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

using Composestar.StarLight.MSBuild.Tasks.BuildConfiguration;

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Makes sure the project is build.
    /// </summary>
    public class BuildProjectTask : Task
    {
        private string _projectFile = "";

        [Required()]
        public string Projects
        {
            get { return _projectFile; }
            set { _projectFile = value; }
        }

        private string _targets = "build";

        public string Targets
        {
            get { return _targets; }
            set { _targets = value; }
        }

        private ITaskItem[] _assemblyFiles;

        [Output()]
        public ITaskItem[] AssemblyFiles
        {
            get { return _assemblyFiles; }
            set { _assemblyFiles = value; }
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

            // Load the project
            Microsoft.Build.BuildEngine.Project project = new Microsoft.Build.BuildEngine.Project(engine);
            project.Load(Projects);
            if (project == null)
            {
                Log.LogError("Could not find project {0}. Aborting.", Projects);
                return false;
            }

            AssemblyFiles = new ITaskItem[5];

            Log.LogMessage("Building {0}", project.FullFileName);
            if (project.Build(Targets))
            {
                Log.LogMessage("Building completed");

                 AssemblyFiles[0] = new TaskItem(@"C:\Documents and Settings\Michiel\My Documents\Visual Studio 2005\Projects\Testing\CecilTesting\TestApp\bin\Debug\TestApp.exe"); 
                    

                // Iterate through the various property groups and subsequently 
                // through the various properties
                foreach (BuildPropertyGroup propertyGroup in project.PropertyGroups)
                {
                    foreach (BuildProperty prop in propertyGroup)
                    {
                        Log.LogMessage("{0}:{1}", prop.Name, prop.Value);
                        if (prop.Name.Equals("assemblyname", StringComparison.CurrentCultureIgnoreCase)  )
                        {
                         }
                    }
                }


                Log.LogMessage("");
                Log.LogMessage("Project Items");
                Log.LogMessage("----------------------------------");

                // Iterate through the various itemgroups
                // and subsequently through the items
                foreach (BuildItemGroup itemGroup in project.ItemGroups)
                {
                    foreach (BuildItem item in itemGroup)
                    {
                        Log.LogMessage("{0}:{1}", item.Name, item.Include);
                    }
                }


                               
                return true;
            }

            return false;
        }


    }
}
