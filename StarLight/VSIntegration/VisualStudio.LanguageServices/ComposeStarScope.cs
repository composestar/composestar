using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;

using Composestar.StarLight.VisualStudio.LanguageServices.Engine;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{

    class ComposeStarScope : AuthoringScope
    {

        LanguageService language;
        Concern concern;
        private AuthoringSink _sink;
        private Methods _methods;
        private string _filePath;

        public ComposeStarScope(Concern concern, LanguageService language)
        {

            this.concern = concern;
            this.language = language;
        }

        public ComposeStarScope(Concern concern, LanguageService language, AuthoringSink sink)
        {
            this.concern = concern;
            this.language = language;
            this._sink = sink;
        }

        public ComposeStarScope(LanguageService language, AuthoringSink sink)
        {

            this.language = language;
            this._sink = sink;
        }

        public override string GetDataTipText(int line, int col, out TextSpan span)
        {
            span = new TextSpan();
            return null;
            //	QuickTipInfo info = _project.GetQuickTip(_filePath, line, col, _sourceText);

            //if (info == null)
            //    return null;

            //span.iStartLine  = info.LineStart;
            //span.iEndLine    = info.LineEnd;
            //span.iStartIndex = info.ColStart;
            //span.iEndIndex   = info.ColEnd;


            //return info.Text;
        }

        public override Declarations GetDeclarations(IVsTextView view, int line, int col, TokenInfo info, ParseReason reason)
        {

            switch (reason)
            {
                case ParseReason.CompleteWord:
                    IList<Declaration> declarations = concern.GetAttributesAt(line + 1, info.StartIndex);
                    ComposeStarDeclarations composeStarDeclarations = new ComposeStarDeclarations(declarations, language);
                    composeStarDeclarations.Sort();
                    return composeStarDeclarations;
                    break;
                case ParseReason.DisplayMemberList:
                case ParseReason.MemberSelect:
                case ParseReason.MemberSelectAndHighlightBraces:
                    IList<Declaration> ret = new List<Declaration>();
                    ret.Add(new Declaration("", "isNamespace()", Declaration.DeclarationType.Predicate, "The name should be in the namespace"));
                    ret.Add(new Declaration("", "isClass()", Declaration.DeclarationType.Predicate, "The name should be in the class"));
                    ret.Add(new Declaration("", "isConcern()", Declaration.DeclarationType.Predicate, "The name should be in the namespace"));
                    ret.Add(new Declaration("", "isTypeWithName(Type, TypeName)", Declaration.DeclarationType.Predicate, "Type should have typename"));
                    return new ComposeStarDeclarations(ret, language);

                    break;
                default:
                    throw new ArgumentException("reason");
            }

            return null;
        }

        public override Methods GetMethods(int line, int col, string name)
        {
            System.Diagnostics.Debug.Print("GetMethods line({0}), col({1}), name({2})", line, col, name);

            IList<Declaration> methods = concern.GetMethodsAt(line + 1, col, name);

            return new ComposeStarMethods(methods);
        }

        public override string Goto(VSConstants.VSStd97CmdID cmd, IVsTextView textView, int line, int col, out TextSpan span)
        {
            span = new TextSpan();
            return null;
        }
    }
}
