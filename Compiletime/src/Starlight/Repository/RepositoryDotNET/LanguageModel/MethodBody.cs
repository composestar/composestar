using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel.InlineInstructions;

namespace Composestar.Repository.LanguageModel
{
    /// <summary>
    /// Contains information about the method body.
    /// </summary>
    public class MethodBody
    {
        private IList<CallInfo> _calls;
        private Instruction _inputFilter;
        
        /// <summary>
        /// Initializes a new instance of the <see cref="T:MethodBody"/> class.
        /// </summary>
        public MethodBody()
        {
            _calls = new List<CallInfo>();
        }

        /// <summary>
        /// Gets or sets the input filter.
        /// </summary>
        /// <value>The input filter.</value>
        public Instruction InputFilter
        {
            get
            {
                return _inputFilter;
            }
            set
            {
                _inputFilter = value;
            }
        }
             
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
