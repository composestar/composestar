
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

    /// <summary>
    /// Indicated the level of debug information collected by the weaver.
    /// </summary>
    public enum WeaveDebug
    {
        /// <summary>
        /// No debugging is collected.
        /// </summary>
        None,
        /// <summary>
        /// Weave statistics are collected.
        /// </summary>
        Statistics,
        /// <summary>
        /// Weave statistics and detailed information is collected.
        /// </summary>
        Detailed
    }

    [ComVisible(true), Guid("9CD2405A-8FB1-4433-A61D-6E81CB33E7F8")]
    public class ComposeStarBuildPropertyPage : SettingsPage
    {
        
        internal enum ConfigurationPropertyPageTag
        {
            DebugLevel,
            WeaveDebugLevel,
            VerifyIL,
			FILTHOutput,
        }
        
        #region fields
        private DebugLevel debugLevel = DebugLevel.Information;
        private WeaveDebug weaveDebugLevel = WeaveDebug.None;
        private bool verifyIL = true;
		private bool filthOutput = false;
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

            if (!String.IsNullOrEmpty(dlevel))
            {
                try
                {
                    this.debugLevel = (DebugLevel)Enum.Parse(typeof(DebugLevel), dlevel);
                }
                catch
                { } //Should only fail if project file is corrupt
            }

            string wlevel = this.GetConfigProperty(ConfigurationPropertyPageTag.WeaveDebugLevel.ToString());

            if (!string.IsNullOrEmpty(wlevel))
            {
                try
                {
                    this.weaveDebugLevel = (WeaveDebug)Enum.Parse(typeof(WeaveDebug), wlevel);
                }
                catch
                { } //Should only fail if project file is corrupt
            }

            bool verifyILTemp = false;
            if (Boolean.TryParse(this.GetConfigProperty(ConfigurationPropertyPageTag.VerifyIL.ToString()), out verifyILTemp ))
                verifyIL = verifyILTemp;

			bool filthOutputTemp = false;
			if (Boolean.TryParse(this.GetConfigProperty(ConfigurationPropertyPageTag.FILTHOutput.ToString()), out filthOutputTemp))
				filthOutput = filthOutputTemp;
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

            SetConfigProperty(ConfigurationPropertyPageTag.DebugLevel.ToString(), this.debugLevel.ToString());
            SetConfigProperty(ConfigurationPropertyPageTag.WeaveDebugLevel.ToString(), this.weaveDebugLevel.ToString());
            SetConfigProperty(ConfigurationPropertyPageTag.VerifyIL.ToString(), verifyIL.ToString() );
			SetConfigProperty(ConfigurationPropertyPageTag.FILTHOutput.ToString(), filthOutput.ToString());

            this.IsDirty = false;

            return VSConstants.S_OK;
        }
        #endregion

        #region exposed properties

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.OutputType"]/*' />
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.WeaveDebugLevel)]
        [SRDescriptionAttribute(SR.WeaveDebugLevelDescription)]
        public WeaveDebug WeaveDebugLevel
        {
            get { return this.weaveDebugLevel ; }
            set { this.weaveDebugLevel = value; this.IsDirty = true; }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.OutputType"]/*' />
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.DebugLevel)]
        [SRDescriptionAttribute(SR.DebugLevelDescription)]
        public DebugLevel DebugLevel
        {
            get { return this.debugLevel; }
            set { this.debugLevel = value; this.IsDirty = true; }
        }
            
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.VerifyIL)]
        [SRDescriptionAttribute(SR.VerifyILDescription)]
        public bool VerifyIL
        {
            get { return this.verifyIL; }
            set { this.verifyIL = value; this.IsDirty = true; }
        }

		[SRCategoryAttribute(SR.Application)]
		[LocDisplayName(SR.FilthOutput)]
		[SRDescriptionAttribute(SR.FilthOutputDescription)]
		public bool FILTHOutout
		{
			get { return this.filthOutput ; }
			set { this.filthOutput = value; this.IsDirty = true; }
		}
       #endregion

    }
}
