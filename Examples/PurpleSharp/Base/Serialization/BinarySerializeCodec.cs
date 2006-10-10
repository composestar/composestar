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

namespace Purple.Serialization
{
  //=================================================================
  /// <summary>
  /// Implementation of the <see cref="ISerializeCodec"/> interface 
  /// for serializing objects to binary data.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public class BinarySerializeCodec : ISerializeCodec {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    private enum TypeIdentifier : byte {
      Object = 1,
      String,
      Int32,
      Int16,
      Float,
      Double
    }

    BinaryWriter writer;
    BinaryReader reader;

    /// <summary>
    /// Stream containing the serialized data;
    /// </summary>
    public Stream Stream { 
      get {
        return stream;
      }
    }
    Stream stream;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Initializes the codec with the stream.
    /// </summary>
    /// <param name="stream">The stream to use.</param>
    /// <param name="write">Reading or writing data?</param>
    public void Init(Stream stream, bool write) {
      this.stream = stream;
      if (write)
        writer = new BinaryWriter(stream);
      else
        reader = new BinaryReader(stream);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Serializes the given object into the given stream.
    /// </summary>
    /// <param name="obj">Object to serialize</param>
    /// <param name="stream">Target stream.</param>
    public static void Save(object obj, Stream stream) {
      Serializer.Instance.Save(obj, stream, new BinarySerializeCodec());
    }

    /// <summary>
    /// Serializes the given object into the given file.
    /// </summary>
    /// <param name="obj">Object to serialize</param>
    /// <param name="fileName">Target file</param>
    public static void Save(object obj, string fileName) {
      Serializer.Instance.Save(obj, fileName, new BinarySerializeCodec());
    }

    /// <summary>
    /// Loads an object from a file.
    /// </summary>
    /// <param name="fileName">Name of file to load object from.</param>
    /// <returns>The loaded object.</returns>
    public static object Load(string fileName) {
      return Serializer.Instance.Load(fileName, new BinarySerializeCodec());
    }

    /// <summary>
    /// Loads an object from a file.
    /// </summary>
    /// <param name="stream">Stream to load object from.</param>
    /// <returns>The loaded object.</returns>
    public static object Load(Stream stream) {
      return Serializer.Instance.Load(stream, new BinarySerializeCodec());
    }

    /// <summary>
    /// Write an interger into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The integer to write.</param>
    public void Write(string name, int num) {
      writer.Write(num);
    }

    /// <summary>
    /// Write a long into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The long to write.</param>
    public void Write(string name, long num) {
      writer.Write(num);
    }

    /// <summary>
    /// Write a string into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="str">The string to write.</param>
    public void Write(string name, string str) {
      writer.Write( (byte)TypeIdentifier.String);
      writer.Write( str);
    }

    /// <summary>
    /// Write a float into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The float to write.</param>
    public void Write(string name, float num) {
      writer.Write(num);
    }

    /// <summary>
    /// Write a bool into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The float to write.</param>
    public void Write(string name, bool num) {
      writer.Write(num);
    }

    /// <summary>
    /// Writes an enum into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="value">The enumeration value.</param>
    public void WriteEnum(string name, object value) {  
      writer.Write((int)value);
    }

    /// <summary>
    /// Write a Type into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="type">The type to write.</param>
    public void Write(string name, Type type) {
      int typeId = Serializer.Instance.GetTypeId(type);
      writer.Write( typeId );
    }

    /// <summary>
    /// Starts with a new object.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="type">Type of the object.</param>
    public void BeginWriteObject(string name, Type type) {
      if (type == typeof(int))
        writer.Write( (byte)TypeIdentifier.Int32);
      else if (type == typeof(short))
        writer.Write( (byte)TypeIdentifier.Int16);
      else if (type == typeof(float))
        writer.Write( (byte)TypeIdentifier.Float);
      else if (type == typeof(double))
        writer.Write( (byte)TypeIdentifier.Double);
      else if (type == typeof(string))
        writer.Write( (byte)TypeIdentifier.String);
      else {
        writer.Write( (byte)TypeIdentifier.Object );
        Write( name, type);
      }
    }

    /// <summary>
    /// Starts with a new object.
    /// </summary>
    /// <param name="typeName">Name of the variable.</param>
    public void BeginWriteObject(string typeName) {
      writer.Write(0);
    }

    /// <summary>
    /// Ends with a new object.
    /// </summary>
    public void EndWriteObject() {
    }

    /// <summary>
    /// Starts reading the next object.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <returns>Returns the type of the next object.</returns>
    public Type BeginReadObject(string name) {
      TypeIdentifier ti = (TypeIdentifier)reader.ReadByte();
      switch (ti) {
        case TypeIdentifier.Double:
          return typeof(double);
        case TypeIdentifier.Float:
          return typeof(float);
        case TypeIdentifier.Int16:
          return typeof(short);
        case TypeIdentifier.Int32:
          return typeof(int);
        case TypeIdentifier.String:
          return typeof(string);
        case TypeIdentifier.Object:
          int typeId = ReadInt(null);
          return Serializer.Instance.GetTypeFromId(typeId);
        default:
          throw new NotSupportedException("Not supported so far! " + ti.ToString());
      }
    }

    /// <summary>
    /// Ends reading an object.
    /// </summary>
    public void EndReadObject() {
    }

    /// <summary>
    /// Tests if the next element is a simple string or in object form.
    /// </summary>
    /// <param name="name">Name of element.</param>
    /// <returns>True if next element is a simple string.</returns>
    public bool IsSimpleString(string name) {
      int ch = reader.PeekChar();
      return ch == (byte)TypeIdentifier.String;
    }

    /// <summary>
    /// Reads an integer from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read integer.</returns>
    public int ReadInt(string name) {
      return reader.ReadInt32();
    }

    /// <summary>
    /// Reads a long from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read long value.</returns>
    public long ReadLong(string name) {
      return reader.ReadInt64();
    }

    /// <summary>
    /// Reads a float from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read float.</returns>
    public float ReadFloat(string name) {
      return reader.ReadSingle();
    }

    /// <summary>
    /// Reads a type from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read type.</returns>
    public Type ReadType(string name) {
      int type = ReadInt(name);
      return Serializer.Instance.GetTypeFromId( type );
    }

    /// <summary>
    /// Reads a string from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read string.</returns>
    public string ReadString(string name) {
      reader.ReadChar();
      return reader.ReadString();
    }

    /// <summary>
    /// Reads a bool from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read bool.</returns>
    public bool ReadBool(string name) {
      return reader.ReadBoolean();
    }

    /// <summary>
    /// Reads an enum from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <param name="type">The enumeration type.</param>
    /// <returns>The read object.</returns>
    public object ReadEnum(string name, Type type) {
      return Enum.ToObject(type, reader.ReadInt32());
    }

    /// <summary>
    /// Closes the stream.
    /// </summary>
    public void Close() {
      if (writer != null) {
        writer.Close();
        writer = null;
      }
      if (reader != null) {
        reader.Close();
        reader = null;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
