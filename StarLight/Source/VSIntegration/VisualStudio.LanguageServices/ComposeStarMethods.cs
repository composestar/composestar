
using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.VisualStudio.Package;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{
    class ComposeStarMethods : Methods {

        private IList<Declaration> methods;

        public ComposeStarMethods(IList<Declaration> methods) {
            this.methods = methods;
        }

        public override int GetCount() {
            return methods != null ? methods.Count : 0;
        }

        public override string GetDescription(int index) {
            return methods != null && 0 <= index && index < methods.Count ? methods[index].Description : "";
        }

        public override string GetType(int index) {
            return methods != null && 0 <= index && index < methods.Count ? methods[index].Type.ToString() : "";
        }

        public override int GetParameterCount(int index) {
            return 0; // methods != null && 0 <= index && index < methods.Count ? methods[index].ParameterCount : 0;
        }

        public override void GetParameterInfo(int index, int parameter, out string name, out string display, out string description) {
            //if (methods != null && 0 <= index && index < methods.Count) {
            //    methods[index].GetParameterInfo(parameter, out name, out display, out description);
            //} else {
                name = display = description = string.Empty;
           // }
        }

        public override string GetName(int index) {
            return methods != null && 0 <= index && index < methods.Count ? methods[index].Title : "";
        }
    }
}
