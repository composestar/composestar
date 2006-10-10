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

namespace Purple.Serialization.Serializers
{
  //=================================================================
  /// <summary>
  /// A implementation of the <see cref="ISerializer"/> interface that 
  /// allows to serialize objects by reading out their Serialize attributes.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  [CannotSerialize]
  public class Attribute : ISerializer {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the singleton instance of an <see cref="Attribute"/>.
    /// </summary>
    public static Attribute Instance {
      get {
        return instance;
      }
    }
    static Attribute instance = new Attribute();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Private constructor
    /// </summary>
    private Attribute() {
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
      Type type = obj.GetType();
		
      MemberInfo[] infos = type.GetMembers(BindingFlags.Public | BindingFlags.NonPublic | 
        BindingFlags.Instance);
      for (int i=0; i<infos.Length; i++) {
        if (infos[i].IsDefined( typeof(SerializeAttribute), false)) {
          SerializeAttribute attribute = (SerializeAttribute)
            infos[i].GetCustomAttributes(typeof(SerializeAttribute), false)[0];

          FieldInfo info = infos[i] as FieldInfo;
          PropertyInfo pInfo = infos[i] as PropertyInfo;

          if (info != null) {
            if (attribute.Primitive)
              stream.Write(info.Name, info.GetValue(obj));
            else
              stream.WriteAsObject(info.Name, info.GetValue(obj));
          }
          else if (pInfo != null && pInfo.CanRead && pInfo.CanWrite) {
            if (attribute.Primitive)
              stream.Write(pInfo.Name, pInfo.GetValue(obj, null));
            else
              stream.WriteAsObject(pInfo.Name, pInfo.GetValue(obj, null));
          }
          else
            throw new Exception("Can't get AND set member: " + pInfo.Name);
        }
      }
    }

    /// <summary>
    /// Deserializes the given object.
    /// </summary>
    /// <param name="stream">Stream containing data.</param>
    /// <param name="type">The type of the object to create.</param>
    /// <returns>The deserialized object.</returns>
    public object Deserialize(Type type, SerializeStream stream) {			
      FieldInfo[] infos = type.GetFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance);
      object obj = Activator.CreateInstance(type, true);
      for (int i=0; i<infos.Length; i++) {
        FieldInfo info = infos[i];
        if (info.IsDefined( typeof(SerializeAttribute), false)) {
          SerializeAttribute attribute = (SerializeAttribute)
            info.GetCustomAttributes(typeof(SerializeAttribute), false)[0];

          if (attribute.Primitive)
            info.SetValue( obj, stream.Read(info.Name, info.FieldType) );
          else
            info.SetValue( obj, stream.ReadObject(info.Name) );
        }
      }

      PropertyInfo[] pInfos = type.GetProperties(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance );
      for (int i=0; i<pInfos.Length; i++) {
        PropertyInfo pInfo = pInfos[i];
        if (pInfo.IsDefined( typeof(SerializeAttribute), false)) {
          SerializeAttribute attribute = (SerializeAttribute)
            pInfo.GetCustomAttributes(typeof(SerializeAttribute), false)[0];

          if (pInfo.CanWrite) {
            if (attribute.Primitive)
              pInfo.SetValue(obj, stream.Read(pInfo.Name, pInfo.PropertyType), null);
            else
              pInfo.SetValue(obj, stream.ReadObject(pInfo.Name), null);
          }
        }
      }
      return obj;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
