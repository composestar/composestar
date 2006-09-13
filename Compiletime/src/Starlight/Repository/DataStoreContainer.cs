using System;
using System.Collections.Generic;
using System.Text;

using com.db4o;

using Composestar.Repository.LanguageModel;

namespace Composestar.Repository
{
    public class DataStoreContainer
    {
        private const string YapFileName = "test.yap";

        private ObjectContainer dbContainer;

        #region Singleton
        static readonly DataStoreContainer instance = new DataStoreContainer();

        // Explicit static constructor to tell C# compiler
        // not to mark type as beforefieldinit
        static DataStoreContainer()
        {
        }

        DataStoreContainer()
        {
            System.IO.File.Delete(YapFileName);
            Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("id").Indexed(true);  
            Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("FullName").Indexed(true);
            dbContainer = Db4o.OpenFile(YapFileName);
        }

        public static DataStoreContainer Instance
        {
            get
            {
                return instance;
            }
        }

        // Destructor, closes the database file
        ~DataStoreContainer()
        {
             dbContainer.Close();
        }
        #endregion

        #region Types functionality
        /// <summary>
        /// Gets the type elements.
        /// </summary>
        /// <returns></returns>
        public IList<TypeElement> GetTypeElements()
        {
            return dbContainer.Query<TypeElement>(typeof(TypeElement));
        }

        /// <summary>
        /// Gets the type element.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <returns></returns>
        public TypeElement GetTypeElement(string fullName)
        {
            IList<TypeElement> ret = dbContainer.Query<TypeElement>(delegate (TypeElement te)
            {
                return te.FullName.Equals(fullName);  
            });

            if (ret.Count == 1)
                return ret[0];
            else 
                return null;
        }

        /// <summary>
        /// Adds the type.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        public void AddTypeElement(TypeElement typeElement)
        {
            dbContainer.Set(typeElement);

        }

        #endregion

        /// <summary>
        /// Adds the method.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        public void AddMethodElement(MethodElement methodElement)
        {
            dbContainer.Set(methodElement);
        }

        /// <summary>
        /// Gets the method element.
        /// </summary>
        /// <param name="typeId">The type id.</param>
        /// <param name="methodName">Name of the method.</param>
        /// <returns></returns>
        public MethodElement GetMethodElement(int typeId, string methodName)
        {
            IList<MethodElement> ret = dbContainer.Query<MethodElement>(delegate (MethodElement me)
            {
                return (me.TypeId == typeId) && (me.Name.Equals(methodName));  
            });

            if (ret.Count == 1)
                return ret[0];
            else 
                return null;
        }
    }
}
