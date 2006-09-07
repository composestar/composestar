using System;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Package;
using System.Runtime.InteropServices;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;

namespace ComposeStar.VisualStudio.Project
{
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Property | AttributeTargets.Field, Inherited = false, AllowMultiple = false)]
    internal sealed class LocDisplayNameAttribute : DisplayNameAttribute
    {
        private string name;

        public LocDisplayNameAttribute(string name)
        {
            this.name = name;
        }

        public override string DisplayName
        {
            get
            {
                string result = SR.GetString(this.name);

                if (result == null)
                {
                    Debug.Assert(false, "String resource '" + this.name + "' is missing");
                    result = this.name;
                }

                return result;
            }
        }
    }

    internal enum GeneralPropertyPageTag
    {
        AssemblyName,
        OutputType,
        RootNamespace,
        StartupObject,
        ApplicationIcon,
        TargetPlatform,
        TargetPlatformLocation
    }

    /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage"]/*' />
    [ComVisible(true), Guid("0DB95424-5D14-4a38-8F4E-BBF9C9F45F0E")]
    public class GeneralPropertyPage : SettingsPage
    {
        private string assemblyName;
        private OutputType outputType;
        private string defaultNamespace;
        private string startupObject;
        private string applicationIcon;
        private PlatformType targetPlatform = PlatformType.v2;
        private string targetPlatformLocation;

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.GeneralPropertyPage"]/*' />
        public GeneralPropertyPage()
        {
            this.Name = SR.GetString(SR.GeneralCaption);
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.GetClassName"]/*' />
        public override string GetClassName()
        {
            return this.GetType().FullName;
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.BindProperties"]/*' />
        protected override void BindProperties()
        {
            try
            {

                if (this.ProjectMgr == null)
                {
                    Debug.Assert(false);
                    return;
                }

                this.assemblyName = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.AssemblyName.ToString(), true);

                string outputType = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.OutputType.ToString(), false);

                if (outputType != null && outputType.Length > 0)
                {
                    try
                    {
                        this.outputType = (OutputType)Enum.Parse(typeof(OutputType), outputType);
                    }
                    catch
                    { } //Should only fail if project file is corrupt
                }

                this.defaultNamespace = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.RootNamespace.ToString(), false);
                this.startupObject = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.StartupObject.ToString(), false);
                this.applicationIcon = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.ApplicationIcon.ToString(), false);

                string targetPlatform = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.TargetPlatform.ToString(), false);

                if (targetPlatform != null && targetPlatform.Length > 0)
                {
                    try
                    {
                        this.targetPlatform = (PlatformType)Enum.Parse(typeof(PlatformType), targetPlatform);
                    }
                    catch
                    { }
                }

                this.targetPlatformLocation = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.TargetPlatformLocation.ToString(), false);
            }
            catch (Exception ex)
            {
                System.Windows.Forms.MessageBox.Show(ex.ToString());
            }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.ApplyChanges"]/*' />
		protected override int ApplyChanges()
        {
            if (this.ProjectMgr == null)
            {
                Debug.Assert(false);
				return VSConstants.E_INVALIDARG;
            }

            this.ProjectMgr.SetProjectProperty(GeneralPropertyPageTag.AssemblyName.ToString(), this.assemblyName);
            this.ProjectMgr.SetProjectProperty(GeneralPropertyPageTag.OutputType.ToString(), this.outputType.ToString());
            this.ProjectMgr.SetProjectProperty(GeneralPropertyPageTag.RootNamespace.ToString(), this.defaultNamespace);
            this.ProjectMgr.SetProjectProperty(GeneralPropertyPageTag.StartupObject.ToString(), this.startupObject);
            this.ProjectMgr.SetProjectProperty(GeneralPropertyPageTag.ApplicationIcon.ToString(), this.applicationIcon);
            this.ProjectMgr.SetProjectProperty(GeneralPropertyPageTag.TargetPlatform.ToString(), this.targetPlatform.ToString());
            this.ProjectMgr.SetProjectProperty(GeneralPropertyPageTag.TargetPlatformLocation.ToString(), this.targetPlatformLocation);
            this.IsDirty = false;

			return VSConstants.S_OK;
		}

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.AssemblyName"]/*' />
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.AssemblyName)]
        [SRDescriptionAttribute(SR.AssemblyNameDescription)]
        public string AssemblyName
        {
            get { return this.assemblyName; }
            set { this.assemblyName = value; this.IsDirty = true; }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.OutputType"]/*' />
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.OutputType)]
        [SRDescriptionAttribute(SR.OutputTypeDescription)]
        public OutputType OutputType
        {
            get { return this.outputType; }
            set { this.outputType = value; this.IsDirty = true; }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.DefaultNamespace"]/*' />
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.DefaultNamespace)]
        [SRDescriptionAttribute(SR.DefaultNamespaceDescription)]
        public string DefaultNamespace
        {
            get { return this.defaultNamespace; }
            set { this.defaultNamespace = value; this.IsDirty = true; }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.StartupObject"]/*' />
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.StartupObject)]
        [SRDescriptionAttribute(SR.StartupObjectDescription)]
        public string StartupObject
        {
            get { return this.startupObject; }
            set { this.startupObject = value; this.IsDirty = true; }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.ApplicationIcon"]/*' />
        [SRCategoryAttribute(SR.Application)]
        [LocDisplayName(SR.ApplicationIcon)]
        [SRDescriptionAttribute(SR.ApplicationIconDescription)]
        public string ApplicationIcon
        {
            get { return this.applicationIcon; }
            set { this.applicationIcon = value; this.IsDirty = true; }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.ProjectFile"]/*' />
        [SRCategoryAttribute(SR.Project)]
        [LocDisplayName(SR.ProjectFile)]
        [SRDescriptionAttribute(SR.ProjectFileDescription)]
        [AutomationBrowsable(false)]
        public string ProjectFile
        {
            get { return Path.GetFileName(this.ProjectMgr.ProjectFile); }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.ProjectFolder"]/*' />
        [SRCategoryAttribute(SR.Project)]
        [LocDisplayName(SR.ProjectFolder)]
        [SRDescriptionAttribute(SR.ProjectFolderDescription)]
        [AutomationBrowsable(false)]
        public string ProjectFolder
        {
            get { return Path.GetDirectoryName(this.ProjectMgr.ProjectFolder); }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.OutputFile"]/*' />
        [SRCategoryAttribute(SR.Project)]
        [LocDisplayName(SR.OutputFile)]
        [SRDescriptionAttribute(SR.OutputFileDescription)]
        [AutomationBrowsable(false)]
        public string OutputFile
        {
            get
            {
                return this.assemblyName + ComposeStarProjectNode.GetOuputExtension(this.outputType);
            }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.TargetPlatform"]/*' />
        [SRCategoryAttribute(SR.Project)]
        [LocDisplayName(SR.TargetPlatform)]
        [SRDescriptionAttribute(SR.TargetPlatformDescription)]
        [AutomationBrowsable(false)]
        public PlatformType TargetPlatform
        {
            get { return this.targetPlatform; }
            set { this.targetPlatform = value; IsDirty = true; }
        }

        /// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.TargetPlatformLocation"]/*' />
        [SRCategoryAttribute(SR.Project)]
        [LocDisplayName(SR.TargetPlatformLocation)]
        [SRDescriptionAttribute(SR.TargetPlatformLocationDescription)]
        [AutomationBrowsable(false)]
        public string TargetPlatformLocation
        {
            get { return this.targetPlatformLocation; }
            set { this.targetPlatformLocation = value; IsDirty = true; }
        }
    }
}
