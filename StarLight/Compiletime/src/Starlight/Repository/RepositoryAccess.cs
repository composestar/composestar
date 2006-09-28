using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.Configuration;
using Composestar.Repository.LanguageModel;
using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.CoreServices;

namespace Composestar.Repository
{
 
    /// <summary>
    /// A layer between the StarLight .NET code and the repository objects. 
    /// Strong typed retrieval of the elements in the repository and 
    /// the ability to add objects to the underlying datastore.
    /// </summary>
    public class RepositoryAccess : ILanguageModelAccessor
    {
        private RepositoryContainerInterface container = null;
        
        /// <summary>
        /// Initializes a new instance of the <see cref="T:RepositoryAccess"/> class.
        /// </summary>
        /// <param name="filename">The repository filename.</param>
        [Obsolete("Use the RepositoryAccess(RepositoryContainerInterface datastore, string fileName) constructor instead")]
        public RepositoryAccess(string fileName)
        {
            if (string.IsNullOrEmpty(fileName))
                throw new ArgumentNullException("filename");

            container = Db4oContainers.Db4oRepositoryContainer.Instance;

            OpenContainer(fileName);
        }

        public RepositoryAccess(RepositoryContainerInterface datastore, string fileName)
        {
            if (string.IsNullOrEmpty(fileName))
                throw new ArgumentNullException("filename");

            container = datastore;

            OpenContainer(fileName);
        }

        /// <summary>
        /// Opens the database container.
        /// </summary>
        public void OpenContainer(String fileName)
        {
            container.OpenContainer(fileName);
        }

        /// <summary>
        /// Closes the database container.
        /// </summary>
        public void CloseContainer()
        {
            container.CloseContainer();
        }

