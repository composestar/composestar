
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Runtime.InteropServices;

using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.TextManager.Interop;

using VSConstants = Microsoft.VisualStudio.VSConstants;

namespace Composestar.StarLight.VisualStudio.LanguageServices {
    /// <summary>
    /// This class implements an intellisense provider for Web projects.
    /// An intellisense provider is a specialization of a language service where it is able to
    /// run as a contained language. The main language service and main editor is usually the
    /// HTML editor / language service, but a specific contained language is hosted for fragments
    /// like the "script" tags.
    /// This object must be COM visible because it will be Co-Created by the host language.
    /// </summary>
    [ComVisible(true)]
    [Guid(ComposeStarConstants.intellisenseProviderGuidString)]
    public sealed class ComposeStarIntellisenseProvider : IVsIntellisenseProject, IDisposable {
       
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        private IVsIntellisenseProjectHost hostProject;
        private List<string> references;
  
        public ComposeStarIntellisenseProvider() {
            System.Diagnostics.Debug.WriteLine("\n\tComposeStarIntellisenseProvider created\n");
            references = new List<string>();
        }

        public int AddAssemblyReference(string bstrAbsPath) {
            
            return VSConstants.S_OK;
        }

        public void Dispose() {
            Dispose(true);
        }

        private void Dispose(bool disposing) {
            if (disposing) {
                Close();
            }
            GC.SuppressFinalize(this);
        }

        public int AddFile(string bstrAbsPath, uint itemid) {
            // TODO: Handle this command.
            return VSConstants.S_OK;
        }

        public int AddP2PReference(object pUnk) {
            // Not supported.
            return VSConstants.E_NOTIMPL;
        }

        public int Close() {
            this.hostProject = null;
           
            return VSConstants.S_OK;
        }

        public int GetCodeDomProviderName(out string pbstrProvider) {
            pbstrProvider = ComposeStarConstants.composeStarCodeDomProviderName;
            return VSConstants.S_OK;
        }

        public int GetCompilerReference(out object ppCompilerReference) {
            ppCompilerReference = null;
            return VSConstants.E_NOTIMPL;
        }

        public int GetContainedLanguageFactory(out IVsContainedLanguageFactory ppContainedLanguageFactory) {
           ppContainedLanguageFactory = null;
            return VSConstants.S_OK;
        }

        public int GetExternalErrorReporter(out IVsReportExternalErrors ppErrorReporter) {
            // TODO: Handle the error reporter
            ppErrorReporter = null;
            return VSConstants.E_NOTIMPL;
        }

        public int GetFileCodeModel(object pProj, object pProjectItem, out object ppCodeModel) {
            ppCodeModel = null;
            return VSConstants.E_NOTIMPL;
        }

        public int GetProjectCodeModel(object pProj, out object ppCodeModel) {
            ppCodeModel = null;
            return VSConstants.E_NOTIMPL;
        }

        public int Init(IVsIntellisenseProjectHost pHost) {
            this.hostProject = pHost;
            return VSConstants.S_OK;
        }

        public int IsCompilableFile(string bstrFileName) {
            string ext = System.IO.Path.GetExtension(bstrFileName);
            if (0 == string.Compare(ext, ComposeStarConstants.composeStarFileExtension, StringComparison.OrdinalIgnoreCase)) {
                return VSConstants.S_OK;
            }
            return VSConstants.S_FALSE;
        }

        public int IsSupportedP2PReference(object pUnk) {
            // P2P references are not supported.
            return VSConstants.S_FALSE;
        }

        public int IsWebFileRequiredByProject(out int pbReq) {
            pbReq = 0;
            return VSConstants.S_OK;
        }

        public int RefreshCompilerOptions() {
            return VSConstants.S_OK;
        }

        public int RemoveAssemblyReference(string bstrAbsPath) {
           
            return VSConstants.S_OK;
        }

        public int RemoveFile(string bstrAbsPath, uint itemid) {
            // TODO: Handle this command.
            return VSConstants.S_OK;
        }

        public int RemoveP2PReference(object pUnk) {
            return VSConstants.S_OK;
        }

        public int RenameFile(string bstrAbsPath, string bstrNewAbsPath, uint itemid) {
            return VSConstants.S_OK;
        }

        public int ResumePostedNotifications() {
            // No-Op
            return VSConstants.S_OK;
        }

        public int StartIntellisenseEngine() {
            // No-Op
            return VSConstants.S_OK;
        }

        public int StopIntellisenseEngine() {
            // No-Op
            return VSConstants.S_OK;
        }

        public int SuspendPostedNotifications() {
            // No-Op
            return VSConstants.S_OK;
        }

        public int WaitForIntellisenseReady() {
            // No-Op
            return VSConstants.S_OK;
        }

   
    }
}
