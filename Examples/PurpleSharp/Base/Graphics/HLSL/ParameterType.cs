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
//  //=================================================================
//  /// <summary>
//  /// HLSL parameter class
//  /// </summary>
//  /// <remarks>
//  ///   <para>Author: Markus Wöß</para>
//  ///   <para>Since: 0.1</para>  
//  /// </remarks>
//  //=================================================================
//  public enum ParameterClass {
//    /// <summary>parameter is a scalar (one single value)</summary>
//    Scalar,
//    /// <summary>parameter is a matrix</summary>
//    Matrix,
//    /// <summary>parameter is a vector</summary>
//    Vector,
//    /// <summary>parameter is a structure of parameters</summary>
//    Struct,
//    /// <summary>parameter is an array of parameters</summary>
//    Array,
//    /// <summary>parameter is a texture</summary>
//    Texture,
//    /// <summary>parameter is a string</summary>
//    String
//  }
//
//  //=================================================================
//  /// <summary>
//  /// HLSL parameter type
//  /// </summary>
//  /// <remarks>
//  ///   <para>Author: Markus Wöß</para>
//  ///   <para>Since: 0.1</para>  
//  /// </remarks>
//  //=================================================================
//  public class ParameterType{
//    //---------------------------------------------------------------
//    #region Variables
//    //---------------------------------------------------------------
//    ParameterClass parameterClass;
//    int rows;
//    int columns;
//    Type baseType;
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------
//
//    //---------------------------------------------------------------
//    #region Properties
//    //---------------------------------------------------------------
//    /// <summary>
//    /// class of parameter
//    /// </summary>
//    public ParameterClass ParameterClass {
//      get {
//        return parameterClass;
//      }
//    }
//
//    /// <summary>
//    /// number of rows for matrices/vectors/arrays
//    /// </summary>
//    public int Rows {
//      get {
//        return rows;
//      }
//    }
//
//    /// <summary>
//    /// number of columns for matrices/vectors/arrays
//    /// </summary>
//    public int Columns {
//      get {
//        return columns;
//      }
//    }
//
//    /// <summary>
//    /// base type of one element
//    /// </summary>
//    public Type BaseType {
//      get {
//        return baseType;
//      }
//    }
//
//    /// <summary>
//    /// returns true if paramterType can be a shader constant
//    /// </summary>
//    /// <returns></returns>
//    public bool IsShaderConstant() {
//      return parameterClass != ParameterClass.Texture;
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
//    /// <param name="paramClass">class of parameter</param>
//    /// <param name="baseType">base type (float, int, bool, ITexture, ..)</param>
//    /// <param name="col">number of columns</param>
//    /// <param name="row">number of rows</param>
//    public ParameterType( ParameterClass paramClass, Type baseType, int col, int row) {
//      parameterClass = paramClass;
//      this.baseType = baseType;
//      columns = col;
//      rows = row;
//    }
//    //---------------------------------------------------------------
//    #endregion
//    //---------------------------------------------------------------
//  }
//}
