using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.Configuration;  
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
        /// Initializes a new instance of the <see cref="T:RepositoryAccess"/> class.
        /// </summary>
        /// <param name="filename">The repository filename.</param>
        public RepositoryAccess(string filename)
        {
            if (string.IsNullOrEmpty(filename))
                throw new ArgumentNullException("filename");

            DataStoreContainer.Instance.RepositoryFileName = filename;
            
        }

        /// <summary>
        /// Closes the database.
        /// </summary>
        public void CloseDatabase()
        {
            DataStoreContainer.Instance.CloseDatabase();  
        }

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
            IList<TypeElement> ret = DataStoreContainer.Instance.GetObjectQuery<TypeElement>(delegate (TypeElement te)
            {
                return te.FullName.Equals(fullName);  
            });

            if (ret.Count == 1)
                return ret[0];
            else 
                return null;          
        }

        /// <summary>
        /// Gets the type elements.
        /// </summary>
        /// <returns></returns>
        public IList<TypeElement> GetTypeElements()
        {
            return DataStoreContainer.Instance.GetObjects<TypeElement>(); 
        }

        /// <summary>
        /// Gets the method info.
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodName">Name of the method.</param>
        /// <returns></returns>
        public MethodElement GetMethodElement(TypeElement typeInfo, string methodName)
        {
            
            if (typeInfo == null)
                return null;

            if (string.IsNullOrEmpty(methodName) )
                return null;

            IList<MethodElement> ret = DataStoreContainer.Instance.GetObjectQuery<MethodElement>(delegate (MethodElement me)
            {
                return (me.ParentTypeId == typeInfo.Id) && (me.Name.Equals(methodName));  
            });

            if (ret.Count == 1)
                return ret[0];
            else 
                return null;
                       
        }

        /// <summary>
        /// Gets the method elements.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<MethodElement> GetMethodElements(TypeElement type)
        {
            //Query query = dbContainer.Query();
            //query.Constrain(typeof(Composestar.Repository.LanguageModel.MethodElement));
            //query.Descend("_parentTypeId").Constrain(type.Id);
            //ObjectSet result = query.Execute();

            // >>> this is slow, no idea why ?
            IList<MethodElement> result = DataStoreContainer.Instance.GetObjectQuery<MethodElement>(delegate(MethodElement me)
            {
                return me.ParentTypeId == type.Id;
            });

            return result;

            //List<MethodElement> m = new List<MethodElement>();
            //foreach (MethodElement me in result)
            //{
            //    m.Add(me);
            //}
            
            //return m;
        }

        /// <summary>
        /// Adds the type element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        public void AddType(TypeElement typeElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement") ;

            // Check if type already exists
            if (DataStoreContainer.Instance.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.FromDLL.Equals(typeElement.FromDLL, StringComparison.CurrentCultureIgnoreCase) &
                       te.FullName.Equals(typeElement.FullName, StringComparison.CurrentCultureIgnoreCase);
            }).Count == 0)
            {
                DataStoreContainer.Instance.StoreObject(typeElement);
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

            fieldElement.ParentTypeId = typeElement.Id;

            DataStoreContainer.Instance.StoreObject(fieldElement);
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
 
            DataStoreContainer.Instance.StoreObject(methodElement);
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

            DataStoreContainer.Instance.StoreObject(paramElement);
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

            callElement.ParentMethodBodyId = methodElement.MethodBody.Id; 

            DataStoreContainer.Instance.StoreObject(callElement);
        }

        /// <summary>
        /// Adds the concern.
        /// </summary>
        /// <param name="concernInformation">The concern information.</param>
        public void AddConcern(ConcernInformation concernInformation)
        {
            if (concernInformation == null)
                throw new ArgumentNullException("concernInformation") ;
           
            // Check if concern already exists
            if (DataStoreContainer.Instance.GetObjectQuery<ConcernInformation>(delegate(ConcernInformation ce)
            {
                return ce.Filename.Equals(concernInformation.Filename, StringComparison.CurrentCultureIgnoreCase);
            }).Count == 0)
            {
                DataStoreContainer.Instance.StoreObject(concernInformation);
            }
        }

        /// <summary>
        /// Gets the common configuration.
        /// </summary>
        /// <returns></returns>
        public CommonConfiguration GetCommonConfiguration()
        {
            CommonConfiguration cc;
            IList<CommonConfiguration> ret = DataStoreContainer.Instance.GetObjects<CommonConfiguration>();
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
 
            DataStoreContainer.Instance.StoreObject(cc);  
        }

    }
}
