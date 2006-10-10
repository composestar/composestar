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
using System.IO;
using System.Diagnostics;
using System.Text;

namespace Purple.Logging {
  //=================================================================
  /// <summary>
  /// the severity of a certain log message
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public enum LogLevel {
    /// <summary>
    /// critical error => execution will be stopped
    /// </summary>
    Error   = 0,
    /// <summary>
    /// warning that something unexpected happended
    /// non-critical error => execution goes on, but error might result into 
    /// another critical error
    /// </summary>
    Warning = 1,
    /// <summary>
    /// information with medium priority
    /// </summary>
    Info    = 2,
    /// <summary>
    /// spam with low priority
    /// </summary>
    Spam    = 3
}

  //=================================================================
  /// <summary>
  /// the logger class
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class Logger {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    LogListeners listeners = new LogListeners();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
		
    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// the base category for this logger - 
    /// the category passed to write is attended to the base category
    /// 
    /// </summary>
    public string BaseCategory {
      get {
        return baseCategory;
      }
    }
    string baseCategory = "";


    /// <summary>
    /// access to listeners
    /// </summary>
    public LogListeners Listeners {
      get {
        return listeners;
      }
    }

    /// <summary>
    /// the default logger instance
    /// </summary>
    public static Logger Instance {
      get {
        if (instance == null) 
          instance = new Logger();
        return instance;
      }
    }
    private static Logger instance = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of a logger
    /// </summary>
    public Logger() {
    }

    /// <summary>
    /// creates a logger with a certain base category
    /// </summary>
    /// <param name="baseCategory">base category to use</param>
    public Logger(string baseCategory) {
      this.baseCategory = baseCategory;
    }

    /// <summary>
    /// creates a logger with a certain base category
    /// </summary>
    /// <param name="baseCategory">base category to use</param>
    /// <param name="baseNode">base node</param>
    public Logger(string baseCategory, Logger baseNode)  : this(baseCategory) {
      if (baseNode != null)
        baseNode.Add(this);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// creates new logger node (for a certain subcategory
    /// </summary>
    /// <param name="category"></param>
    /// <returns></returns>
    public Logger CreateNode(string category) {
      Logger logger = new Logger();
      logger.baseCategory = this.baseCategory;
      logger.Listeners.AddRange(listeners);

      if (logger.baseCategory.Length != 0)
        logger.baseCategory += '.';
      logger.baseCategory += category;
      return logger;
    }

    internal void Add(Logger logger) {
      if (baseCategory.Length != 0) {
        if (logger.baseCategory.Length != 0)
          logger.baseCategory = baseCategory + '.' + logger.baseCategory;
        else
          logger.baseCategory = baseCategory;
      }
      logger.Listeners.AddRange(listeners);
    }

    /// <summary>
    /// get the current stack trace
    /// </summary>
    /// <param name="skipFrames">number of frames to skip</param>
    /// <returns>the stack trace string</returns>
    public string GetStackTrace(int skipFrames) {
      return new System.Diagnostics.StackTrace(skipFrames + 1, true).ToString();
    }

    /// <summary>
    /// write info with low priority to the log file
    /// </summary>
    /// <param name="message">info to write</param>
    /// <param name="category">category of info (e.g: Purple.Graphics.TwoD)</param>
    public void Spam( string message, string category ) {
      Log( LogLevel.Spam, message, category );
    }

    /// <summary>
    /// write info with low priority to the log file
    /// </summary>
    /// <param name="message">info to write</param>
    public void Spam( string message ) {
      Spam( message, "");
    }

    /// <summary>
    /// write an info to the log file
    /// </summary>
    /// <param name="message">info to write</param>
    /// <param name="category">category of info (e.g: Purple.Graphics.TwoD)</param>
    public void Info( string message, string category ) {
      Log( LogLevel.Info, message, category );
    }

    /// <summary>
    /// writes info to the log file
    /// </summary>
    /// <param name="message">info to write</param>
    public void Info( string message) {
      Info( message, "");
    }

    /// <summary>
    /// write a warning to the log file
    /// </summary>
    /// <param name="message">warning to write</param>
    /// <param name="category">category of this warning (e.g: Purple.Graphics.TwoD)</param>
    public void Warning( string message, string category ) {
      Log( LogLevel.Warning, message, category );
    }

    /// <summary>
    /// write a warning to the log file
    /// </summary>
    /// <param name="message">warning to write</param>
    public void Warning( string message ) {
      Warning( message, "" );
    }

    /// <summary>
    /// write and error message to the log file
    /// </summary>
    /// <param name="message">message to write</param>
    /// <param name="category">category of this message (e.g: Purple.Graphics.TwoD)</param>
    public void Error( string message, string category ) {
      Log( LogLevel.Error, message, category );
    }

    /// <summary>
    /// write an error message to the log file
    /// </summary>
    /// <param name="message">message to write</param>
    public void Error( string message ) {
      Error(message, "");
    }

    /// <summary>
    /// writes a line to the debug output
    /// </summary>
    /// <param name="level">severity level of the message</param>
    /// <param name="message">message to send</param>
    /// <param name="category">the category (e.g: Purple.Graphics.TwoD)</param>
    public void Log(LogLevel level, string message, string category) {
      if (baseCategory.Length != 0) {
        if (category.Length != 0)
          category = baseCategory + '.' + category;
        else
          category = baseCategory;
      }

      listeners.Log(level, message, category);
    }

    /// <summary>
    /// Creates a string from a log message.
    /// </summary>
    /// <param name="level">log level of message</param>
    /// <param name="message">message to use</param>
    /// <param name="category">category to use</param>
    /// <param name="logTime">Flag that indicates if the time should be logged.</param>
    /// <param name="logAssembly">Flag that indicates if the assembly should be logged.</param>
    /// <returns>the final string</returns>
    public static string CreateString(LogLevel level, string message, string category,
      bool logTime, bool logAssembly) {

      StringBuilder builder = new StringBuilder(128);
      builder.AppendFormat( "{0,7}", '[' + level.ToString() + ']' );
			
      if (logTime)
        builder.AppendFormat( " {0:hh:mm:ss} ", System.DateTime.Now);

      if (logAssembly)
        builder.AppendFormat( "{0,12}", System.AppDomain.CurrentDomain.FriendlyName);

      builder.AppendFormat( "{0, -50}", message );
      if (category != "")
        builder.Append(" (" + category + ')');
      return builder.ToString();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
