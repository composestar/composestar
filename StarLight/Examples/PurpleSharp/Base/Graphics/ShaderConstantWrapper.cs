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
using System.Collections;

using Purple.Math;
using Purple.Graphics.Core;

namespace Purple.Graphics {
  //=================================================================
  /// <summary>
  /// A wrapper for a shader constant.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class ShaderConstantWrapper : IShaderConstant {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the wrapped constant.
    /// </summary>
    public IShaderConstant InternalConstant {
      get {
        return internalConstant;
      }
    }
    IShaderConstant internalConstant;

    /// <summary>
    /// Returns the object that is used for mapping it to the constant table on the cpu 
    /// or the a parameter of a shader.
    /// </summary>
    public object MappingObject {
      get {
        return mappingObject;
      }
    }
    object mappingObject;

    /// <summary>
    /// The type of the shader constant.
    /// </summary>
    public ShaderConstantType ConstantType { 
      get {
        return internalConstant.ConstantType;
      }
    }

    /// <summary>
    /// The shader values.
    /// </summary>
    public Array Values {
      get {
        return internalConstant.Values;
      }
    }

    /// <summary>
    /// Name of the constant.
    /// </summary>
    public string Name { 
      get {
        return internalConstant.Name;
      }
    }

    /// <summary>
    /// Returns the semantic of the ShaderConstant.
    /// </summary>
    public string Semantic {
      get {
        return internalConstant.Semantic;
      }
    }

    /// <summary>
    /// The child objects.
    /// </summary>
    public ShaderConstants Children {
      get {
        return children;
      }
    }
    ShaderConstants children = new ShaderConstants();

    /// <summary>
    /// UpdateHandler for updating the shaderConstant before it is used.
    /// </summary>
    public UpdateHandler UpdateHandler { 
      get {
        return internalConstant.UpdateHandler;
      }
      set {
        internalConstant.UpdateHandler = value;
      }
    }

    /// <summary>
    /// List of annotations.
    /// </summary>
    public SortedList Annotations {
      get {
        return annotations;
      }
    }
    SortedList annotations = new SortedList();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Creates a new ShaderConstantWrapper.
    /// </summary>
    /// <param name="constant">The constant to wrap.</param>
    /// <param name="mappingObject">The object that is used for mapping it to the constant table on the cpu or to a native shader parameter.</param>
    public ShaderConstantWrapper(IShaderConstant constant, object mappingObject) {
      this.internalConstant = constant;
      this.mappingObject = mappingObject;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------	
    /// <summary>
    /// Update the shader constant.
    /// </summary>
    public void Update() {
      internalConstant.Update();
    }

    /// <summary>
    /// Set an array of floats.
    /// </summary>
    /// <param name="floats">Floats to set.</param>
    public void Set(float[] floats) {
      internalConstant.Set(floats);
    }

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    public void Set( float value ) {
      internalConstant.Set(value);
    }

    /// <summary>
    /// Set an array of ints.
    /// </summary>
    /// <param name="ints">The integers to set.</param>
    public void Set(int[] ints) {
      internalConstant.Set(ints);
    }

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    public void Set( int value ) {
      internalConstant.Set(value);
    }

    /// <summary>
    /// Set an array of bools.
    /// </summary>
    /// <param name="bools">The bools to set.</param>
    public void Set(bool[] bools) {
      internalConstant.Set(bools);
    }

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    public void Set( bool value ) {
      internalConstant.Set(value);
    }

    /// <summary>
    /// Sets a matrix.
    /// </summary>
    /// <param name="matrix">The matrix to set.</param>
    public void Set( Matrix4 matrix ) {
      internalConstant.Set(matrix);
    }

    /// <summary>
    /// Sets a matrix.
    /// </summary>
    /// <param name="matrix">The matrix to set.</param>
    public void Set( Matrix3 matrix ) {
      internalConstant.Set(matrix);
    }

    /// <summary>
    /// Sets a 3d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    public void Set( Vector3 vec ) {
      internalConstant.Set(vec);
    }

    /// <summary>
    /// Sets a 4d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    public void Set( Vector4 vec ) {
      internalConstant.Set(vec);
    }

    /// <summary>
    /// Sets a 2d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    public void Set( Purple.Math.Vector2 vec ) {
      internalConstant.Set(vec);
    }

    /// <summary>
    /// Sets a texture.
    /// </summary>
    /// <param name="tex">The texture to set.</param>
    public void Set( ITexture tex) {
      internalConstant.Set(tex);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}

