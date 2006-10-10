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

namespace Purple.Serialization
{
  //=================================================================
  /// <summary>
  /// Attribute the marks a certain property or field that it should be 
  /// serialized by the AttributeSerializer.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  [AttributeUsage(AttributeTargets.Property | AttributeTargets.Field,
     AllowMultiple = false, Inherited = true)]
  public class SerializeAttribute : Attribute {
    //-------------------------------------------------------------------------
    #region Variables
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    #endregion
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    #region Properties
    //-------------------------------------------------------------------------
    /// <summary>
    /// Flag that indicates if an object should be saved as primitive or as a object.
    /// </summary>
    public bool Primitive {
      get {
        return primitive;
      }
    }
    private bool primitive;
    //-------------------------------------------------------------------------
    #endregion
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    #region Initialisation
    //-------------------------------------------------------------------------
    /// <summary>
    /// Serialize Attribute defines how objects are serialized.
    /// </summary>		
    public SerializeAttribute() : this(false) {
    }

    /// <summary>
    /// Serialize Attribute defines how objects are serialized.
    /// </summary>		
    /// <param name="primitive">Flag that indicates if an object should be saved as primitive or as a object.</param>
    public SerializeAttribute( bool primitive ) {
      this.primitive = primitive;
    }
    //-------------------------------------------------------------------------
    #endregion
    //-------------------------------------------------------------------------
  }
}
