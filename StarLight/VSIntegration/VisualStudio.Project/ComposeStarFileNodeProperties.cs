
/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

using System;
using System.ComponentModel;
using System.Text;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Runtime.InteropServices;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Package.Automation;
using Microsoft.Win32;
using EnvDTE;
using IOleServiceProvider = Microsoft.VisualStudio.OLE.Interop.IServiceProvider;

namespace Composestar.StarLight.VisualStudio.Project
{
    [ComVisible(true), CLSCompliant(false)]
    public class ComposeStarFileNodeProperties : FileNodeProperties
    {
        #region ctors
        public ComposeStarFileNodeProperties(HierarchyNode node)
            : base(node)
        {
        }
        #endregion

        #region properties
        [Browsable(false)]
        public string Url
        {
            get
            {
                return "file:///" + this.Node.Url;
            }
        }
        [Browsable(false)]
        public string SubType
        {
            get
            {
                return ((ComposeStarFileNode)this.Node).SubType;
            }
            set
            {
                ((ComposeStarFileNode)this.Node).SubType = value;
            }
        }
        #endregion
    }
}
