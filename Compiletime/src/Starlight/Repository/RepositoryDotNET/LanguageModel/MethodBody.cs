using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel.Actions;

namespace Composestar.Repository.LanguageModel
{
    /// <summary>
    /// Contains information about the method body.
    /// </summary>
    public class MethodBody
    {
        private IList<CallInfo> _calls;
        private IList<Action> _inputFilters;
        private IList<Action> _outputFilters;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:MethodBody"/> class.
        /// </summary>
        public MethodBody()
        {
            _calls = new List<CallInfo>();
            _inputFilters = new List<Action>();
            _outputFilters = new List<Action>();
        }

        /// <summary>
        /// Gets or sets the input filters.
        /// </summary>
        /// <value>The input filters.</value>
        public IList<Action> InputFilters
        {
            get
            {
                return _inputFilters;
            }
            set
            {
                _inputFilters = value;
            }
        }

        /// <summary>
        /// Gets or sets the output filters.
        /// </summary>
        /// <value>The output filters.</value>
        public IList<Action> OutputFilters
        {
            get
            {
                return _outputFilters;
            }
            set
            {
                _outputFilters = value;
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
