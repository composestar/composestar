////*****************************************************************************
////     ____                              ___                __ __      
////    /\  _`\                           /\_ \              _\ \\ \__   
////    \ \ \L\ \ __  __   _ __   _____   \//\ \       __   /\__  _  _\  
////     \ \ ,__//\ \/\ \ /\`'__\/\ '__`\   \ \ \    /'__`\ \/_L\ \\ \L_ 
////      \ \ \/ \ \ \_\ \\ \ \/ \ \ \L\ \   \_\ \_ /\  __/   /\_   _  _\
////       \ \_\  \ \____/ \ \_\  \ \ ,__/   /\____\\ \____\  \/_/\_\\_\/
////        \/_/   \/___/   \/_/   \ \ \/    \/____/ \/____/     \/_//_/ 
////                                \ \_\                                
////                                 \/_/                                            
////                  Purple# - The smart way of programming games
//#region //
//// Copyright (c) 2002-2003 by 
////   Markus Wöß
////   Bunnz@Bunnz.com
////   http://www.bunnz.com
////
//// This library is free software; you can redistribute it and/or
//// modify it under the terms of the GNU Lesser General Public
//// License as published by the Free Software Foundation; either
//// version 2.1 of the License, or (at your option) any later version.
////
//// This library is distributed in the hope that it will be useful,
//// but WITHOUT ANY WARRANTY; without even the implied warranty of
//// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//// Lesser General Public License for more details.
////
//// You should have received a copy of the GNU Lesser General Public
//// License along with this library; if not, write to the Free Software
//// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//#endregion
////*****************************************************************************
//using System;
//
//namespace Purple.Graphics.HLSL {
//
//  //=================================================================
//  /// <summary>
//  /// variability of HLSL parameter
//  /// </summary>
//  /// <remarks>
//  ///   <para>Author: Markus Wöß</para>
//  ///   <para>Since: 0.1</para>  
//  /// </remarks>
//  //=================================================================
//  public enum Variability {
//    /// <summary>value can change with each invocation of the program. </summary>
//    Varying,
//    /// <summary>A uniform parameter is one whose value does not chance with each invocation of a program, but whose value can change between groups of program invocations. </summary>
//    Uniform,
//    /// <summary>parameter never changes for the life of a compiled program. Modifying a constant parameter requires program recompilation. </summary>
//    Constant,
//    /// <summary>A structure parameter that contains parameters that differ in variability.</summary>
//    Mixed,
//  }
//
//  //=================================================================
//  /// <summary>
//  /// direction of HLSL parameter
//  /// </summary>
//  /// <remarks>
//  ///   <para>Author: Markus Wöß</para>
//  ///   <para>Since: 0.1</para>  
//  /// </remarks>
//  //=================================================================
//  public enum Direction {
//    /// <summary>Specifies an input parameter.</summary>
//    In,
//    /// <summary>Specifies an output parameter. </summary>
//    Out,
//    /// <summary>Specifies a parameter that is both input and output. </summary>
//    InOut,
//    /// <summary>No direction</summary>
//    None
//  }
//
//  //=================================================================
//  /// <summary>
//  /// HLSL parameter
//  /// </summary>
//  /// <remarks>
//  ///   <para>Author: Markus Wöß</para>
//  ///   <para>Since: 0.1</para>  
//  /// </remarks>
//  //=================================================================
//  public class Parameter {
//    //---------------------------------------------------------------
//    #region Variables
//    //---------------------------------------------------------------
//    string name;
//    Variability variability;
//    Direction direction;
//    Parameters parameters;
//    ParameterType parameterType;
//    DeclarationUsage declarationUsage;
//    int index;
//    string semantics;
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------
//
//    //---------------------------------------------------------------
//    #region IShaderConstant members
//    //---------------------------------------------------------------
//    /// <summary>
//    /// test if parameter is a shader constant
//    /// </summary>
//    /// <returns></returns>
//    public bool IsShaderConstant() {
//      return direction == Direction.In && variability == Variability.Uniform && parameterType.IsShaderConstant();
//    }
//
//    /// <summary>
//    /// name of the constant
//    /// </summary>
//    public string ConstantName {
//      get {
//        if (semantics != null && semantics.Length != 0)
//          return semantics;
//        return name;
//      }
//    }
//
//    /// <summary>
//    /// shader constant values
//    /// </summary>
//    public object[] Values { 
//      get {
//        if (!IsShaderConstant())
//          return null;
//        return ShaderConstants.Instance[ ConstantName ].Values;
//      }
//    }
//
//    /// <summary>
//    /// set value of shader constant
//    /// </summary>
//    /// <param name="matrix">matrix to set</param>
//    public void Set( Purple.Math.Matrix4 matrix ) {
//      System.Diagnostics.Debug.Assert( IsShaderConstant());
//      ShaderConstants.Instance[ ((ShaderConstant)this).Name ].Set( matrix );
//    }
//
//    /// <summary>
//    /// set value of shader constant
//    /// </summary>
//    /// <param name="vec">vector to set</param>
//    public void Set( Purple.Math.Vector3 vec ) {
//      System.Diagnostics.Debug.Assert( IsShaderConstant() );
//      ShaderConstants.Instance[ ((IShaderConstant)this).Name ].Set( vec );
//    }
//
//    /// <summary>
//    /// set value of shader constant
//    /// </summary>
//    /// <param name="vec">vector to set</param>
//    public void Set( Purple.Math.Vector4 vec ) {
//      System.Diagnostics.Debug.Assert( IsShaderConstant() );
//      ShaderConstants.Instance[ ((IShaderConstant)this).Name ].Set( vec );
//    }
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------
//
//    //---------------------------------------------------------------
//    #region Properties
//    //---------------------------------------------------------------
//    /// <summary>
//    /// name of the parameter
//    /// </summary>
//    public String Name {
//      get {
//        return name;
//      }
//    }
//
//    /// <summary>
//    /// variability of parameter (const, uniform, ...)
//    /// </summary>
//    public Variability Variability {
//      get {
//        return variability;
//      }
//    }
//
//    /// <summary>
//    /// direction of parameter (in, out, ...)
//    /// </summary>
//    public Direction Direction {
//      get {
//        return direction;
//      }
//    }
//
//    /// <summary>
//    /// holds array or struct data
//    /// </summary>
//    public Parameters Parameters {
//      get {
//        return parameters;
//      }
//    }    
//
//    /// <summary>
//    /// type of parameter
//    /// </summary>
//    public ParameterType ParameterType {
//      get {
//        return parameterType;
//      }
//    }
//
//    /// <summary>
//    /// DeclUsage of parameter
//    /// </summary>
//    public DeclarationUsage DeclarationUsage {
//      get {
//        return declarationUsage;
//      }
//    }
//
//    /// <summary>
//    /// index of parameter in constant table
//    /// or DeclarationUsageIndex
//    /// </summary>
//    public int Index {
//      get {
//        return index;
//      }
//    }
//
//    /// <summary>
//    /// semantics - string version of DeclarationUsage
//    /// but also covers user defined semantics
//    /// </summary>
//    public string Semantics {
//      get {
//        return semantics;
//      }
//    }
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------
//
//    //---------------------------------------------------------------
//    #region Initialisation
//    //---------------------------------------------------------------
//    /// <summary>
//    /// constructor
//    /// </summary>
//    // /// <param name="parameters">array/struct parameters</param>
//    public Parameter(string name, ParameterType parameterType, float[] values, Variability variability, 
//      Direction direction, DeclarationUsage declarationUsage, int index, string semantics, Parameters parameters) {
//      this.name = name;
//      this.variability = variability;
//      this.direction = direction;
//      this.parameters = parameters;
//      this.parameterType = parameterType;
//      this.index = index;
//      this.declarationUsage = declarationUsage;
//      this.semantics = semantics;
//    }
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------
//  }
//}
