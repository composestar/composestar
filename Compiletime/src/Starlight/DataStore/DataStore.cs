using System;
using System.Collections.Generic;
using System.Text;
using com.db4o;

namespace Composestar.DataStoreDotNET
{
    /// <summary>
    /// Contains the DataStore.
    /// </summary>
    class DataStoreContainer
    {
        private const string databaseFilename = "test.yap";

        private ObjectContainer database;

        #region Singleton
        static readonly DataStoreContainer instance =new DataStoreContainer();

        // Explicit static constructor to tell C# compiler
        // not to mark type as beforefieldinit
        static DataStoreContainer()
        {
        }

        DataStoreContainer()
        {
            database = Db4o.OpenFile(databaseFilename);
        }

        public static DataStoreContainer Instance
        {
            get
            {
                return instance;
            }
        }
        #endregion

        // Destructor, closes the database file
        ~DataStoreContainer()
        {
            database.Close();
            database.Dispose();
        }



   
    }
}
