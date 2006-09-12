using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository.LanguageModel
{

    /// <summary>
    /// Contains information about a method.
    /// </summary>
    public class MethodInfo
    {
        private string _name;
        private string _returntype;
        private IList<ParameterInfo> _parameters; 
        private MethodBody _methodBody;
         // TODO: add the folling properties: callconstruction??, iscontructor, isabstract, isfinal, isprivate, ispublic, isstactic, isvirtual

        /// <summary>
        /// Initializes a new instance of the <see cref="T:MethodInfo"/> class.
        /// </summary>
        public MethodInfo()
        {
            _methodBody = new MethodBody();
            _parameters = new List<Repository.LanguageModel.ParameterInfo>();
            _calls = new List<Repository.LanguageModel.CallInfo>();
        }

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        /// <summary>
        /// Gets or sets the type of the return value.
        /// </summary>
        /// <value>The type of the return value.</value>
        public string ReturnType
        {
            get { return _returntype; }
            set { _returntype = value; }
        }

        /// <summary>
        /// Gets or sets the parameters.
        /// </summary>
        /// <value>The parameters.</value>
        public IList<ParameterInfo> Parameters
        {
            get
            {
               return _parameters;
            }
            set
            {
                _parameters = value;
            }
        }

        /// <summary>
        /// Gets or sets the method body.
        /// </summary>
        /// <value>The method body.</value>
        public MethodBody MethodBody
        {
            get
            {
                return _methodBody;
            }
            set
            {
                _methodBody = value;
            }
        }    

    }
}
