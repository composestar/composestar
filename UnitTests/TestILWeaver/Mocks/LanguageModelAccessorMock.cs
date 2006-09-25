using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel;
using Composestar.Repository.LanguageModel.ConditionExpressions;
using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.CoreServices;

namespace TestILWeaver.Mocks
{
    class LanguageModelAccessorMock : ILanguageModelAccessor
    {
        private Dictionary<string, TypeElement> _typeElementsByName = new Dictionary<string, TypeElement>();
        private Dictionary<TypeElement, List<Internal>> _internalsByTypeElement = new Dictionary<TypeElement, List<Internal>>();
        private Dictionary<TypeElement, List<MethodElement>> _methodsByTypeElement = new Dictionary<TypeElement, List<MethodElement>>();

        private void AddTypeElement(TypeElement element)
        {
            if (!_typeElementsByName.ContainsKey(element.FullName))
                _typeElementsByName.Add(element.FullName, element);
            else
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


        private void AddMethod(TypeElement type, MethodElement method)
        {
            if (!_methodsByTypeElement.ContainsKey(type))
            {
                _methodsByTypeElement.Add(type, new List<MethodElement>());
            }
            _methodsByTypeElement[type].Add(method);
        }

        #region ILanguageModelAccessor Members

        public MethodElement GetMethodElementBySignature(TypeElement typeInfo, string methodSignature)
        {
            if (_methodsByTypeElement.ContainsKey(typeInfo))
                foreach (MethodElement method in _methodsByTypeElement[typeInfo])
                {
                    if (method.Signature.Equals(methodSignature))
                        return method;
                }
            return null;
        }

        public IList<MethodElement> GetMethodElements(TypeElement type)
        {
            return _methodsByTypeElement[type];
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

        public void AddInputFilter(string typeName, string methodSignature, InlineInstruction instruction)
        {
            TypeElement typeElement = new TypeElement();
            typeElement.FullName = typeName;

            AddTypeElement(typeElement);

            MethodElement methodElement = new MethodElement();
            methodElement.Signature = methodSignature;

            methodElement.MethodBody.InputFilter = instruction;

            AddMethod(typeElement, methodElement);
             
        }
        
    }
}
