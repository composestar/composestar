using System;
using System.ComponentModel.Design;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

using Microsoft.VisualStudio;
using IOleServiceProvider = Microsoft.VisualStudio.OLE.Interop.IServiceProvider;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Shell;
using Shell = Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.Win32;

using Composestar.StarLight.VisualStudio.LanguageServices;

using EnvDTE;

namespace Composestar.StarLight.VisualStudio.Project
{

    [DefaultRegistryRoot("Software\\Microsoft\\VisualStudio\\8.0Exp")]
    [ProvideProjectFactory(typeof(ComposeStarProjectFactory), "StarLight", "StarLight Project Files (*.cpsproj);*.cpsproj", "cpsproj", "cpsproj", ".\\NullPath", LanguageVsTemplate = "StarLight")]
    [SingleFileGeneratorSupportRegistrationAttribute(typeof(ComposeStarProjectFactory))]
    [ProvideObject(typeof(GeneralPropertyPage))]
    [ProvideObject(typeof(ComposeStarBuildPropertyPage))]
    [ProvideMenuResource(1000, 1)]
    [ProvideEditorExtensionAttribute(typeof(EditorFactory), ".cps", 32)]
    [ProvideEditorLogicalView(typeof(EditorFactory), "{7651a702-06e5-11d1-8ebd-00a0c90f26ea}")]  //LOGVIEWID_Designer
    [ProvideEditorLogicalView(typeof(EditorFactory), "{7651a701-06e5-11d1-8ebd-00a0c90f26ea}")]  //LOGVIEWID_Code
    [PackageRegistration(UseManagedResourcesOnly = true)]
    [ProvideLoadKey("standard", "8.0", "ComposeStar StarLight", "University of Twente", 150)]	
    [InstalledProductRegistration(true, null, null, null)]
    [Guid("9D31CB73-40A7-4dcc-8C01-BBFBBB66001C")]
    public class ComposeStarProjectPackage : ProjectPackage, IVsInstalledProduct
    {
        protected override void Initialize()
        {
            base.Initialize();
            this.RegisterProjectFactory(new ComposeStarProjectFactory(this));
            this.RegisterEditorFactory(new EditorFactory(this));
        }

        #region IVsInstalledProduct Members
		/// <summary>
		/// This method is called during Devenv /Setup to get the bitmap to
		/// display on the splash screen for this package.
		/// </summary>
		/// <param name="pIdBmp">The resource id corresponding to the bitmap to display on the splash screen</param>
		/// <returns>HRESULT, indicating success or failure</returns>
		public int IdBmpSplash(out uint pIdBmp)
		{
			pIdBmp = 300;

			return VSConstants.S_OK;
		}

		/// <summary>
		/// This method is called to get the icon that will be displayed in the
		/// Help About dialog when this package is selected.
		/// </summary>
		/// <param name="pIdIco">The resource id corresponding to the icon to display on the Help About dialog</param>
		/// <returns>HRESULT, indicating success or failure</returns>
		public int IdIcoLogoForAboutbox(out uint pIdIco)
		{
			pIdIco = 400;

			return VSConstants.S_OK;
		}

	    /// <summary>
        /// This methods provides the product official name, it will be
        /// displayed in the help about dialog.
        /// </summary>
        /// <param name="pbstrName">Out parameter to which to assign the product name</param>
        /// <returns>HRESULT, indicating success or failure</returns>
        public int OfficialName(out string pbstrName)
        {
            pbstrName = GetResourceString("@ProductName");
            return VSConstants.S_OK;
        }

        /// <summary>
        /// This methods provides the product description, it will be
        /// displayed in the help about dialog.
        /// </summary>
        /// <param name="pbstrProductDetails">Out parameter to which to assign the description of the package</param>
        /// <returns>HRESULT, indicating success or failure</returns>
        public int ProductDetails(out string pbstrProductDetails)
        {
            pbstrProductDetails = GetResourceString("@ProductDetails");
            return VSConstants.S_OK;
        }

        /// <summary>
        /// This methods provides the product version, it will be
        /// displayed in the help about dialog.
        /// </summary>
        /// <param name="pbstrPID">Out parameter to which to assign the version number</param>
        /// <returns>HRESULT, indicating success or failure</returns>
        public int ProductID(out string pbstrPID)
        {
            pbstrPID = GetResourceString("@ProductID");
            return VSConstants.S_OK;
        }

