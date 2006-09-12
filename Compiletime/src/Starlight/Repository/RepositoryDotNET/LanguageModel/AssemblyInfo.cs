using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository.LanguageModel
{
    public class AssemblyInfo
    {
        private string _name;
        private string _version;

        public string Name
        {
            get
            {
                return _name;
            }
            set
            {
                _name = value;
            }

        }

        public string Version
        {
            get
            {
                return _version;
            }
            set
            {
                _version = value;
            }
        }

    }
}
