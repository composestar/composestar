using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace Composestar.DataStore.LanguageModel
{
    public class TypeInfo
    {
        private string _name;
        private string _fullname;
        private string _basetype;
        private bool _isabstract;
        private bool _isinterface;
        private bool _issealed;
        private bool _isvaluetype;
        private bool _isenum;
        private IList _methods;  // initialize db-aware with ObjectContainer().Ext().Collections().NewLinkedList(), which doesnt support generics

        // TODO: Add the following properties
        // assemblyname/assemblyinfo type is contained in
        //type.Attributes
        //type.Fields
        //type.CustomAttributes
        //type.Properties

        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        public string FullName
        {
            get { return _fullname; }
            set { _fullname = value; }
        }

        public string BaseType
        {
            get { return _basetype; }
            set { _basetype = value; }
        }

        public bool IsAbstract
        {
            get { return _isabstract; }
            set { _isabstract = value; }
        }

        public bool IsEnum
        {
            get { return _isenum; }
            set { _isenum = value; }
        }

        public bool IsInterface
        {
            get { return _isinterface; }
            set { _isinterface = value; }
        }

        public bool IsSealed
        {
            get { return _issealed; }
            set { _issealed = value; }
        }

        public bool IsValueType
        {
            get { return _isvaluetype; }
            set { _isvaluetype = value; }
        }

        public void AddMethod(MethodInfo methodinfo)
        {
            if (_methods == null) _methods = DataStore.GetInstance().GetObjectContainer().Ext().Collections().NewLinkedList();
  
            _methods.Add(methodinfo);
        }

        public int MethodCount()
        {
            if (_methods == null) return 0;

            return _methods.Count;
        }

        public IEnumerator<LanguageModel.MethodInfo> GetMethodEnumerator()
        {
            if (_methods == null) return null;

            IList<LanguageModel.MethodInfo> methods = DataStore.GetInstance().GetObjectContainer().Query<LanguageModel.MethodInfo>(delegate(LanguageModel.MethodInfo mi)
            {
                return true;
            });

            return methods.GetEnumerator();
        }

    }
}