		#endregion

		/// <summary>
		/// This method loads a localized string based on the specified resource.
		/// </summary>
		/// <param name="resourceName">Resource to load</param>
		/// <returns>String loaded for the specified resource</returns>
		public string GetResourceString(string resourceName)
		{
			string resourceValue;
			IVsResourceManager resourceManager = (IVsResourceManager)GetService(typeof(SVsResourceManager));
			if (resourceManager == null)
			{
				throw new InvalidOperationException("Could not get SVsResourceManager service. Make sure the package is Sited before calling this method");
			}
			Guid packageGuid = this.GetType().GUID;
			int hr = resourceManager.LoadResourceString(ref packageGuid, -1, resourceName, out resourceValue);
			Microsoft.VisualStudio.ErrorHandler.ThrowOnFailure(hr);
			return resourceValue;
		}
    }


    [GuidAttribute(ComposeStarProjectFactory.ComposeStarProjectFactoryGuid)]
    public class ComposeStarProjectFactory : Microsoft.VisualStudio.Package.ProjectFactory
    {
        public const string ComposeStarProjectFactoryGuid = "D0A7707A-04E2-4aa9-A44E-89783E5BD8A8";

        #region ctor
        public ComposeStarProjectFactory(ComposeStarProjectPackage package)
            : base(package)
        {

        }
        #endregion

        #region overridden methods
        protected override Microsoft.VisualStudio.Package.ProjectNode CreateProject()
        {
            ComposeStarProjectNode project = new ComposeStarProjectNode(this.Package as ComposeStarProjectPackage);
            project.SetSite((IOleServiceProvider)((IServiceProvider)this.Package).GetService(typeof(IOleServiceProvider)));
            return project;
        }
        #endregion
    }

    [Guid("2A5A7A6D-34A5-408a-9924-4A23E581F55C")]
    public class ComposeStarProjectNode : Microsoft.VisualStudio.Package.ProjectNode, IVsProjectSpecificEditorMap2 //, IVsFilterAddProjectItemDlg2 
    {
        #region fields
        private ComposeStarProjectPackage package;
        private Guid GUID_MruPage = new Guid("{19B97F03-9594-4c1c-BE28-25FF030113B3}");
        private VSLangProj.VSProject vsProject = null;
        private Microsoft.VisualStudio.Designer.Interfaces.IVSMDCodeDomProvider codeDomProvider;
        private static ImageList composeStarImageList;
        #endregion

        #region IVsFilterAddProjectItemDlg2

        //public int FilterListItemByCategory([InAttribute] ref Guid rguidProjectItemTemplates, [InAttribute] string pszCategoryName, out int pfFilter)
        //{
        //    pfFilter = false;
        //    return VSConstants.S_OK; 
        //}

        //int FilterListItemByLocalizedName (    [InAttribute] ref Guid rguidProjectItemTemplates,    [InAttribute] string pszLocalizedName,    out int pfFilter)
        //{
        //        pfFilter = false;
        //    return VSConstants.S_OK; 
        
        //}



        #endregion


        #region enums

        public enum ComposeStarImageName
        {
            cpsFile = 0,
            cpsProject = 1,
        }


        #endregion

        #region Properties
        /// <summary>
        /// Returns the outputfilename based on the output type
        /// </summary>
        public string OutputFileName
        {
            get
            {
                string assemblyName = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.AssemblyName.ToString(), true);

                string outputTypeAsString = this.ProjectMgr.GetProjectProperty(GeneralPropertyPageTag.OutputType.ToString(), false);
                OutputType outputType = (OutputType)Enum.Parse(typeof(OutputType), outputTypeAsString);

                return assemblyName + GetOuputExtension(outputType);
                
            }
        }
        /// <summary>
        /// Retreive the CodeDOM provider
        /// </summary>
        protected internal Microsoft.VisualStudio.Designer.Interfaces.IVSMDCodeDomProvider CodeDomProvider
        {
            get
            {
                if (codeDomProvider == null)
                    codeDomProvider = new VSMDComposeStarProvider(this.VSProject);
                return codeDomProvider;
            }
        }
        /// <summary>
        /// Get the VSProject corresponding to this project
        /// </summary>
        protected internal VSLangProj.VSProject VSProject
        {
            get
            {
                if (vsProject == null)
                    vsProject = new Microsoft.VisualStudio.Package.Automation.OAVSProject(this);
                return vsProject;
            }
        }
        private IVsHierarchy InteropSafeHierarchy
        {
            get
            {
                IntPtr unknownPtr = Utilities.QueryInterfaceIUnknown(this);
                if (IntPtr.Zero == unknownPtr)
                {
                    return null;
                }
                IVsHierarchy hier = Marshal.GetObjectForIUnknown(unknownPtr) as IVsHierarchy;
                return hier;
            }
        }

