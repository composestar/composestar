using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Build.Utilities;
using Composestar.StarLight.CoreServices.Settings.Providers;
using System.Resources;

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Base task for all starlight tasks
    /// </summary>
    public abstract class StarlightTask : Task
    {
        private string _useversion;

        /// <summary>
        /// Sets the StarLight version to use
        /// </summary>
        public string UseVersion
        {
            set { _useversion = value; }
            get { return _useversion; }
        }

        protected StarlightTask()
            : base()
        {}

        protected StarlightTask(ResourceManager taskResources)
            : base(taskResources)
		{}

        protected StarlightTask(ResourceManager taskResources, string helpKeywordPrefix)
            : base(taskResources,helpKeywordPrefix)
        {}

        public override bool Execute()
        {
            RegistrySettingsProvider.UseVersion = _useversion;
            return true;
        } 
    }
}
