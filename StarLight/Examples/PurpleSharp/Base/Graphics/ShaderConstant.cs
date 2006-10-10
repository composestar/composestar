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
  //=================================================================
  /// <summary>
  /// A shader constant.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class ShaderConstant : IShaderConstant {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The type of the shader constant.
    /// </summary>
    public ShaderConstantType ConstantType { 
      get {
        return constantType;
      }
    }
    ShaderConstantType constantType = ShaderConstantType.Undefined;

    /// <summary>
    /// The shader values.
    /// </summary>
    public Array Values {
      get {
        return values;
      }
    }
    Array values;

    /// <summary>
    /// Name of the constant.
    /// </summary>
    public string Name { 
      get {
        return name;
      }
    }
    string name;

    /// <summary>
    /// Returns the semantic of the ShaderConstant.
    /// </summary>
    public string Semantic {
      get {
        return semantic;
      }
    }
    string semantic;

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
        return updateHandler;
      }
      set {
        updateHandler = value;
      }
    }
    UpdateHandler updateHandler;

    /// <summary>
    /// This value can be changed by the UpdateHandler. Everytime e.g. the matrices change, the counter is increased. 
    /// The UpdateHandler can than easily test if the current ShaderConstant is up to date.
    /// </summary>
    public int LastUpdate {
      get {
        return lastUpdate;
      }
      set {
        lastUpdate = value;
      }
    }
    int lastUpdate = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Creates a new ShaderConstant.
    /// </summary>
    /// <param name="name">Name of the constant.</param>
    public ShaderConstant(string name) : this(name, null) {     
    }

    /// <summary>
    /// Creates a new ShaderConstant.
    /// </summary>
    /// <param name="name">Name of the constant.</param>
    /// <param name="updateHandler">The updateHandler to use.</param>
    public ShaderConstant(string name, UpdateHandler updateHandler) {
      this.name = name;
      this.semantic = name;
      this.UpdateHandler = updateHandler;
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
      if (updateHandler != null)
        updateHandler(this);
      foreach(IShaderConstant child in Children)
        child.Update();
    } 

    /// <summary>
    /// Set an array of floats.
    /// </summary>
    /// <param name="floats">Floats to set.</param>
    public void Set(float[] floats) {
      lastUpdate++;
      constantType = ShaderConstantType.Float;
      values = floats;
    }

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    public void Set( float value ) {
      lastUpdate++;
      Resize(4, ShaderConstantType.Float);
      values.SetValue( value, 0);
      values.SetValue( 0.0f, 1);
      values.SetValue( 0.0f, 2);
      values.SetValue( 1.0f, 3);
    }

    /// <summary>
    /// Set an array of ints.
    /// </summary>
    /// <param name="ints">The integers to set.</param>
    public void Set(int[] ints) {
      lastUpdate++;
      constantType = ShaderConstantType.Integer;
      values = ints;
    }

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    public void Set( int value ) {
      lastUpdate++;
      Resize(4, ShaderConstantType.Integer);
      values.SetValue( value, 0);
      values.SetValue( 0, 1);
      values.SetValue( 0, 2);
      values.SetValue( 1, 3);
    }

    /// <summary>
    /// Set an array of bools.
    /// </summary>
    /// <param name="bools">The bools to set.</param>
    public void Set(bool[] bools) {
      lastUpdate++;
      constantType = ShaderConstantType.Boolean;
      values = bools;
    }

    /// <summary>
    /// Set value of shader constant.
    /// </summary>
    /// <param name="value">Value to set.</param>
    public void Set( bool value ) {
      lastUpdate++;
      Resize(4, ShaderConstantType.Boolean);
      values.SetValue( value, 0);
      values.SetValue( false, 1);
      values.SetValue( false, 2);
      values.SetValue( true, 3);
    }

    /// <summary>
    /// Sets a matrix.
    /// </summary>
    /// <param name="matrix">The matrix to set.</param>
    public void Set( Matrix4 matrix ) {
      lastUpdate++;
      Resize(16, ShaderConstantType.Float);
      for (int i=0; i<16; i++)
        values.SetValue(matrix[i], i);
    }

    /// <summary>
    /// Sets a matrix.
    /// </summary>
    /// <param name="matrix">The matrix to set.</param>
    public void Set( Matrix3 matrix ) {
      lastUpdate++;
      Resize(12, ShaderConstantType.Float);
      for (int i=0; i<9; i++) {
        values.SetValue(matrix[i], i + i/3);
      }
      values.SetValue(0.0f, 2);
      values.SetValue(0.0f, 5);
      values.SetValue(0.0f, 8);
    }

    /// <summary>
    /// Sets a 3d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    public void Set( Vector3 vec ) {
      lastUpdate++;
      Resize(4, ShaderConstantType.Float);
      values.SetValue( vec.X, 0 );
      values.SetValue( vec.Y, 1 );
      values.SetValue( vec.Z, 2 );
      values.SetValue( 1.0f, 3 );
    }

    /// <summary>
    /// Sets a 4d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    public void Set( Vector4 vec ) {
      lastUpdate++;
      Resize(4, ShaderConstantType.Float);
      values.SetValue( vec.X, 0 );
      values.SetValue( vec.Y, 1 );
      values.SetValue( vec.Z, 2 );
      values.SetValue( vec.W, 3 );
    }

    /// <summary>
    /// Sets a 2d vector.
    /// </summary>
    /// <param name="vec">Vector to set.</param>
    public void Set( Purple.Math.Vector2 vec ) {
      lastUpdate++;
      Resize(4, ShaderConstantType.Float);
      values.SetValue( vec.X, 0 );
      values.SetValue( vec.Y, 1 );
      values.SetValue( 0.0f, 2 );
      values.SetValue( 1.0f, 3 );
    }

    /// <summary>
    /// Sets a texture.
    /// </summary>
    /// <param name="tex">The texture to set.</param>
    public void Set( ITexture tex) {
      lastUpdate++;
      Resize(1, ShaderConstantType.Texture);
      values.SetValue( tex, 0);
    }

    void Resize(int size, ShaderConstantType type) {
      if (this.constantType == type && values.Length == size) {
        // no resize necessary
      } else {
        this.constantType = type;
        if (type == ShaderConstantType.Float)
          values = new float[size];
        else if (type == ShaderConstantType.Integer)
          values = new int[size];
        else if (type == ShaderConstantType.Boolean)
          values = new bool[size];
        else if (type == ShaderConstantType.Texture)
          values = new ITexture[size];
      }
    }

    /// <summary>
    /// Adds a child shader constant to the current constant. This may be a field of 
    /// a structure.
    /// </summary>
    /// <param name="name">Name of the child.</param>
    /// <param name="registerIndex">The used registerIndex.</param>
    public void AddChild( string name, int registerIndex ) {
      children.Add( new ShaderConstant(name) );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