        /// <summary>
        /// ComposeStar specific project images
        /// </summary>
        public static ImageList ComposeStarImageList
        {
            get
            {
                return composeStarImageList;
            }
            set
            {
                composeStarImageList = value;
            }
        }
        #endregion

        #region ctor

        static ComposeStarProjectNode()
        {
            ComposeStarImageList = Utilities.GetImageList(typeof(ComposeStarProjectNode).Assembly.GetManifestResourceStream("Composestar.StarLight.VisualStudio.Project.Resources.StarLightImageList.bmp"));
        }

        public ComposeStarProjectNode(ComposeStarProjectPackage pkg)
        {
            this.package = pkg;
            this.NodeProperties = new ComposeStarProjectNodeProperties(this);
            this.CanFileNodesHaveChilds = true;
            this.OleServiceProvider.AddService(typeof(VSLangProj.VSProject), new OleServiceProvider.ServiceCreatorCallback(this.CreateServices), false);
            this.SupportsProjectDesigner = true;

            InitializeCATIDs();
        }

        public override object GetIconHandle(bool open)
        {
            return PackageUtilities.GetIntPointerFromImage(ComposeStarImageList.Images[(int)ComposeStarImageName.cpsProject]);
        }     

        /// <summary>
        /// Provide mapping from our browse objects and automation objects to our CATIDs
        /// </summary>
        private void InitializeCATIDs()
        {
            // The following properties classes are specific to python so we can use their GUIDs directly
            this.AddCATIDMapping(typeof(ComposeStarProjectNodeProperties), typeof(ComposeStarProjectNodeProperties).GUID);
            this.AddCATIDMapping(typeof(ComposeStarFileNodeProperties), typeof(ComposeStarFileNodeProperties).GUID);
            this.AddCATIDMapping(typeof(OAComposeStarFileItem), typeof(OAComposeStarFileItem).GUID);
            // The following are not specific to ComposeStar and as such we need a separate GUID (we simply used guidgen.exe to create new guids)
            this.AddCATIDMapping(typeof(FolderNodeProperties), new Guid("91ACC179-D77D-4624-8325-C2ABA2B3987D"));
            // This one we use the same as python file nodes since both refer to files
            this.AddCATIDMapping(typeof(FileNodeProperties), typeof(ComposeStarFileNodeProperties).GUID);
            // Because our property page pass itself as the object to display in its grid, we need to make it have the same CATID
            // as the browse object of the project node so that filtering is possible.
            this.AddCATIDMapping(typeof(GeneralPropertyPage), typeof(ComposeStarProjectNodeProperties).GUID);
            this.AddCATIDMapping(typeof(ComposeStarBuildPropertyPage), typeof(ComposeStarProjectNodeProperties).GUID);

            // We could also provide CATIDs for references and the references container node, if we wanted to.
        }

        #endregion

        #region overridden properties

        /// <summary>
        /// Return -1 from the ImageIndex so that VS will use the result from 
        /// GetIconHandle() instead
        /// </summary>
        public override int ImageIndex
        {
            get
            {
                return -1;
            }
        }

        public override Guid ProjectGuid
        {
            get
            {
                return typeof(ComposeStarProjectFactory).GUID;
            }
        }
        public override string ProjectType
        {
            get
            {
                return "StarLight";
            }
        }
        internal override object Object
        {
            get
            {
                return this.VSProject;
            }
        }
        #endregion

