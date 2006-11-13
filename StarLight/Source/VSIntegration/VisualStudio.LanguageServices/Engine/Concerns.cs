using System;
using System.Collections.Generic;
using System.Reflection;
using System.Diagnostics;
using System.IO;
using Composestar.StarLight.VisualStudio.Babel;   
using Microsoft.VisualStudio.TextManager.Interop;

namespace Composestar.StarLight.VisualStudio.LanguageServices.Engine {

    // Disable the "IdentifiersShouldNotMatchKeywords" warning.
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1716")]
    public class Concern {
     
        private string name;
           
        public Concern(string name) {
            this.name = name;
        }
        
        // Disable the "VariableNamesShouldNotMatchFieldNames" warning.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Maintainability", "CA1500")]
        public IList<Declaration> GetAttributesAt(int line, int column) {
            //Node node;
            //Scope scope;
            List<Declaration> attributes = new List<Declaration>();

            //if (Locate(line, column, out node, out scope)) {
            //    FieldExpr field = node as FieldExpr;
            //    if (null != field) {
            //        IList<Inferred> result = Engine.Infer(this, field.target, scope);
            //        if (result != null) {
            //            foreach (Inferred s in result) {
            //                foreach (Name name in s.Names) {
            //                    attributes.Add(new Declaration(name.GetString()));
            //                }
            //            }
            //        }
            //    } else {
            //        foreach (Name name in scope.GetNamesCurrent()) {
            //            attributes.Add(new Declaration(name.GetString()));
            //        }
            //        for (; ; ) {
            //            scope = scope.Parent;
            //            if (scope == null) break;
            //            IEnumerable<Name> namesOuter = scope.GetNamesOuter();
            //            if(namesOuter != null)
            //            {
            //                foreach (Name name in namesOuter)
            //                {
            //                    attributes.Add(new Declaration(name.GetString()));
            //                }
            //            }
            //        }
            //        AddBuiltins(attributes);
            //    }
            //} else {
            //    foreach (Name name in module.GetNamesOuter()) {
            //        attributes.Add(new Declaration(name.GetString()));
            //    }
            //    AddBuiltins(attributes);
            //}
            AddBuiltins(attributes);
            return attributes;
        }

        private void AddBuiltins(List<Declaration> attributes)    {
           
 
        }

        // Disable the "ReviewUnusedParameters" warning.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA1801", MessageId="name")]
        // Disable the "VariableNamesShouldNotMatchFieldNames" warning.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Maintainability", "CA1500", MessageId="name")]
        public IList<Declaration> GetMethodsAt(int line, int column, string name) {
            //Node node;
            //Scope scope;
            //Node context;
            //IList<Inferred> methods = null;
            //Name nodeName = null;
            //if (Locate(typeof(CallExpr), line, column, out node, out scope, out context)) {
            //    if (context != null) {
            //        node = ((CallExpr)context).target;
            //    }

            //    FieldExpr fe;
            //    NameExpr ne;

            //    if ((fe = node as FieldExpr) != null) {
            //        nodeName = fe.name;
            //        methods = Engine.Infer(this, fe, scope);
            //    } else if ((ne = node as NameExpr) != null) {
            //        nodeName = ne.name;
            //        methods = Engine.Infer(this, node, scope);
            //    }
            //}
            //if (methods != null) {
            //    IList<FunctionInfo> infos = null;
            //    foreach (Inferred inf in methods) {
            //        infos = Engine.Union(infos, inf.InferMethods(nodeName));
            //    }
            //    return infos;
            //}
            IList<Declaration> ret = new List<Declaration>();
            ret.Add(new Declaration("", "isNamespace()", Declaration.DeclarationType.Predicate, "The name should be in the namespace") ); 
            ret.Add(new Declaration("", "isClass()", Declaration.DeclarationType.Predicate, "The name should be in the class") );
            ret.Add(new Declaration("", "isConcern()", Declaration.DeclarationType.Predicate, "The name should be in the namespace") );
            ret.Add(new Declaration("", "isTypeWithName(Type, TypeName)", Declaration.DeclarationType.Predicate, "Type should have typename") );
            return ret;
        }

//        private bool Locate(Type contextType, int line, int column, out Node node, out Scope scope, out Node context) {
 //           Locator locator = new Locator(contextType, line, column);
//            global.Walk(locator);
//            node = locator.Candidate;
//            scope = locator.Scope != null ? scopes[locator.Scope] : null;
//            context = locator.Context;

//#if DEBUG
//            if (node != null) {
//                Debug.Print("Located {0} in {1} at {2}:{3}-{4}:{5}",
//                    node, context != null ? (object)context : (object)"<unknown>",
//                    node.start.line, node.start.column,
//                    node.end.line, node.end.column
//                );
//            }
//#endif

//            return node != null && context != null;
//        }

//        private bool Locate(int line, int column, out Node node, out Scope scope) {
//            Locator locator = new Locator(line, column);
//            global.Walk(locator);

//            node = locator.Candidate;
//            scope = locator.Scope != null ? scopes[locator.Scope] : null;

//#if DEBUG
//            if (node != null) {
//                Debug.Print("Located {0} at {1}:{2}-{3}:{4}",
//                    node,
//                    node.start.line, node.start.column,
//                    node.end.line, node.end.column
//                );
//            }
//#endif

//            return node != null;
//        }
    }

    public class Concerns {
        Dictionary<string, Concern> concerns = new Dictionary<string, Concern>();
    
        public Concerns() {
            LoadTypes(typeof(string).Assembly);
            LoadTypes(typeof(System.Diagnostics.Debug).Assembly);
            LoadBuiltins();
        }

    

        private void LoadTypes(Assembly assembly) {
            Type[] types = assembly.GetExportedTypes();

            foreach (Type type in types) {
                //ReflectedModule scope = global;
                //string[] ns = type.Namespace.Split('.');
                //string full = "";
                //bool dot = false;
                //foreach (string n in ns) {
                //    full = dot ? full + "." + n : n;
                //    dot = true;
                //    scope = scope.EnsureNamespace(full, Name.Make(n));
                //}
                //scope.AddType(type);
            }
        }

        private void LoadBuiltins() {
            //Assembly asm = typeof(IronPython.Hosting.PythonEngine).Assembly;
            //object[] attributes = asm.GetCustomAttributes(typeof(PythonModuleAttribute), false);
            //foreach (PythonModuleAttribute pma in attributes) {
            //    if (pma.type == typeof(IronPython.Modules.Builtin)) {
            //        global.AddType(pma.name, builtins);
            //    } else {
            //        global.AddPythonType(pma.name, pma.type);
            //    }
            //}
            //global.AddPythonType("sys", typeof(IronPython.Runtime.SystemState));
            //global.AddPythonType("clr", typeof(IronPython.Modules.ClrModule));
        }
    }

 }
