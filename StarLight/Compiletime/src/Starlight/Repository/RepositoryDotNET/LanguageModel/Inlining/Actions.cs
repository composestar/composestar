using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel.InlineInstructions;

namespace Composestar.Repository.LanguageModel.Actions
{

    /// <summary>
    /// An abstract action to be implemented by the subactions.
    /// </summary>
    public abstract class Action
    {
    }

    /// <summary>
    /// A filter action.
    /// </summary>
    public class FilterAction : Action
    {
        private String _filterType;
        private String _target;
        private String _selector;

        /// <summary>
        /// Gets or sets the selector.
        /// </summary>
        /// <value>The selector.</value>
        public String Selector
        {
            get
            {
                return _selector;
            }
            set
            {
                _selector = value;
            }
        }

        /// <summary>
        /// Gets or sets the target.
        /// </summary>
        /// <value>The target.</value>
        public String Target
        {
            get
            {
                return _target;
            }
            set
            {
                _target = value;
            }
        }

        /// <summary>
        /// Gets or sets the type of the filter.
        /// </summary>
        /// <value>The type of the filter.</value>
        public String FilterType
        {
            get
            {
                return _filterType;
            }
            set
            {
                _filterType = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterAction"/> class.
        /// </summary>
        /// <param name="filterType">Type of the filter.</param>
        /// <param name="target">The target.</param>
        /// <param name="selector">The selector.</param>
        public FilterAction(String filterType, string target, string selector)
        {
            _filterType = filterType;
            _target = target;
            _selector = selector;
        }

    }

    /// <summary>
    /// A context action.
    /// </summary>
    public class ContextAction : Action
    {

        private String _contextType;
        private MethodInfo _method;
        private bool _isEnabled;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is enabled.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is enabled; otherwise, <c>false</c>.
        /// </value>
        public bool IsEnabled
        {
            get
            {
                return _isEnabled;
            }
            set
            {
                _isEnabled = value;
            }
        }

        /// <summary>
        /// Gets or sets the method.
        /// </summary>
        /// <value>The method.</value>
        public MethodInfo Method
        {
            get
            {
                return _method;
            }
            set
            {
                _method = value;
            }
        }

        /// <summary>
        /// Gets or sets the type of the context.
        /// </summary>
        /// <value>The type of the context.</value>
        public String ContextType
        {
            get
            {
                return _contextType;
            }
            set
            {
                _contextType = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ContextAction"/> class.
        /// </summary>
        /// <param name="_contextType">Type of the _context.</param>
        /// <param name="method">The method.</param>
        public ContextAction(String contextType, MethodInfo method)
        {
            this._contextType = contextType;
            this._method = method;
        }

    }
}
