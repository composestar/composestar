using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

using com.db4o;
using com.db4o.config;
using com.db4o.ext;
using com.db4o.query;

using Composestar.Repository.LanguageModel;

namespace Composestar.Repository
{
    public class DataStoreContainer
    {
        /// <summary>
        /// Filename of the yap database.
        /// </summary>
        private string _yapFileName = String.Empty;

        private static String[] ASSEMBLY_MAP = new String[]{
            "Composestar.Repository.LanguageModel",
            "Composestar.Repository.LanguageModel",
        };

        /// <summary>
        /// Gets or sets the name of the repository file.
        /// </summary>
        /// <value>The name of the repository file.</value>
        public string RepositoryFileName
        {
            get
            {
                return _yapFileName;
            }
            set
            {
                if (!_yapFileName.Equals(value, StringComparison.CurrentCultureIgnoreCase))
                {
                    // Changing the name, so close and reopen.
                    if (dbContainer != null)
                        dbContainer.Close();

                    _yapFileName = value;
                    OpenDatabase();
                }
                else
                {
                    // Name of db did not change, see if the db is open
                    OpenDatabase();                    
                }
            }
        }

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
            Db4o.Configure().CallConstructors(false);

            Db4o.Configure().ObjectClass(typeof(MethodElement)).CascadeOnUpdate(true);
            Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.Inlining.Block)).CascadeOnActivate(true);
            Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.Inlining.FilterAction)).CascadeOnActivate(true);

            // Indexes
            Db4o.Configure().ObjectClass(typeof(MethodElement)).ObjectField("_parentTypeId").Indexed(true);
            Db4o.Configure().ObjectClass(typeof(ParameterElement)).ObjectField("_parentMethodId").Indexed(true);

            Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_id").Indexed(true);
            Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_fullName").Indexed(true);
        }

        /// <summary>
        /// Gets the instance.
        /// </summary>
        /// <value>The instance.</value>
        public static DataStoreContainer Instance
        {
            get
            {
                return instance;
            }
        }

        /// <summary>
        /// Releases unmanaged resources and performs other cleanup operations before the
        /// <see cref="T:Composestar.Repository.DataStoreContainer"/> is reclaimed by garbage collection.
        /// </summary>
        /// <remarks>
        /// Destructor, closes the database file
        /// </remarks> 
        ~DataStoreContainer()
        {
            CloseDatabase();
            dbContainer.Dispose(); 
        }
        #endregion

        /// <summary>
        /// Opens the database.
        /// Setting the repository filename has the same effect.
        /// </summary>        
        public void OpenDatabase()
        {
            adjustClassNames();
            dbContainer = Db4o.OpenFile(RepositoryFileName);
        }

        /// <summary>
        /// Closes the database.
        /// </summary>
        public void CloseDatabase()
        {
            if (dbContainer != null)
                dbContainer.Close();
        }

        /// <summary>
        /// Deletes the database.
        /// </summary>
        public void DeleteDatabase()
        {
            CheckForOpenDatabase();

            CloseDatabase();

            File.Delete(_yapFileName);
        }

        /// <summary>
        /// Adjusts the class names.
        /// </summary>
        private void adjustClassNames()
        {
            ObjectContainer objectContainer = Db4o.OpenFile(RepositoryFileName);

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

        /// <summary>
        /// Store the object.
        /// </summary>
        /// <param name="o">The object to store.</param>
        public void StoreObject(Object o)
        {
            if (o == null)
                throw new ArgumentNullException("object", Properties.Resources.ObjectIsNull);

            CheckForOpenDatabase();

            dbContainer.Set(o);
        }

        /// <summary>
        /// Gets the objects.
        /// </summary>
        /// <returns></returns>
        public IList<T> GetObjects<T>()
        {
            CheckForOpenDatabase();
            Type t = typeof(T);
              
            return dbContainer.Query<T>(t);
        }

        /// <summary>
        /// Gets the object query.
        /// </summary>
        /// <param name="match">The match.</param>
        /// <returns></returns>
        public IList<T> GetObjectQuery<T>(Predicate<T> match)
        {
            CheckForOpenDatabase();

            return dbContainer.Query<T>(match);
        }

        /// <summary>
        /// Checks for open database.
        /// </summary>
        private void CheckForOpenDatabase()
        {
            if (String.IsNullOrEmpty(_yapFileName) | dbContainer == null)
                throw new ArgumentException(Properties.Resources.DatabaseNotOpen, "RepositoryFilename");

        }
    }
}
