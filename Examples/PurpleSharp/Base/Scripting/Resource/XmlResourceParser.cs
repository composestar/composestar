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
/*using System;
using System.IO;
using System.Xml;
using System.Collections;

namespace Purple.Scripting.Resource
{
  //=================================================================
  /// <summary>
  /// a parser for the xml version of the resource script
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
	public class XmlResourceParser : IParser, IResourceParser
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    XmlTextReader reader;
    ResourceSemantics semantics;
    ResourceParser expressionParser = new ResourceParser();
    string fileName;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new instance of an xml resource parser
    /// </summary>
		public XmlResourceParser()
		{
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// loads a certain script
    /// </summary>
    /// <param name="script">script to load</param>
    public void Load(string script) {
      fileName = "script";
      Stream stream = Purple.Tools.StringHelper.ToStream(script);
      LoadStream(stream);
    }

    /// <summary>
    /// loads a new script from a stream
    /// </summary>
    /// <param name="stream">stream to load script from</param>
    public void LoadStream(Stream stream) {
      fileName = "script";
      reader = new XmlTextReader(stream);
      reader.WhitespaceHandling = WhitespaceHandling.None;
    }

    /// <summary>
    /// loads a script from a file
    /// </summary>
    /// <param name="fileName">name of file to load script from</param>
    public void LoadFile(string fileName) {
      this.fileName = fileName;
      FileStream stream = new FileStream(fileName, FileMode.Open, FileAccess.Read);
      LoadStream(stream);
    }

    /// <summary>
    /// execute script
    /// </summary>
    /// <returns>the variables in form of an IDictionary object</returns>
    /// <exception cref="Purple.Scripting.ParserException">in case of a syntactical error a ParserException is thrown</exception>
    /// <exception cref="Purple.Scripting.ScannerException">in case of a textual error a ScannerException is thrown</exception>
    /// <exception cref="Purple.Scripting.SemanticException">in case of a semantical error a SemanticException is thrown</exception>
    public Variables Execute() {
      ResourceSemantics semantics = new ResourceSemantics(null);
      ((IResourceParser)this).Execute(semantics);
      return semantics.GetVariables();
    }

    void IResourceParser.Execute(ResourceSemantics semantics) {
      this.semantics = semantics;
      Next();
      if (!IsElement("script"))
        Fail("Couldn't find element: script");
      Script();
      if (!reader.EOF)
        Fail("End of Script wasn't reached!");
    }

    private void Script() {
      if (!IsElement("script"))
        Fail("Couldn't find element: script");
      Next();
      Block();
      Next();
    }

    private void Bind() {
      string name = Get("name");
      string type = Get("type");
      if (semantics.Enabled)
        semantics.TypeBinding.Add( name, type);
      Next();
    }

    private void Include() {
      string file = Get("file");
      semantics.Include(file);
      Next();
    }

    private void Namespace() {
      string name = Get("name");
      semantics.Push(name);
      Next();
      Block();
      semantics.Pop();
      Next();
    }

    private void Block() {

      while(reader.NodeType != XmlNodeType.EndElement) {
        string tagName = reader.Name;
        switch(tagName) {
          case "bind":
            Bind();
            break;
          case "include":
            Include();
            break;
          case "namespace":
            Namespace();
            break;
          case "if":
            If();
            break;
          default:
            Assignment();
            break;
        }
      }
    }

    private void If() {
      string conditionString = Get("condition");
      expressionParser.Load(conditionString);
      object condition = expressionParser.ExecuteExpression(semantics);
      semantics.If(condition);
      Next();
      if (!IsElement("then"))
        Fail("Expected then clause!");
      Next();
      Block();
      Next();
      if (IsElement("else")) {
        semantics.BeginElse(condition);
        Next();
        Block();
        Next();
        semantics.EndElse(condition);
      }
      semantics.EndIf(condition);
      Next();
    }

    private void Assignment(string tagName, string varName, string valueString) {
      object obj = null;
      switch (tagName) {
        case "var":
          obj = Value(valueString);
          break;
        case "int":
          obj = Convert.ToInt32(Value(valueString));
          break;
        case "string":
          obj = Value(valueString).ToString();
          break;
        case "float":
          obj = Convert.ToSingle(Value(valueString));
          break;
        default:
          obj = CreateObject(tagName, valueString);
        break;
      }

      this.expressionParser.Load(varName);
      object linkedObj;
      object indexer;
      this.expressionParser.ExecuteSetIdents(this.semantics, out linkedObj, out varName, out indexer);
       semantics.Set(linkedObj, varName, indexer, obj);
    }

    private void Assignment() {
      string tagName = reader.Name;
      string varName = Get("name");
      string valueString = reader.GetAttribute("value");
      Assignment(tagName, varName, valueString);
    }

    private object CreateObject(string tagName, string value) {
      object ret = null;
      int depth = reader.Depth;
      if (value != null)
        ret = Value(value);
      else
        Next();
      if (IsText()) {
        string valueString = reader.Value;
        if (ret == null)
          ret = Value(valueString);
        else
          Next();
      }
      else if (depth < reader.Depth) {
        if (ret == null)
          ret = semantics.CreateObject(tagName);
        semantics.Push(ret);
        while (depth < reader.Depth) {
          Assignment();     
        }
        semantics.Pop();
      } else if (value == null) {
        ret = semantics.CreateObject(tagName);
      }
      if (reader.NodeType == XmlNodeType.EndElement)
        Next();
      return ret;
    }

    private object Value(string valueString) {
      string value = valueString;
      if (value == null)
        value = reader.ReadString();
      if (value == null)
        Fail("Couldn't find value!");
      expressionParser.Load(value);
      object ret = expressionParser.ExecuteExpression(semantics);
      Next();
      return ret;
    }

    private void Next() {
      if (reader.EOF)
        Fail("End of script reached!");
      while (reader.Read() && (reader.NodeType != XmlNodeType.Element && 
                               reader.NodeType != XmlNodeType.EndElement &&
                               reader.NodeType != XmlNodeType.Text)) ;
    }

    private string Get(string attribute) {
      string ret = reader.GetAttribute(attribute);
      if (ret == null)
        Fail("Couldn't find attribute: " + attribute);
      return ret;
    }

    private bool IsText() {
      return reader.NodeType == XmlNodeType.Text;
    }

    private bool IsElement(string element) {
      return (reader.NodeType == XmlNodeType.Element && reader.Name == element);
    }

    private bool IsEndElement(string element) {
      return (reader.NodeType == XmlNodeType.EndElement && reader.Name == element);
    }

    private void Fail(string errorMessage) {
      throw new ParserException(fileName, reader.LineNumber, reader.LinePosition, errorMessage);
    }    
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}*/
