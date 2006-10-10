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
using System.Reflection.Emit;
using System.Collections;
using Purple.Code;

namespace Purple.Aspect {
  //=================================================================
  /// <summary>
  /// A weaver creates an aspect template from the interface of the 
  /// non-aspect component which can then be connected to an aspect configuration 
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// A more detailed documentation should soon be available at www.bunnz.com
  /// </remarks>
  //=================================================================
  public class Weaver {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------    
    string assemblyName;
    CodeBuilder codeBuilder;
    //---------------------------------------------------------------    
    #endregion
    //---------------------------------------------------------------    

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------    
    /// <summary>
    /// name of assembly to generate (without extension)
    /// </summary>
    public string AssemblyName {
      get {
        return assemblyName;
      }
      set {
        assemblyName = value;
      }
    }
    //---------------------------------------------------------------    
    #endregion
    //---------------------------------------------------------------    
    
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------    
    /// <summary>
    /// Weaver for an aspect template
    /// </summary>
    public Weaver(string assemblyName) {
      Init(assemblyName);
    }
    //---------------------------------------------------------------    
    #endregion
    //---------------------------------------------------------------    
    
    //---------------------------------------------------------------
    #region Public Methods
    //---------------------------------------------------------------    
    /// <summary>
    /// initializes the weaver
    /// </summary>
    /// <param name="assemblyName">name of assembly to create</param>
    public void Init(string assemblyName) {
      AssemblyName = assemblyName;
      // Create new Aspect Assembly
      codeBuilder = new CodeBuilder(assemblyName);
    }
    
    /// <summary>
    /// saves assembly to disc
    /// </summary>
    public void Save() {
      codeBuilder.Save();    
    }

    /// <summary>
    /// takes a certain baseInterface and weaves an IAspectComponent Type
    /// the objects of this type are wrappers which add a certain functionality defined
    /// by the connected aspect(s)
    /// </summary>
    /// <param name="iface">interface to create wrapper for</param>
    /// <param name="additional">additional interfaces the aspect/object component should support</param>
    /// <returns>created Type</returns>
    public Type WeaveType(Type iface, Type[] additional) {   
         
      // Create new type
      TypeCodeBuilder tcBuilder = codeBuilder.DefineType("Aspect" + iface.Name);     

      // Params
      IDictionary fields = new Hashtable();

      // add implementation for interface IAspectComponent
      ImplementAspectComponent(iface, tcBuilder, fields);      
      
      // add implementation for interface iface
      ImplementInterface(iface, "OnMethodCall", tcBuilder, fields);

      // add additional interfaces
      if (additional != null)
        foreach(Type t in additional) {
          ImplementInterface(t, "OnAdditionalMethodCall", tcBuilder, fields);
        }      
      
      // create type and save assembly
      Type aspectType = tcBuilder.TypeBuilder.CreateType();                
      
      return(aspectType);
    }

    /// <summary>
    /// takes a certain baseInterface and weaves an IAspectComponent Type
    /// the objects of this type are wrappers which add a certain functionality defined
    /// by the connected aspect(s)
    /// </summary>
    /// <param name="iface">interface to create wrapper for</param>
    /// <returns>created Type</returns>
    public Type WeaveType(Type iface) {                   
      return(WeaveType(iface, null));
    }
    //--------------------------------------------------------------- 
    #endregion
    //--------------------------------------------------------------- 
    
    //---------------------------------------------------------------
    #region Internal Helper Methods
    //---------------------------------------------------------------    
    void ImplementAspectComponent(Type baseInterface, TypeCodeBuilder tcBuilder, IDictionary fields) {

      // Add Interface IAspectComponent
      TypeBuilder builder = tcBuilder.TypeBuilder;
      builder.AddInterfaceImplementation(typeof(IAspectComponent));                  

      // property baseInstance            
      FieldBuilder fbBaseInstance = builder.DefineField("baseInstance", baseInterface, FieldAttributes.Private);                  
      tcBuilder.DefineStandardProperty("BaseInstance", fbBaseInstance, typeof(object), true, true, MethodAttributes.Public);
      fields["baseInstance"] = fbBaseInstance;

      // property aspect
      FieldBuilder fbAspect = builder.DefineField("aspectDispatcher", typeof(IAspectDispatcher), FieldAttributes.Private);
      tcBuilder.DefineStandardProperty("AspectDispatcher", fbAspect, typeof(IAspectDispatcher), true, true, MethodAttributes.Public);      
      fields["aspectDispatcher"] = fbAspect;
    }

