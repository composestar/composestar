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
using System.Collections;

namespace Purple.Logging {
  //=================================================================
  /// <summary>
  /// collection of ILogListener objects
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class LogListeners : ICollection {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    ArrayList list = ArrayList.Synchronized(new ArrayList());
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    ///  create new instance of logListeners
    /// </summary>
    public LogListeners() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// get element by index
    /// </summary>
    public ILogListener this[int index] {
      get {
        return (ILogListener)list[index];
      }
    }

    /// <summary>
    /// writes a line to the debug output
    /// </summary>
    /// <param name="level">severity level of log message</param>
    /// <param name="message">message to send</param>
    /// <param name="category">the category (e.g: Graphics.DirectX.Rendering)</param>
    public void Log(LogLevel level, string message, string category) {
      for (int i=0; i<list.Count; i++) {
        ILogListener logListener = list[i] as ILogListener;
        if (logListener != null && logListener.Level >= level)
          logListener.Log(level, message, category);
      }
    }

    /// <summary>
    /// adds a certain amount of log listeners to 
    /// </summary>
    /// <param name="col">collection with log listeners</param>
    public void AddRange(ICollection col) {
      foreach(object obj in col)
        Add((ILogListener)obj);
    }

    /// <summary>
    /// add a loglistener to the list
    /// </summary>
    /// <param name="logListener">element to add</param>
    public void Add(ILogListener logListener) {
      list.Add(logListener);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	
    //---------------------------------------------------------------
    #region ICollection Member
    //---------------------------------------------------------------
    /// <summary>
    /// returns true if collection is synchronized
    /// </summary>
    public bool IsSynchronized {
      get {
        return list.IsSynchronized;
      }
    }

    /// <summary>
    /// returns the number of listeners
    /// </summary>
    public int Count {
      get {
        return list.Count;
      }
    }

    /// <summary>
    /// copies all LogListeners to an array starting at a certain index
    /// </summary>
    /// <param name="array">destination array</param>
    /// <param name="index">index to start in destination array</param>
    public void CopyTo(Array array, int index) {
      list.CopyTo(array, index);
    }

    /// <summary>
    /// the object to use for synchronization
    /// </summary>
    public object SyncRoot {
      get {
        return list.SyncRoot;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region IEnumerable Member
    //---------------------------------------------------------------
    /// <summary>
    /// returns the enumerator object to use for enumeration
    /// </summary>
    /// <returns></returns>
    public IEnumerator GetEnumerator() {
      return list.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
