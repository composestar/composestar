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
  /// The implementation of the semantics of the P# resource script.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// <note type="note">This class was created by evolutionary prototyping and 
  /// should be reengineered sometime. ;-)</note>
  /// </remarks>
  //=================================================================
  internal class ResourceSemantics {
    //---------------------------------------------------------------
    #region Internal classes
    //---------------------------------------------------------------
    /// <summary>
    /// Handler that is executed during assignment.
    /// </summary>
    internal delegate ParserValue AssignmentHandler();

    /// <summary>
    /// The class the wraps a static class.
    /// </summary>
    public class StaticClass : ParserValue{

      /// <summary>
      /// Returns the name of the token.
      /// </summary>
      public string Name {
        get {
          return (string)Value;
        }
      }

      /// <summary>
      /// Creates a new instance of a static class.
      /// </summary>
      /// <param name="name">The token name.</param>
      /// <param name="type">The type.</param>
      public StaticClass(string name, Type type) {
        this.Value = name;
        this.Type = type;
      }
    }

    /// <summary>
    /// The class that wraps a scope class.
    /// </summary>
    public class ScopeClass : ParserValue {
      /// <summary>
      /// Returns the name of the token.
      /// </summary>
      public string Name {
        get {
          return (string)Value;
        }
      }

      /// <summary>
      /// Returns the scope.
      /// </summary>
      public ResourceScope Scope {
        get {
          return scope;
        }
      }
      ResourceScope scope;

      /// <summary>
      /// Creates a new scope class.
      /// </summary>
      /// <param name="name">Name of scope.</param>
      /// <param name="scope">The scope.</param>
      public ScopeClass(string name, ResourceScope scope) {
        this.Value = name;
        this.scope = scope;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    IScanner scanner = null;
    StringDictionary alreadyIncluded = new StringDictionary();

    /// <summary>
    /// returns true if semantics are enabled
    /// </summary>
    public bool Enabled {
      get {
        return enabledCounter == 0;
      }
    }
    int enabledCounter = 0;

    /// <summary>
    /// the global scope
    /// </summary>
    public ResourceScope GlobalScope {
      get {
        if (!Enabled)
          return null;
        return (ResourceScope)scopes[0];
      }
    }
    ArrayList scopes = new ArrayList();

    /// <summary>
    /// access to all namespaces
    /// </summary>
    public SortedList Namespaces {
      get {
        if (!Enabled)
          return null;
        return namespaces;
      }
    }
    SortedList namespaces = new SortedList();

    /// <summary>
    /// the current scope
    /// </summary>
    public ResourceScope CurrentScope {
      get {
        if (!Enabled)
          return null;
        return (ResourceScope)scopes[scopes.Count - 1];
      }
    }

    /// <summary>
    /// string to class binding
    /// </summary>
    public Hashtable TypeBinding {
      get {
        return typeBinding;
      }
    }
    Hashtable typeBinding = new Hashtable();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new semantics object 
    /// </summary>
    public ResourceSemantics(IScanner scanner) {
      this.scanner = scanner;
      scopes.Add(new ResourceScope("")); // add global scope
      namespaces.Add("", scopes[0]);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    public void AddBinding(string name, string typeName) {
      Type t = Type.GetType(typeName);
      if (t == null)
        Fail("Can't bind: " + typeName);
      TypeBinding.Add(name,t);
    }

    public ParserValue InvokeConstructor(Type type, ArrayList parameters) {
      Type[] types = ParserValue.GetTypes(parameters);
      ConstructorInfo info = type.GetConstructor( types );
      if (info == null)
        Fail("Couldn't find suiting constructor for: " + type.FullName);
      object createdInstance = info.Invoke( ParserValue.GetValues(parameters) );
      return new ParserValue( createdInstance, info.ReflectedType );
    }

    public void PreInvoke(string name, ParserValue obj, 
      out ParserValue invokeObject, out bool invokeConstructor) {
      
      invokeObject = new ParserValue(null, obj.Type);
      invokeConstructor = true;

      if (!Enabled)
        return;

      if (obj.IsNull) {
        // constructor
        if (name == "#")
          invokeObject.Type = ShortCut;
        else if (TypeBinding.ContainsKey(name))
          invokeObject.Type = (Type)TypeBinding[name];
        else if (CurrentScope.LinkedObject.IsNotNull) {
          invokeConstructor = false;
          invokeObject = CurrentScope.LinkedObject;
        } else
          Fail("Error: " + name + "!");
      }  // static method 
      else if (obj is StaticClass) {
        invokeConstructor = false;
        // static method
        invokeObject.Type = (obj as StaticClass).Type;
      }// method 
      else {
        invokeConstructor = false;
        invokeObject = obj;
      }
    }

    public ParserValue Invoke(string name, ParserValue invokeObject, bool invokeConstructor, ArrayList parameters) {
      if (!Enabled)
        return ParserValue.Empty;

      if (invokeConstructor)
        return InvokeConstructor(invokeObject.Type, parameters);

      BindingFlags bindingFlags;
      if (invokeObject.Value == null)
        bindingFlags = BindingFlags.Public | BindingFlags.Static  | BindingFlags.InvokeMethod;
      else
        bindingFlags = BindingFlags.Public | BindingFlags.Instance | BindingFlags.InvokeMethod;

      MethodInfo info = invokeObject.Type.GetMethod(name, bindingFlags, null, this.CalcTypes(parameters), null );
      if (info == null)
        Fail("Couldn't find method " + name + " for class " + invokeObject.Type.ToString());
      object result = null;
      try {
        result = info.Invoke(invokeObject.Value, ParserValue.GetValues(parameters));
      } catch (Exception ex) {
        Fail("Error invoking method: " + invokeObject.Type.ToString() + "." + name + Environment.NewLine +
          ex.InnerException.ToString());
      }
      return new ParserValue( result, info.ReturnType);
    }

    /*public ParserValue Invoke(string name, ParserValue obj, ArrayList parameters) {
      
      if (!Enabled)
        return ParserValue.Empty;

      if (obj.IsNull) {
        // constructor
        if (name == "#") {
          return InvokeConstructor(ShortCut, parameters);
        }          
        if (TypeBinding.ContainsKey(name)) {
          Type type = (Type)TypeBinding[name];
          return InvokeConstructor(type, parameters);
        } else if (CurrentScope.LinkedObject.IsNotNull) {
          MethodInfo info = CurrentScope.LinkedObject.Type.GetMethod(name, BindingFlags.Public | BindingFlags.Instance | BindingFlags.InvokeMethod, null, 
            this.CalcTypes(parameters), null );
          if (info == null)
            Fail("Couldn't find method: " + name);
          return new ParserValue( info.Invoke(CurrentScope.LinkedObject, ParserValue.GetValues(parameters)), info.ReturnType);
        }
      }  // static method 
      else if (obj is StaticClass) {
        // static method
        Type type = (obj as StaticClass).Type;
        MethodInfo info = type.GetMethod(name, BindingFlags.Public | BindingFlags.Static | BindingFlags.InvokeMethod);
        if (info == null)
          Fail("Couldn't find method " + name + " for static class " + type.ToString());
        return  new ParserValue( info.Invoke(null, ParserValue.GetValues(parameters)), info.ReturnType);
      }// method 
      else {
        MethodInfo info = obj.Type.GetMethod(name, BindingFlags.Public | BindingFlags.Instance | BindingFlags.InvokeMethod, null, 
          this.CalcTypes(parameters), null );
        if (info == null)
          Fail("Couldn't find method: " + name);
        return new ParserValue( info.Invoke(obj.Value, ParserValue.GetValues(parameters)), info.ReturnType);
      }
      return ParserValue.Empty;
    }*/

    private Type[] CalcTypes(ArrayList parameters) {
      Type[] types = new Type[parameters.Count];
      for (int i=0; i<parameters.Count; i++)
        types[i] = (parameters[i] as ParserValue).Type;
      return types;
    }

    public ParserValue Get(ParserValue obj, string varName, ParserValue indexer) {
      if (!Enabled)
        return ParserValue.Empty;

      if (varName != null)
        obj = Get(obj, varName);
      if (indexer.IsNotNull)
        obj = Index(obj, indexer);
      return obj;
    }

    public void Set(ParserValue obj, string varName, ParserValue indexer, ParserValue result) {
      if (!this.Enabled)
        return;

      if (obj is StaticClass) {
        StaticClass staticClass = (StaticClass)obj;
        Type type = staticClass.Type;
        if (type == null)
          Fail("Couldn't get type for: " + staticClass.Type.FullName);
        if (!SetStaticField(type, varName, result))
          obj = CurrentScope.Get(staticClass.Name);
        else
          return;
      }

      if (indexer.IsNotNull) {
        obj = Get(obj, varName, ParserValue.Empty);
        SetIndex(obj, indexer, result);
      } else {
        if (obj.IsNull)
          CurrentScope.Set(varName, result);
        else {
          if (!SetField(obj, varName, result))
              Fail("Couldn't find field: " + varName);
        }
      }
    }

    internal static bool SetStaticField(Type type, string varName, ParserValue result) {
      FieldInfo info = type.GetField(varName, BindingFlags.Public | BindingFlags.Static);
      if (info != null)
        info.SetValue( null, ChangeType(result.Value, info.FieldType));
      else {
        PropertyInfo pInfo = type.GetProperty(varName, BindingFlags.Public | BindingFlags.Static);
        if (pInfo != null)
          pInfo.SetValue( null, ChangeType(result.Value, pInfo.PropertyType), null);
        else
          return false;
      }
      return true;
    }


    internal static bool SetField(ParserValue obj, string varName, ParserValue result) {
      FieldInfo info = obj.Type.GetField(varName, BindingFlags.Public | BindingFlags.Instance);
      if (info != null)
        info.SetValue( obj.Value, ChangeType(result.Value, info.FieldType));
      else {
        PropertyInfo pInfo = obj.Type.GetProperty(varName, BindingFlags.Public | BindingFlags.Instance);
        if (pInfo != null)
          pInfo.SetValue( obj.Value, ChangeType(result.Value, pInfo.PropertyType), null);
        else
          return false;
      }
      return true;
    }

    public void Push(ParserValue linkedObject) {
      if (Enabled) {
        scopes.Add( new ResourceScope(CurrentScope, linkedObject) );
      }
    }

    public void Push(string ns) {
      if (Enabled) {
        scopes.Add( new ResourceScope(CurrentScope, ns));
        namespaces.Add( CurrentScope.Name, CurrentScope);
      }
    }

    public void Pop() {
      if (Enabled)
        scopes.RemoveAt( scopes.Count - 1 );
    }

    public ParserValue Calc(ParserValue obj1, ParserValue obj2, string symbol) {
      if (!Enabled)
        return ParserValue.Empty;

      ParserValue result = TryOperator(obj1, obj2, symbol);
      if (result.IsNotNull)
        return result;

      if (obj1.IsAString || obj2.IsAString)
        return CalcString(obj1.Value.ToString(), obj2.Value.ToString(), symbol);

      if (obj1.IsAFloat || obj2.IsAFloat)
        return CalcFloat(Convert.ToSingle(obj1.Value), Convert.ToSingle(obj2.Value), symbol);

      if (obj1.IsAInt && obj2.IsAInt)
        return CalcInt((int)obj1.Value, (int)obj2.Value, symbol);

      return ParserValue.Empty;
    }

    private ParserValue TryOperator(ParserValue obj1, ParserValue obj2, string symbol) {
      string operation = "";
      switch(symbol) {
        case "+":
          operation = "op_Addition";
          break;
        case "*":
          operation = "op_Multiplication";
          break;
        case "/":
          operation = "op_Division";
          break;
        case "-": 
          operation = "op_Subtraction";
          break;
        case "==":
          operation = "op_Equality";
          break;
        case "!=":
          operation = "op_Inequality";
          break;
        case "<":
          operation = "op_LessThan";
          break;
        case "<=":
          operation = "op_LessThanOrEqual";
          break;
        case ">":
          operation = "op_GreaterThan";
          break;
        case ">=":
          operation = "op_GreaterThanOrEqual";
          break;
        default:
          Fail("Coudln't find operation for symbol: " + symbol);
          break;
      }

      MethodInfo m = obj1.Type.GetMethod(operation, System.Reflection.BindingFlags.Public | System.Reflection.BindingFlags.Static, 
        null, new Type[]{obj1.Type, obj2.Type}, null);
      if (m != null)
        return new ParserValue(m.Invoke(null, new object[] {obj1.Value, obj2.Value}), m.ReturnType);
      return ParserValue.Empty;
    }

    private ParserValue CalcString(string obj1, string obj2, string symbol) {
      switch (symbol) {
        case "+":
          return new ParserValue( obj1 + obj2 );
        case "==":
          return new ParserValue( obj1 == obj2);
        case "!=":
          return new ParserValue( obj1 != obj2 );
        case "<":
          return new ParserValue( string.Compare(obj1, obj2) < 0 );
        case "<=":
          return new ParserValue( string.Compare(obj1, obj2) <= 0 );
        case ">":
          return new ParserValue( string.Compare(obj1, obj2) > 0 );
        case ">=":
          return new ParserValue( string.Compare(obj1, obj2) >= 0 );
        default:
          Fail("Couldn't apply operation " + symbol + " on strings!");
          break;
      }
      return ParserValue.Empty;
    }

    private ParserValue CalcFloat(float obj1, float obj2, string symbol) {
      switch(symbol) {
        case "+":
          return new ParserValue( obj1 + obj2);
        case "*":
          return new ParserValue( obj1 * obj2);
        case "/":
          return new ParserValue( obj1 / obj2);
        case "-": 
          return new ParserValue( obj1 - obj2);
        case "==":
          return new ParserValue( obj1 == obj2);
        case "!=":
          return new ParserValue( obj1 != obj2);
        case "<":
          return new ParserValue( obj1 < obj2);
        case "<=":
          return new ParserValue( obj1 <= obj2);
        case ">":
          return new ParserValue( obj1 > obj2);
        case ">=":
          return new ParserValue( obj1 >= obj2);
        default:
          Fail("Couldn't apply operation " + symbol + " on floats!");
        return ParserValue.Empty;
      }
    }

    private ParserValue CalcInt(int obj1, int obj2, string symbol) {
      switch(symbol) {
        case "+":
          return new ParserValue( obj1 + obj2);
        case "*":
          return new ParserValue( obj1 * obj2);
        case "/":
          return new ParserValue( obj1 / obj2);
        case "-": 
          return new ParserValue( obj1 - obj2);
        case "==":
          return new ParserValue( obj1 == obj2);
        case "!=":
          return new ParserValue( obj1 != obj2);
        case "<":
          return new ParserValue( obj1 < obj2);
        case "<=":
          return new ParserValue( obj1 <= obj2);
        case ">":
          return new ParserValue( obj1 > obj2);
        case ">=":
          return new ParserValue( obj1 >= obj2);
        default:
          Fail("Couldn't apply operation " + symbol + " on integers!");
        return ParserValue.Empty;
      }
    }

    public void Invert(ParserValue obj1, int sign) {
      if (!Enabled || sign==1)
        return;

      if (obj1.IsAFloat)
        obj1.Value = sign * (float)(obj1.Value);
      else if (obj1.IsAInt)
        obj1.Value = sign * (int)(obj1.Value);
    }

    public ParserValue CreateObject(string type) {
      if (!Enabled)
        return ParserValue.Empty;
      Type t = (Type)TypeBinding[type];
      if (t == null)
        Fail("Couldn't create object for type: " + type);
      return new ParserValue(System.Activator.CreateInstance(t));
    }

    public void If(ParserValue exp) {
      // Push();
      if (exp.IsNull || exp.Value.Equals(false))
        enabledCounter--;
    }

    public void BeginElse(ParserValue exp) {
      if (exp.IsNull || exp.Value.Equals(false))
        enabledCounter++;
      else
        enabledCounter--;
    }

    public void EndElse(ParserValue exp) {
      if (exp.IsNull || exp.Value.Equals(false))
        enabledCounter--;
      else
        enabledCounter++;
    }

    public void EndIf(ParserValue exp) {
      if (exp.IsNull || exp.Value.Equals(false))
      enabledCounter++;
      // Pop();
    }

    public ParserValue Concatenation(bool b1, bool b2, string symbol) {
      switch (symbol) {
        case "||":
          return new ParserValue( b1 || b2, typeof(bool) );
        case "&&":
          return new ParserValue( b1 && b2, typeof(bool) );
        default:
          Fail("Couldn't apply operation " + symbol + " on strings!");
        return new ParserValue( false, typeof(bool) );
      }
    }

    internal static bool GetField(ParserValue obj, string token, out ParserValue result) {
      result = ParserValue.Empty;
      FieldInfo info = obj.Type.GetField(token, BindingFlags.Public | BindingFlags.Instance);
      if (info != null) {
        result = new ParserValue( info.GetValue(obj.Value), info.FieldType) ;
        return true;
      }

      PropertyInfo pInfo = obj.Type.GetProperty(token, BindingFlags.Public | BindingFlags.Instance);
      if (pInfo != null) {
        result = new ParserValue( pInfo.GetValue(obj.Value,null), pInfo.PropertyType) ;
        return true;
      }
      return false;
    }

    internal static Type GetFieldType(Type type, string token) {
      FieldInfo info = type.GetField(token, BindingFlags.Public | BindingFlags.Instance);
      if (info != null)
        return info.FieldType;

      PropertyInfo pInfo = type.GetProperty(token, BindingFlags.Public | BindingFlags.Instance);
      if (pInfo != null)
        return pInfo.PropertyType;
      return null;
    }

    internal static bool GetStaticField(Type type, string token, out ParserValue result) {
      result = ParserValue.Empty;
      FieldInfo info = type.GetField(token, BindingFlags.Public | BindingFlags.Static);
      if (info != null) {
        result = new ParserValue( info.GetValue(null), info.FieldType);
        return true;
      }

      PropertyInfo pInfo = type.GetProperty(token, BindingFlags.Public | BindingFlags.Static);
      if (pInfo != null) {
        result = new ParserValue( pInfo.GetValue(null,null), pInfo.PropertyType) ;
        return true;
      }
      return false;
    }

    internal static bool ContainsField(ParserValue obj, string name) {
      FieldInfo info = obj.Type.GetField(name, BindingFlags.Public | BindingFlags.Instance);
      if (info != null)
        return true;
      PropertyInfo pInfo = obj.Type.GetProperty(name, BindingFlags.Public | BindingFlags.Instance);
      if (pInfo != null)
        return true;
      return false;
    }

    public ParserValue Get(ParserValue obj, string token) {
      if (!Enabled)
        return ParserValue.Empty;

      if (obj.IsNull) {
        if (token == "#")
          obj = new StaticClass( token, ShortCut);
        else if (TypeBinding.ContainsKey(token)) {
          Type type = (Type)TypeBinding[token];
          obj = new StaticClass(token, type);
        } else if (CurrentScope.Contains(token)) {
          obj = CurrentScope.Get(token);
        } else if (Namespaces.Contains(token)) {
          obj = new ScopeClass(token, (ResourceScope)Namespaces[token]);
        } else
          Fail("Can't find field or type: " + token);
        return obj;
      }
      ParserValue result = ParserValue.Empty;
      if (obj is StaticClass) {
        Type type = (obj as StaticClass).Type;
        if (GetStaticField(type, token, out result))
          return result;
        obj = CurrentScope.Get((obj as StaticClass).Name);
      } else if (obj is ScopeClass) {
        ResourceScope scope = (obj as ScopeClass).Scope;
        if (scope.Contains(token))
          return scope.Get(token);
        string newScopeName = scope.Name + "." + token;
        if (Namespaces.Contains(newScopeName))
          return new ScopeClass( newScopeName, (ResourceScope)Namespaces[newScopeName] );
        else
          obj = CurrentScope.Get(scope.Name);
      }
      if (!GetField(obj, token, out result))
        Fail("Couldn't get field/property " + token + " for type " + obj.Type.ToString());
      return result;
    }

    public ParserValue Index(ParserValue obj, ParserValue indexer) {
      if (!Enabled)
        return ParserValue.Empty;

      if (obj.Type.IsArray) {
        int index = Convert.ToInt32(indexer.Value);
        if ((obj.Value as Array).Length > index) {
          Array array = (obj.Value as Array);
          return new ParserValue(array.GetValue( index ), array.GetType().GetElementType()) ;
        }
        Fail("Index out of range: " + index);
      }
        
      
      PropertyInfo info = obj.Type.GetProperty("Item", new Type[] {indexer.Type});
      if (info == null)
        Fail("Couldn't find indexer for: " + indexer.Value);
      MethodInfo mi = info.GetGetMethod();
      if (mi == null)
        Fail("Indexer has no getter method!");
      return new ParserValue(mi.Invoke(obj.Value, new object[]{indexer.Value}), mi.ReturnType); 
    }

    public void SetIndex(ParserValue obj, ParserValue indexer, ParserValue val) {
      if (!Enabled)
        return;

      if (obj.Type.IsArray) {
        (obj.Value as Array).SetValue( val.Value, Convert.ToInt32(indexer.Value) );
      } else {      
        PropertyInfo info = obj.Type.GetProperty("Item", new Type[] {indexer.Type});
        if (info == null)
          Fail("Couldn't find indexer for: " + indexer);
        MethodInfo mi = info.GetSetMethod();
        if (mi == null)
          Fail("Indexer has no getter method!");
        mi.Invoke(obj.Value, new object[]{val.Value, indexer.Value}); 
      }
    }

    public void Include(string fileName) {
      if (!Enabled)
        return;

      FileInfo fileInfo = new FileInfo(fileName);
      if (!fileInfo.Exists)
        Fail("Couldn't find file: " + fileName);

      fileName = fileInfo.FullName;

      if (alreadyIncluded.ContainsKey(fileName))
        Fail( "File: " + fileName + " was already included!" );

      IResourceParser parser = null;
      /*if (fileInfo.Extension.ToLower() == ".xml")
        parser = new XmlResourceParser();
      else*/
        parser = new ResourceParser();

      try {
        parser.LoadFile(fileName);
      } catch {
        Fail( "Can't load " + fileName );
      }
      alreadyIncluded.Add(fileName, fileName);
      parser.Execute(this);
    }

    private void Fail(string error) {
      throw new SemanticException( scanner.FileName, scanner.Line, scanner.Column, error);
    }

    /// <summary>
    /// returns the variables collection
    /// </summary>
    /// <returns></returns>
    public Variables GetVariables() {
      Variables vars = new Variables();
      foreach (DictionaryEntry entry in namespaces) {
        vars.Add((string)entry.Key, ((ResourceScope)entry.Value).Variables);
      }
      return vars;
    }

    public Type ShortCut {
      get {
        if (shortCuts.Count == 0)
          Fail("No ShortCut defined!");
        return (Type)shortCuts[shortCuts.Count - 1];
      }
    }
    private System.Collections.ArrayList shortCuts = new System.Collections.ArrayList();

    public void PushShortCut(Type type) {
      if (!Enabled)
        return;
      shortCuts.Add(type);
    }

    public void PopShortCut() {
      if (!Enabled)
        return;
      shortCuts.RemoveAt(shortCuts.Count - 1);
    }

    public bool PushClassShortCut(Type type) {
      if (!Enabled)
        return false;

      object[] attributes = type.GetCustomAttributes(typeof(ShortCutAttribute), false);
      if (attributes.Length == 0)
        return false;
      PushShortCut( (attributes[0] as ShortCutAttribute).Type );
      return true;
    }

    public bool PushAssignShortCut(ParserValue obj, string varName, ParserValue indexer) {
      if (!Enabled)
        return false;

      Type type = null;
      if (obj.IsNotNull)
        type = obj.Type;
      else if (CurrentScope.LinkedObject.IsNotNull)
        type = CurrentScope.LinkedObject.Type;
      else
        return false;

      if (varName != null) {
        type = GetFieldType(type, varName);
        if (type == null)
          return false;
      }

      if (indexer.IsNotNull && type.IsArray)
        PushShortCut(type.GetElementType());
      else
        PushShortCut(type);
      return true;
    }

    static object ChangeType(object obj, Type targetType) {
      IConvertible conv = obj as IConvertible;
      if (conv != null)
        return Convert.ChangeType(obj, targetType);
      return obj;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
