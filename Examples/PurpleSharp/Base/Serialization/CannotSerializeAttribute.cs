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

namespace Purple.Serialization {
  //=================================================================
  /// <summary>
  /// Attribute the marks an object that it can't be serialized.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// Classes of the Purple.Serialization.Serializers namespace can't be 
  /// serialized - except if they are explicitely replaced by the Serializer 
  /// or ReferenceManager.
  /// In addition, these classes are ignored by the ReferenceManager.
  /// </remarks>
  //=================================================================
  [AttributeUsage(AttributeTargets.Class, AllowMultiple = false, Inherited = false)]
  public class CannotSerializeAttribute : Attribute {
    //-------------------------------------------------------------------------
    #region Variables
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    #endregion
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    #region Properties
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    #endregion
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    #region Initialisation
    //-------------------------------------------------------------------------
    /// <summary>
    /// Serialize Attribute defines how objects are serialized.
    /// </summary>		
    public CannotSerializeAttribute() {
    }
    //-------------------------------------------------------------------------
    #endregion
    //-------------------------------------------------------------------------
  }
}
