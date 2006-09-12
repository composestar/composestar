using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository.LanguageModel
{

    public class MethodInfo
    {
        private string _name;
        private string _returntype;
        private IList<ParameterInfo> _parameters;  // initialize db-aware with ObjectContainer().Ext().Collections().NewLinkedList(), which doesnt support generics
        private IList<CallInfo> _calls;
        // TODO: add the folling properties: callconstruction??, iscontructor, isabstract, isfinal, isprivate, ispublic, isstactic, isvirtual

        public MethodInfo()
        {
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
