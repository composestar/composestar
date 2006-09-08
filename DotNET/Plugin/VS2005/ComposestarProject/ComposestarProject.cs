using System;
using System.Diagnostics;
using System.Globalization;
using System.Runtime.InteropServices;
using System.ComponentModel.Design;

using Microsoft.Win32;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.Package;

using IOleServiceProvider = Microsoft.VisualStudio.OLE.Interop.IServiceProvider;

namespace Trese.ComposestarProject
{
	[PackageRegistration(UseManagedResourcesOnly = true)]
	[DefaultRegistryRoot("Software\\Microsoft\\VisualStudio\\8.0")]
	[ProvideProjectFactory(typeof(ComposestarProjectFactory), "Composestar", "Compose* Project Files (*.cpsproj);*.cpsproj", "cpsproj", "cpsproj", null, LanguageVsTemplate = "Composestar")]
	[Guid(GuidList.guidComposestarProjectPkgString)]
	public sealed class ComposestarProjectPackage : ProjectPackage
	{
		public ComposestarProjectPackage()
		{
		}

		protected override void Initialize()
		{
			base.Initialize();
			RegisterProjectFactory(new ComposestarProjectFactory(this));
		}

		[Guid("C09DB752-2D24-4A23-85F1-8221583E0CF1")]
		public class ComposestarProjectFactory : ProjectFactory
		{
			public ComposestarProjectFactory(ComposestarProjectPackage package)
				: base(package)
			{
			}

			protected override ProjectNode CreateProject()
			{
				ComposestarProjectNode node = new ComposestarProjectNode(this.Package as ComposestarProjectPackage);
				node.SetSite((IOleServiceProvider)((IServiceProvider)this.Package).GetService(typeof(IOleServiceProvider)));
				return node;
			}
		}

		[Guid("2B4C33EB-9080-4AC4-B2EE-597152B38695")]
		public class ComposestarProjectNode : ProjectNode, IVsProjectSpecificEditorMap2
		{
			public ComposestarProjectNode(ComposestarProjectPackage package)
			{
			}

			#region IVsProjectSpecificEditorMap2 Members

			public int GetSpecificEditorProperty(string pszMkDocument, int propid, out object pvar)
			{
				throw new Exception("The method or operation is not implemented.");
			}

			public int GetSpecificEditorType(string pszMkDocument, out Guid pguidEditorType)
			{
				throw new Exception("The method or operation is not implemented.");
			}

			public int GetSpecificLanguageService(string pszMkDocument, out Guid pguidLanguageService)
			{
				throw new Exception("The method or operation is not implemented.");
			}

			public int SetSpecificEditorProperty(string pszMkDocument, int propid, object var)
			{
				throw new Exception("The method or operation is not implemented.");
			}

			#endregion

			public override Guid ProjectGuid
			{
				get { return typeof(ComposestarProjectFactory).GUID; }
			}

			public override string ProjectType
			{
				get { return "Composestar"; }
			}
		}
	}
}