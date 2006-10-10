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
using System.IO;
using System.Collections;

namespace Purple.Serialization {
  //=================================================================
  /// <summary>
  /// To be inserted!
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public class Serializer {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Hashtable types = new Hashtable();
    Hashtable typeNames = new Hashtable();
    Hashtable ids = new Hashtable();
    Hashtable names = new Hashtable();

    /// <summary>
    /// Returns the singleton instance of the <see cref="Serializer"/>.
    /// </summary>
    public static Serializer Instance {
      get {
        return instance;
      }
    }
    static Serializer instance = new Serializer();

    /// <summary>
    /// Table that maps types to serializers.
    /// </summary>
    public Hashtable Serializers {
      get {
        return serializers;
      }
    }
    Hashtable serializers = new Hashtable();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Private constructor.
    /// </summary>
    private Serializer() {
      // Todo - find some better solution ;-)
      AddBinding(typeof(Boolean), -6, "Bool", Purple.Serialization.Serializers.Primitive.Instance);
      AddBinding(typeof(Int32), -5, "Int", Purple.Serialization.Serializers.Primitive.Instance);
      AddBinding(typeof(Single), -4, "Float", Purple.Serialization.Serializers.Primitive.Instance);
      AddBinding(typeof(Purple.Serialization.Serializers.String), -3, "String");
      AddBinding(typeof(Purple.Serialization.Serializers.Array), -2, "Array");
      AddBinding(typeof(Purple.Serialization.Serializers.Reference), -1, "Reference");
      AddBinding(typeof(Purple.PlugIn.Factory), 1, "Factory", Purple.PlugIn.Factory.Instance);
      AddBinding(typeof(Purple.Math.Vector2), 2, "Vector2");
      AddBinding(typeof(Purple.Math.Vector3), 3, "Vector3");
      AddBinding(typeof(Purple.Math.Vector4), 4, "Vector4");
      AddBinding(typeof(Purple.Math.Matrix3), 5, "Matrix3");
      AddBinding(typeof(Purple.Math.Matrix4), 6, "Matrix4");
      AddBinding(typeof(Purple.AI.Gesture), 7, "Gesture");
      AddBinding(typeof(Purple.Math.AABB), 8, "AABB");
      AddBinding(typeof(Purple.Graphics.Geometry.AnimationClip), 9, "AnimationClip");
      AddBinding(typeof(Purple.Graphics.GraphicsSettings), 10, "GraphicsSettings");
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the id of a certain type.
    /// </summary>
    /// <param name="type">Type to return id for.</param>
    /// <returns>The id for a certain type.</returns>
    internal int GetTypeId(Type type) {
      return (int)ids[type];
    }

    /// <summary>
    /// Returns the type of a certain id.
    /// </summary>
    /// <param name="id">The id to retrieve type for.</param>
    /// <returns>The type for a certain id.</returns>
    internal Type GetTypeFromId(int id) {
      return (Type)types[id];
    }

    /// <summary>
    /// Returns the type name for a certain type.
    /// </summary>
    /// <param name="type">The type to retrieve the name for.</param>
    /// <returns>The name of a certain type.</returns>
    internal string GetTypeString(Type type) {
      return names[type] as string;
    }

    /// <summary>
    /// Returns the type for a certain typeName.
    /// </summary>
    /// <param name="typeName">The name of the type.</param>
    /// <returns>The type for the given typeName.</returns>
    internal Type GetTypeFromString(string typeName) {
      return typeNames[typeName] as Type;
    }

    /// <summary>
    /// Serializes the given object into the given stream.
    /// </summary>
    /// <param name="obj">Object to serialize</param>
    /// <param name="stream">Target stream.</param>
    /// <param name="codec">The codec to use.</param>
    public void Save(object obj, Stream stream, ISerializeCodec codec) {
      SerializeStream serializeStream = new SerializeStream(codec, stream, true);
      serializeStream.Save(obj);
      serializeStream.Close();
    }

    /// <summary>
    /// Serializes the given object into the given file.
    /// </summary>
    /// <param name="obj">Object to serialize</param>
    /// <param name="fileName">Target file</param>
    /// <param name="codec">The codec to use.</param>
    public void Save(object obj, string fileName, ISerializeCodec codec) {
      using (FileStream stream = new FileStream(fileName, FileMode.Create, FileAccess.Write)) {
        Save(obj, stream, codec);
      }
    }

    /// <summary>
    /// Loads an object from a file.
    /// </summary>
    /// <param name="fileName">Name of file to load object from.</param>
    /// <param name="codec">The codec to use for loading the file.</param>
    /// <returns>The loaded object.</returns>
    public object Load(string fileName, ISerializeCodec codec) {
      using (FileStream stream = new FileStream(fileName, FileMode.Open, FileAccess.Read)) {
        return Load(stream, codec);
      }
    }

    /// <summary>
    /// Loads an object from a file.
    /// </summary>
    /// <param name="stream">Stream to load object from.</param>
    /// <param name="codec">The codec to use for loading the file.</param>
    /// <returns>The loaded object.</returns>
    public object Load(Stream stream, ISerializeCodec codec) {
      SerializeStream serializeStream = new SerializeStream(codec, stream, false);
      object obj = serializeStream.Load();
      serializeStream.Close();
      return obj;
    }

    /// <summary>
    /// Loads an object from a byte array.
    /// </summary>
    /// <param name="data">The data containing the object.</param>
    /// <param name="codec">The codec to use for serialization.</param>
    /// <returns>The deserialized object.</returns>
    public object Load(byte[] data, ISerializeCodec codec) {
      using (MemoryStream stream = new MemoryStream(data)) {
        return Load(stream, codec);
      }
    }        

    /// <summary>
    /// Add a new type to id binding.
    /// </summary>
    /// <param name="type">The type to bind to a certain id.</param>
    /// <param name="id">The id to use for a certain type.</param>
    /// <param name="name">The typeName to use for a certain binding.</param>
    /// <param name="serializer">The serializer object.</param>
    public void AddBinding(Type type, int id, string name, ISerializer serializer) {
      if (ids.Contains(type))
        throw new ArgumentException("Type already bound!");
      ids.Add(type, id);

      if (types.Contains(id))
        throw new ArgumentException("Id already bound!");
      types.Add(id, type);

      if (names.Contains(type))
        throw new ArgumentException("Type already bound!");
      names.Add(type, name);

      if (typeNames.Contains(name))
        throw new ArgumentException("TypeName already bound!");
      typeNames.Add(name, type);

      serializers.Add( type, serializer);
    }

    /// <summary>
    /// Add a new type to id binding.
    /// </summary>
    /// <param name="type">The type to bind to a certain id.</param>
    /// <param name="id">The id to use for a certain type.</param>
    /// <param name="name">The typeName to use for a certain binding.</param>
    public void AddBinding(Type type, int id, string name) {
      // Todo: Make attribute driven
      //if (type.IsDefined()); 
      if (typeof(ISerializer).IsAssignableFrom(type))
        AddBinding(type, id, name, (ISerializer)Activator.CreateInstance( type, true));
      else
        AddBinding(type, id, name, Purple.Serialization.Serializers.Attribute.Instance);
    }

    /// <summary>
    /// Add a new type to id binding.
    /// </summary>
    /// <param name="type">The type to bind to a certain id.</param>
    /// <param name="id">The id to use for a certain type.</param>
    public void AddBinding(Type type, int id) {
      AddBinding(type, id, type.FullName);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
