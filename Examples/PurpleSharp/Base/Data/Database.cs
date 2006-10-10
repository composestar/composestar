//*****************************************************************************
//     ____                              ___                __ __      
//    /\  _`\                           /\_ \              _\ \\ \__   
//    \ \ \L\ \ __  __   _ __   _____   \//\ \       __   /\__  _  _\  
//     \ \ ,__//\ \/\ \ /\`'__\/\ '__`\   \ \ \    /'__`\ \/_L\ \\ \L_ 
//      \ \ \/ \ \ \_\ \\ \ \/ \ \ \L\ \   \_\ \_ /\  __/   /\_   _  _\
//       \ \_\  \ \____/ \ \_\  \ \ ,__/   /\____\\ \____\  \/_/\_\\_\/
//        \/_/   \/___/   \/_/   \ \ \/    \/____/ \/____/     \/_//_/ 
//                                \ \_\                                
//                                 \/_/                                            
//                  Purple# - The smart way of programming games
#region //
// Copyright (c) 2002-2003 by 
//   Markus Wöß
//   Bunnz@Bunnz.com
//   http://www.bunnz.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#endregion
//*****************************************************************************
using System;
using System.Data;
using System.Data.OleDb;

namespace Purple.Data {
  //=================================================================
  /// <summary>
  /// Helper class for accessing physical databases
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public class Database {
    //-------------------------------------------------------------------------
    #region Variables
    //-------------------------------------------------------------------------
    static Database db = new Database();
    OleDbConnection connection = null;    
    //-------------------------------------------------------------------------
    #endregion
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    #region Construction
    //-------------------------------------------------------------------------    
    /// <summary>
    /// initializes Database object
    /// </summary>
    /// <param name="connectionString">string with info about location, ...</param>
    /// <exception cref="InvalidOperationException">
    ///   Cannot connect without specifying a data source or server!
    /// </exception>
    /// <exception cref="System.Data.SqlClient.SqlException">A connection-level error occurred while opening the connection.</exception>
    Database(string connectionString) {
      Connect(connectionString);
    }    

    /// <summary>
    /// initializes Database object
    /// </summary>
    Database() {
    }
    //-------------------------------------------------------------------------
    #endregion
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    #region General Database Methods
    //-------------------------------------------------------------------------
    /// <summary>
    /// connects to a certain database
    /// </summary>
    /// <param name="connectionString">string with info about location, ...</param>
    /// <exception cref="InvalidOperationException">
    ///   Cannot connect without specifying a data source or server!
    /// </exception>
    /// <exception cref="System.Data.SqlClient.SqlException">A connection-level error occurred while opening the connection.</exception>
    public void Connect(string connectionString) {
      connection = new OleDbConnection(connectionString);              
    }

    /// <summary>
    /// opens the connection to a certain database
    /// </summary>
    public void Open() {
      connection.Open();
    }    

    /// <summary>
    /// tests if connection is open
    /// </summary>
    /// <returns>true if connection is open, false otherwise</returns>
    public bool IsOpen() {
      if (connection == null)
        return false;
      return connection.State == ConnectionState.Open;
    }

    /// <summary>
    /// closes the connection to the database
    /// </summary>
    public void Close() {
      connection.Close();      
    }

    /// <summary>
    /// loads a whole table into a DataSet
    /// </summary>
    /// <param name="tableName">name of table to load</param>
    /// <returns>filled DataSet</returns>
    public DataSet LoadTable(string tableName) {      
      OleDbDataAdapter adapter = new OleDbDataAdapter("select * from " + tableName, connection);
      DataSet dataSet = new DataSet(tableName);
      adapter.Fill(dataSet, tableName);      
      return dataSet;
    }

    /// <summary>
    /// loads a certain view defined by a SQL statement into the dataset
    /// </summary>
    /// <param name="sql">sql statement</param>
    /// <param name="viewName">name of view</param>
    /// <returns>filled dataSet</returns>
    public DataSet Load(string sql, string viewName) {      
      OleDbDataAdapter adapter = new OleDbDataAdapter(sql, connection);
      DataSet dataSet = new DataSet(viewName);
      adapter.Fill(dataSet, viewName);      
      return dataSet;
    }    

    /// <summary>
    /// executes an SQL statement on a certain database
    /// </summary>
    /// <param name="sql">SQL statement</param>
    /// <returns>true if successful otherwise false</returns>
    public bool Execute(string sql) {
      try {                
        OleDbCommand command = new OleDbCommand(sql, connection);
        command.ExecuteNonQuery();        
      } catch (Exception e) {
        Console.WriteLine(e);        
        return false;
      }
      return true;
    }

    //-------------------------------------------------------------------------
  #endregion
    //-------------------------------------------------------------------------
  }
}
