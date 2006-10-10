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
  /// The serializer for pritmitive types.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// This class could be split up into a serializer class per primitive. 
  /// That could speed up the serialization process a little bit.
  /// </remarks>
  //=================================================================
  [CannotSerialize]
  public class Primitive : ISerializer {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the singleton instance of an <see cref="Primitive"/>.
    /// </summary>
    public static Primitive Instance {
      get {
        return instance;
      }
    }
    static Primitive instance = new Primitive();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    private Primitive() {
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
      Type type = obj.GetType();
      if (type == typeof(int))
        stream.Write("value", (int)obj);
      else if (type == typeof(float))
        stream.Write("value", (float)obj);
      else if (type == typeof(bool))
        stream.Write("value", obj.ToString());
      else
        throw new NotSupportedException("Type: " + type.Name + " not supported at the moment");
    }

    /// <summary>
    /// Deserializes the given object.
    /// </summary>
    /// <param name="stream">Stream containing data.</param>
    /// <param name="type">The type of the object to create.</param>
    /// <returns>The deserialized object.</returns>
    public object Deserialize(Type type, SerializeStream stream) {
      if (type == typeof(int))
        return stream.ReadInt("value");
      else if (type == typeof(float))
        return stream.ReadFloat("value");
      else if (type == typeof(bool))
        return stream.ReadBool("value");
      else
        throw new NotSupportedException("Type: " + type.Name + " not supported at the moment");
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