        #region overridden methods
        public override int Close()
        {
            if (null != Site)
            {
                IComposeStarLibraryManager libraryManager = Site.GetService(typeof(IComposeStarLibraryManager)) as IComposeStarLibraryManager;
                if (null != libraryManager)
                {
                    libraryManager.UnregisterHierarchy(this.InteropSafeHierarchy);
                }
            }

            return base.Close();
        }
        public override void Load(string filename, string location, string name, uint flags, ref Guid iidProject, out int canceled)
        {
            base.Load(filename, location, name, flags, ref iidProject, out canceled);
            // WAP ask the designer service for the CodeDomProvider corresponding to the project node.
            this.OleServiceProvider.AddService(typeof(SVSMDCodeDomProvider), new OleServiceProvider.ServiceCreatorCallback(this.CreateServices), false);
            this.OleServiceProvider.AddService(typeof(System.CodeDom.Compiler.CodeDomProvider), new OleServiceProvider.ServiceCreatorCallback(this.CreateServices), false);

            IComposeStarLibraryManager libraryManager = Site.GetService(typeof(IComposeStarLibraryManager)) as IComposeStarLibraryManager;
            if (null != libraryManager)
            {
                libraryManager.RegisterHierarchy(this.InteropSafeHierarchy);
            }
        }
        /// <summary>
        /// Overriding to provide project general property page
        /// </summary>
        /// <returns></returns>
        protected override Guid[] GetConfigurationIndependentPropertyPages()
        {
            Guid[] result = new Guid[1];
            result[0] = typeof(GeneralPropertyPage).GUID;
            return result;
        }

        /// <summary>
        /// Returns the configuration dependent property pages.
        /// Specify here a property page. By returning no property page the configuartion dependent properties will be neglected.
        /// Overriding, but current implementation does nothing
        /// To provide configuration specific page project property page, this should return an array bigger then 0
        /// (you can make it do the same as GetPropertyPageGuids() to see its impact)
        /// </summary>
        /// <param name="config"></param>
        /// <returns></returns>
        protected override Guid[] GetConfigurationDependentPropertyPages()
        {
            Guid[] result = new Guid[1];
            result[0] = typeof(ComposeStarBuildPropertyPage).GUID;
            return result;
        }

        /// <summary>
        /// Overriding to provide customization of files on add files.
        /// This will replace tokens in the file with actual value (namespace, class name,...)
        /// </summary>
        /// <param name="source">Full path to template file</param>
        /// <param name="target">Full path to destination file</param>
        public override void AddFileFromTemplate(string source, string target)
        {
            if (!System.IO.File.Exists(source))
                throw new FileNotFoundException(String.Format("Template file not found: {0}", source));

            // The class name is based on the new file name
            string className = Path.GetFileNameWithoutExtension(target);
            string namespce = this.FileTemplateProcessor.GetFileNamespace(target, this);

            this.FileTemplateProcessor.AddReplace("%className%", className);
            this.FileTemplateProcessor.AddReplace("%namespace%", namespce);
            try
            {
                this.FileTemplateProcessor.UntokenFile(source, target);
            }
            catch (Exception e)
            {
                throw new FileLoadException("Failed to add template file to project", target, e);
            }
        }
        /// <summary>
        /// Evaluates if a file is an Concern code file based on is extension
        /// </summary>
        /// <param name="strFileName">The filename to be evaluated</param>
        /// <returns>true if is a code file</returns>
        public override bool IsCodeFile(string strFileName)
        {
            // We do not want to assert here, just return silently.
            if (String.IsNullOrEmpty(strFileName))
            {
                return false;
            }
            return (String.Compare(Path.GetExtension(strFileName), ".cps", StringComparison.OrdinalIgnoreCase) == 0);

        }

