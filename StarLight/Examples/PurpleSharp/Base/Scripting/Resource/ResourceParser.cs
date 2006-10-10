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
using System.Reflection;
using System.Collections;
using System.Collections.Specialized;

namespace Purple.Scripting.Resource
{
  //=================================================================
  /// <summary>
  /// A <see cref="ParserValue"/> contains a value and a type.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// <para>For finding out the correct method it is necessary to know 
  /// the types parameters. While it is usually very easy to retrieve the type of an 
  /// object, it is rather hard if the parameter is <c>null</c>.</para>
  /// <para>For that reasons, values and types are stored in <see cref="ParserValue"/> 
  /// objects.</para>
  /// </remarks>
  //=================================================================
  internal class ParserValue {
    public static ParserValue empty = new ParserValue(null, null);

    /// <summary>
    /// The value of the <see cref="ParserValue"/>.
    /// </summary>
    public object Value;

    /// <summary>
    /// The type of the <see cref="ParserValue"/>.
    /// </summary>
    /// <remarks>
    /// Although the value may be null, this element contains a type. This is necessary 
    /// for finding the best suited method, although the parameters may be null.
    /// </remarks>
    public Type Type;

    /// <summary>
    /// Creates a new instance of a parser value.
    /// </summary>
    /// <param name="value">The value of the <see cref="ParserValue"/>.</param>
    /// <param name="type">The type of the <see cref="ParserValue"/>, if the 
    /// value is not null, the type of the value is used.</param>
    public ParserValue(object value, Type type) {
      this.Value = value;
      if (value != null)
        this.Type = value.GetType();
      else
        this.Type = type;
    }

    /// <summary>
    /// Creates a new instance of a parser value.
    /// </summary>
    /// <param name="value">The new value.</param>
    public ParserValue(object value) {
      System.Diagnostics.Debug.Assert(value != null);
      this.Value = value;
      this.Type = value.GetType();
    }

    /// <summary>
    /// Protected standard constructor.
    /// </summary>    
    protected ParserValue() {
    }

    /// <summary>
    /// Returns true if the value of the element is null.
    /// </summary>
    public bool IsNull {
      get {
        return Value == null;
      }
    }

    /// <summary>
    /// Returns true if the value of the element is not null.
    /// </summary>
    public bool IsNotNull {
      get {
        return !IsNull;
      }
    }

    /// <summary>
    /// Test if the current element isA type.
    /// </summary>
    /// <param name="type">The type to test for.</param>
    /// <returns>True if the current element isA type.</returns>
    public bool IsA(Type type) {
      return type.IsAssignableFrom(this.Type);
    }

    /// <summary>
    /// Returns true if the current <see cref="ParserValue"/> isa <see cref="string"/>.
    /// </summary>
    public bool IsAString {
      get {
        return IsA(typeof(string));
      }
    }

    /// <summary>
    /// Returns true if the current <see cref="ParserValue"/> isa <see cref="int"/>.
    /// </summary>
    public bool IsAInt {
      get {
        return IsA(typeof(int));
      }
    }

    /// <summary>
    /// Returns true if the current <see cref="ParserValue"/> isa <see cref="float"/>.
    /// </summary>
    public bool IsAFloat {
      get {
        return IsA(typeof(float));
      }
    }

    /// <summary>
    /// Creates an empty <see cref="ParserValue"/>
    /// </summary>
    public static ParserValue Empty {
      get {
        return empty;
      }
    }

    /// <summary>
    /// Converts an <see cref="ArrayList"/> of <see cref="ParserValue"/>s to an array.
    /// </summary>
    /// <param name="list"><see cref="ArrayList"/> of <see cref="ParserValue"/>s.</param>
    /// <param name="type">The elementType of the array.</param>
    /// <returns>A new parserValue containing the array.</returns>
    public static ParserValue ToArray(ArrayList list, Type type) {
      Array array = Array.CreateInstance(type, list.Count);
      for (int i=0; i<list.Count; i++) {
        ParserValue val = (ParserValue)list[i];
        array.SetValue( val.Value, i);
      }
      return new ParserValue( array, array.GetType() );
    }

    /// <summary>
    /// Extracts an array of <see cref="Type"/> objects from the parameters.
    /// </summary>
    /// <param name="parameters">List of <see cref="ParserValue"/> objects.</param>
    /// <returns>Array of extracted <see cref="Type"/> objects.</returns>
    public static Type[] GetTypes(ArrayList parameters) {
      Type[] types = new Type[parameters.Count];
      for (int i=0; i<parameters.Count; i++) {
        types[i] = (parameters[i] as ParserValue).Type;
      }
      return types;
    }

