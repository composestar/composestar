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
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Text;

namespace Purple.Logging {
  //=================================================================
  /// <summary>
  /// A file listener for writing log information to files
  /// file is secured with a mutex or 
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class FileListener : ILogListener {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private string fileName = null;
    /// <summary>
    /// the stream writer used for non-mutex logging
    /// </summary>
    protected StreamWriter writer = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// minimum severity of the log message to get logged
    /// </summary>
    public LogLevel Level { 
      get {
        return logLevel;
      }
      set {
        logLevel = value;
      }
    }
    private LogLevel logLevel = LogLevel.Spam;
    
    /// <summary>
    /// flag which indicates if time is added to logEntries
    /// </summary>
    public bool LogTime {
      get {
        return logTime;
      }
      set {
        logTime = value;
      }
    }
    bool logTime = true;

    /// <summary>
    /// flat indicating if the current assembly should be logged
    /// </summary>
    public bool LogAssembly {
      get {
        return logAssembly;
      }
      set {
        logAssembly = value;
      }
    }
    bool logAssembly = false;

    /// <summary>
    /// mutex for secure file access
    /// when mutex is null, a simple critical section is used
    /// </summary>
    public Mutex Mutex {
      get {
        return mutex;
      }
      set {
        mutex = value;
      }
    }
    Mutex mutex = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// create an instance of the file listener
    /// </summary>
    public FileListener(string fileName) {
      this.fileName = fileName;
    }

    /// <summary>
    /// write a simple message for a given category to the file
    /// </summary>
    /// <param name="level">log level of message</param>
    /// <param name="message">message to write</param>
    /// <param name="category">category to use</param>
    public void Log(LogLevel level, string message, string category) {
      if (mutex != null) {
        mutex.WaitOne();
        try {
          StreamWriter writer = new StreamWriter( CurrentFileName() , true);
          writer.WriteLine( CreateString(level, message, category) );
          writer.Close();
        } 
        catch ( Exception ) {
          // ignore
        }
        finally {
          mutex.ReleaseMutex();
        }
      }
      else {
        try {
          if (this.writer == null)
            writer = new StreamWriter( CurrentFileName() , true);
          writer.WriteLine( CreateString(level, message, category) );
          writer.Flush();
        } catch {}
      }
    }

    /// <summary>
    /// create the string to write
    /// </summary>
    /// <param name="level">log level of message</param>
    /// <param name="message">message to use</param>
    /// <param name="category">category to use</param>
    /// <returns>the final string</returns>
    protected virtual string CreateString(LogLevel level, string message, string category) {

      return Logger.CreateString(level, message, category, logTime, logAssembly);
    }

    /// <summary>
    /// returns the currently used fileName
    /// </summary>
    /// <returns>fileName</returns>
    protected virtual string CurrentFileName() {
      return fileName;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
