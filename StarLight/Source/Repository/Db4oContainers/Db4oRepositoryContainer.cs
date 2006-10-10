using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

using com.db4o;
using com.db4o.config;
using com.db4o.ext;
using com.db4o.query;

using Composestar.Repository.LanguageModel;
using Composestar.StarLight.CoreServices;
  
namespace Composestar.Repository.Db4oContainers
{
    /// <summary>
    /// 
    /// </summary>
    public class Db4oRepositoryContainer : IRepositoryContainer 
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
                //if (!_yapFileName.Equals(value, StringComparison.CurrentCultureIgnoreCase))
                //{
                // Changing the name, so close and reopen.
                //if (dbContainer != null)
                //    dbContainer.Close();

                _yapFileName = value;
                //OpenContainer();
                //}
                //else
                //{
                // Name of db did not change, see if the db is open
                //OpenContainer();                    
                //}
            }
        }

        private ObjectContainer dbContainer;

        #region Singleton
        static readonly Db4oRepositoryContainer instance = new Db4oRepositoryContainer();

        // Explicit static constructor to tell C# compiler
        // not to mark type as beforefieldinit
        static Db4oRepositoryContainer()
        {
        }

        Db4oRepositoryContainer()
        {
            Db4oConfiguration.Configure();
        }

        /// <summary>
        /// Gets the instance.
        /// </summary>
        /// <value>The instance.</value>
        public static Db4oRepositoryContainer Instance
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
        ~Db4oRepositoryContainer()
        {
            CloseContainer();
            dbContainer.Dispose();
        }
        #endregion

        private bool _debugMode = false;

        /// <summary>
        /// Gets or sets a value indicating whether [debug mode].
        /// </summary>
        /// <value><c>true</c> if [debug mode]; otherwise, <c>false</c>.</value>
        public bool DebugMode
        {
            get { return _debugMode; }
            set { _debugMode = value; }
        }

        /// <summary>
        /// Opens the database.
        /// Setting the repository filename has the same effect.
        /// </summary>        
        public void OpenContainer(String fileName)
        {
            RepositoryFileName = fileName;
            if (File.Exists(fileName)) AdjustClassNames();
            dbContainer = Db4o.OpenFile(fileName);

            //((YapStream)dbContainer).GetNativeQueryHandler().QueryExecution += new com.db4o.inside.query.QueryExecutionHandler(Program_QueryExecution);
 
        }

        /// <summary>
        /// Closes the database.
        /// </summary>
        public void CloseContainer()
        {
            if (dbContainer != null)
                dbContainer.Close();
        }

        /// <summary>
        /// Deletes the database.
        /// </summary>
        public void DeleteContainer()
        {
            CheckForOpenDatabase();

            CloseContainer();

            File.Delete(_yapFileName);
        }

        static void Program_QueryExecution(object sender, com.db4o.inside.query.QueryExecutionEventArgs args)
        {
            //using (StreamWriter writer = File.AppendText(GetPersonalFilePath("CFNativeQueriesEnabler.Example.txt")))
            //{/
            Console.WriteLine("{0} - {1}: {2}", DateTime.Now, args.ExecutionKind, args.Predicate);
            //}
        }

        /// <summary>
        /// Adjusts the class names.
        /// </summary>
        private void AdjustClassNames()
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
        /// <param name="obj">The object to store.</param>
        public void StoreObject(Object obj)
        {
            if (obj == null)
                throw new ArgumentNullException("object", Properties.Resources.ObjectIsNull);

            CheckForOpenDatabase();

            dbContainer.Set(obj);
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
        /// Get object
        /// </summary>
        /// <param name="match">Match</param>
        /// <returns>T</returns>
        public T GetObject<T>(Predicate<T> match)
        {
            CheckForOpenDatabase();

            IList<T> ret = dbContainer.Query<T>(match);

            if (ret.Count == 1)
                return ret[0];
            else
                return default(T);

        } // GetObject`1(match)

        /// <summary>
        /// Checks for an open database. Throws an <see cref="T:ArgumentException"></see> when the database is not opened or a filename is not specified.
        /// </summary>
        private void CheckForOpenDatabase()
        {
            if (String.IsNullOrEmpty(_yapFileName) | dbContainer == null)
                throw new ArgumentException(Properties.Resources.DatabaseNotOpen, "RepositoryFilename");

        }

        /// <summary>
        /// Check if the specified type exists in the database using a SODA query.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <param name="fromDLL">The assembly filename.</param>
        /// <returns><c>True</c> when the type exists, <c>false</c> otherelse.</returns>
        public bool TypeExists(string fullName, string fromDLL)
        {
            Query query = dbContainer.Query();
            query.Constrain(typeof(Composestar.Repository.LanguageModel.TypeElement));
            query.Descend("_fullName").Constrain(fullName);
            query.Descend("_fromDLL").Constrain(fromDLL);
            ObjectSet result = query.Execute();

            if (result.Count == 1) return true;

            return false;
        }

        /// <summary>
        /// Deletes the objects.
        /// </summary>
        public void DeleteObjects<T>()
        {
            CheckForOpenDatabase();

            ObjectSet result = dbContainer.Get(typeof(T));
            foreach (object item in result)
            {
                dbContainer.Delete(item);
            }
        }

        /// <summary>
        /// Deletes the objects.
        /// </summary>
        /// <param name="match">The match.</param>
        public void DeleteObjects<T>(Predicate<T> match)
        {

            CheckForOpenDatabase();

            IList<T> result = dbContainer.Query<T>(match);
            foreach (object item in result)
            {
                dbContainer.Delete(item);
            }
        }

        public void Commit()
        {
            dbContainer.Commit();
        }
    }
}
