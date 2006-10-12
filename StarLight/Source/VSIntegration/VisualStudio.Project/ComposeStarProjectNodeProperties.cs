
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
    [ComVisible(true), CLSCompliant(false), System.Runtime.InteropServices.ClassInterface(ClassInterfaceType.AutoDual)]
    [Guid("7107C71C-5F04-4a67-9CDF-6EB39CE13D93")]
    public class ComposeStarProjectNodeProperties : ProjectNodeProperties
    {
        #region ctors
        public ComposeStarProjectNodeProperties(ProjectNode node)
            : base(node)
        {
        }
        #endregion

        #region properties
        [Browsable(false)]
        public string OutputFileName
        {
            get
            {
                return ((ComposeStarProjectNode)(this.Node.ProjectMgr)).OutputFileName;
            }
        }
      
        [Browsable(false)]
        public string AssemblyName
        {
            get
            {
                return this.Node.ProjectMgr.GetProjectProperty(ProjectFileConstants.AssemblyName);
            }
            set
            {
                this.Node.ProjectMgr.SetProjectProperty(ProjectFileConstants.AssemblyName, value);
            }
        }

        [Browsable(false)]
        public string DefaultNamespace
        {
            get
            {
                return this.Node.ProjectMgr.GetProjectProperty(ProjectFileConstants.RootNamespace);
            }
            set
            {
                this.Node.ProjectMgr.SetProjectProperty(ProjectFileConstants.RootNamespace, value);
            }
        }

        [Browsable(false)]
        public string RootNamespace
        {
            get
            {
                return this.Node.ProjectMgr.GetProjectProperty(ProjectFileConstants.RootNamespace);
            }
            set
            {
                this.Node.ProjectMgr.SetProjectProperty(ProjectFileConstants.RootNamespace, value);
            }
        }

        [Browsable(false)]
        public string OutputType
        {
            get
            {
                return this.Node.ProjectMgr.GetProjectProperty(ProjectFileConstants.OutputType);
            }
            set
            {
                this.Node.ProjectMgr.SetProjectProperty(ProjectFileConstants.OutputType, value);
            }
        }

        #endregion
        	
    }
}
