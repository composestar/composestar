using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    /// <summary>
    /// Generate documentation here...
    /// </summary>
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = false)]
    public class FilterTypeAttribute : Attribute
    {

        #region Private Variables
        
        /// <summary>
        /// The name of the filtertype
        /// </summary>
        private string _name;

        /// <summary>
        /// The accept-call action
        /// </summary>
        private string _acceptCallAction;

        /// <summary>
        /// The reject-call action
        /// </summary>
        private string _rejectCallAction;

        /// <summary>
        /// The accept-return action
        /// </summary>
        private string _acceptReturnAction;

        /// <summary>
        /// The reject-return action
        /// </summary>
        private string _rejectReturnAction;

        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
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

        /// <summary>
        /// Gets or sets the accept call action.
        /// </summary>
        /// <value>The accept call action.</value>
        public string AcceptCallAction
        {
            get
            {
                return _acceptCallAction;
            }
            set
            {
                _acceptCallAction = value;
            }
        }

        /// <summary>
        /// Gets or sets the reject call action.
        /// </summary>
        /// <value>The reject call action.</value>
        public string RejectCallAction
        {
            get
            {
                return _rejectCallAction;
            }
            set
            {
                _rejectCallAction = value;
            }
        }

        /// <summary>
        /// Gets or sets the accept return action.
        /// </summary>
        /// <value>The accept return action.</value>
        public string AcceptReturnAction
        {
            get
            {
                return _acceptReturnAction;
            }
            set
            {
                _acceptReturnAction = value;
            }
        }

        /// <summary>
        /// Gets or sets the reject return action.
        /// </summary>
        /// <value>The reject return action.</value>
        public string RejectReturnAction
        {
            get
            {
                return _rejectReturnAction;
            }
            set
            {
                _rejectReturnAction = value;
            }
        }

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterTypeAnnotation"/> class.
        /// </summary>
        /// <param name="name">The name of the type.</param>
        /// <param name="acceptCallAction">The accept call action.</param>
        /// <param name="rejectCallAction">The reject call action.</param>
        /// <param name="acceptReturnAction">The accept return action.</param>
        /// <param name="rejectReturnAction">The reject return action.</param>
        public FilterTypeAttribute(string name, string acceptCallAction, string rejectCallAction,
                    string acceptReturnAction, string rejectReturnAction)
        {
            _name = name;
            _acceptCallAction = acceptCallAction;
            _rejectCallAction = rejectCallAction;
            _acceptReturnAction = acceptReturnAction;
            _rejectReturnAction = rejectReturnAction;
        }

        #endregion

    }
}