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
  /// especially responsible for creating Types
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public class TypeCodeBuilder {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    CodeBuilder codeBuilder;
    TypeBuilder typeBuilder;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// codeBuilder object
    /// </summary>
    public CodeBuilder CodeBuilder {
      get {
        return codeBuilder;
      }
    }

    /// <summary>
    /// current type builder object
    /// </summary>
    public TypeBuilder TypeBuilder {
      get {
        return typeBuilder;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// for fast creation of types
    /// </summary>
    /// <param name="codeBuilder"></param>
    /// <param name="typeBuilder"></param>
    public TypeCodeBuilder(CodeBuilder codeBuilder, TypeBuilder typeBuilder) {			
      this.codeBuilder = codeBuilder;
      this.typeBuilder = typeBuilder;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// create an standard implementation of a property
    /// </summary>
    /// <param name="name">name of property</param>
    /// <param name="field">field to return or set</param>
    /// <param name="propertyType">type of property</param>
    /// <param name="hasSetter">property has a setter method?</param>
    /// <param name="hasGetter">property has a getter method?</param>
    /// <param name="visibility">can be public, protected, ...</param>
    /// <returns>of created property</returns>
    public PropertyBuilder DefineStandardProperty(string name, FieldInfo field, Type propertyType, 
                                                  bool hasSetter, bool hasGetter, MethodAttributes visibility) {
      
      // define attributes of property setter/getter methods
      MethodAttributes methodAttributes = 
          MethodAttributes.HideBySig | MethodAttributes.SpecialName | MethodAttributes.Virtual | 
          MethodAttributes.Final | MethodAttributes.NewSlot | visibility;

      // define property
      PropertyBuilder pb = typeBuilder.DefineProperty(name, PropertyAttributes.None, propertyType, null);

      // Setter stuff
      if (hasSetter) {        
        // create method
        MethodBuilder mbSetter = typeBuilder.DefineMethod("set_" + name, methodAttributes, 
          typeof(void), new Type[]{propertyType});             

        // generate code
        ILGenerator ilSetter = mbSetter.GetILGenerator();
        ilSetter.Emit(OpCodes.Ldarg_0);
        ilSetter.Emit(OpCodes.Ldarg_1);
        ilSetter.Emit(OpCodes.Stfld, field);
        ilSetter.Emit(OpCodes.Ret);     

        pb.SetSetMethod(mbSetter);
      }

      if (hasGetter) {
        // create method
        MethodBuilder mbGetter = typeBuilder.DefineMethod("get_" + name, methodAttributes,
          propertyType, null);      

        // generate code
        ILGenerator ilGetter = mbGetter.GetILGenerator();
        ilGetter.Emit(OpCodes.Ldarg_0);
        ilGetter.Emit(OpCodes.Ldfld, field);      
        ilGetter.Emit(OpCodes.Ret);

        pb.SetGetMethod(mbGetter);
      }                
      return(pb);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