    /// <summary>
    /// Extracts an array of values from the parameters.
    /// </summary>
    /// <param name="parameters">List of <see cref="ParserValue"/> objects.</param>
    /// <returns>Array of extracted values.</returns>
    public static Object[] GetValues(ArrayList parameters) {
      Object[] objects = new Object[parameters.Count];
      for (int i=0; i<parameters.Count; i++) {
        objects[i] = (parameters[i] as ParserValue).Value;
      }
      return objects;
    }
  }

  //=================================================================
  /// <summary>
  /// the internal interface for resource parsers
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  /// </remarks>
  //=================================================================
  internal interface IResourceParser : IParser {
    /// <summary>
    /// execute the script
    /// </summary>
    /// <param name="semantics">the semantics to use</param>
    void Execute(Resource.ResourceSemantics semantics);
  }

  //=================================================================
  /// <summary>
  /// a resource script parser for loading all kind of resources
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
  public class ResourceParser : Parser, IResourceParser {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ResourceSemantics semantics;

    /// <summary>
    /// the scanner object, which is used internally by the parser for tokenization of the script
    /// </summary>
    protected internal override IScanner Scanner {
      get {
        return scanner;
      }
      set {
        scanner = value;
      }
    }
    private IScanner scanner = new ResourceScanner();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new resource parser object
    /// </summary>
    public ResourceParser() {
    }

