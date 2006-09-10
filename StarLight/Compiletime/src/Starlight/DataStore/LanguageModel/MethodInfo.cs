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
        private IList _parameters;
        
        // TODO: add the folling properties: callconstruction??, iscontructor, isabstract, isfinal, isprivate, ispublic, isstactic, isvirtual
        
        
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        public string ReturnType
        {
            get { return _returntype; }
            set { _returntype = value; }
        }

        public IList Parameters
        {
            get {
                if (_parameters == null) _parameters = DataStore.GetInstance().GetObjectContainer().Ext().Collections().NewLinkedList();
                
                return _parameters; 
            }
            set {
                if (_parameters == null) _parameters = DataStore.GetInstance().GetObjectContainer().Ext().Collections().NewLinkedList();
 
                _parameters = value; 
            }
        }

    }
}
