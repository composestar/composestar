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

namespace Purple.Code
{
  //=================================================================
  /// <summary>
  /// provides some helper function for handling meta methods  
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
	public class MethodHelper
	{
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// returns array containing the types of the parameters of a certain method
    /// </summary>
    /// <param name="m">method</param>
    /// <returns>array of parameter types</returns>
    static public Type[] GetParameterTypes(MethodBase m) {
      // get array of ParameterInfo objects
      ParameterInfo[] paramInfos = m.GetParameters();
      // create another array with the same size holding Type[] objects
      Type[] paramTypes = new Type[paramInfos.Length];
      // fill array
      for (int i=0; i<paramInfos.Length; i++)
        paramTypes[i] = paramInfos[i].ParameterType;
      // and return
      return paramTypes;
    }  

    /// <summary>
    /// returns the exact method object for a certain object instance    
    /// </summary>
    /// <param name="obj">object instance</param>
    /// <param name="method">method to take name and parameters from</param>
    /// <returns>methodBase or null</returns>
    static public MethodBase GetMethod(object obj, MethodBase method) {
      MethodBase result = obj.GetType().GetMethod(method.Name, GetParameterTypes(method));
      return(result);      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