        public AssemblyElement GetAssemblyElementByFileName(string fileName)
        {
            IList<AssemblyElement> ret = container.GetObjectQuery<AssemblyElement>(delegate(AssemblyElement ae)
            {
                return ae.FileName.Equals(fileName);
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the type info.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <returns></returns>
        public TypeElement GetTypeElement(string fullName)
        {
            IList<TypeElement> ret = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.FullName.Equals(fullName);
            });

            if (ret.Count == 1)
                return ret[0];
              
            return null;
        }

        /// <summary>
        /// Gets the type info.
        /// </summary>
        /// <param name="fullName">The AQFN.</param>
        /// <returns></returns>
        public TypeElement GetTypeElementByAFQN(string AFQN)
        {
            IList<TypeElement> ret = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.AFQN.Equals(AFQN);
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the type element by id.
        /// </summary>
        /// <param name="typeId">The type id.</param>
        /// <returns></returns>
        public TypeElement GetTypeElementById(string typeId)
        {
             IList<TypeElement> ret = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.Id.Equals(typeId);
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the type elements.
        /// </summary>
        /// <returns></returns>
        public IList<TypeElement> GetTypeElements()
        {
            return container.GetObjects<TypeElement>();
        }

        /// <summary>
        /// Gets the method info.
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodName">Name of the method.</param>
        /// <returns></returns>
        public MethodElement GetMethodElementByName(TypeElement typeInfo, string methodName)
        {
            if (typeInfo == null)
                return null;

            if (string.IsNullOrEmpty(methodName))
                return null;

            IList<MethodElement> ret = container.GetObjectQuery<MethodElement>(delegate(MethodElement me)
            {
                return (me.ParentTypeId == typeInfo.Id) & (me.Name.Equals(methodName, StringComparison.CurrentCultureIgnoreCase));
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the method element by signature.
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodSignature">The method signature.</param>
        /// <returns></returns>
        public MethodElement GetMethodElementBySignature(TypeElement typeInfo, string methodSignature)
        {
            if (typeInfo == null)
                return null;

            if (string.IsNullOrEmpty(methodSignature))
                return null;

            IList<MethodElement> ret = container.GetObjectQuery<MethodElement>(delegate(MethodElement me)
            {
                return (me.ParentTypeId == typeInfo.Id) && (me.Signature.Equals(methodSignature, StringComparison.CurrentCultureIgnoreCase));
            });

            if (ret.Count == 1)
                return ret[0];

            return null;

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
            });

            return result;
        }

        /// <summary>
        /// Gets the parameter elements.
        /// </summary>
        /// <param name="type">The method.</param>
        /// <returns></returns>
        public IList<ParameterElement> GetParameterElements(MethodElement method)
        {
            IList<ParameterElement> result = container.GetObjectQuery<ParameterElement>(delegate(ParameterElement pe)
            {
                return pe.ParentMethodId == method.Id;
            });

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
            //com.db4o.query.Query query = container.Query();
            //query.Constrain(typeof(Composestar.Repository.LanguageModel.AttributeElement));
            //query.Descend("_parentId").Constrain(element.Id);
            //ObjectSet result = query.Execute();

            //List<AttributeElement> m = new List<AttributeElement>();
            //foreach (AttributeElement me in result)
            //{
            //    m.Add(me);
            //}

            //return m;

            //IList<AttributeElement> result = container.GetObjectQuery<AttributeElement>(delegate(AttributeElement ae)
            //{
            //    return ae.ParentId == element.Id;
            //});

            //return result;
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
            });

            return result;

        }

        /// <summary>
        /// Gets the externals by type element.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<External> GetExternalsByTypeElement(TypeElement type)
        {
            if (type == null)
                return null;

            IList<External> result = container.GetObjectQuery<External>(delegate(External external)
            {
                return external.ParentTypeId == type.Id;
            });

            return result;

        }

        /// <summary>
        /// Gets the internals by type element.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<Internal> GetInternalsByTypeElement(TypeElement type)
        {
            if (type == null)
                return null;

            IList<Internal> result = container.GetObjectQuery<Internal>(delegate(Internal intern)
            {
                return intern.ParentTypeId == type.Id;
            });

            return result;

        }

        /// <summary>
        /// Gets the call by method element.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <returns></returns>
        public IList<CallElement> GetCallByMethodElement(MethodElement methodElement)
        {
            if (methodElement == null | methodElement.MethodBody == null)
                return null;

            IList<CallElement> result = container.GetObjectQuery<CallElement>(delegate(CallElement ce)
           {
               return ce.ParentMethodBodyId == methodElement.MethodBody.Id;
           });

            return result;
        }

        /// <summary>
        /// Gets the condition by name.
        /// </summary>
        /// <param name="name">The name of the condition.</param>
        /// <returns></returns>
        public Condition GetConditionByName(string name)
        {
             IList<Condition> ret = container.GetObjectQuery<Condition>(delegate(Condition cd)
            {
                return cd.Name.Equals(name) ;
            });

            if (ret.Count == 1)
                return ret[0];

            return null; 
        }

        public void AddAssembly(AssemblyElement assemblyElement)
        {
            if (assemblyElement == null)
                throw new ArgumentNullException("assemblyElement");

            container.StoreObject(assemblyElement);
        }

        /// <summary>
        /// Adds the type element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        public void AddType(TypeElement typeElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            // Check if type already exists
            if (GetTypeElementByAFQN(typeElement.AFQN) == null)
            {
                container.StoreObject(typeElement);
            }
        }

        /// <summary>
        /// Adds the field.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <param name="fieldElement">The field element.</param>
        public void AddField(TypeElement typeElement, FieldElement fieldElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            if (fieldElement == null)
                throw new ArgumentNullException("fieldElement");
            
            // add the field element
            fieldElement.ParentTypeId = typeElement.Id;
            container.StoreObject(fieldElement);
        }

        /// <summary>
        /// Adds the method element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <param name="methodElement">The method element.</param>
        public void AddMethod(TypeElement typeElement, MethodElement methodElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            // Add the method element
            methodElement.ParentTypeId = typeElement.Id;
            container.StoreObject(methodElement);
        }

        /// <summary>
        /// Adds the parameter.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <param name="paramElement">The param element.</param>
        public void AddParameter(MethodElement methodElement, ParameterElement paramElement)
        {
            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            if (paramElement == null)
                throw new ArgumentNullException("paramElement");

            // Store the parameter element
            paramElement.ParentMethodId = methodElement.Id;
            container.StoreObject(paramElement);
        }

        /// <summary>
        /// Adds the call to method.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <param name="callElement">The call element.</param>
        public void AddCallToMethod(MethodElement methodElement, CallElement callElement)
        {
            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            if (callElement == null)
                throw new ArgumentNullException("callElement");

            callElement.ParentMethodBodyId = methodElement.MethodBody.Id;

            container.StoreObject(callElement);
        }

        /// <summary>
        /// Adds the atribute element.
        /// </summary>
        /// <param name="repositoryElement">The repository element.</param>
        /// <param name="attributeElement">The attribute element.</param>
        public void AddAttribute(IRepositoryElement repositoryElement, AttributeElement attributeElement)
        {
            if (repositoryElement == null)
                throw new ArgumentNullException("repositoryElement");

            if (attributeElement == null)
                throw new ArgumentNullException("attributeElement");

            attributeElement.ParentId = repositoryElement.Id;
            attributeElement.ParentType = repositoryElement.GetType().ToString();

            container.StoreObject(attributeElement);
        }

        /// <summary>
        /// Adds the concern.
        /// </summary>
        /// <param name="concernInformation">The concern information.</param>
        public void AddConcern(ConcernInformation concernInformation)
        {
            if (concernInformation == null)
                throw new ArgumentNullException("concernInformation");

            // Check if concern already exists
            if (container.GetObjectQuery<ConcernInformation>(delegate(ConcernInformation ce)
            {
                return ce.Filename.Equals(concernInformation.Filename, StringComparison.CurrentCultureIgnoreCase);
            }).Count == 0)
            {
                container.StoreObject(concernInformation);
            }
        }

        /// <summary>
        /// Gets the common configuration.
        /// </summary>
        /// <returns></returns>
        public CommonConfiguration GetCommonConfiguration()
        {
            IList<CommonConfiguration> ret = container.GetObjects<CommonConfiguration>();
            if (ret.Count == 1)
                return ret[0];
            else
                return new CommonConfiguration();
        }

        /// <summary>
        /// Sets the common configuration.
        /// </summary>
        /// <param name="cc">The cc.</param>
        public void SetCommonConfiguration(CommonConfiguration cc)
        {
            if (cc == null)
                throw new ArgumentNullException("commonconfiguration");

            container.StoreObject(cc);
        }

        /// <summary>
        /// Closes this instance.
        /// </summary>
        public void Close()
        {
            CloseContainer();
        }
    }
}
