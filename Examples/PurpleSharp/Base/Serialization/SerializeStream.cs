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

namespace Purple.Serialization
{
  //=================================================================
  /// <summary>
  /// The stream containing the data of the serialized element.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
	public class SerializeStream : IDisposable
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Stack idStack = new Stack();
    ISerializeCodec codec;
    bool write;

    /// <summary>
    /// Returns the reference manager for the current stream.
    /// </summary>
    public ReferenceManager ReferenceManager { 
      get {
        return refManager;
      }
    }
    ReferenceManager refManager = new ReferenceManager();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new SerializeStream object.
    /// </summary>
    /// <param name="codec">The codec to use.</param>
    /// <param name="stream">The target or source stream.</param>
    /// <param name="write">Flag that indicates if the serializeStream should be used for reading or writing.</param>
		public SerializeStream(ISerializeCodec codec, Stream stream, bool write)
		{
      this.codec = codec;
      codec.Init(stream, write);
      this.write = write;
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Saves the object to the stream.
    /// </summary>
    /// <param name="obj"></param>
    internal void Save(object obj) {
      Write(null, obj);
    }

    /// <summary>
    /// Loads the object from the stream.
    /// </summary>
    /// <returns>The loaded object.</returns>
    internal object Load() {
      return ReadObject(null);
    }

    /// <summary>
    /// Writes a given Type to the stream.
    /// </summary>
    /// <param name="name">The name of the field.</param>
    /// <param name="type">The type to write.</param>
    public void Write(string name, Type type) {
      codec.Write(name, type);
    }

    /// <summary>
    /// Write an interger to the stream.
    /// </summary>
    /// <param name="name">Name of the field.</param>
    /// <param name="num">The integer to write.</param>
    public void Write(string name, int num) {
      codec.Write(name, num);
    }

    /// <summary>
    /// Writes a long to the stream.
    /// </summary>
    /// <param name="name">Name of the field.</param>
    /// <param name="num">The long to write.</param>
    public void Write(string name, long num) {
      codec.Write(name, num);
    }

    /// <summary>
    /// Write a string into the stream.
    /// </summary>
    /// <param name="name">Name of the field.</param>
    /// <param name="str">The string to write.</param>
    public void Write(string name, string str) {
      codec.Write(name, str);
      //WriteObjectPrivate(name, obj);
    }

    /// <summary>
    /// Write a float to the stream.
    /// </summary>
    /// <param name="name">Name of the field.</param>
    /// <param name="num">The float to write.</param>
    public void Write(string name, float num) {
      codec.Write(name, num);
    }

    /// <summary>
    /// Write a bool to the stream.
    /// </summary>
    /// <param name="name">Name of the field.</param>
    /// <param name="num">The bool to write.</param>
    public void Write(string name, bool num) {
      codec.Write(name, num);
    }

    /// <summary>
    /// Writes an enum into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="value">The enumeration value.</param>
    public void WriteEnum(string name, object value) {
      codec.WriteEnum(name, value);
    }

    /// <summary>
    /// Writes an object (or primitive).
    /// </summary>
    /// <param name="name">Name of object.</param>
    /// <param name="obj">Object to write.</param>
    internal void Write(string name, object obj) {
      // if object is a null reference, serialize a reference object with id 0
      if (obj == null)
        Write( name, Purple.Serialization.Serializers.Reference.Null); 
      else {
        Type type = obj.GetType();

        // handle primitive types
        if (type.IsPrimitive)
          SerializePrimitive(name, obj);
          // handle enums
        else if (type.IsEnum)
          codec.WriteEnum(name, obj);
        else if (type == typeof(string))
          codec.Write(name, (string)obj);
        else
          WriteAsObject(name, obj);
      }
    }

    /// <summary>
    /// Writes object but also primitve types as an object.
    /// </summary>
    /// <param name="name">Name of field.</param>
    /// <param name="obj">Object to write.</param>
    public void WriteAsObject(string name, object obj) {
      // check to object to serialize if it was already serialized and return the object
      // or a newly created reference object
      obj = ReferenceManager.Serialize(obj);
      Type type = obj.GetType();

      // if object is an array, wrap it into our Array object.
      if (type.IsArray)
        WriteObjectPrivate(name, new Purple.Serialization.Serializers.Array((Array)obj));
        // handle strings
      else if (type == typeof(string))
        WriteObjectPrivate(name, new Purple.Serialization.Serializers.String((String)obj));
      else
        WriteObjectPrivate(name, obj);
    }

    private void WriteObjectPrivate(string name, object obj) { 
      Type type = obj.GetType();
      // start a new object
      codec.BeginWriteObject(name, type);

      ISerializer serializer = null;
      if (Serializer.Instance.Serializers[type].GetType() == type)
        serializer = obj as ISerializer;
      else
        serializer = Serializer.Instance.Serializers[type] as ISerializer;

      serializer.Serialize(obj, this);
         
      // end object
      codec.EndWriteObject();
    }

    private void SerializePrimitive(string name, object obj) {
      if (obj is Int32)
        codec.Write(name, (int)obj);
      else if (obj is float) 
        codec.Write(name, (float)obj);
      else if (obj is Boolean)
        codec.Write(name, (bool)obj);
      else
        Log.Error("Primitive type: " + obj.GetType() + " isn't supported!");
    }

    /// <summary>
    /// Reads an integer from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read integer.</returns>
    public int ReadInt(string name) {
      return codec.ReadInt(name);
    }

    /// <summary>
    /// Reads a long from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read long value.</returns>
    public long ReadLong(string name) {
      return codec.ReadLong(name);
    }

    /// <summary>
    /// Reads a float from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read float.</returns>
    public float ReadFloat(string name) {
      return codec.ReadFloat(name);
    }

    /// <summary>
    /// Reads a bool from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read bool.</returns>
    public bool ReadBool(string name) {
      return codec.ReadBool(name);
    }

    /// <summary>
    /// Reads a string from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read string.</returns>
    public string ReadString(string name) {
      if (codec.IsSimpleString(name))
        return codec.ReadString(name);
      else
        return (string)this.ReadObject(name);
    }

    /// <summary>
    /// Reads an enum from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <param name="type">The enumeration type.</param>
    /// <returns>The read object.</returns>
    public object ReadEnum(string name, Type type) {
      return codec.ReadEnum(name, type);
    }

    /// <summary>
    /// Reads a type from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read type.</returns>
    public Type ReadType(string name) {
      return codec.ReadType(name);
    }

    /// <summary>
    /// Reads an object (or primitive) from the stream.
    /// </summary>
    /// <param name="name">Name of field.</param>
    /// <param name="type">The type of the field.</param>
    /// <returns>The read object.</returns>
    internal object Read(string name, Type type) {
      if (type.IsPrimitive)
        return DeserializePrimitive(name, type);
      else if (type.IsEnum)
        return this.ReadEnum(name, type);
      else {
        //if (type.IsArray)
        //  return ReadObject(name);
        //else 
        if (type == typeof(string))
          return ReadString(name);
        else 
          return ReadObject(name);
      }
    }

    /// <summary>
    /// Deserializes an object of a given type.
    /// </summary>
    /// <param name="name">Name of object to deserialize.</param>
    /// <returns>The deserialized object.</returns>
    public object ReadObject(string name) {
      Type type = BeginReadObject(name);

      ISerializer serializer = Serializer.Instance.Serializers[type] as ISerializer;
      object ret = serializer.Deserialize(type, this);

      EndReadObject(ret);
      return ret;
    }

    /// <summary>
    /// Enters an object.
    /// </summary>
    /// <param name="name"></param>
    /// <returns></returns>
    public Type EnterObject(string name) {
      if (write) {
        codec.BeginWriteObject(name);
        return null;
      }
      else
        return codec.BeginReadObject(name);
    }

    /// <summary>
    /// Leaves an object.
    /// </summary>
    /// <returns></returns>
    public void LeaveObject() {
      if (write)
        codec.EndWriteObject();
      else
        codec.EndReadObject();
    }

    private Type BeginReadObject(string name) {
      Type type = codec.BeginReadObject(name);
      if (type == null)
        throw new System.Configuration.ConfigurationException("Can't read type: " + name);

      int id = 0;
      if (type.IsClass && type != typeof(Purple.Serialization.Serializers.Reference))
        id = refManager.Reserve();
      idStack.Push(id);
      return type;
    }

    private void EndReadObject(object obj) {
      codec.EndReadObject();

      int id = (int)idStack.Pop();
      if (id != 0)
        refManager.Set(id, obj);
    }

    private object DeserializePrimitive(string name, Type type) {
      if (type == typeof(Int32)) 
        return codec.ReadInt(name);
      else if (type == typeof(float))
        return codec.ReadFloat(name);
      else if (type == typeof(bool))
        return codec.ReadBool(name);
      else
        Log.Error("Primitive type: " + type.Name + " isn't supported!");
      return null;
    }

    /// <summary>
    /// Closes the stream.
    /// </summary>
    public void Close() {
      (this as IDisposable).Dispose();
    }

    void IDisposable.Dispose() {
      if (codec != null)
        codec.Close();
      codec = null;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
