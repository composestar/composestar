
using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;

using Composestar.StarLight.VisualStudio.LanguageServices;
   
namespace Composestar.StarLight.VisualStudio.LanguageServices.Engine
{
   
    public class Analyzer  {
       
        
        private Analyzer() {
          
        }

        public static Concern Analyze(Concerns concerns, string name, string text)
        {
            //CompilerContext context = new CompilerContext(sink);
            //Parser parser = Parser.FromString(context, name, text);
            //Stmt stmt = parser.ParseFileInput();
            //CpsLexer parser = new CpsLexer(text, 0);
            
            //Analyzer analyzer = new Analyzer();

            Concern c = new Concern(name);
          

            return c;

            //return analyzer.DoAnalyze(modules, name, stmt);
        }

        private Concern DoAnalyze(Concerns concerns, string name) {
            //GlobalSuite global = new GlobalSuite(root);
            //module = new Module(modules, name, global, scopes);

            //ModuleScope modsc;
            //module.ModuleScope = modsc = new ModuleScope(module, null, global);

            //PushScope(modsc);

            //root.Walk(this);

            //foreach (FieldAssignment fer in this.fields) {
            //    fer.Infer(module);
            //}
            //return module;
            return null;
        }
                
    }
}
