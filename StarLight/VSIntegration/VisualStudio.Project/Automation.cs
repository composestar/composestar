
using System;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.Shell;
using System.Runtime.InteropServices;
using System.Collections.Generic;
using System.Collections;
using System.Runtime.Serialization;
using System.Reflection;
using IServiceProvider = System.IServiceProvider;
using Microsoft.VisualStudio.OLE.Interop;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Package.Automation;

namespace ComposeStar.VisualStudio.Project
{
    /// <summary>
    /// Add support for automation on cps files.
    /// </summary>
    public class OAComposeStarFileItem : OAFileItem
    {
        #region ctors
        public OAComposeStarFileItem(OAProject project, FileNode node)
            : base(project, node)
        {
        }
        #endregion

        #region overridden methods
        public override EnvDTE.Window Open(string viewKind)
        {
            if (string.Compare(viewKind, EnvDTE.Constants.vsViewKindPrimary) == 0)
            {
                // Get the subtype and decide the viewkind based on the result
                if (((ComposeStarFileNode)this.Node).IsFormSubType)
                {
                    return base.Open(EnvDTE.Constants.vsViewKindDesigner);
                }
            }
            return base.Open(viewKind);
        }
        #endregion
    }

}
