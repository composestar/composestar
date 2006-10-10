////*****************************************************************************
////     ____                              ___                __ __      
////    /\  _`\                           /\_ \              _\ \\ \__   
////    \ \ \L\ \ __  __   _ __   _____   \//\ \       __   /\__  _  _\  
////     \ \ ,__//\ \/\ \ /\`'__\/\ '__`\   \ \ \    /'__`\ \/_L\ \\ \L_ 
////      \ \ \/ \ \ \_\ \\ \ \/ \ \ \L\ \   \_\ \_ /\  __/   /\_   _  _\
////       \ \_\  \ \____/ \ \_\  \ \ ,__/   /\____\\ \____\  \/_/\_\\_\/
////        \/_/   \/___/   \/_/   \ \ \/    \/____/ \/____/     \/_//_/ 
////                                \ \_\                                
////                                 \/_/                                            
////                  Purple# - The smart way of programming games
//#region //
//// Copyright (c) 2002-2003 by 
////   Markus Wöß
////   Bunnz@Bunnz.com
////   http://www.bunnz.com
////
//// This library is free software; you can redistribute it and/or
//// modify it under the terms of the GNU Lesser General Public
//// License as published by the Free Software Foundation; either
//// version 2.1 of the License, or (at your option) any later version.
////
//// This library is distributed in the hope that it will be useful,
//// but WITHOUT ANY WARRANTY; without even the implied warranty of
//// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//// Lesser General Public License for more details.
////
//// You should have received a copy of the GNU Lesser General Public
//// License along with this library; if not, write to the Free Software
//// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//#endregion
////*****************************************************************************
//using System;
//using System.Collections;
//
//namespace Purple.Graphics.HLSL {
//  //=================================================================
//  /// <summary>
//  /// class providing access to HLSL parameters
//  /// </summary>
//  /// <remarks>
//  ///   <para>Author: Markus Wöß</para>
//  ///   <para>Since: 0.1</para>  
//  /// </remarks>
//  //=================================================================
//  public class Parameters : IEnumerable {
//    //---------------------------------------------------------------
//    #region Variables
//    //---------------------------------------------------------------
//    // Just an arrayList and not an hashtable, 
//    // cause there will be just a few paramters
//    ArrayList parameters = new ArrayList();
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------	
//
//    //---------------------------------------------------------------
//    #region Properties
//    //---------------------------------------------------------------
//    /// <summary>
//    /// get enumerator for enumeration over parameters
//    /// </summary>
//    /// <returns>IEnumerator object</returns>
//    public IEnumerator GetEnumerator() {
//      return parameters.GetEnumerator();
//    }
//
//    /// <summary>
//    /// get parameter by index
//    /// </summary>
//    public Parameter this[int index] {
//      get {
//        return (Parameter)parameters[index];
//      }
//    }
//
//    /// <summary>
//    /// get parameter by name
//    /// </summary>
//    public Parameter this[string name] {
//      get {
//        foreach(Parameter param in parameters)
//          if (param.Name.Equals(name))
//            return param;
//        return null;
//      }
//    }
//
//    /// <summary>
//    /// number of parameters
//    /// </summary>
//    public int Count {
//      get {
//        return parameters.Count;
//      }
//    }    
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------	
//
//    //---------------------------------------------------------------
//    #region Initialisation
//    //---------------------------------------------------------------	
//    /// <summary>
//    /// construction
//    /// </summary>
//    public Parameters() {
//    }
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------	
//
//    //---------------------------------------------------------------
//    #region Methods
//    //---------------------------------------------------------------	
//    /// <summary>
//    /// adds a parameter to the list
//    /// </summary>
//    /// <param name="param">param to add</param>
//    public void Add( Parameter param ) {
//      parameters.Add(param);
//    }
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------	
//  }
//}
