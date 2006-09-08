using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.DataStoreDotNET
{
    class DataStore
    {
        private static DataStore instance;
        private ObjectContainer database;

        // Constructor, opens the database file
        private DataStore()
        {
            database = Db4o.OpenFile("test.yap");
        }

        // Destructor, closes the database file
        ~DataStore()
        {
            database.Close();
            database.Dispose();
        }

        public static DataStore GetInstance()
        {
            if (instance == null)
            {
                instance = new DataStore();
            }

            return instance;
        }


    }
}
