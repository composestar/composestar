
using System;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.Package;
using System.Runtime.InteropServices;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;

using IOleServiceProvider = Microsoft.VisualStudio.OLE.Interop.IServiceProvider;

namespace Composestar.StarLight.VisualStudio.Project
{
    public enum DebugLevel
    {
        NotSet = -1,
        Error = 0,
        Crucial = 1,
        Warning = 2,
        Information = 3,
        Debug = 4
    }

    [ComVisible(true), Guid("9CD2405A-8FB1-4433-A61D-6E81CB33E7F8")]
    public class ComposeStarBuildPropertyPage : SettingsPage
    {
        
        internal enum ConfigurationPropertyPageTag
        {
            DebugLevel,
        }
        
        #region fields
        private DebugLevel debugLevel = DebugLevel.Information;
        #endregion

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ComposeStarBuildPropertyPage"/> class.
        /// </summary>
        public ComposeStarBuildPropertyPage()
        {
            this.Name = SR.GetString(SR.BuildCaption);
        }

        #region overriden methods
        /// <summary>
        /// Gets the name of the class.
        /// </summary>
        /// <returns></returns>
        public override string GetClassName()
        {
            return this.GetType().FullName;
        }

        /// <summary>
        /// Binds the properties.
        /// </summary>
        protected override void BindProperties()
        {
            if (this.ProjectMgr == null)
            {
                Debug.Assert(false);
                return;
            }

            string dlevel = this.GetConfigProperty(ConfigurationPropertyPageTag.DebugLevel.ToString());

            if (dlevel != null && dlevel.Length > 0)
            {
                try
                {
                    this.debugLevel = (DebugLevel)Enum.Parse(typeof(DebugLevel), dlevel);
                }
                catch
                { } //Should only fail if project file is corrupt
            }

        }

        /// <summary>
        /// Applies the changes.
        /// </summary>
        /// <returns></returns>
        protected override int ApplyChanges()
        {
            if (this.ProjectMgr == null)
            {
                Debug.Assert(false);
                return VSConstants.E_INVALIDARG;
            }

            SetConfigProperty(ConfigurationPropertyPageTag.DebugLevel.ToString(), ((int)this.debugLevel).ToString() );

            this.IsDirty = false;

            return VSConstants.S_OK;
        }
        #endregion

        #region exposed properties


        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.OutputType"]/*' />
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.DebugLevel)]
        [SRDescriptionAttribute(SR.DebugLevelDescription)]
        public DebugLevel DebugLevel
        {
            get { return this.debugLevel; }
            set { this.debugLevel = value; this.IsDirty = true; }
        }
            

       #endregion

    }
}
