using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace Composestar.DataStore.LanguageModel
{
    public class MethodInfo
    {
        private string _name;
        private string _returntype;
        private IList _parameters;  // initialize db-aware with ObjectContainer().Ext().Collections().NewLinkedList(), which doesnt support generics
        private IList _calls;
        // TODO: add the folling properties: callconstruction??, iscontructor, isabstract, isfinal, isprivate, ispublic, isstactic, isvirtual


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
        public IList Parameters
        {
            get {
                if (_parameters == null) _parameters = DataStoreContainer.Instance.GetObjectContainer().Ext().Collections().NewLinkedList();
                
                return _parameters; 
            }
            set {
                if (_parameters == null) _parameters = DataStoreContainer.Instance.GetObjectContainer().Ext().Collections().NewLinkedList();
 
                _parameters = value; 
            }
        }

        /// <summary>
        /// Gets or sets the calls.
        /// </summary>
        /// <value>The calls made by this method.</value>
        public IList Calls
        {
            get
            {
                if (_calls == null) _calls = DataStoreContainer.Instance.GetObjectContainer().Ext().Collections().NewLinkedList();

                return _calls;
            }
            set
            {
                if (_calls == null) _calls = DataStoreContainer.Instance.GetObjectContainer().Ext().Collections().NewLinkedList();

                _calls = value;
            }
        }

    }
}
