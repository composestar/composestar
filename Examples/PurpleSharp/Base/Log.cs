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
using Purple.Logging;

namespace Purple
{
  //=================================================================
  /// <summary>
  /// standard logger
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
	public class Log
	{
    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// the internally used logger object
    /// </summary>
    public static Logger Logger {
      get {
        return logger;
      }
    }
    private static Logger logger;

    /// <summary>
    /// static constructor of log class
    /// </summary>
    static Log() {
      logger = Logger.Instance;
      logger.Listeners.Add( new DebugWindowListener() );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// get the current stack trace
    /// </summary>
    /// <param name="skipFrames">number of frames to skip</param>
    /// <returns>the stack trace string</returns>
    public static string GetStackTrace(int skipFrames) {
      return logger.GetStackTrace(skipFrames);
    }

    /// <summary>
    /// write info with low priority to the log file
    /// </summary>
    /// <param name="message">info to write</param>
    /// <param name="category">category of info (e.g: Purple.Graphics.TwoD)</param>
    public static void Spam( string message, string category ) {
      logger.Spam(message, category);
    }

    /// <summary>
    /// write info with low priority to the log file
    /// </summary>
    /// <param name="message">info to write</param>
    public static void Spam( string message ) {
      logger.Spam( message);
    }

    /// <summary>
    /// write an info to the log file
    /// </summary>
    /// <param name="message">info to write</param>
    /// <param name="category">category of info (e.g: Purple.Graphics.TwoD)</param>
    public static void Info( string message, string category ) {
      logger.Info(message, category);
    }

    /// <summary>
    /// writes info to the log file
    /// </summary>
    /// <param name="message">info to write</param>
    public static void Info( string message) {
      logger.Info(message);
    }

    /// <summary>
    /// write a warning to the log file
    /// </summary>
    /// <param name="message">warning to write</param>
    /// <param name="category">category of this warning (e.g: Purple.Graphics.TwoD)</param>
    public static void Warning( string message, string category ) {
      logger.Warning(message, category);
    }

    /// <summary>
    /// write a warning to the log file
    /// </summary>
    /// <param name="message">warning to write</param>
    public static void Warning( string message ) {
      logger.Warning( message );
    }

    /// <summary>
    /// write and error message to the log file
    /// </summary>
    /// <param name="message">message to write</param>
    /// <param name="category">category of this message (e.g: Purple.Graphics.TwoD)</param>
    public static void Error( string message, string category ) {
      logger.Error(message, category);
    }

    /// <summary>
    /// write an error message to the log file
    /// </summary>
    /// <param name="message"></param>
    public static void Error( string message ) {
      logger.Error(message);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
