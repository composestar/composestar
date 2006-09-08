/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

using System.ComponentModel.Design;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.OLE.Interop;
using Microsoft.VisualStudio.TextManager.Interop;

namespace Composestar.StarLight.VisualStudio.LanguageServices {

    [PackageRegistration(UseManagedResourcesOnly = true)]
    [ProvideLoadKey("standard", "1.0", "Visual Studio Integration of ComposeStar Language Service", "University of Twente", 1)]
    [ProvideService(typeof(ComposeStarLangServ), ServiceName = "StarLight")]
    [ProvideLanguageService(typeof(ComposeStarLangServ), "StarLight", 100,
       CodeSense = true,
       EnableLineNumbers = true,       
       QuickInfo = true,
       EnableCommenting = true,
       MatchBraces = true,
       ShowCompletion = true,
       ShowMatchingBrace = true,
        AutoOutlining = true 
    )]
    [ProvideLanguageExtension(typeof(ComposeStarLangServ), ComposeStarConstants.composeStarFileExtension )]
    [Guid(ComposeStarConstants.packageGuidString)]    
    [ProvideIntellisenseProvider(typeof(ComposeStarIntellisenseProvider), "StarLightCodeProvider", "ComposeStar StarLight", ".cps", "ComposeStar;StarLight", "StarLight")]
    [ProvideObject(typeof(ComposeStarIntellisenseProvider))]
    [RegisterSnippetsAttribute(ComposeStarConstants.languageServiceGuidString, false, 131, "StarLight", @"CodeSnippets\SnippetsIndex.xml", @"CodeSnippets\Snippets\", @"CodeSnippets\Snippets\")]
    public class ComposeStarPackage : Package, IOleComponent {
        private uint componentID;
      
        public ComposeStarPackage() {
            IServiceContainer container = this as IServiceContainer;
            ServiceCreatorCallback callback = new ServiceCreatorCallback(CreateService);
            container.AddService(typeof(ComposeStarLangServ), callback, true);
        }

        private void RegisterForIdleTime() {
            IOleComponentManager mgr = GetService(typeof(SOleComponentManager)) as IOleComponentManager;
            if (componentID == 0 && mgr != null) {
                OLECRINFO[] crinfo = new OLECRINFO[1];
                crinfo[0].cbSize = (uint)Marshal.SizeOf(typeof(OLECRINFO));
                crinfo[0].grfcrf = (uint)_OLECRF.olecrfNeedIdleTime |
                                              (uint)_OLECRF.olecrfNeedPeriodicIdleTime;
                crinfo[0].grfcadvf = (uint)_OLECADVF.olecadvfModal |
                                              (uint)_OLECADVF.olecadvfRedrawOff |
                                              (uint)_OLECADVF.olecadvfWarningsOff;
                crinfo[0].uIdleTimeInterval = 1000;
                int hr = mgr.FRegisterComponent(this, crinfo, out componentID);
            }
        }

        protected override void Dispose(bool disposing) {
            try {
                if (componentID != 0) {
                    IOleComponentManager mgr = GetService(typeof(SOleComponentManager)) as IOleComponentManager;
                    if (mgr != null) {
                        mgr.FRevokeComponent(componentID);
                    }
                    componentID = 0;
                }               
            } finally {
                base.Dispose(disposing);
            }
        }

        private object CreateService(IServiceContainer container, Type serviceType) {
            object service = null;
            if (typeof(ComposeStarLangServ) == serviceType)
            {
                ComposeStarLangServ language = new ComposeStarLangServ();
                language.SetSite(this);
                RegisterForIdleTime();
                service = language;           
            }
            return service;
        }

        #region IOleComponent Members

        public int FContinueMessageLoop(uint uReason, IntPtr pvLoopData, MSG[] pMsgPeeked) {
            return 1;
        }

        public int FDoIdle(uint grfidlef) {
            bool periodic = ((grfidlef & (uint)_OLEIDLEF.oleidlefPeriodic) != 0);
            ComposeStarLangServ svc = (ComposeStarLangServ)GetService(typeof(ComposeStarLangServ));
            if (svc != null)
            {
                svc.OnIdle(periodic);
            }
            return 0;

            //ComposeStarLangServ pl = GetService(typeof(ComposeStarLangServ)) as ComposeStarLangServ;
            //if (pl != null) {
            //    pl.OnIdle((grfidlef & (uint)_OLEIDLEF.oleidlefPeriodic) != 0);
            //}
         
            //return 0;
        }

        public int FPreTranslateMessage(MSG[] pMsg) {
            return 0;
        }

        public int FQueryTerminate(int fPromptUser) {
            return 1;
        }

        public int FReserved1(uint dwReserved, uint message, IntPtr wParam, IntPtr lParam) {
            return 1;
        }

        public IntPtr HwndGetWindow(uint dwWhich, uint dwReserved) {
            return IntPtr.Zero;
        }

        public void OnActivationChange(IOleComponent pic, int fSameComponent, OLECRINFO[] pcrinfo, int fHostIsActivating, OLECHOSTINFO[] pchostinfo, uint dwReserved) {
        }

        public void OnAppActivate(int fActive, uint dwOtherThreadID) {
        }

        public void OnEnterState(uint uStateID, int fEnter) {
        }

        public void OnLoseActivation() {
        }

        public void Terminate() {
        }

        #endregion
    }
}
