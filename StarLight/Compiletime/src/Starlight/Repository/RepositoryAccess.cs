using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel;  
using Composestar.Repository.LanguageModel.Inlining;

namespace Composestar.Repository
{
    /// <summary>
    /// A layer between the StarLight code and the repository objects. 
    /// Strong typed retrieval of the elements in the repository.
    /// </summary>
    public class RepositoryAccess
    {

        /// <summary>
        /// Gets or sets the filename of the repository.
        /// </summary>
        /// <value>The filename.</value>
        public string Filename
        {
            get
            {
                return DataStoreContainer.Instance.RepositoryFileName;
            }
            set
            {
                DataStoreContainer.Instance.RepositoryFileName = value;
            }
        }

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
        /// Gets the type elements.
        /// </summary>
        /// <returns></returns>
        public IList<TypeElement> GetTypeElements()
        {
            return DataStoreContainer.Instance.GetTypeElements();  
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

            mi = DataStoreContainer.Instance.GetMethodElement(typeInfo.Id, methodName);
            return mi;
        }

        /// <summary>
        /// Adds the type element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        public void AddType(TypeElement typeElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement") ;

            Composestar.Repository.DataStoreContainer.Instance.StoreObject(typeElement);
        }

        /// <summary>
        /// Adds the method element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <param name="methodElement">The method element.</param>
        public void AddMethod(TypeElement typeElement, MethodElement methodElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement") ;

            if (methodElement == null)
                throw new ArgumentNullException("methodElement") ;

            methodElement.ParentTypeId = typeElement.Id;
 
            Composestar.Repository.DataStoreContainer.Instance.StoreObject(methodElement);
        }


        /// <summary>
        /// Adds the parameter.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <param name="paramElement">The param element.</param>
        public void AddParameter(MethodElement methodElement, ParameterElement paramElement)
        {
            if (methodElement == null)
                throw new ArgumentNullException("methodElement") ;

            if (paramElement == null)
                  throw new ArgumentNullException("paramElement");

            paramElement.ParentMethodId = methodElement.Id; 

            Composestar.Repository.DataStoreContainer.Instance.StoreObject(paramElement);
        }


        /// <summary>
        /// Adds the call to method.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <param name="callElement">The call element.</param>
        public void AddCallToMethod(MethodElement methodElement, CallElement callElement)
        {
            if (methodElement == null)
                throw new ArgumentNullException("methodElement") ;

            if (callElement == null)
                  throw new ArgumentNullException("callElement");

            callElement.ParentMethodBody = methodElement.MethodBody; 

            Composestar.Repository.DataStoreContainer.Instance.StoreObject(callElement);
        }
    }
}
