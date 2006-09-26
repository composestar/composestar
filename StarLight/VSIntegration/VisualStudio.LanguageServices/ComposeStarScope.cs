using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;

using Composestar.StarLight.VisualStudio.LanguageServices.Engine;   

namespace Composestar.StarLight.VisualStudio.LanguageServices
{
 
    class ComposeStarScope : AuthoringScope {

        LanguageService language;
        Concern concern;

        public ComposeStarScope(Concern concern, LanguageService language)
        {
            this.concern = concern;
            this.language = language;
        }
        
        public override string GetDataTipText(int line, int col, out TextSpan span) {
            span = new TextSpan();
            //span.iStartLine = line;
            //span.iEndLine = line;
            //span.iStartIndex = col - 10;
            //span.iEndIndex = col + 10;
            //return "GetDataTipText";
            return null;
        }

        public override Declarations GetDeclarations(IVsTextView view, int line, int col, TokenInfo info, ParseReason reason) {
            System.Diagnostics.Debug.Print("GetDeclarations line({0}), col({1}), TokenInfo(type {2} at {3}-{4} triggers {5}), reason({6})",
                line, col, info.Type, info.StartIndex, info.EndIndex, info.Trigger, reason);

            IList<Declaration> declarations = concern.GetAttributesAt(line + 1, info.StartIndex);
            //declarations.Add(new Declaration("", "test decl", Declaration.DeclarationType.Function, "description for Declarations"));
         //   System.Windows.Forms.MessageBox.Show("getDeclaration");
            ComposeStarDeclarations composeStarDeclarations = new ComposeStarDeclarations(declarations, language);
            composeStarDeclarations.AddDeclaration(new Declaration("", "test GetDeclarations2", Declaration.DeclarationType.Function, "test"));
            ((ComposeStarLangServ)language).AddSnippets(ref composeStarDeclarations);
            composeStarDeclarations.Sort();
            return composeStarDeclarations;
        }

        public override Methods GetMethods(int line, int col, string name) {
            System.Diagnostics.Debug.Print("GetMethods line({0}), col({1}), name({2})", line, col, name);
          //  System.Windows.Forms.MessageBox.Show("getMethods");
            IList<Declaration> methods = concern.GetMethodsAt(line + 1, col, name);
            methods.Add(new Declaration("test declMethods"));
            return new ComposeStarMethods(methods);
        }

        public override string Goto(VSConstants.VSStd97CmdID cmd, IVsTextView textView, int line, int col, out TextSpan span) {
            span = new TextSpan();            
            return null;
        }
    }
}
