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
  /// An aspect in the sense of "Aspect Oriented Programming"  
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// Usually AOP is realized by using special (cross)Compiler. 
  /// The Purple Way of AOP makes a different approach and integrates
  /// parts of the philosophy combined with my own ideas in the engine.
  /// 
  /// An aspect implements several features which can then be added to components
  /// accessed over interfaces
  /// 
  /// A more detailed documentation should soon be available at www.bunnz.com
  /// </remarks>
  //=================================================================
  public abstract class Aspect : IAspect{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    object obj;
    MethodBase method;
    object[] parameters;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// object for which current method was called
    /// </summary>
    public object Object { 
      get {
        return obj;
      }
      set {
        obj = value;
      }
    }

    /// <summary>
    /// method which was called
    /// </summary>
    public MethodBase Method { 
      get {
        return method;
      }
      set {
        method = value;
      }
    }

    /// <summary>
    /// parameters passed to the method
    /// </summary>
    public object[] Parameters { 
      get {
        return parameters;
      }
      set {
        parameters = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// called before original method is called
    /// </summary>        
    public abstract void Before();

    /// <summary>
    /// called instead of original method
    /// </summary>        
    /// <returns>return value of method</returns>
    public abstract object Instead();

    /// <summary>
    /// called after original method is called
    /// </summary>        
    public abstract void After();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
