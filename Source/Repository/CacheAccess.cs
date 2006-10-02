using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.Configuration;
using Composestar.Repository.LanguageModel;
using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.CoreServices;

namespace Composestar.Repository
{
    public class CacheAccess
    {
        private CacheContainerInterface container = null;

        public CacheAccess(CacheContainerInterface cache, String cacheFolder)
        {
            container = cache;
            cache.CacheFolder = cacheFolder;
        }

        public void CloseContainer()
        {
            container.CloseContainer();
        }

        public bool IsCached(String assembly)
        {
            return container.IsCached(assembly);
        }

        public TypeElement GetTypeElementByAFQN(string typeName, string assembly)
        {
            IList<TypeElement> ret = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.FullName.Equals(typeName) && te.Assembly.Equals(assembly);
            }, assembly);

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the field elements.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<FieldElement> GetFieldElements(TypeElement type)
        {
            IList<FieldElement> result = container.GetObjectQuery<FieldElement>(delegate(FieldElement fe)
            {
                return fe.ParentTypeId == type.Id;
            }, type.Assembly);

            return result;
        }

        /// <summary>
        /// Gets the method elements.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<MethodElement> GetMethodElements(TypeElement type)
        {
            IList<MethodElement> result = container.GetObjectQuery<MethodElement>(delegate(MethodElement me)
            {
                return me.ParentTypeId == type.Id;
            }, type.Assembly);

            return result;
        }

        /// <summary>
        /// Gets the parameter elements.
        /// </summary>
        /// <param name="type">The method.</param>
        /// <returns></returns>
        public IList<ParameterElement> GetParameterElements(TypeElement type, MethodElement method)
        {
            IList<ParameterElement> result = container.GetObjectQuery<ParameterElement>(delegate(ParameterElement pe)
            {
                return pe.ParentMethodId == method.Id;
            }, type.Assembly);

            return result;
        }

                /// <summary>
        /// Gets the parameter elements.
        /// </summary>
        /// <param name="type">The method.</param>
        /// <returns></returns>
        public IList<AttributeElement> GetAttributeElements(IRepositoryElement element)
        {
            throw new NotImplementedException("Severe performance penalty on current implementation, disabled for the time being");

        }

        public void AddType(TypeElement typeElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            // Check if type already exists
            container.StoreObject(typeElement, typeElement.Assembly);
        }

        public void AddField(TypeElement typeElement, FieldElement fieldElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            if (fieldElement == null)
                throw new ArgumentNullException("fieldElement");

            // add the field element
            fieldElement.ParentTypeId = typeElement.Id;
            container.StoreObject(fieldElement, typeElement.Assembly);
        }

        public void AddMethod(TypeElement typeElement, MethodElement methodElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            // Add the method element
            methodElement.ParentTypeId = typeElement.Id;
            container.StoreObject(methodElement, typeElement.Assembly);
        }

        public void AddParameter(TypeElement typeElement, MethodElement methodElement, ParameterElement paramElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            if (paramElement == null)
                throw new ArgumentNullException("paramElement");

            // Store the parameter element
            paramElement.ParentMethodId = methodElement.Id;
            container.StoreObject(paramElement, typeElement.Assembly);
        }
    }
}
