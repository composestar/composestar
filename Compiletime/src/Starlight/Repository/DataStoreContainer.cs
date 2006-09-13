using System;
using System.Collections.Generic;
using System.Text;

using com.db4o;
using com.db4o.config;
using com.db4o.ext;
using com.db4o.query;

using Composestar.Repository.LanguageModel;

namespace Composestar.Repository
{
    public class DataStoreContainer
    {
        private const string YapFileName = "test.yap";

        private static String[] ASSEMBLY_MAP = new String[]{
            "Composestar.Repository.LanguageModel",
            "Repository.LanguageModel",
        };

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
            adjustClassNames();

            Db4o.Configure().CallConstructors(false);

            // Indexes
            Db4o.Configure().ObjectClass(typeof(MethodElement)).ObjectField("_parentTypeId").Indexed(true);
            Db4o.Configure().ObjectClass(typeof(ParameterElement)).ObjectField("_parentMethodId").Indexed(true);

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

        public static void adjustClassNames()
        {
            ObjectContainer objectContainer = Db4o.OpenFile(YapFileName);
            
            StoredClass[] classes = objectContainer.Ext().StoredClasses();
            for (int i = 0; i < classes.Length; i++)
            {
                StoredClass storedClass = classes[i];
                String name = storedClass.GetName();
                String newName = null;
                int pos = name.IndexOf(",");
                if (pos == -1)
                {
                    for (int j = 0; j < ASSEMBLY_MAP.Length; j += 2)
                    {
                        pos = name.IndexOf(ASSEMBLY_MAP[j]);
                        if (pos == 0)
                        {
                            newName = name + ", " + ASSEMBLY_MAP[j + 1];
                            break;
                        }
                    }
                }
                if (newName != null)
                {
                    storedClass.Rename(newName);
                }
            }
            objectContainer.Close();
        }

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

        public IList<MethodElement> GetMethodElements(Composestar.Repository.LanguageModel.TypeElement type)
	    {
		    Query query = dbContainer.Query();
		    query.Constrain(typeof(Composestar.Repository.LanguageModel.MethodElement));
		    query.Descend("_parentTypeId").Constrain(type.Id);
		    ObjectSet result = query.Execute();

            // >>> this is slow, no idea why ?
            //IList<MethodElement> result = dbContainer.Query<MethodElement>(delegate(MethodElement me)
            //{
            //    return me.ParentTypeId == type.Id;
            //});

            List<MethodElement> m = new List<MethodElement>();
            foreach (MethodElement me in result)
            {
                m.Add(me);
            }
            
            return m;
	    }


        /// <summary>
        /// Adds the method.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        public void AddMethodElement(MethodElement methodElement)
        {
            dbContainer.Set(methodElement);
        }

        public void AddParameterElement(ParameterElement parameterelement)
        {
            dbContainer.Set(parameterelement);
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
                return (me.ParentTypeId == typeId) && (me.Name.Equals(methodName));  
            });

            if (ret.Count == 1)
                return ret[0];
            else 
                return null;
        }
    }
}
