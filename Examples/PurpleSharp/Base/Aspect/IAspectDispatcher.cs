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

namespace Purple.Aspect {
  //=================================================================
  /// <summary>
  /// IAspectDispatcher gets informed by IAspectComponent when a method is called
  /// IAspectDispatcher has to send this information to all registered IAspects
  /// and/or has the task to call the base method
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// A more detailed documentation should soon be available at www.bunnz.com
  /// </remarks>
  //=================================================================
  public interface IAspectDispatcher {		
    /// <summary>
    /// called instead of original method
    /// </summary>
    /// <param name="obj">object for which method is called</param>
    /// <param name="method">method which is called</param>
    /// <param name="parameters">parameters passed to the method</param>
    /// <returns>return value of method</returns>
    object OnMethodCall(object obj, MethodBase method, object[] parameters);

    /// <summary>
    /// called instead of additional methods
    /// </summary>
    /// <param name="obj">object for which method is called</param>
    /// <param name="method">method which is called</param>
    /// <param name="parameters">parameters passed to the method</param>
    /// <returns>return value of method</returns>
    object OnAdditionalMethodCall(object obj, MethodBase method, object[] parameters);

    /// <summary>
    /// calls original method
    /// </summary>
    /// <param name="obj">object for which method is called</param>
    /// <param name="method">method which is called</param>
    /// <param name="parameters">parameters passed to the method</param>
    /// <returns>return value of method</returns>
    object BaseMethod(object obj, MethodBase method, object[] parameters);

    /// <summary>
    /// links an aspect to a certain method
    /// </summary>
    /// <param name="aspect">aspect to link</param>
    /// <param name="method">method to inform aspect about</param>
    void Add(IAspect aspect, MethodBase method);

    /// <summary>
    /// links an aspect to all methods of a certain type
    /// </summary>
    /// <param name="aspect">aspect to link</param>
    /// <param name="iface">iface defining methods to link aspect to</param>
    void Add(IAspect aspect, Type iface);

    /// <summary>
    /// sets an aspect to be called instead of a certain method
    /// </summary>
    /// <param name="aspect">aspect with implementation</param>
    /// <param name="method">method to replace</param>
    /// <returns>true if aspect was set; false if there was already another one set</returns>
    bool SetInstead(IAspect aspect, MethodBase method);

    /// <summary>
    /// sets an aspect to be called instead of a certain method
    /// </summary>
    /// <param name="aspect">aspect with implementation</param>
    /// <param name="iface">interface defining methods to be replaced</param>
    /// <returns>true if aspect was set for all methods; false if for at least one method another aspect was already set</returns>
    bool SetInstead(IAspect aspect, Type iface);  
  }
}