        /// <summary>
        /// Create a file node based on an msbuild item.
        /// </summary>
        /// <param name="item">The msbuild item to be analyzed</param>
        /// <returns>ComposeStarFileNode or FileNode</returns>
        public override FileNode CreateFileNode(ProjectElement item)
        {
            if (item == null)
            {
                throw new ArgumentNullException("item");
            }

            string include = item.GetMetadata(ProjectFileConstants.Include);
            ComposeStarFileNode newNode = new ComposeStarFileNode(this, item);
            newNode.OleServiceProvider.AddService(typeof(EnvDTE.Project), this.ProjectMgr.GetAutomationObject(), false);
            newNode.OleServiceProvider.AddService(typeof(EnvDTE.ProjectItem), newNode.GetAutomationObject(), false);
            newNode.OleServiceProvider.AddService(typeof(VSLangProj.VSProject), new OleServiceProvider.ServiceCreatorCallback(this.CreateServices), false);
            if (IsCodeFile(include))
            {
                newNode.OleServiceProvider.AddService(
                    typeof(SVSMDCodeDomProvider), new OleServiceProvider.ServiceCreatorCallback(this.CreateServices), false);
            }

            return newNode;
        }

        /// <summary>
        /// Creates the format list for the open file dialog
        /// </summary>
        /// <param name="formatlist">The formatlist to return</param>
        /// <returns>Success</returns>
        public override int GetFormatList(out string formatlist)
        {
            formatlist = String.Format(CultureInfo.CurrentCulture, SR.GetString(SR.ProjectFileExtensionFilter), "\0", "\0");
            return VSConstants.S_OK;
        }

        /// <summary>
        /// This overrides the base class method to show the VS 2005 style Add reference dialog. The ProjectNode implementation
        /// shows the VS 2003 style Add Reference dialog.
        /// </summary>
        /// <returns>S_OK if succeeded. Failure other wise</returns>
        public override int AddProjectReference()
        {
            IVsComponentSelectorDlg2 componentDialog;
            Guid guidEmpty = Guid.Empty;
            VSCOMPONENTSELECTORTABINIT[] tabInit = new VSCOMPONENTSELECTORTABINIT[5];
            string strBrowseLocations = Path.GetDirectoryName(this.BaseURI.Uri.LocalPath);

            //Add the .NET page
            tabInit[0].dwSize = (uint)Marshal.SizeOf(typeof(VSCOMPONENTSELECTORTABINIT));
            tabInit[0].varTabInitInfo = 0;
            tabInit[0].guidTab = VSConstants.GUID_COMPlusPage;

            //Add the COM page
            tabInit[1].dwSize = (uint)Marshal.SizeOf(typeof(VSCOMPONENTSELECTORTABINIT));
            tabInit[1].varTabInitInfo = 0;
            tabInit[1].guidTab = VSConstants.GUID_COMClassicPage;

            //Add the Project page
            tabInit[2].dwSize = (uint)Marshal.SizeOf(typeof(VSCOMPONENTSELECTORTABINIT));
            // Tell the Add Reference dialog to call hierarchies GetProperty with the following
            // propID to enablefiltering out ourself from the Project to Project reference
            tabInit[2].varTabInitInfo = (int)__VSHPROPID.VSHPROPID_ShowProjInSolutionPage;
            tabInit[2].guidTab = VSConstants.GUID_SolutionPage;

            // Add the Browse page			
            tabInit[3].dwSize = (uint)Marshal.SizeOf(typeof(VSCOMPONENTSELECTORTABINIT));
            tabInit[3].guidTab = NativeMethods.GUID_BrowseFilePage;
            tabInit[3].varTabInitInfo = 0;

            //// Add the Recent page			
            tabInit[4].dwSize = (uint)Marshal.SizeOf(typeof(VSCOMPONENTSELECTORTABINIT));
            tabInit[4].guidTab = GUID_MruPage;
            tabInit[4].varTabInitInfo = 0;

            uint pX = 0, pY = 0;


            componentDialog = this.GetService(typeof(SVsComponentSelectorDlg)) as IVsComponentSelectorDlg2;
            try
            {
                // call the container to open the add reference dialog.
                if (componentDialog != null)
                {
                    // Let the project know not to show itself in the Add Project Reference Dialog page
                    this.ShowProjectInSolutionPage = false;

                    // call the container to open the add reference dialog.
                    ErrorHandler.ThrowOnFailure(componentDialog.ComponentSelectorDlg2(
                        (System.UInt32)(__VSCOMPSELFLAGS.VSCOMSEL_MultiSelectMode | __VSCOMPSELFLAGS.VSCOMSEL_IgnoreMachineName),
                        (IVsComponentUser)this,
                        0,
                        null,
                Microsoft.VisualStudio.Package.SR.GetString(Microsoft.VisualStudio.Package.SR.AddReferenceDialogTitle),   // Title
                        "VS.AddReference",						  // Help topic
                        ref pX,
                        ref pY,
                        (uint)tabInit.Length,
                        tabInit,
                        ref guidEmpty,
                        "*.dll",
                        ref strBrowseLocations));
                }
            }
            catch (COMException e)
            {
                Trace.WriteLine("Exception : " + e.Message);
                return e.ErrorCode;
            }
            finally
            {
                // Let the project know it can show itself in the Add Project Reference Dialog page
                this.ShowProjectInSolutionPage = true;
            }
            return VSConstants.S_OK;
        }

