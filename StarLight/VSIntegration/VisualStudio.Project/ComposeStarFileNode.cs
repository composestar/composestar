
/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

using System;
using System.Text;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Runtime.InteropServices;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Package.Automation;
using Microsoft.Win32;
using EnvDTE;
using IOleServiceProvider = Microsoft.VisualStudio.OLE.Interop.IServiceProvider;
using OleConstants = Microsoft.VisualStudio.OLE.Interop.Constants;
using VsCommands = Microsoft.VisualStudio.VSConstants.VSStd97CmdID;
using VsCommands2K = Microsoft.VisualStudio.VSConstants.VSStd2KCmdID;

namespace ComposeStar.VisualStudio.Project
{
    class ComposeStarFileNode : FileNode
    {
        #region fields
        private OAVSProjectItem vsProjectItem;
        #endregion

        #region properties
        /// <summary>
        /// Returns bool indicating whether this node is of subtype "Form"
        /// </summary>
        public bool IsFormSubType
        {
            get
            {
                string result = this.ItemNode.GetMetadata(ProjectFileConstants.SubType);
                if (!String.IsNullOrEmpty(result) && string.Compare(result, ProjectFileAttributeValue.Form, true, CultureInfo.InvariantCulture) == 0)
                    return true;
                else
                    return false;
            }
        }
        /// <summary>
        /// Returns the SubType of an ComposeStar FileNode. It is 
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
            this.NodeProperties = new ComposeStarFileNodeProperties(this);
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
        /// <summary>
        /// Returs an Iron Python FileNode specific object implmenting DTE.ProjectItem
        /// </summary>
        /// <returns></returns>
        public override object GetAutomationObject()
        {
            return new OAComposeStarFileItem(this.ProjectMgr.GetAutomationObject() as OAProject, this);
        }

        /// <summary>
        /// Open a file depending on the SubType property associated with the file item in the project file
        /// </summary>
        protected override void DoDefaultAction()
        {
            FileDocumentManager manager = this.GetDocumentManager() as FileDocumentManager;
            Debug.Assert(manager != null, "Could not get the FileDocumentManager");

            Guid viewGuid = (IsFormSubType ? NativeMethods.LOGVIEWID_Designer : NativeMethods.LOGVIEWID_TextView);
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

            //if (guidCmdGroup == PythonMenus.guidIronPythonProjectCmdSet)
            //{
            //    if (cmd == (uint)PythonMenus.SetAsMain.ID)
            //    {
            //        // Set the MainFile project property to the Filename of this Node
            //        ((PythonProjectNode)this.ProjectMgr).SetProjectProperty(PythonProjectFileConstants.MainFile, this.GetRelativePath());
            //        return VSConstants.S_OK;
            //    }
            //}
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

            //else if (guidCmdGroup == PythonMenus.guidIronPythonProjectCmdSet)
            //{
            //    if (cmd == (uint)PythonMenus.SetAsMain.ID)
            //    {
            //        result |= QueryStatusResult.SUPPORTED | QueryStatusResult.ENABLED;
            //        return VSConstants.S_OK;
            //    }
            //}
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
