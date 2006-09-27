using System;
using Composestar.Repository.LanguageModel;
using System.Collections.Generic;

namespace Composestar.StarLight.CoreServices
{
    public interface ILanguageModelAccessor
    {
        /// <summary>
        /// Gets the method element by signature.
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodSignature">The method signature.</param>
        /// <returns></returns>
        MethodElement GetMethodElementBySignature(TypeElement typeInfo, string methodSignature);

        /// <summary>
        /// Gets the method elements.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        IList<MethodElement> GetMethodElements(TypeElement type);

        /// <summary>
        /// Gets the type element.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <returns></returns>
        TypeElement GetTypeElement(string fullName);

        
        /// <summary>
        /// Gets the type element by Id.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <returns></returns>
        TypeElement GetTypeElementById(string typeId);

        /// <summary>
        /// Gets the type elements.
        /// </summary>
        /// <returns></returns>
        IList<TypeElement> GetTypeElements();

        /// <summary>
        /// Gets the internals by type element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <returns></returns>
        IList<Internal> GetInternalsByTypeElement(TypeElement typeElement);

        /// <summary>
        /// Gets the externals by type element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <returns></returns>
        IList<External> GetExternalsByTypeElement(TypeElement typeElement);

        /// <summary>
        /// Gets the call by method element.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <returns></returns>
        IList<CallElement> GetCallByMethodElement(MethodElement methodElement);

        /// <summary>
        /// Gets the condition by name.
        /// </summary>
        /// <param name="name">The name of the condition.</param>
        /// <returns></returns>
        Condition GetConditionByName(string name);

        /// <summary>
        /// Closes this instance.
        /// </summary>
        void Close();
    }
}
