using System;
using Composestar.Repository.LanguageModel;
using System.Collections.Generic;

namespace Composestar.StarLight.CoreServices
{
    public interface ILanguageModelAccessor
    {
        MethodElement GetMethodElementBySignature(TypeElement typeInfo, string methodSignature);
        
        IList<MethodElement> GetMethodElements(TypeElement type);
        
        TypeElement GetTypeElement(string fullName);

        IList<TypeElement> GetTypeElements();

        IList<Internal> GetInternalsByTypeElement(TypeElement typeElement);

        IList<External> GetExternalsByTypeElement(TypeElement typeElement);

        IList<CallElement> GetCallByMethodElement(MethodElement methodElement);
    }
}
