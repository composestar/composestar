using System;
using System.Collections.Generic;
using System.IO;
using System.IO.IsolatedStorage;
using System.Text;

using com.db4o;
using com.db4o.config;
using com.db4o.ext;
using com.db4o.query;

using Composestar.Repository.LanguageModel;

namespace Composestar.Repository.Db4oContainers
{
    public class Db4oCacheContainer : CacheContainerInterface
    {        
        /// <summary>
        /// Filename of the yap database.
        /// </summary>
        private string _yapFileName = String.Empty;

        private System.Collections.Hashtable dbContainer;
        private String _cacheFolder = String.Empty;

        #region Singleton
        static readonly Db4oCacheContainer instance = new Db4oCacheContainer();

        // Explicit static constructor to tell C# compiler
        // not to mark type as beforefieldinit
        static Db4oCacheContainer()
        {
        }

        Db4oCacheContainer()
        {
            // Configure the db4o engine
            Db4oConfiguration.Configure();
            dbContainer = new System.Collections.Hashtable();
        }

        /// <summary>
        /// Gets the instance.
        /// </summary>
        /// <value>The instance.</value>
        public static Db4oCacheContainer Instance
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
        ~Db4oCacheContainer()
        {
            CloseContainer();
            //if (dbContainer != null) dbContainer.Dispose(); 
        }
        #endregion

        private bool _debugMode = false;

        public bool DebugMode
        {
            get { return _debugMode; }
            set { _debugMode = value; }
        }
	

        public String CacheFolder
        {
            get { return _cacheFolder; }
            set { _cacheFolder = value; }
        }

        /// <summary>
        /// Opens the database.
        /// </summary>  
        public void OpenContainer(String fileName)
        {
            if (!dbContainer.Contains(fileName))
            {

            }

            if (this._yapFileName == String.Empty)
            {
                if (!Directory.Exists(Path.Combine(CacheFolder, "TypesCache")))
                {
                    Directory.CreateDirectory(Path.Combine(CacheFolder, "TypesCache"));
                }

                this._yapFileName = Path.Combine(CacheFolder, "TypesCache\\types.yap");
            }

           // dbContainer = Db4o.OpenFile(this._yapFileName);

            if (DebugMode)
            {
               // ((YapStream)dbContainer).GetNativeQueryHandler().QueryExecution += new com.db4o.inside.query.QueryExecutionHandler(Program_QueryExecution);
            }

        }

        static void Program_QueryExecution(object sender, com.db4o.inside.query.QueryExecutionEventArgs args)
        {
            //using (StreamWriter writer = File.AppendText(GetPersonalFilePath("CFNativeQueriesEnabler.Example.txt")))
            //{/
                Console.WriteLine("{0} - {1}: {2}", DateTime.Now, args.ExecutionKind, args.Predicate);
            //}
        }


        /// <summary>
        /// Closes the database.
        /// </summary>
        public void CloseContainer()
        {
            //if (dbContainer != null)
               // dbContainer.Close();
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

            //dbContainer.Set(o);
        }

        public IList<T> GetObjectByAFQN<T>(Predicate<T> match, String AFQN)
        {



            return null;
           // return dbContainer.Query<T>(match);
        }

        /// <summary>
        /// Gets the objects.
        /// </summary>
        /// <returns></returns>
        public IList<T> GetObjects<T>()
        {
            CheckForOpenDatabase();
            Type t = typeof(T);

            return null;// dbContainer.Query<T>(t);
        }

        /// <summary>
        /// Gets the object query.
        /// </summary>
        /// <param name="match">The match.</param>
        /// <returns></returns>
        public IList<T> GetObjectQuery<T>(Predicate<T> match)
        {
            CheckForOpenDatabase();

            return null;// dbContainer.Query<T>(match);
        }

        /// <summary>
        /// Checks for open database.
        /// </summary>
        private void CheckForOpenDatabase()
        {
            if (String.IsNullOrEmpty(_yapFileName) | dbContainer == null)
                throw new ArgumentException(Properties.Resources.DatabaseNotOpen, "RepositoryFilename");

        }

        public com.db4o.query.Query Query()
        {
            return null;// dbContainer.Query();
        }
    }
}
