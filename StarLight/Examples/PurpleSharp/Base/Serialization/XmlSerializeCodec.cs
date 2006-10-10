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
using System.Xml;
using System.Collections;

namespace Purple.Serialization
{
  //=================================================================
  /// <summary>
  /// Implementation of the <see cref="ISerializeCodec"/> interface 
  /// for serializing objects to xml files.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  ///   This one is slow but very useful for finding serialization bugs.
  ///   For faster serialization I suggest to use the <see cref="BinarySerializeCodec"/>.
  /// </remarks>
  //=================================================================
  public class XmlSerializeCodec : ISerializeCodec {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Stream stream;
    XmlDocument document;
    XmlElement node;
    ArrayList indices = new ArrayList();
    bool write;

    /// <summary>
    /// Stream containing the serialized data;
    /// </summary>
    public Stream Stream { 
      get {
        return stream;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialization
    //---------------------------------------------------------------
    /// <summary>
    /// Serializes the given object into the given stream.
    /// </summary>
    /// <param name="obj">Object to serialize</param>
    /// <param name="stream">Target stream.</param>
    public static void Save(object obj, Stream stream) {
      Serializer.Instance.Save(obj, stream, new XmlSerializeCodec());
    }

    /// <summary>
    /// Serializes the given object into the given file.
    /// </summary>
    /// <param name="obj">Object to serialize</param>
    /// <param name="fileName">Target file</param>
    public static void Save(object obj, string fileName) {
       Serializer.Instance.Save(obj, fileName, new XmlSerializeCodec());
    }

    /// <summary>
    /// Loads an object from a file.
    /// </summary>
    /// <param name="fileName">Name of file to load object from.</param>
    /// <returns>The loaded object.</returns>
    public static object Load(string fileName) {
      return Serializer.Instance.Load(fileName, new XmlSerializeCodec());
    }

    /// <summary>
    /// Loads an object from a file.
    /// </summary>
    /// <param name="stream">Stream to load object from.</param>
    /// <returns>The loaded object.</returns>
    public static object Load(Stream stream) {
      return Serializer.Instance.Load(stream, new XmlSerializeCodec());
    }

    /// <summary>
    /// Initializes the codec with the stream.
    /// </summary>
    /// <param name="stream">The stream to use.</param>
    /// <param name="write">Reading or writing data?</param>
    public void Init(Stream stream, bool write) {
      this.write = write;
      this.stream = stream;
      if (write) {
        document = new XmlDocument();
        document.AppendChild( document.CreateXmlDeclaration("1.0", System.Text.Encoding.Default.BodyName, null));
      } else {
        document = new XmlDocument();
        document.Load(stream);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Write an interger into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The integer to write.</param>
    public void Write(string name, int num) {
      node.SetAttribute(name, num.ToString());
    }

    /// <summary>
    /// Write a long into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The long to write.</param>
    public void Write(string name, long num) {
      node.SetAttribute(name, num.ToString());
    }

    /// <summary>
    /// Write a string into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="str">The string to write.</param>
    public void Write(string name, string str) {
      node.SetAttribute(name, str);
    }

    /// <summary>
    /// Write a float into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The float to write.</param>
    public void Write(string name, float num) {
      node.SetAttribute(name, num.ToString());
    }

    /// <summary>
    /// Writes an enum into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="value">The enumeration value.</param>
    public void WriteEnum(string name, object value) {  
      node.SetAttribute(name, value.ToString());
    }

    /// <summary>
    /// Write a bool into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="num">The float to write.</param>
    public void Write(string name, bool num) {
      node.SetAttribute(name,num.ToString());
    }

    /// <summary>
    /// Write a Type into the stream.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="type">The type to write.</param>
    public void Write(string name, Type type) {
      Write( name, Serializer.Instance.GetTypeString(type) );
    }

    /// <summary>
    /// Starts with a new object.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <param name="type">Type of the object.</param>
    public void BeginWriteObject(string name, Type type) {
      string elementName = Serializer.Instance.GetTypeString(type);
      System.Diagnostics.Debug.Assert(elementName != null, "Type: " + type.FullName + " not bound to Serializer!");
      XmlElement newNode = document.CreateElement(elementName);
      if (node == null)
        document.AppendChild( newNode );
      else 
        node.AppendChild(newNode);
      node = newNode;

      if (name != null)
        Write( "name", name );
    }

    /// <summary>
    /// Starts with a new object.
    /// </summary>
    /// <param name="typeName">Name of the variable.</param>
    public void BeginWriteObject(string typeName) {
      XmlElement newNode = document.CreateElement(typeName);
      if (node == null)
        document.AppendChild( newNode );
      else 
        node.AppendChild(newNode);
      node = newNode;
    }


    /// <summary>
    /// Ends with a new object.
    /// </summary>
    public void EndWriteObject() {
      node = node.ParentNode as XmlElement;
    }

    /// <summary>
    /// Tests if the next element is a simple string or in object form.
    /// </summary>
    /// <param name="name">Name of element.</param>
    /// <returns>True if next element is a simple string.</returns>
    public bool IsSimpleString(string name) {
      return node.HasAttribute(name);
    }

    /// <summary>
    /// Starts reading the next object.
    /// </summary>
    /// <param name="name">Name of the variable.</param>
    /// <returns>Returns the type of the next object.</returns>
    public Type BeginReadObject(string name) {
      indices.Add(0);
      if (name != null)
        if (node == null)
          node = document.DocumentElement;
        else
          node = FindElement(node, "name", name);
      else {
        if (node == null)
          node = document.DocumentElement;
        else {
          int index = (int)indices[ indices.Count - 2 ];
          node = node.ChildNodes[index] as XmlElement;
          index++;
          indices[ indices.Count - 2 ] = index;
        }
      }
      return Serializer.Instance.GetTypeFromString(node.Name);
    }

    /// <summary>
    /// Ends reading an object.
    /// </summary>
    public void EndReadObject() {
      node = node.ParentNode as XmlElement;
      indices.RemoveAt( indices.Count - 1 );
    }

    /// <summary>
    /// Reads an integer from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read integer.</returns>
    public int ReadInt(string name) {
      return int.Parse( node.Attributes[name].Value );
    }

    /// <summary>
    /// Reads a long from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read long value.</returns>
    public long ReadLong(string name) {
      return long.Parse( node.Attributes[name].Value );
    }

    /// <summary>
    /// Reads a float from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read float.</returns>
    public float ReadFloat(string name) {
      return float.Parse( node.Attributes[name].Value );
    }

    /// <summary>
    /// Reads a bool from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read bool.</returns>
    public bool ReadBool(string name) {
      return bool.Parse( node.Attributes[name].Value );
    }

    /// <summary>
    /// Reads a type from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read type.</returns>
    public Type ReadType(string name) {
      string type = node.Attributes[name].Value; 
      return Serializer.Instance.GetTypeFromString( type );
    }

    /// <summary>
    /// Reads a string from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <returns>The read string.</returns>
    public string ReadString(string name) {
      return node.Attributes[name].Value;
    }

    /// <summary>
    /// Reads an enum from the stream.
    /// </summary>
    /// <param name="name">Name of the element to read.</param>
    /// <param name="type">The enumeration type.</param>
    /// <returns>The read object.</returns>
    public object ReadEnum(string name, Type type) {
      return Enum.Parse(type, node.Attributes[name].Value);
    }

    private XmlElement FindElement(XmlNode parent, string attribute, string name) {
      XmlNodeList list = parent.ChildNodes;
      for (int i=0; i<list.Count; i++) {
        XmlElement element = list[i] as XmlElement;
        if (element.HasAttribute(attribute) && element.GetAttribute(attribute) == name)
          return element;
      }
      // not found
      return null;
    }

    /// <summary>
    /// Closes the stream.
    /// </summary>
    public void Close() {
      if (write)
        document.Save(stream);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
