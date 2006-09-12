using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository.LanguageModel
{
    /// <summary>
    /// Contains information about the method body.
    /// </summary>
    public class MethodBody
    {
        private IList<CallInfo> _calls;

        /// <summary>
        /// Gets or sets the calls.
        /// </summary>
        /// <value>The calls made by this method.</value>
        public IList<CallInfo> Calls
        {
            get
            {
                return _calls;
            }
            set
            {
                _calls = value;
            }
        }



    }
}
