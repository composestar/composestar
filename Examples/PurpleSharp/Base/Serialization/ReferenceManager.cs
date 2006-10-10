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
using System.Collections;

namespace Purple.Serialization
{
  //=================================================================
  /// <summary>
  /// The <see cref="ReferenceManager"/> is used by the <see cref="Serializer"/> and 
  /// is used to ensure that objects aren't serialized twice but just the references.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public class ReferenceManager {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Hashtable objects = new Hashtable();
    Hashtable ids = new Hashtable();
    int id = 1;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of the <see cref="ReferenceManager"/>.
    /// </summary>
    public ReferenceManager() {
      Reset();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Resets the manager.
    /// </summary>
    public void Reset() {
      objects.Clear();
      ids.Clear();
      id = 1;
      ids.Add(0, null);
    }

    /// <summary>
    /// Checks if the object was already serialized and if needed replaces it 
    /// with a <see cref="Purple.Serialization.Serializers.Reference"/> object.
    /// </summary>
    /// <param name="obj">The object to check.</param>
    /// <returns>The object to serialize.</returns>
    public object Serialize(object obj) {
      if (obj == null)
        return new Purple.Serialization.Serializers.Reference( 0 );

      if (obj.GetType().IsClass && !obj.GetType().IsDefined( typeof(CannotSerializeAttribute), false ) ) {
        if (objects.Contains(obj)) {
          return new Purple.Serialization.Serializers.Reference( (int)objects[obj] );
        } 
        objects.Add(obj, id++);
      }
      return obj;
    }

    /// <summary>
    /// Checks if the object is a <see cref="Purple.Serialization.Serializers.Reference"/> object and 
    /// if needed replaces it with the real object.
    /// </summary>
    /// <param name="obj">The object to check for.</param>
    /// <returns>The deserialized object.</returns>
    public object Deserialize(object obj) {
      Purple.Serialization.Serializers.Reference reference = obj as Purple.Serialization.Serializers.Reference;
      if (reference != null)
        return ids[reference.Id];  
      ids.Add(id++, obj);
      return obj;
    }

    /// <summary>
    /// Reserves and id for an object that isn't yet constructed.
    /// </summary>
    /// <returns></returns>
    public int Reserve() {
      ids.Add(id, "Reserved");
      return id++;
    }

    /// <summary>
    /// Assigns an object to an already reserved slot.
    /// </summary>
    /// <param name="id">The reserved slot.</param>
    /// <param name="obj">The object to assign.</param>
    /// <returns>The assigned object.</returns>
    public object Set(int id, object obj) {
      ids[id] = obj;
      return obj;
    }

    /// <summary>
    /// Returns the reference for a certain id.
    /// </summary>
    /// <param name="id">The id to return object for.</param>
    /// <returns>The reference for a certain id.</returns>
    public object GetReference(int id) {
      if (ids.Count <= id)
        throw new ArgumentOutOfRangeException("Id is invalid!");
      return ids[id];
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
