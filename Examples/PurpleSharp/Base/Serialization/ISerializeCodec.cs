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
  /// An abstract interface for a codec that defines the way a SerializeStream
  /// gets serialized.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public interface ISerializeCodec { 
    
    /// <summary>
    /// Initializes the codec with the stream.
    /// </summary>
    /// <param name="stream">The stream to use.</param>
    /// <param name="write">Reading or writing data?</param>
    void Init(Stream stream, bool write);

    /// <summary>
    /// Write an interger into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The integer to write.</param>
    void Write(string name, int num);

    /// <summary>
    /// Write a long into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The long to write.</param>
    void Write(string name, long num);

    /// <summary>
    /// Write a string into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="str">The string to write.</param>
    void Write(string name, string str);

    /// <summary>
    /// Write a float into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The float to write.</param>
    void Write(string name, float num);

    /// <summary>
    /// Write a bool into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The float to write.</param>
    void Write(string name, bool num);

    /// <summary>
    /// Write a Type into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="type">The type to write.</param>
    void Write(string name, Type type);

    /// <summary>
    /// Writes an enum into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="value">The enumeration value.</param>
    void WriteEnum(string name, object value);

    /// <summary>
    /// Reads an integer from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read integer.</returns>
    int ReadInt(string name);

    /// <summary>
    /// Reads a long from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read long value.</returns>
    long ReadLong(string name);

    /// <summary>
    /// Reads a float from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read float.</returns>
    float ReadFloat(string name);

    /// <summary>
    /// Reads a bool from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read bool.</returns>
    bool ReadBool(string name);

    /// <summary>
    /// Reads a string from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read string.</returns>
    string ReadString(string name);

    /// <summary>
    /// Reads an enum from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <param name="type">The enumeration type.</param>
    /// <returns>The read object.</returns>
    object ReadEnum(string name, Type type);

    /// <summary>
    /// Tests if the next element is a simple string or in object form.
    /// </summary>
    /// <param name="name">Name of element.</param>
    /// <returns>True if next element is a simple string.</returns>
    bool IsSimpleString(string name);

    /// <summary>
    /// Reads a type from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read type.</returns>
    Type ReadType(string name);

    /// <summary>
    /// Starts with a new object.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="type">Type of the object.</param>
    void BeginWriteObject(string name, Type type);

    /// <summary>
    /// Starts with a new object.
    /// </summary>
    /// <param name="typeName">Name of the variable.</param>
    void BeginWriteObject(string typeName);

    /// <summary>
    /// Ends with a new object.
    /// </summary>
    void EndWriteObject();

    /// <summary>
    /// Starts reading the next object.
    /// </summary>
    /// <param name="name">Name of the variable to read - or null to read next.</param>
    /// <returns>Returns the type of the next object.</returns>
    Type BeginReadObject(string name);

    /// <summary>
    /// Ends reading an object.
    /// </summary>
    void EndReadObject();

    /// <summary>
    /// Closes the stream.
    /// </summary>
    void Close();
	}
}