    /// <summary>
    /// creates a new resource parser and loads the script from a certain file
    /// </summary>
    /// <param name="fileName">fileName of script to load</param>
    /// <returns>a new resource parser</returns>
    public static ResourceParser FromFile(string fileName) {
      ResourceParser p = new ResourceParser();
      p.LoadFile(fileName);
      return p;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// execute the script
    /// </summary>
    /// <returns>The variables, that were created during the execution of the script.</returns>
    public override Variables Execute() {
      ResourceSemantics semantics = new ResourceSemantics(this.Scanner);
      ((IResourceParser)this).Execute( semantics );
       return semantics.GetVariables();
    }

    /// <summary>
    /// Executes script, without reseting the semantics.
    /// </summary>
    /// <param name="script">Script to execute.</param>
    /// <returns>The collection of variables.</returns>
    public override Variables Execute(string script) {
      Load(script);
      if (semantics == null)
        new ResourceSemantics(this.Scanner);
      ((IResourceParser)this).Execute( semantics );
      return semantics.GetVariables();
    }

    internal object ExecuteExpression(ResourceSemantics semantics) {
      this.semantics = semantics;
      scanner.Next();

      return Expression().Value;
    }

    internal void ExecuteSetIdents(ResourceSemantics semantics, out ParserValue obj, out string varName, out ParserValue indexer) {
      this.semantics = semantics;
      scanner.Next();
      SetIdents(out obj, out varName, out indexer);
    }

    void IResourceParser.Execute(ResourceSemantics semantics) {
      // EBNF: Start = Expression
      this.semantics = semantics;
      scanner.Next();

      Block();
     
      if (scanner.TokenType != TokenType.EndOfScript)
        throw new ParserException(scanner.FileName, scanner.Line, scanner.Column, "End of script wasn't reached!");      
    }

    private void Block() {
      // EBNF: Block = { (namespace "{" Block "}" | Statement) }
      while (scanner.TokenType == TokenType.Ident || scanner.IsSymbol("#")) {
        if (scanner.Token == "namespace") {
          if (semantics.Enabled && semantics.CurrentScope.LinkedObject.IsNotNull)
            Fail("Namespace not allowed within another object declaration.");
          scanner.Next();
          if (scanner.TokenType != TokenType.Ident)
            Fail("Namespace name missing.");
          semantics.Push(scanner.Token);
          scanner.Next();
          if (!scanner.IsSymbol("{"))
            FailSymbol("{", "namespace") ;
          scanner.Next();
          Block();
          if (!scanner.IsSymbol("}"))
            FailSymbol("}","namespace");
          semantics.Pop();
          scanner.Next();
        }
        else if (scanner.Token == "if") {
          IfStatement();
        }
        else {
          Statement();
        }
      }
    }

    private void Include() {
      // EBNF: Include = "include" string
      scanner.Next();
      if (scanner.TokenType != TokenType.String)
        Fail("Error while parsing the name of the file to include!");
      semantics.Include(scanner.Token);
      scanner.Next();
    }

    private void Bind() {
      // EBNF: Bind = "bind" ident "," string
      scanner.Next();
      if (scanner.TokenType != TokenType.Ident)
        Fail("Assumed name for type binding!");
      string name = scanner.Token;
      scanner.Next();
      if (!scanner.IsSymbol(","))
        FailSymbol(",","bind");
      scanner.Next();
      if (scanner.TokenType != TokenType.String)
        Fail("Error while parsing class name for type binding!");
      string className = scanner.Token;
      scanner.Next();

      semantics.AddBinding( name, className);
    }

    private void IfStatement() {
      // EBNF: IfStatement = "if" "(" Expression ")" ( 
      scanner.Next();
      if (!scanner.IsSymbol("("))
        FailSymbol("(","if");
      scanner.Next();
      ParserValue result = Expression();
      if (!scanner.IsSymbol(")"))
        FailSymbol(")","if");
      scanner.Next();
      semantics.If(result);
      if (scanner.IsSymbol("{")) {
        scanner.Next();
        while(!scanner.IsSymbol("}")) {
          Statement();
        }
        scanner.Next();
      } else {
        Statement();
      }
      if (scanner.IsIdent("else")) {
        semantics.BeginElse(result);
        scanner.Next();
        if (scanner.IsSymbol("{")) {
          scanner.Next();
          while(!scanner.IsSymbol("}")) {
            Statement();
          }
          scanner.Next();
        } else {
          Statement();
        }
        semantics.EndElse(result);
      }
      semantics.EndIf(result);
    }

    private void Statement() {
      //EBNF: Statement = HashStatement | IdentStatement ";" | IfStatement
      if (scanner.IsIdent("if")) {
        IfStatement();
      } else if (scanner.IsSymbol("#")) {
        HashStatement();
      } else {
        IdentStatement();
        if (!scanner.IsSymbol(";"))
          FailSymbol(";", "Statement");
        scanner.Next();
      }
    }

    private void HashStatement() {
      // EBNF: "#" ( Bind | Include ) 
      if (scanner.IsSymbol("#")) {
        scanner.Next();
        if (scanner.IsIdent("bind")) {
          Bind();
        }
        else if (scanner.IsIdent("include")) {
          Include();
        }
        else
          Fail("Unknown # command");
      }
    }

    private void IdentStatement() {
      // EBNF: IdentStatement = SetIdents [ "=" Assignment ]

      ParserValue obj = ParserValue.Empty;
      string varName;
      ParserValue indexer = ParserValue.Empty;
      SetIdents(out obj, out varName, out indexer);

      if (scanner.IsSymbol("=")) {
        bool pushed = semantics.PushAssignShortCut(obj, varName, indexer);
        ParserValue result = Assignment();
        semantics.Set(obj, varName, indexer, result);
        if (pushed)
          semantics.PopShortCut();
      }
    }

    private void SetIdents(out ParserValue obj, out string varName, out ParserValue indexer) {
      // EBNF: SetIdent {. SetIdent }
      obj = ParserValue.Empty;
      SetIdent(ref obj, out varName, out indexer);

      while (scanner.IsSymbol(".")) {
        scanner.Next();
        obj = semantics.Get(obj, varName, indexer);
        SetIdent(ref obj, out varName, out indexer);
      }
    }

    private ParserValue MethodCall(string name, ParserValue obj) {
      bool invokeConstructor;
      ParserValue invokeObject;
      semantics.PreInvoke(name, obj, out invokeObject, out invokeConstructor);
      bool pushed = semantics.PushClassShortCut(invokeObject.Type);
      
      // EBNF: "(" [Param] {"," Param}] ")"
      if (!scanner.IsSymbol("("))
        FailSymbol("(", "method call");
      scanner.Next();

      ArrayList parameters = new ArrayList();
      if (!scanner.IsSymbol(")")) {
        Param(parameters);
      }
      while (!scanner.IsSymbol(")")) {
        if (!scanner.IsSymbol(","))
          FailSymbol(",", "params");
        scanner.Next();
        Param(parameters);
      }
      scanner.Next();

      if (pushed)
        semantics.PopShortCut();
      return semantics.Invoke(name, invokeObject, invokeConstructor, parameters);
    }

    private void Param(ArrayList parameters) {
      // EBNF: Expression
      ParserValue result = Expression();
      parameters.Add(result);
    }

    private ParserValue Assignment() {
      // EBNF: "=" Expression    
      if (!scanner.IsSymbol("="))
        FailSymbol("=", "assignment");
      scanner.Next();
      
      return Expression();
    }

    private ParserValue Expression() {
      // EBNF: ConcatenatedExpression "then" ConcatenatedExpression "else" ConcatenatedExpression
      ParserValue result = ConcatenatedExpression();
      if (scanner.IsIdent("then")) {
        scanner.Next();
        semantics.If(result);
        ParserValue trueExpression = ConcatenatedExpression();
        if (!scanner.IsIdent("else"))
          Fail("Missing else in if expression!");
        scanner.Next();
        semantics.BeginElse(result);
        ParserValue falseExpression = ConcatenatedExpression();
        semantics.EndElse(result);
        semantics.EndIf(result);
        if ((bool)result.Value)
          return trueExpression;
        else
          return falseExpression;
      }
      return result;
    }

    private ParserValue ConcatenatedExpression() {
      // EBNF: CompareExpression [ ("&&", "||") CompareExpression]
      ParserValue result = CompareExpression();
      if (scanner.IsSymbol("&&", "||")) {
        string symbol = scanner.Token;
        scanner.Next();
        ParserValue result2 = CompareExpression();
        result = semantics.Concatenation( (bool)result.Value, (bool)result2.Value, symbol);
      }
      return result;
    }

    private ParserValue CompareExpression() {
      // EBNF: SimpleExpression [ ("==", "!=", "<", ">", "<=", ">=") SimpleExpression]
      ParserValue result = SimpleExpression();
      if (scanner.IsSymbol("==", "!=", "<", ">", "<=", ">=")) {
        string symbol = scanner.Token;
        scanner.Next();
        ParserValue result2 = SimpleExpression();
        result = semantics.Calc(result, result2, symbol);
      }
      return result;
    }

    private ParserValue SimpleExpression() {
      
      // EBNF: Expression = Term [("+" | "-") Term]
      ParserValue result = Term();
      while (scanner.IsSymbol("+", "-")) {
        string symbol = scanner.Token;

        scanner.Next();
        ParserValue result2 = Term();

        result = semantics.Calc( result, result2, symbol ); // SEM
      }
      return result;
    }

    private ParserValue Term() {
      // EBNF: Term = RValue [("*" | "/") RValue]
      ParserValue result = RValue();
      while (scanner.IsSymbol("*", "/")) {
        string symbol = scanner.Token;

        scanner.Next();
        ParserValue result2 = RValue();

        result = semantics.Calc( result, result2, symbol ); // SEM
      }
      return result;
    }

    private ParserValue RValue() {
      // EBNF: RValue = [(-,+)](number | string | true | false | Ident {"." Ident} | "(" Expression ")" )
      int sign = 1;
      ParserValue result = ParserValue.Empty;

      if (scanner.IsSymbol("-", "+")) {
        if (scanner.Token == "-")
          sign = -1;          
        scanner.Next();
      }

      if (scanner.TokenType == TokenType.Number) {
        string token = scanner.Token;
        char lastChar = token[token.Length - 1];
        switch (lastChar) {
          case 'f':
            token = token.Substring(0, token.Length-1);
            result = new ParserValue(sign * float.Parse(token, cultureInfo), typeof(float));
            break;
          case 'x':
            token = token.Substring(0, token.Length-1);
            float value = float.Parse(token, cultureInfo) / Purple.Graphics.TwoD.QuadManager.Instance.TargetSize.X;
            result = new ParserValue(sign * value, typeof(float));
            break;  
          case 'y':
            token = token.Substring(0, token.Length-1);
            float val = float.Parse(token, cultureInfo) / Purple.Graphics.TwoD.QuadManager.Instance.TargetSize.Y;
            result = new ParserValue(sign * val, typeof(float));
            break;
          default:
            if ( scanner.Token.IndexOf('.') != -1 || lastChar == 'f')
              result = new ParserValue(sign * float.Parse(scanner.Token, cultureInfo), typeof(float));
            else
              result = new ParserValue(sign * int.Parse(scanner.Token, cultureInfo), typeof(int));
          break;
        }
        scanner.Next();
      } else if (scanner.TokenType == TokenType.String) {
        result = new ParserValue(scanner.Token, typeof(string));
        scanner.Next();
      } else if (scanner.TokenType == TokenType.Ident || scanner.IsSymbol("#")) {
        if (scanner.Token == "true") {
          result = new ParserValue(true, typeof(bool));
          scanner.Next();
        }
        else if (scanner.Token == "false") {
          result = new ParserValue(false, typeof(bool));
          scanner.Next();
        }
        else {
          Ident(ref result);
          while (scanner.IsSymbol(".")) {
            scanner.Next();
            Ident(ref result);
          }
          semantics.Invert(result, sign);
        }
      } else if (scanner.IsSymbol("(")) {
        scanner.Next();
        result = Expression();
        semantics.Invert(result, sign);
        if (!scanner.IsSymbol(")"))
          FailSymbol(")", "expression");
        scanner.Next();
      };
      return result;
    }

    private void SetIdent(ref ParserValue obj, out string varName, out ParserValue indexer) {
      // EBNF: ident ( ["[" Expression "]"] | [MethodCall ["[" Expression "]"]] )
      varName = scanner.Token;
      indexer = ParserValue.Empty;
      if (scanner.TokenType != TokenType.Ident)
        Fail("Error while parsing ident");
      scanner.Next();
      
      if (scanner.IsSymbol("[")) {
        obj = semantics.Get(obj, varName, indexer);
        indexer = Index();
        varName = null;
      }
      else if (scanner.IsSymbol("(")) {
        obj = MethodCall(varName, obj);
        varName = null;
        if (scanner.IsSymbol("[")) {
          indexer = Index();
        }
      }
    }

    private ParserValue FirstIndex(ParserValue result) {
      //EBNF: Expression | "[" [size] "]" ["{" [ expression ] { "," expresson } "}"]
      if (result is ResourceSemantics.StaticClass) {
        ResourceSemantics.StaticClass sc = (ResourceSemantics.StaticClass)result;
        scanner.Next();
        int arraySize = 0;
        if (!scanner.IsSymbol("]")) {
          ParserValue size = Expression();
          if (size.Type != typeof(int))
            Fail("Assumed size of a array as integer");
          arraySize = (int)size.Value;
        }
        if (!scanner.IsSymbol("]"))
          FailSymbol("]", "Array");
        scanner.Next();
        if (scanner.IsSymbol("{")) {
          semantics.PushShortCut(sc.Type);
          scanner.Next();
          ArrayList list = new ArrayList();
          while (!scanner.IsSymbol("}")) {
            list.Add( Expression() );
            if (!scanner.IsSymbol("}", ","))
              Fail("Error when parsing array members");
            if (scanner.IsSymbol(","))
              scanner.Next();
          }
          result = ParserValue.ToArray(list, sc.Type);
          semantics.PopShortCut();
        } else {
          Array array = Array.CreateInstance(sc.Type, arraySize);
          result = new ParserValue(array, array.GetType());
        }
        scanner.Next();
        return result;
      }
      else {
        ParserValue indexer = Index();
        result = semantics.Index(result, indexer);
        return result;
      }
    }

    private void Ident(ref ParserValue result) {
      //EBNF: ( ident | "#" ) ["[" FirstIndex "]"] [MethodCall ["[" Expression "]"]] [ "{" newObject "}" ][MethodCall ["[" Expression "]"]]
      if (scanner.TokenType != TokenType.Ident && !scanner.IsSymbol("#"))
        Fail("Error while parsing ident");
      string token = scanner.Token;
      scanner.Next();
      if (!scanner.IsSymbol("{", "("))
        result = semantics.Get(result, token);
      
      if (scanner.IsSymbol("[")) {
        result = FirstIndex(result);
      }
      if (scanner.IsSymbol("{")) {
        scanner.Next();
        result = NewObject(result, token);
        if (!scanner.IsSymbol("}"))
          FailSymbol("}", "new object");
        scanner.Next();
      }
      if (scanner.IsSymbol("(")) {
        result = MethodCall(token, result);
        if (scanner.IsSymbol("[")) {
          ParserValue indexer = Index();
          result = semantics.Index(result, indexer);
        }
      }
      if (scanner.IsSymbol("{")) {
        scanner.Next();
        result = NewObject(result, token);
        if (!scanner.IsSymbol("}"))
          FailSymbol("}", "new object");
        scanner.Next();
      }
    }

    private ParserValue Index() {
      if (!scanner.IsSymbol("["))
        FailSymbol("[", "index");
      scanner.Next();
      ParserValue indexer = Expression();
      if (!scanner.IsSymbol("]")) 
        FailSymbol("]", "index");
      scanner.Next();
      return indexer;
    }

    private ParserValue NewObject(ParserValue result, string type) {
      if (result.IsNull)
        result = semantics.CreateObject(type);
      if (result.IsNull)
        Fail("Couldn't create object of type: " + type);
      semantics.Push( result );
      Block();
      semantics.Pop();
      return result;
    }

    private void Fail(string error) {
      throw new ParserException( scanner.FileName, scanner.Line, scanner.Column, error);
    }

    private void FailSymbol(string symbol, string area) {
      throw new ParserException( scanner.FileName, scanner.Line, scanner.Column, 
        "Symbol " + symbol + " is missing (" + area + ")");
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