        protected override ConfigProvider CreateConfigProvider()
        {
            return new ComposeStarConfigProvider(this);
        }

        #endregion

        #region Methods
        /// <summary>
        /// Creates the services exposed by this project.
        /// </summary>
        private object CreateServices(Type serviceType)
        {
            object service = null;
            if (typeof(SVSMDCodeDomProvider) == serviceType)
            {
                service = this.CodeDomProvider;
            }
            else if (typeof(System.CodeDom.Compiler.CodeDomProvider) == serviceType)
            {
                service = this.CodeDomProvider.CodeDomProvider;
            }
            else if (typeof(VSLangProj.VSProject) == serviceType)
            {
                service = this.VSProject;
            }
            return service;
        }
        #endregion


        #region IVsProjectSpecificEditorMap2 Members

        public int GetSpecificEditorProperty(string mkDocument, int propid, out object result)
        {
            // initialize output params
            result = null;

            //Validate input
            if (string.IsNullOrEmpty(mkDocument))
                throw new ArgumentException("Was null or empty", "mkDocument");

            // Make sure that the document moniker passed to us is part of this project
            // We also don't care if it is not a python file node
            uint itemid;
            ErrorHandler.ThrowOnFailure(ParseCanonicalName(mkDocument, out itemid));
            HierarchyNode hierNode = NodeFromItemId(itemid);
            if (hierNode == null || ((hierNode as ComposeStarFileNode) == null))
                return VSConstants.E_NOTIMPL;

            switch (propid)
            {
                case (int)__VSPSEPROPID.VSPSEPROPID_UseGlobalEditorByDefault:
                    // we do not want to use global editor for form files
                    result = true;
                    break;
                case (int)__VSPSEPROPID.VSPSEPROPID_ProjectDefaultEditorName:
                    result = "Form Editor";
                    break;
            }

            return VSConstants.S_OK;
        }

        public int GetSpecificEditorType(string mkDocument, out Guid guidEditorType)
        {
            // Ideally we should at this point initalize a File extension to EditorFactory guid Map e.g.
            // in the registry hive so that more editors can be added without changing this part of the
            // code. StarLight only makes usage of one Editor Factory and therefore we will return 
            // that guid
            if (IsCodeFile(mkDocument))
            {
                guidEditorType = EditorFactory.guidEditorFactory;
            }
            else
            {              
                guidEditorType = Guid.Empty; 
            }
            return VSConstants.S_OK;
        }

        public int GetSpecificLanguageService(string mkDocument, out Guid guidLanguageService)
        {
            guidLanguageService = Guid.Empty;
            return VSConstants.E_NOTIMPL;
        }

        public int SetSpecificEditorProperty(string mkDocument, int propid, object value)
        {
            return VSConstants.E_NOTIMPL;
        }

        #endregion

        #region static methods
        public static string GetOuputExtension(OutputType outputType)
        {
            if (outputType == OutputType.Library)
            {
                return "." + OutputFileExtension.dll.ToString();
            }
            else
            {
                return "." + OutputFileExtension.exe.ToString();
            }
        }
        #endregion

    }

    /// <summary>
    /// Type of outputfile extension supported by ComposeStar Project
    /// </summary>
    internal enum OutputFileExtension
    {
        exe,
        dll
    }

    /// <summary>
    /// This class defines constants being used by the ComposeStar Project File
    /// </summary>
    public static class ComposeStarProjectFileConstants
    {
        public const string MainFile = "MainFile";
    }

}
