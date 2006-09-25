using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.CoreServices;
using Composestar.Repository.LanguageModel;

namespace TestILWeaver.Mocks
{
    class LanguageModelAccessorMock : ILanguageModelAccessor
    {
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
            return null;
        }

        public IList<TypeElement> GetTypeElements()
        {
            return new List<TypeElement>();
        }

        public IList<Internal> GetInternalsByTypeElement(TypeElement typeElement)
        {
            List<Internal> internals = new List<Internal>();

            return internals;
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
    }
}
