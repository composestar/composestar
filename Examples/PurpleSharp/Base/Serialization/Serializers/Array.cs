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

namespace Purple.Serialization.Serializers {
  //=================================================================
  /// <summary>
  /// An object that is used for storing arrays.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  [CannotSerialize]
  public class Array : ISerializer {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    System.Array array;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    private Array() {
    }

    /// <summary>
    /// Creates a new instance of a wrapper for an array.
    /// </summary>
    /// <param name="array">Array to wrap.</param>
    internal Array(System.Array array) {
      this.array = array;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Serializes the given object.
    /// </summary>
    /// <param name="obj">The object to serialize.</param>
    /// <param name="stream">Stream to fill with data.</param>
    public void Serialize(object obj, SerializeStream stream) {
      stream.Write( "elementType", array.GetType().GetElementType() );
      stream.Write( "length", array.Length );
      for (int i=0; i<array.Length; i++) {
        object value = array.GetValue(i);
        stream.WriteAsObject(null, value);
      }
    }

    /// <summary>
    /// Deserializes the given object.
    /// </summary>
    /// <param name="stream">Stream containing data.</param>
    /// <param name="type">The type of the object to create.</param>
    /// <returns>The deserialized object.</returns>
    public object Deserialize(Type type, SerializeStream stream) {
      Type elementType = stream.ReadType( "elementType" );
      int length = stream.ReadInt("length");
      System.Array array = System.Array.CreateInstance( elementType, length );
      for (int i=0; i<array.Length; i++)
        array.SetValue( stream.ReadObject(null) ,i);
      return array;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
