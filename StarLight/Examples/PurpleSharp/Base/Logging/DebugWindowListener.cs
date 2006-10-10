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

namespace Purple.Logging {
  //=================================================================
  /// <summary>
  /// a listener sending log messages to the debug window
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class DebugWindowListener : ILogListener {
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
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// create an instance of a debug window listener
    /// </summary>
    public DebugWindowListener() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------		
    /// <summary>
    /// write a simple message
    /// </summary>
    /// <param name="level">log level of message</param>
    /// <param name="message">message to write</param>
    /// <param name="category">category to use</param>
    public void Log(LogLevel level, string message, string category) {
      System.Diagnostics.Debug.WriteLine( Logger.CreateString(level, message, category, true, false) );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
