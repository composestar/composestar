using System;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;

using Microsoft.VisualStudio;
using OleConstants = Microsoft.VisualStudio.OLE.Interop.Constants;
using IOleServiceProvider = Microsoft.VisualStudio.OLE.Interop.IServiceProvider;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Package.Automation;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using VsCommands2K = Microsoft.VisualStudio.VSConstants.VSStd2KCmdID;
using VsCommands = Microsoft.VisualStudio.VSConstants.VSStd97CmdID;
using Microsoft.Win32;

using EnvDTE;

namespace Composestar.StarLight.VisualStudio.Project
{
    class ComposeStarFileNode : FileNode
    {
        #region fields
        private OAVSProjectItem vsProjectItem;
        private SelectionElementValueChangedListener selectionChangedListener;
        #endregion

        #region properties
        /// <summary>
        /// Returns bool indicating whether this node is of subtype "Form"
        /// </summary>
        public bool IsFormSubType
        {
            get
            {
                return false;
            }
        }
        /// <summary>
        /// Returns the SubType of a Concern FileNode. 
        /// </summary>
        public string SubType
        {
            get
            {
                return this.ItemNode.GetMetadata(ProjectFileConstants.SubType);
            }
            set
            {
                this.ItemNode.SetMetadata(ProjectFileConstants.SubType, value);
            }
        }

        protected internal VSLangProj.VSProjectItem VSProjectItem
        {
            get
            {
                if (null == vsProjectItem)
                {
                    vsProjectItem = new OAVSProjectItem(this);
                }
                return vsProjectItem;
            }
        }
        #endregion

        #region ctors
        internal ComposeStarFileNode(ProjectNode root, ProjectElement e)
            : base(root, e)
        {
            selectionChangedListener = new SelectionElementValueChangedListener(new ServiceProvider((IOleServiceProvider)root.GetService(typeof(IOleServiceProvider))), root);
            selectionChangedListener.Init();
        }
        #endregion

        #region overridden properties

        internal override object Object
        {
            get
            {
                return this.VSProjectItem;
            }
        }
        #endregion

        #region overridden methods

        public override int Close()
        {
            if (selectionChangedListener != null)
                selectionChangedListener.Dispose();
            return base.Close();
        }

        /// <summary>
        /// Returs an StarLight FileNode specific object implmenting DTE.ProjectItem
        /// </summary>
        /// <returns></returns>
        public override object GetAutomationObject()
        {
            return new OAComposeStarFileItem(this.ProjectMgr.GetAutomationObject() as OAProject, this);
        }

        public override object GetIconHandle(bool open)
        {
            if (IsFormSubType)
                return PackageUtilities.GetIntPointerFromImage(this.ProjectMgr.ImageList.Images[(int)ProjectNode.ImageName.WindowsForm]);
            if (this.FileName.ToLower().EndsWith(".cps"))
                return PackageUtilities.GetIntPointerFromImage(ComposeStarProjectNode.ComposeStarImageList.Images[(int)ComposeStarProjectNode.ComposeStarImageName.cpsFile]);
            return base.GetIconHandle(open); 
        }

        /// <summary>
        /// Open a file depending on the SubType property associated with the file item in the project file
        /// </summary>
        protected override void DoDefaultAction()
        {
            FileDocumentManager manager = this.GetDocumentManager() as FileDocumentManager;
        
            Debug.Assert(manager != null, "Could not get the FileDocumentManager");

            Guid viewGuid = (IsFormSubType ? NativeMethods.LOGVIEWID_Designer : NativeMethods.LOGVIEWID_Code);
            IVsWindowFrame frame;
            manager.Open(false, false, viewGuid, out frame, WindowFrameShowAction.Show);
        }

        protected override int ExecCommandOnNode(Guid guidCmdGroup, uint cmd, uint nCmdexecopt, IntPtr pvaIn, IntPtr pvaOut)
        {           
            Debug.Assert(this.ProjectMgr != null, "The ComposeStarFileNode has no project manager");

            if (this.ProjectMgr == null)
            {
                throw new InvalidOperationException();
            }

            if (guidCmdGroup == ComposeStarMenus.guidComposeStarProjectCmdSet)
            {
                if (cmd == (uint)ComposeStarMenus.SetAsMain.ID)
                {
                    // Set the MainFile project property to the Filename of this Node
                    ((ComposeStarProjectNode)this.ProjectMgr).SetProjectProperty(ComposeStarProjectFileConstants.MainFile, this.GetRelativePath());
                    return VSConstants.S_OK;
                }
            }
            else if (guidCmdGroup == Microsoft.VisualStudio.Package.VsMenus.guidStandardCommandSet2K)
            {
                switch ((VsCommands2K)cmd)
                {
                    case VsCommands2K.EXCLUDEFROMPROJECT:
                        return this.ExcludeFromProject();
                }
            }
            else if (guidCmdGroup == Microsoft.VisualStudio.Shell.VsMenus.guidStandardCommandSet97)
            {
                switch ((VsCommands)cmd)
                {
                    case VsCommands.Delete:
                        this.ProjectMgr.DeleteItem(0, this.ID);
                        return VSConstants.S_OK;  
                } // switch
            } // if

            return base.ExecCommandOnNode(guidCmdGroup, cmd, nCmdexecopt, pvaIn, pvaOut);
        }

        /// <summary>
        /// Handles the menuitems
        /// </summary>		
        protected override int QueryStatusOnNode(Guid guidCmdGroup, uint cmd, IntPtr pCmdText, ref QueryStatusResult result)
        {
            if (guidCmdGroup == Microsoft.VisualStudio.Shell.VsMenus.guidStandardCommandSet97)
            {
                switch ((VsCommands)cmd)
                {
                    case VsCommands.OpenWith:
                    case VsCommands.Open: 
                    case VsCommands.OpenProjectItem:
                    case VsCommands.Rename: 
                    case VsCommands.Delete: 
                    case VsCommands.AddNewItem:
                    case VsCommands.AddExistingItem:
                    case VsCommands.ViewCode:
                        result |= QueryStatusResult.SUPPORTED | QueryStatusResult.ENABLED;
                        return VSConstants.S_OK;
                    case VsCommands.ViewForm:
                        if (IsFormSubType)
                            result |= QueryStatusResult.SUPPORTED | QueryStatusResult.ENABLED;
                        return VSConstants.S_OK;
                }
            }

            else if (guidCmdGroup == ComposeStarMenus.guidComposeStarProjectCmdSet)
            {
                if (cmd == (uint)ComposeStarMenus.SetAsMain.ID)
                {
                    result |= QueryStatusResult.NOTSUPPORTED;
                    return VSConstants.S_OK;
                }
            }
            return base.QueryStatusOnNode(guidCmdGroup, cmd, pCmdText, ref result);
        }

        #endregion
        
        #region helper methods
        protected string GetRelativePath()
        {
            string relativePath = Path.GetFileName(this.ItemNode.GetMetadata(ProjectFileConstants.Include));
            HierarchyNode parent = this.Parent;
            while (parent != null && !(parent is ProjectNode))
            {
                relativePath = Path.Combine(parent.Caption, relativePath);
                parent = parent.Parent;
            }
            return relativePath;
        }
        #endregion
    }
}
