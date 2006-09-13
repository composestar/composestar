using System;
using System.Collections.Generic;
using System.Text;

using com.db4o;

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
        public IList<Composestar.Repository.LanguageModel.TypeInfo> GetTypes()
        {
            return dbContainer.Query<Repository.LanguageModel.TypeInfo>(typeof(Repository.LanguageModel.TypeInfo));
        }

        public void AddType(Composestar.Repository.LanguageModel.TypeInfo typeinfo)
        {
            dbContainer.Set(typeinfo);

        }

        #endregion

        public void AddMethod(Composestar.Repository.LanguageModel.MethodInfo methodinfo)
        {
            dbContainer.Set(methodinfo);
        }
    }
}