    void ImplementInterface(Type baseInterface, string calledMethod, TypeCodeBuilder tcBuilder, IDictionary fields) {

      // Add Interface baseInterface
      TypeBuilder builder = tcBuilder.TypeBuilder;
      builder.AddInterfaceImplementation(baseInterface);

      // get methods from baseInterface and create wrapper methods
      MethodInfo[] methods = baseInterface.GetMethods();  
      foreach (MethodInfo m in methods)
        ImplementMethod(m, calledMethod, fields, builder);
    }

    void ImplementMethod(MethodInfo m, string calledMethod, IDictionary fields, TypeBuilder builder) {
      // define method
      Type[] paramTypes = MethodHelper.GetParameterTypes(m);
      MethodBuilder mb = builder.DefineMethod(m.Name, MethodAttributes.Public | MethodAttributes.Virtual,
        m.ReturnType, paramTypes);

      // get intermediate code generator
      ILGenerator il = mb.GetILGenerator();  

      // implement local fields
      ImplementLocalFields(il, paramTypes, fields);
        
      // implements method calls for interface IAspectComponent     
      ImplementAspectMethod(calledMethod, il, fields);

      // return value if method has an return value
      ImplementReturnValue(il, m);    
      il.Emit(OpCodes.Ret);  
    }

    void ImplementLocalFields(ILGenerator il, Type[] paramTypes, IDictionary fields) {
      // ------------------ local fields --------------------        
      // methodinfo of currently called method
      il.DeclareLocal(typeof(MethodInfo));         
      // array of params
      il.DeclareLocal(typeof(object[]));   

      // ---------------- fill local fields -----------------  
      // store current method in local field 0      
      il.EmitCall(OpCodes.Call, typeof(MethodBase).GetMethod("GetCurrentMethod"), null);                                       
      il.Emit(OpCodes.Stloc_0);

      // create array with length of params for local field 1
      il.Emit(OpCodes.Ldc_I4, paramTypes.Length);
      il.Emit(OpCodes.Newarr, typeof(object));
      il.Emit(OpCodes.Stloc_1);
      // fill array
      for (int i=0; i<paramTypes.Length; i++) {
        il.Emit(OpCodes.Ldloc_1);               // load array field
        il.Emit(OpCodes.Ldc_I4, i);             // load index onto stack
        il.Emit(OpCodes.Ldarg, i+1);            // load argument number i+1
        if (paramTypes[i].IsValueType)          // test if it is a valuetype
          il.Emit(OpCodes.Box, paramTypes[i]);  //   yes => box it
        il.Emit(OpCodes.Stelem_Ref);            // store value into array
      }; 
    }

    void ImplementAspectMethod(string name, ILGenerator il, IDictionary fields) {
      // ---------------- call before method ----------------          
      il.Emit(OpCodes.Ldarg_0);        // put aspect onto stack
      il.Emit(OpCodes.Ldfld, (FieldInfo)fields["aspectDispatcher"]);        
      il.Emit(OpCodes.Ldarg_0);        // put baseInstance onto stack  (param object obj)
      il.Emit(OpCodes.Ldfld, (FieldInfo)fields["baseInstance"]);
      il.Emit(OpCodes.Ldloc_0);        // put local field 0            (param MethodBase method)
      il.Emit(OpCodes.Ldloc_1);        // put local field 1            (param object[] parameters)
      il.EmitCall(OpCodes.Callvirt, typeof(IAspectDispatcher).GetMethod(name), null);
    }

