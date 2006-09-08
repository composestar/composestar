
using System;
using System.Collections.Generic;
using System.Diagnostics;

using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;

namespace Composestar.StarLight.VisualStudio.LanguageServices {
    
    public class ComposeStarSink  {

        AuthoringSink authoringSink;

        public ComposeStarSink(AuthoringSink authoringSink) {
            this.authoringSink = authoringSink;     
        }

        //        private static TextSpan CodeToText(Hosting.CodeSpan code) {
        //            TextSpan span = new TextSpan();
        //            if (code.startLine > 0) {
        //                span.iStartLine = code.startLine - 1;
        //            }
        //            span.iStartIndex = code.startColumn;
        //            if (code.endLine > 0) {
        //                span.iEndLine = code.endLine - 1;
        //            }
        //            span.iEndIndex = code.endColumn;
        //            return span;
        //        }

        public void AddError(string path, string message, string lineText, int startLine, int startColumn, int endLine, int endColumn, int errorCode, Severity severity)
        {
            TextSpan span = new TextSpan();
            if (startLine > 0) {
                span.iStartLine = startLine - 1;
            }
            span.iStartIndex = startColumn;
            if (endLine > 0) {
                span.iEndLine = endLine - 1;
            }
            span.iEndIndex = endColumn;
            authoringSink.AddError(path, message, span, severity);
        }

        //public override void MatchPair(Hosting.CodeSpan span, Hosting.CodeSpan endContext, int priority) {
        //    authoringSink.MatchPair(CodeToText(span), CodeToText(endContext), priority);
        //}

        //public override void EndParameters(Hosting.CodeSpan span) {
        //    authoringSink.EndParameters(CodeToText(span));
        //}

        //public override void NextParameter(Hosting.CodeSpan span) {
        //    authoringSink.NextParameter(CodeToText(span));
        //}

        //public override void QualifyName(Hosting.CodeSpan selector, Hosting.CodeSpan span, string name) {
        //    authoringSink.QualifyName(CodeToText(selector), CodeToText(span), name);
        //}

        //public void StartName(CodeSpan span, string name)
        //{
        //    authoringSink.StartName() .StartName(CodeToText(span), name);
        //}

        //public override void StartParameters(Hosting.CodeSpan span) {
        //    authoringSink.StartParameters(CodeToText(span));
        //}
    }
}
