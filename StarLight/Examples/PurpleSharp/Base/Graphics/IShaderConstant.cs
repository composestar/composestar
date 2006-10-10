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

using Purple.Math;
using Purple.Graphics.Core;

namespace Purple.Graphics {
  /// <summary>
  /// Inform application to update constant.
  /// </summary>
  public delegate void UpdateHandler(ShaderConstant constant);

  /// <summary>
  /// All different types of a ShaderConstant.
  /// </summary>
  public enum ShaderConstantType {
    /// <summary>
    /// The shader constant contains a float value.
    /// </summary>
    Float,
    /// <summary>
    /// The shader constant contains an integer.
    /// </summary>
    Integer,
    /// <summary>
    /// The shader constant contains a boolean.
    /// </summary>
    Boolean,
    /// <summary>
    /// The shader constant contains a texture.
    /// </summary>
    Texture,
    /// <summary>
    /// The shader constant contains a sampler.
    /// </summary>
    Sampler,
    /// <summary>
    /// Undefined value.
    /// </summary>
    Undefined
  }

  //=================================================================
  /// <summary>
  /// Abstract interface for a shader constant.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public interface IShaderConstant {
    /// <summary>
    /// The type of the shader constant.
    /// </summary>
    ShaderConstantType ConstantType { get; }

    /// <summary>
    /// The shader values.
    /// </summary>
    Array Values { get; }

    /// <summary>
    /// Name of the constant.
    /// </summary>
    string Name { get; }

    /// <summary>
    /// Returns the semantic of the ShaderConstant.
    /// </summary>
    string Semantic { get; }

    /// <summary>
    /// UpdateHandler for updating the shaderConstant before it is used.
    /// </summary>
    UpdateHandler UpdateHandler { get; set; }

    /// <summary>
    /// The child objects.
    /// </summary>
    ShaderConstants Children { get; }

    /// <summary>
    /// Update the shader constant.
    /// </summary>
    void Update();

    /// <summary>
    /// Set an array of floats.
    /// </summary>
    /// <param name="floats">Floats to set.</param>
    void Set(float[] floats);

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    void Set( float value );

    /// <summary>
    /// Set an array of ints.
    /// </summary>
    /// <param name="ints">The integers to set.</param>
    void Set(int[] ints);

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    void Set( int value );

    /// <summary>
    /// Set an array of bools.
    /// </summary>
    /// <param name="bools">The bools to set.</param>
    void Set(bool[] bools);

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    void Set( bool value );

    /// <summary>
    /// Sets a matrix.
    /// </summary>
    /// <param name="matrix">The matrix to set.</param>
    void Set( Matrix4 matrix );

    /// <summary>
    /// Sets a matrix.
    /// </summary>
    /// <param name="matrix">The matrix to set.</param>
    void Set( Matrix3 matrix );

    /// <summary>
    /// Sets a 3d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    void Set( Vector3 vec );

    /// <summary>
    /// Sets a 4d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    void Set( Vector4 vec );

    /// <summary>
    /// Sets a 2d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    void Set( Purple.Math.Vector2 vec );

    /// <summary>
    /// Sets a texture.
    /// </summary>
    /// <param name="tex">The texture to set.</param>
    void Set( ITexture tex);
  }
}
