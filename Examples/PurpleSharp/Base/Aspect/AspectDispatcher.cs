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
using System.Reflection;
using System.Diagnostics;
using System.Collections;
using Purple.Code;

namespace Purple.Aspect {
  //=================================================================
  /// <summary>
  /// concrete implementation of IAspectDispatcher
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// A more detailed documentation should soon be available at www.bunnz.com
  /// </remarks>
  //=================================================================
  public class AspectDispatcher : IAspectDispatcher{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    IDictionary methodTable = new Hashtable();
    IDictionary insteadTable = new Hashtable();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// standard constructor
    /// </summary>
    public AspectDispatcher() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// links an aspect to a certain method
    /// </summary>
    /// <param name="aspect">aspect to link</param>
    /// <param name="method">method to inform aspect about</param>
    public void Add(IAspect aspect, MethodBase method) {
      IList list;
      string key = method.ToString();

      // If table doesn't contains key => create new list and add tuple
      if (!methodTable.Contains(key)) {
        list = new ArrayList();
        methodTable[key] = list;
      } // else just readout list
      else
        list = (IList)methodTable[key];
      list.Add(aspect);
    }

    /// <summary>
    /// links an aspect to all methods of a certain type
    /// </summary>
    /// <param name="aspect">aspect to link</param>
    /// <param name="iface">iface defining methods to link aspect to</param>    
    public void Add(IAspect aspect, Type iface) {
      MethodInfo[] methods = iface.GetMethods();      

      foreach(MethodInfo m in methods)
         Add(aspect, m);      
    }

    /// <summary>
    /// sets an aspect to be called instead of a certain method
    /// </summary>
    /// <param name="aspect">aspect with implementation</param>
    /// <param name="method">method to replace</param>
    /// <returns>true if aspect was set; false if there was already another one set</returns>
    public bool SetInstead(IAspect aspect, MethodBase method) {
      string key = method.ToString();
      if (insteadTable.Contains(key))
        return(false);
      insteadTable[key] = aspect;
      return(true);
    }

    /// <summary>
    /// sets an aspect to be called instead of a certain method
    /// </summary>
    /// <param name="aspect">aspect with implementation</param>
    /// <param name="iface">interface defining methods to be replaced</param>
    /// <returns>true if aspect was set for all methods; false if for at least one method another aspect was already set</returns>
    public bool SetInstead(IAspect aspect, Type iface) {
      MethodInfo[] methods = iface.GetMethods();      
      bool ret = true;

      foreach(MethodInfo m in methods)
        ret &= SetInstead(aspect, m);    
      return(ret);
    }

    /// <summary>
    /// called instead of original method
    /// </summary>
    /// <param name="obj">object for which method is called</param>
    /// <param name="method">method which was called</param>
    /// <param name="parameters">parameters passed to the method</param>
    /// <returns>return value of method</returns>
    public object OnMethodCall(object obj, MethodBase method, object[] parameters) {
      object ret = null;
      IList list = (IList)methodTable[method.ToString()];

      // call Before method of all linked aspects
      if (list != null)
        foreach(IAspect aspect in list) {
          aspect.Method = method;
          aspect.Object = obj;
          aspect.Parameters = parameters;

          aspect.Before();

          // call base method
          ret = BaseMethod(obj, method, parameters);              

          aspect.After();
        }
      else
        // call base method
        ret = BaseMethod(obj, method, parameters);  
          
      return(ret);
    }

    /// <summary>
    /// called instead of additional methods
    /// </summary>
    /// <param name="obj">object for which method is called</param>
    /// <param name="method">method which is called</param>
    /// <param name="parameters">parameters passed to the method</param>
    /// <returns>return value of method</returns>
    public object OnAdditionalMethodCall(object obj, MethodBase method, object[] parameters) {
      object ret = null;
      string key = method.ToString();

      IList aspects = (IList)methodTable[key];
      if (aspects.Count == 1) {
        IAspect aspect = (IAspect)aspects[0];  
        aspect.Method = method;
        aspect.Object = obj;
        aspect.Parameters = parameters;
        ret = BaseMethod(aspect, method, parameters);                
      }
      else
        throw new AspectException("No or too many aspects connected to additional method: " + method.ToString());
      return ret;
    }

    /// <summary>
    /// calls original method
    /// </summary>
    /// <param name="obj">object for which method is called</param>
    /// <param name="method">method which is called</param>
    /// <param name="parameters">parameters passed to the method</param>
    /// <returns>return value of method</returns>
    public object BaseMethod(object obj, MethodBase method, object[] parameters) {
      method = MethodHelper.GetMethod(obj, method);
      return(method.Invoke(obj, parameters));           
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
