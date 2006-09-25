using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.CoreServices;
using Composestar.Repository.LanguageModel;

namespace TestILWeaver.Mocks
{
    class LanguageModelAccessorMock : ILanguageModelAccessor
    {
        private Dictionary<string, TypeElement> _typeElementsByName = new Dictionary<string, TypeElement>();
        private Dictionary<TypeElement, List<Internal>> _internalsByTypeElement = new Dictionary<TypeElement, List<Internal>>();

        private void AddTypeElement(TypeElement element)
        {
            _typeElementsByName[element.FullName] = element;
        }

        private void AddInternal(TypeElement type, Internal @internal)
        {
            if (!_internalsByTypeElement.ContainsKey(type))
            {
                _internalsByTypeElement.Add(type, new List<Internal>());
            }
            _internalsByTypeElement[type].Add(@internal);
        }


        #region ILanguageModelAccessor Members

        public MethodElement GetMethodElementBySignature(TypeElement typeInfo, string methodSignature)
        {
            return null;
        }

        public IList<MethodElement> GetMethodElements(TypeElement type)
        {
            return new List<MethodElement>();
        }

        public TypeElement GetTypeElement(string fullName)
        {
            if (_typeElementsByName.ContainsKey(fullName))
            {
                return _typeElementsByName[fullName];
            }

            return null;
        }

        public IList<TypeElement> GetTypeElements()
        {
            return new List<TypeElement>( _typeElementsByName.Values );
        }

        public IList<Internal> GetInternalsByTypeElement(TypeElement typeElement)
        {
            if (_internalsByTypeElement.ContainsKey(typeElement))
            {
                return _internalsByTypeElement[typeElement];
            }
            return new List<Internal>();
        }

        public IList<External> GetExternalsByTypeElement(TypeElement typeElement)
        {
            return new List<External>();
        }

        public IList<CallElement> GetCallByMethodElement(MethodElement methodElement)
        {
            return new List<CallElement>();
        }

        public void Close()
        {

        }

        #endregion

        internal void AddInternalToType(string typeName, string internalTypeName, string internalName)
        {
            TypeElement typeElement = new TypeElement();
            typeElement.FullName = typeName;

            AddTypeElement(typeElement);

            Internal intrnal = new Internal();
            intrnal.Type = internalTypeName;
            intrnal.Name = internalName;

            AddInternal(typeElement, intrnal);
        }
    }
}
