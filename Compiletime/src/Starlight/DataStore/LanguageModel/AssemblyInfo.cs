using System;
using System.Collections;
using System.Text;

using com.db4o;

namespace Composestar.DataStoreDotNET.LanguageModel
{
    public class AssemblyInfo
    {
        private string _name = "";
        private string _version = "";

        private Hashtable _classes;
 
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        public string Version
        {
            get { return _version; }
            set { _version = value; }
        }

        public void AddClass(ClassInfo ci)
        {
            
        }
    }
}
