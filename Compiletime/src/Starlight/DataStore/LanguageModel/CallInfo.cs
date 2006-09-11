using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.DataStore.LanguageModel
{
    public class CallInfo
    {
        private string _methodReference;


        /// <summary>
        /// Gets or sets the method reference.
        /// </summary>
        /// <value>The method reference.</value>
        public string MethodReference
        {
            get
            {
                return _methodReference;
            }
            set
            {
                _methodReference = value;
            }
        }
    }
}
