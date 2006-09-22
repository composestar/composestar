using System;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Package;
using System.Runtime.InteropServices;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;

namespace Composestar.StarLight.VisualStudio.Project
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
		RepositoryFilename,
	}


	/// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage"]/*' />
	[ComVisible(true), Guid("C30AC387-23B0-4488-AF5E-C31A9DAC58A6")]
	public class GeneralPropertyPage : SettingsPage, EnvDTE80.IInternalExtenderProvider
	{
		#region fields
		private string repositoryFilename = @"obj\starlight.yap";
		#endregion

		/// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.GeneralPropertyPage"]/*' />
		public GeneralPropertyPage()
		{
			this.Name = SR.GetString(SR.GeneralCaption);
		}

		#region overriden methods
		/// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.GetClassName"]/*' />
		public override string GetClassName()
		{
			return this.GetType().FullName;
		}

		/// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.BindProperties"]/*' />
		protected override void BindProperties()
		{
			if (this.ProjectMgr == null)
			{
				Debug.Assert(false);
				return;
			}

			this.repositoryFilename  = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.RepositoryFilename.ToString(), true);
          
      

		}

		/// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.ApplyChanges"]/*' />
		protected override int ApplyChanges()
		{
			if (this.ProjectMgr == null)
			{
				Debug.Assert(false);
				return VSConstants.E_INVALIDARG;
			}

			this.ProjectMgr.SetProjectProperty(GeneralPropertyPageTag.RepositoryFilename.ToString(), this.repositoryFilename);
	        
			this.IsDirty = false;

			return VSConstants.S_OK;
		}
		#endregion

		#region exposed properties
		/// <include file='doc\PropertyPages.uex' path='docs/doc[@for="GeneralPropertyPage.AssemblyName"]/*' />
		[SRCategoryAttribute(SR.Application)]
		[LocDisplayName(SR.RepositoryFilename)]
		[SRDescriptionAttribute(SR.RepositoryFilenameDescription)]
		public string RepositoryFilename
		{
			get { return this.repositoryFilename ; }
			set { this.repositoryFilename = value; this.IsDirty = true; }
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

		#endregion

		#region IInternalExtenderProvider Members

		bool EnvDTE80.IInternalExtenderProvider.CanExtend(string extenderCATID, string extenderName, object extendeeObject)
		{
			IVsHierarchy outerHierarchy = HierarchyNode.GetOuterHierarchy(this.ProjectMgr);
			if (outerHierarchy is EnvDTE80.IInternalExtenderProvider)
				return ((EnvDTE80.IInternalExtenderProvider)outerHierarchy).CanExtend(extenderCATID, extenderName, extendeeObject);
			return false;
		}

		object EnvDTE80.IInternalExtenderProvider.GetExtender(string extenderCATID, string extenderName, object extendeeObject, EnvDTE.IExtenderSite extenderSite, int cookie)
		{
			IVsHierarchy outerHierarchy = HierarchyNode.GetOuterHierarchy(this.ProjectMgr);
			if (outerHierarchy is EnvDTE80.IInternalExtenderProvider)
				return ((EnvDTE80.IInternalExtenderProvider)outerHierarchy).GetExtender(extenderCATID, extenderName, extendeeObject, extenderSite, cookie);
			return null;
		}

		object EnvDTE80.IInternalExtenderProvider.GetExtenderNames(string extenderCATID, object extendeeObject)
		{
			IVsHierarchy outerHierarchy = HierarchyNode.GetOuterHierarchy(this.ProjectMgr);
			if (outerHierarchy is EnvDTE80.IInternalExtenderProvider)
				return ((EnvDTE80.IInternalExtenderProvider)outerHierarchy).GetExtenderNames(extenderCATID, extendeeObject);
			return null;
		}

		#endregion

		#region ExtenderSupport
		[Browsable(false)]
		[AutomationBrowsable(false)]
		public virtual string ExtenderCATID
		{
			get
			{
				Guid catid = this.ProjectMgr.ProjectMgr.GetCATIDForType(this.GetType());
				if (Guid.Empty.CompareTo(catid) == 0)
					throw new NotImplementedException();
				return catid.ToString("B");
			}
		}
		[Browsable(false)]
		[AutomationBrowsable(false)]
		public object ExtenderNames
		{
			get
			{
				EnvDTE.ObjectExtenders extenderService = (EnvDTE.ObjectExtenders)this.ProjectMgr.GetService(typeof(EnvDTE.ObjectExtenders));
				return extenderService.GetExtenderNames(this.ExtenderCATID, this);
			}
		}
		public object get_Extender(string extenderName)
		{
			EnvDTE.ObjectExtenders extenderService = (EnvDTE.ObjectExtenders)this.ProjectMgr.GetService(typeof(EnvDTE.ObjectExtenders));
			return extenderService.GetExtender(this.ExtenderCATID, extenderName, this);
		}
		#endregion

	}
}
