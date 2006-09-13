using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel;  
using Composestar.Repository.LanguageModel.Inlining;
using Composestar.Repository;   

namespace Composestar.StarLight.ILWeaver.DataRetriever
{
    /// <summary>
    /// A layer between the IL weaver and the repository objects. 
    /// Strong typed retrieval of the elements in the repository.
    /// </summary>
    public class RepositoryRetriever
    {

        /// <summary>
        /// Gets the type info.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <returns></returns>
        public TypeElement GetTypeElement(string fullName)
        {
            TypeElement ti;
            ti = DataStoreContainer.Instance.GetTypeElement(fullName);
   
            return ti;
        }

        /// <summary>
        /// Gets the method info.
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodName">Name of the method.</param>
        /// <returns></returns>
        public MethodElement GetMethodElement(TypeElement typeInfo, string methodName)
        {
            MethodElement mi;

            if (typeInfo == null)
                return null;

            if (string.IsNullOrEmpty(methodName) )
                return null;

            mi = DataStoreContainer.Instance.GetMethodElement(typeInfo.id, methodName);
            return mi;
        }
    }
}