    void ImplementReturnValue(ILGenerator il, MethodInfo m) {
      // --------------- care for return value -------------
      if (m.ReturnType == typeof(void))       // if no return value => pop from stack
        il.Emit(OpCodes.Pop);
      else if (m.ReturnType.IsValueType) {    // if return value is a valueType
        il.Emit(OpCodes.Unbox, m.ReturnType); // unbox
        il.Emit(OpCodes.Ldind_Ref);
      }                
    }
    //--------------------------------------------------------------- 
    #endregion
    //--------------------------------------------------------------- 
  }

	/// <summary>
	/// 
	/// </summary>
  public interface IVec {
		/// <summary>
		/// 
		/// </summary>
		/// <param name="x"></param>
    void SetX(int x);
		/// <summary>
		/// 
		/// </summary>
		/// <param name="y"></param>
		/// <returns></returns>
    int SetY(int y);
  }

	/// <summary>
	/// 
	/// </summary>
  public class Vec : IVec{
		/// <summary>
		/// 
		/// </summary>
		/// <param name="x"></param>
    public void SetX(int x) {
      Log.Info("Vec.SetX: " + x);
    }

		/// <summary>
		/// 
		/// </summary>
		/// <param name="y"></param>
		/// <returns></returns>
    public int SetY(int y) {
      Log.Info("Vec.SetY: " + y);
      return(y);
    } 
  } 

	/// <summary>
	/// 
	/// </summary>
  public interface IAdditional {
		/// <summary>
		/// 
		/// </summary>
		/// <param name="x"></param>
		/// <returns></returns>
    int method(int x);
  }

	/// <summary>
	/// 
	/// </summary>
  public class ExampleAspect : Aspect, IAdditional {
		/// <summary>
		/// 
		/// </summary>
    public override void Before() {
      Log.Info("Before Method: " + Method.Name);
    }

		/// <summary>
		/// 
		/// </summary>
		/// <returns></returns>
    public override object Instead() {
      Log.Info("Instead of Method1: " + Method.Name);
      object ret = Method.Invoke(Object, Parameters);           
      Log.Info("Instead of Method2: " + Method.Name);
      return(ret);
    }

		/// <summary>
		/// 
		/// </summary>
    public override void After() {
      Log.Info("After Method: " + Method.Name);
    }

		/// <summary>
		/// 
		/// </summary>
		/// <param name="x"></param>
		/// <returns></returns>
    public int method(int x) {
      Log.Info("Called method: " + x.ToString() + "   Object: " + Object.ToString());
      return(x*2);
    }
  }

	/// <summary>
	/// 
	/// </summary>
  public class Test {
		/// <summary>
		/// 
		/// </summary>
    public Test() {

      // Create Assembly "Aspect" which includes a proxy for an IVec interface
      // and the given additional interface IAdditional
      Weaver weaver = new Weaver("Aspect");
      Type aspectType = weaver.WeaveType(typeof(IVec), new Type[]{typeof(IAdditional)});
      weaver.Save();

      // From the created type create an instance
      // assign base object and dispatcher
      IAspectComponent obj = (IAspectComponent)Activator.CreateInstance(aspectType);      
      obj.BaseInstance = new Vec();
      obj.AspectDispatcher = new AspectDispatcher();

      // assign aspects to methods
      IAspect ea = new ExampleAspect();
      obj.AspectDispatcher.Add(ea, typeof(IVec).GetMethod("SetX"));
      obj.AspectDispatcher.Add(ea, typeof(IAdditional));

      // execute methods
      IVec vec = (IVec) obj;
      vec.SetX(10);
      Log.Info("RetVal: " + vec.SetY(15));

      // and also methods implemented by additional interfaces
      IAdditional add = (IAdditional) obj;
      int x = add.method(5);
      Log.Info("RetVal - Additional: " + x);


      /*Point p = new Point();      
      Debug.WriteLine(p.GetX());
      Debug.WriteLine(p.GetY());      

      AspectCompiler ac = new AspectCompiler();
      p = (Point) ac.Deploy(p, new AspectContainer());
      Debug.WriteLine(p.GetX());
      Debug.WriteLine(p.GetY());  
      Debug.WriteLine("Test");  */
          
    }
  }
}
