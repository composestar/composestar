using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Text;
using com.db4o;

namespace Composestar.DataStore
{
    /// <summary>
    /// Contains the DataStore.
    /// </summary>
    public class DataStoreContainer
    {
        private const string databaseFilename = "test.yap";

        private bool _open;
        private ObjectContainer database;

        #region Singleton
        static readonly DataStoreContainer instance = new DataStoreContainer();

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

        // Destructor, closes the database file
        ~DataStoreContainer()
        {
            database.Close();
            //database.Dispose();
        }
        #endregion

        #region Database IO
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Open()
        {
            if (!_open)
            {
                database = Db4o.OpenFile(databaseFilename);
                _open = true;
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Close()
        {
            if (_open)
            {
                //database.Commit();
                database.Close();
                _open = false;
            }
        }

        public ObjectContainer GetObjectContainer()
        {
            return database;
        }
        #endregion

        #region Type Accessabilty
        public void AddType(LanguageModel.TypeInfo typeinfo)
        {
            if (!_open) this.Open();

            database.Set(typeinfo);
        }

        public bool ContainsType(String fullname)
        {
            IList<LanguageModel.TypeInfo> types = database.Query<LanguageModel.TypeInfo>(delegate(LanguageModel.TypeInfo ti)
            {
                return ti.FullName == fullname;
            });

            if (types != null)
            {
                if (types.Count > 0) return true;
            }

            return false;
        }

        public IEnumerator<LanguageModel.TypeInfo> GetTypeEnumerator()
        {
            IList<LanguageModel.TypeInfo> types = database.Query<LanguageModel.TypeInfo>(delegate(LanguageModel.TypeInfo ti)
            {
                return true;
            });
            
            return types.GetEnumerator();
        }

        public LanguageModel.TypeInfo GetType(string fullname)
        {
            IList<LanguageModel.TypeInfo> types = database.Query<LanguageModel.TypeInfo>(delegate(LanguageModel.TypeInfo ti)
            {
                return ti.FullName == fullname;
            });

            if (types != null)
            {
                if (types.Count == 1) return types[0];
            }

            return null;
        }
        #endregion


    }
}
