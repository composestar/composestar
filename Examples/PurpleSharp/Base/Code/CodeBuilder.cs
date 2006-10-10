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

namespace Purple.Code {
  //=================================================================
  /// <summary>
  /// class to speed up dynamic code creation via System.Reflection.Emit
  /// especially responsible for creating Assemblies
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public class CodeBuilder {
    //---------------------------------------------------------------
    #region Variables    
    //---------------------------------------------------------------
    private AssemblyName assemblyName;
    private AssemblyBuilder assemblyBuilder;
    private ModuleBuilder moduleBuilder;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// returns the name of the assembly in form of a string object
    /// </summary>
    public string AssemblyName {
      get {
        return assemblyName.Name;
      }
    }    

    /// <summary>
    /// returns the assemblyBuilder object
    /// </summary>
    public AssemblyBuilder AssemblyBuilder {
      get {
        return assemblyBuilder;
      }
    }

    /// <summary>
    /// returns the moduleBuilder object
    /// </summary>
    public ModuleBuilder ModuleBuilder {
      get {
        return moduleBuilder;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// for fast creation of assemblies
    /// </summary>
    /// <param name="assemblyName">name of assembly</param>
    public CodeBuilder(string assemblyName) {			
      Create(assemblyName);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// creates an assembly/module for the current application domain and the given name
    /// </summary>
    /// <param name="assemblyName">name of assembly</param>
    public void Create(string assemblyName) {
      // first create name for assembly
      CreateAssemblyName(assemblyName);      
      // then the assembly builder
      CreateAssemblyBuilder(AssemblyBuilderAccess.RunAndSave);
    }

    /// <summary>
    /// define a new type for the created assembly
    /// </summary>
    /// <param name="name">name of type</param>
    /// <returns>TypeBuilder object to specify Type</returns>
    public TypeCodeBuilder DefineType(string name) {
      return new TypeCodeBuilder(this, moduleBuilder.DefineType(name));
    }

    /// <summary>
    /// save the assembly
    /// </summary>
    public void Save() {
      assemblyBuilder.Save(assemblyName.Name + ".dll");                      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Internal methods
    //---------------------------------------------------------------
    void CreateAssemblyName(string assemblyName) {
      // create assembly object and define some properties
      this.assemblyName = new AssemblyName(); 
      this.assemblyName.Name = assemblyName;
      this.assemblyName.Version = new Version("0.1");
    }

    void CreateAssemblyBuilder(AssemblyBuilderAccess access) {
      // ApplicationDomain && AssemblyBuilder
      AppDomain currentDomain = AppDomain.CurrentDomain;
      assemblyBuilder = currentDomain.DefineDynamicAssembly(assemblyName, access);
      moduleBuilder = assemblyBuilder.DefineDynamicModule(assemblyName.Name + "Module", assemblyName.Name + ".dll");      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
