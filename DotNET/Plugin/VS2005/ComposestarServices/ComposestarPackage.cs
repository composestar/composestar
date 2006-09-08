using System;
using System.Diagnostics;
using System.Globalization;
using System.Runtime.InteropServices;
using System.ComponentModel.Design;
using Microsoft.Win32;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.OLE.Interop;
using Microsoft.VisualStudio.Shell;

namespace Trese.ComposestarSupport
{
	[PackageRegistration(UseManagedResourcesOnly = true)]
	[DefaultRegistryRoot("Software\\Microsoft\\VisualStudio\\8.0")]
//	[InstalledProductRegistration(false, "#100", "#102", "1.0", IconResourceID = 400)]
	[ProvideLoadKey("Standard", "1.0", "Composestar", "Trese", 1)]
	[ProvideService(typeof(ComposestarLanguage), ServiceName = "Composestar")]
	[ProvideLanguageService(typeof(ComposestarLanguage), "Composestar", 100,
		CodeSense = true,
		EnableCommenting = true,
		MatchBraces = true,
		ShowCompletion = true,
		ShowMatchingBrace = true)]
	[ProvideLanguageExtension(typeof(ComposestarLanguage), ".cps")]
	[Guid(GuidList.guidComposestarSupportPkgString)]
    public sealed class ComposestarPackage : Package
    {
        public ComposestarPackage()
        {
            IServiceContainer container = this as IServiceContainer;
            ServiceCreatorCallback callback = new ServiceCreatorCallback(CreateService);
            container.AddService(typeof(ComposestarLanguage), callback, true);
        }

        #region Package Members

        /// <summary>
        /// Initialization of the package; this method is called right after the package is sited, so this is the place
        /// where you can put all the initilaization code that rely on services provided by VisualStudio.
        /// </summary>
        protected override void Initialize()
        {
            base.Initialize();
        }

        #endregion

        private object CreateService(IServiceContainer container, Type serviceType)
        {
            object service = null;
			if (serviceType == typeof(ComposestarLanguage))
            {
                ComposestarLanguage language = new ComposestarLanguage();
                language.SetSite(this);
//              RegisterForIdleTime();
                service = language;
            }
            return service;
        }
    }
}