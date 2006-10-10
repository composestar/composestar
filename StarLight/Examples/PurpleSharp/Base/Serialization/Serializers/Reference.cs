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

namespace Purple.Serialization.Serializers
{
  //=================================================================
  /// <summary>
  /// An object that is used for storing references.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  [CannotSerialize]
	public class Reference : ISerializer
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the id of the referenced object that can be resolved via 
    /// the ReferenceManager.
    /// </summary>
    public int Id {
      get {
        return id;
      }
    }
    int id;

    /// <summary>
    /// Returns the reference object for 
    /// </summary>
    public static Reference Null {
      get {
        return nullReference;
      }
    }
    static Reference nullReference = new Reference(0);
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    private Reference() {
    }

    /// <summary>
    /// Creates a new object holding a reference to another object.
    /// </summary>
    /// <param name="id">The id to of the object to create <see cref="Reference"/> for.</param>
		internal Reference( int id )
		{
      this.id = id;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region ISerializer Members
    //---------------------------------------------------------------
    /// <summary>
    /// Serializes the given object.
    /// </summary>
    /// <param name="obj">The object to serialize.</param>
    /// <param name="stream">Stream to fill with data.</param>
    public void Serialize(object obj, SerializeStream stream) {
      stream.Write("id", id);
    }

    /// <summary>
    /// Deserializes the given object.
    /// </summary>
    /// <param name="stream">Stream containing data.</param>
    /// <param name="type">The type of the object to create.</param>
    /// <returns>The deserialized object.</returns>
    public object Deserialize(Type type, SerializeStream stream) {
      int id = stream.ReadInt("id");
      return stream.ReferenceManager.GetReference(id);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
