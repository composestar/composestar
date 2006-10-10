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

namespace Purple.Debug {
  //=================================================================
  /// <summary>
  /// a tool that allows to add and remove objects to the watch list
  /// these objects can then be queried and their value read out
  /// this class doesn't influences garbage collection of these objects
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public class WatchList {
    //---------------------------------------------------------------    
    #region Variables
    //---------------------------------------------------------------
    static WatchList instance = new WatchList();
    Hashtable watches = new Hashtable();
    //---------------------------------------------------------------    
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------    
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// get default instance of the watch list
    /// </summary>
    public static WatchList Instance {
      get {
        return instance;
      }
    }

    /// <summary>
    /// get object specified by name
    /// </summary>
    public object this[string name] {
      get {
        if (!Contains(name))
          return null;
        WeakReference wr = (WeakReference)watches[name];    
        return wr.Target;
      }
    }
    //---------------------------------------------------------------    
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------    
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor
    /// </summary>
    public WatchList() {
    }
    //---------------------------------------------------------------    
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------    
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// adds an object to the watch list
    /// </summary>
    /// <param name="name">name of variable</param>
    /// <param name="obj">object instance</param>
    public void Add(string name, object obj) {
      System.Diagnostics.Debug.Assert( name != null );
      watches.Add(name, new WeakReference(obj) );
    }

    /// <summary>
    /// removes an object from the watch list
    /// </summary>
    /// <param name="name">name of object to remove</param>
    public void Remove(string name) {
      System.Diagnostics.Debug.Assert( name != null);
      watches.Remove(name);
    }

    /// <summary>
    /// determine if a certain variable is contained by the watch list
    /// </summary>
    /// <param name="name">name of object</param>
    /// <returns>true if object is contained</returns>
    public bool Contains(string name) {
      return watches.Contains(name);
    }

    /// <summary>
    /// tests if object specified by name is not garbage collected
    /// </summary>
    /// <param name="name">name of object</param>
    /// <returns>true if object is not garbaged collected</returns>
    public bool IsAlive(string name) {
      WeakReference wr = (WeakReference) watches[name];
      return wr.IsAlive;
    }
    //---------------------------------------------------------------    
    #endregion
    //---------------------------------------------------------------
  }
}
