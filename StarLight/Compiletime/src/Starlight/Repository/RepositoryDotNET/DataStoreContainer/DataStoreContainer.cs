using System;
using System.Collections.Generic;
using System.Text;

using System.Data.Common;
using System.Data.SQLite;

namespace Composestar.Repository
{
    public class DataStoreContainer
    {
        private const string sqlFilename = "test.db";

        private DbProviderFactory fact;
        private DbConnection cnn;

        #region Singleton
        static readonly DataStoreContainer instance = new DataStoreContainer();

        // Explicit static constructor to tell C# compiler
        // not to mark type as beforefieldinit
        static DataStoreContainer()
        {
        }

        DataStoreContainer()
        {
            fact = DbProviderFactories.GetFactory("System.Data.SQLite"); 
            
            this.Open();
            
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
            this.Close();
        }
        #endregion

        public void Open()
        {
            cnn = fact.CreateConnection();
            cnn.ConnectionString = "Data Source=" + sqlFilename;
            cnn.Open();

        }

        public void Close()
        {

        }

        public void CreateTables()
        {
            // Create a list of the tables in the database file
            IList<String> tablenames = new List<String>();
            using (DbCommand cmd = cnn.CreateCommand())
            {
                cmd.CommandText = "SELECT name FROM sqlite_master WHERE type='table';";
                cmd.Prepare();
                using (DbDataReader rd = cmd.ExecuteReader())
                {
                    if (rd.Read())
                    {
                        tablenames.Add(rd.GetString(0));
                    }
                    //else throw new ArgumentOutOfRangeException("No data in table");
                }
            }

            // Create tables (unless they already exist)
            using (DbTransaction dbTrans = cnn.BeginTransaction())
            {
                if (!tablenames.Contains("Types"))
                {
                    using (DbCommand cmd = cnn.CreateCommand())
                    {
                        cmd.CommandText = "CREATE TABLE Types (TypeID GUID primary key, Name VARCHAR(100), FullName VARCHAR(250), BaseType VARCHAR(100))";
                        cmd.ExecuteNonQuery();
                    }
                }

                if (!tablenames.Contains("Methods"))
                {
                    using (DbCommand cmd = cnn.CreateCommand())
                    {
                        cmd.CommandText = "CREATE TABLE Methods (MethodID GUID primary key, Type GUID, Name VARCHAR(100), ReturnType VARCHAR(100))";
                        cmd.ExecuteNonQuery();
                    }
                }

                if (!tablenames.Contains("Parameters"))
                {
                    using (DbCommand cmd = cnn.CreateCommand())
                    {
                        cmd.CommandText = "CREATE TABLE Parameters (ParameterID integer primary key autoincrement, Method GUID, Name VARCHAR(100), integer Ordinal, Type VARCHAR(100))";
                        cmd.ExecuteNonQuery();
                    }
                }

                dbTrans.Commit();
            }
        }

        public void AddTypes(IList<LanguageModel.TypeInfo> types)
        {
            using (DbTransaction dbTrans = cnn.BeginTransaction())
            {
                using (DbCommand cmd = cnn.CreateCommand())
                {
                    // Get all types from the list
                    foreach (Repository.LanguageModel.TypeInfo ti in types)
                    {
                        Guid typeID = Guid.NewGuid();

                        cmd.CommandText = String.Format("INSERT INTO Types (TypeID, Name) VALUES('{0}', '{1}')", typeID, ti.Name);
                        cmd.ExecuteNonQuery();

                        // Get all the methods for this type
                        foreach (Repository.LanguageModel.MethodInfo mi in ti.Methods)
                        {
                            Guid methodID = Guid.NewGuid();
                            cmd.CommandText = String.Format("INSERT INTO Methods (MethodID, Type, Name, ReturnType) VALUES('{0}', '{1}', '{2}', '{3}')", methodID, typeID, mi.Name, mi.ReturnType);
                            cmd.ExecuteNonQuery();

                            // Get all the parameters for this method
                            foreach (Repository.LanguageModel.ParameterInfo pi in mi.Parameters)
                            {
                                cmd.CommandText = String.Format("INSERT INTO Parameters (Method, Name, Type) VALUES('{0}', '{1}', '{2}')", methodID, pi.Name, pi.ParameterType);
                                cmd.ExecuteNonQuery();                               
                            }
                        }


                    }
                }

                dbTrans.Commit();
            }
        }

        public IList<LanguageModel.TypeInfo> GetTypes()
        {
            // Construct entire object structure from database

            return null;
        }

        public LanguageModel.TypeInfo GetType(String fullname)
        {
            // Get type with name from db

            return new LanguageModel.TypeInfo();
        }

    }
}
