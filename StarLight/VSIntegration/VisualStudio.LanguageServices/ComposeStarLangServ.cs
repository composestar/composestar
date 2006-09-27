using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Security.Permissions;
using System.Text;

using ErrorHandler = Microsoft.VisualStudio.ErrorHandler;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.TextManager.Interop;
using VSConstants = Microsoft.VisualStudio.VSConstants;
using Composestar.StarLight.VisualStudio.LanguageServices.Engine;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{
    /// <summary>
    /// This class implements language service that supplies syntax highlighting based on the CPS format.
    /// </summary>

    // This attribute indicates that this managed type is visible to COM
    [ComVisible(true)]
    [Guid(ComposeStarConstants.languageServiceGuidString)]
    class ComposeStarLangServ : LanguageService, IVsLanguageTextOps
    {
        private ComposeStarScanner scanner;
        Concerns concerns = new Concerns();

        private LanguagePreferences preferences;

        private Dictionary<IVsTextView, ComposeStarSource> specialSources;

        // This array contains the definition of the colorable items provided by this
        // language service.
        // This specific language does not really need to provide colorable items because it
        // does not define any item different from the default ones, but the base class has
        // an empty implementation of IVsProvideColorableItems, so any language service that
        // derives from it must implement the methods of this interface, otherwise there are
        // errors when the shell loads an editor to show a file associated to this language.
        private static ComposeStarColorableItem[] colorableItems = {
            // The first 6 items in this list MUST be these default items.
            new ComposeStarColorableItem("Keyword", COLORINDEX.CI_BLUE, COLORINDEX.CI_USERTEXT_BK),
            new ComposeStarColorableItem("Comment", COLORINDEX.CI_DARKGREEN, COLORINDEX.CI_USERTEXT_BK),
            new ComposeStarColorableItem("Identifier", COLORINDEX.CI_SYSPLAINTEXT_FG, COLORINDEX.CI_USERTEXT_BK),
            new ComposeStarColorableItem("String", COLORINDEX.CI_MAROON, COLORINDEX.CI_USERTEXT_BK),
            new ComposeStarColorableItem("Number", COLORINDEX.CI_SYSPLAINTEXT_FG, COLORINDEX.CI_USERTEXT_BK),
            new ComposeStarColorableItem("Text", COLORINDEX.CI_SYSPLAINTEXT_FG, COLORINDEX.CI_USERTEXT_BK)
        };

        public ComposeStarLangServ()
        {
            specialSources = new Dictionary<IVsTextView, ComposeStarSource>();            
        }

        /// <summary>
        /// This method parses the source code based on the specified ParseRequest object.
        /// </summary>
        /// <param name="req">The <see cref="ParseRequest"/> describes how to parse the source file.</param>
        /// <returns>If successful, returns an <see cref="AuthoringScope"/> object; otherwise, returns a null value.</returns>
        public override AuthoringScope ParseSource(ParseRequest req)
        {
            if (null == req)
            {
                throw new ArgumentNullException("req");
            }
            Debug.Print("ParseSource at ({0}:{1}), reason {2}", req.Line, req.Col, req.Reason);
            ComposeStarSource source = null;
            if (specialSources.TryGetValue(req.View, out source) && (null != source.ScopeCreator))
            {
                return source.ScopeCreator(req);
            }
         
            ComposeStarSink sink = new ComposeStarSink(req.Sink);
         
            return new ComposeStarScope(concerns.AnalyzeConcern(sink, req.FileName, req.Text), this);
        }

               
        /// <summary>
        /// Language name property.
        /// </summary>        
        public override string Name
        {
            get { return "ComposeStar Language Service"; }
        }

        /// <summary>
        /// Create and return instantiation of a parser represented by RegularExpressionScanner object.
        /// </summary>
        /// <param name="buffer">An <see cref="IVsTextLines"/> represents lines of source to parse.</param>
        /// <returns>Returns a RegularExpressionScanner object</returns>
        public override IScanner GetScanner(Microsoft.VisualStudio.TextManager.Interop.IVsTextLines buffer)
        {
            if (scanner == null)
            {
                // Create new ComposeStarScanner instance
                scanner = new ComposeStarScanner();
            }

            return scanner;
        }

        /// <summary>
        /// Returns a <see cref="LanguagePreferences"/> object for this language service.
        /// </summary>
        /// <returns>Returns a LanguagePreferences object</returns>
        public override LanguagePreferences GetLanguagePreferences()
        {
            if (preferences == null)
            {
                // Create new LanguagePreferences instance
                preferences = new LanguagePreferences(this.Site, typeof(ComposeStarLangServ).GUID, "ComposeStar Language Service");
                preferences.EnableQuickInfo = true;                 
                preferences.EnableCodeSense = true;
                preferences.EnableCommenting = true;
                preferences.AutoListMembers = true;
                preferences.ParameterInformation = true;
                preferences.LineNumbers = true;
                
              //  preferences.Init();  
            }

            return preferences;
        }

        public override Source CreateSource(IVsTextLines buffer)
        {
            return new ComposeStarSource(this, buffer, new ComposeStarColorizer(this, buffer, GetScanner(buffer)));
        }

        public override void OnIdle(bool periodic)
        {
            Source src = GetSource(this.LastActiveTextView);
            if (src != null && src.LastParseTime == Int32.MaxValue)
            {
                src.LastParseTime = 0;
            }
            base.OnIdle(periodic);
        }

        public override System.Windows.Forms.ImageList GetImageList()
        {
            System.Windows.Forms.ImageList il = base.GetImageList();
            return il;
        }

         public override string GetFormatFilterList()
        {
            return Resources.ComposeStarFormatFilter;
        }

        public override int GetItemCount(out int count)
        {
            count = colorableItems.Length;
            return Microsoft.VisualStudio.VSConstants.S_OK;
        }

        public override int GetColorableItem(int index, out IVsColorableItem item)
        {
            if (index < 1)
            {
                throw new ArgumentOutOfRangeException("index");
            }
            item = colorableItems[index - 1];
            return Microsoft.VisualStudio.VSConstants.S_OK;
        }

        private List<VsExpansion> expansionsList;

        public override void OnParseComplete(ParseRequest req)
        {
            if (this.expansionsList == null)
            {
                GetSnippets();
            }
        }

        private int classNameCounter = 0;

        public override ExpansionFunction CreateExpansionFunction(ExpansionProvider provider, string functionName)
        {
            ExpansionFunction function = null;
            if (functionName == "GetName")
            {
                ++classNameCounter;
                function = new ComposeStarGetNameExpansionFunction(provider, classNameCounter);
            }
            return function;
        }

        // Disable the "DoNotPassTypesByReference" warning.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1045")]
        public void AddSnippets(ref ComposeStarDeclarations declarations)
        {
            if (null == this.expansionsList)
            {
                return;
            }
            foreach (VsExpansion expansionInfo in this.expansionsList)
            {
                declarations.AddDeclaration(new Declaration(expansionInfo));
            }
        }

        // Disable the "DoNotIndirectlyExposeMethodsWithLinkDemands" warning as OnParseComplete can not have a LinkDemand.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Security", "CA2122")]
        private void GetSnippets()
        {
        
            if (null == this.expansionsList)
            {
                this.expansionsList = new List<VsExpansion>();
            }
            else
            {
                this.expansionsList.Clear();
            }
            IVsTextManager2 textManager = Package.GetGlobalService(typeof(SVsTextManager)) as IVsTextManager2;
            if (textManager == null)
            {
                return;
            }
            SnippetsEnumerator enumerator = new SnippetsEnumerator(textManager, GetLanguageServiceGuid());
            foreach (VsExpansion expansion in enumerator)
            {
                if (!string.IsNullOrEmpty(expansion.shortcut))
                {
                    this.expansionsList.Add(expansion);
                }
            }
        }

        public override void Dispose()
        {
            try
            {
                // Clear the special sources
                foreach (ComposeStarSource source in specialSources.Values)
                {
                    source.Dispose();
                }
                specialSources.Clear();

                // Dispose the preferences.
                if (null != preferences)
                {
                    preferences.Dispose();
                    preferences = null;
                }

                // Dispose the scanner.
                if (null != scanner) {
                    scanner.Dispose();
                    scanner = null;
                }
            }
            finally
            {
                base.Dispose();
            }
        }

        public void AddSpecialSource(ComposeStarSource source, IVsTextView view)
        {
            specialSources.Add(view, source);
        }

        public override ViewFilter CreateViewFilter(CodeWindowManager mgr, IVsTextView newView)
        {
            // This call makes sure debugging events can be received
            // by our view filter.
            base.GetIVsDebugger();
            return new ComposeStarViewFilter(mgr, newView);
        }

        internal class ComposeStarGetNameExpansionFunction : ExpansionFunction
        {
            private int nameCount;

            public ComposeStarGetNameExpansionFunction(ExpansionProvider provider, int counter)
                : base(provider)
            {
                nameCount = counter;
            }

            public override string GetCurrentValue()
            {
                string name = "MyConcern";
                name += nameCount.ToString(System.Globalization.CultureInfo.InvariantCulture);
                return name;
            }
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1033:InterfaceMethodsShouldBeCallableByChildTypes")]
        int IVsLanguageTextOps.Format(IVsTextLayer pTextLayer, TextSpan[] ptsSel) {
            return VSConstants.E_NOTIMPL;
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1033:InterfaceMethodsShouldBeCallableByChildTypes")]
        int IVsLanguageTextOps.GetDataTip(IVsTextLayer pTextLayer, TextSpan[] ptsSel, TextSpan[] ptsTip, out string pbstrText) {
            pbstrText = string.Empty;
            return VSConstants.E_NOTIMPL;
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1033:InterfaceMethodsShouldBeCallableByChildTypes")]
        int IVsLanguageTextOps.GetPairExtent(IVsTextLayer pTextLayer, TextAddress ta, TextSpan[] pts) {
            return VSConstants.E_NOTIMPL;
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1033:InterfaceMethodsShouldBeCallableByChildTypes")]
        int IVsLanguageTextOps.GetWordExtent(IVsTextLayer pTextLayer, TextAddress ta, WORDEXTFLAGS flags, TextSpan[] pts) {
            return VSConstants.E_NOTIMPL;
        }
    }
}
