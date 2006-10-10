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

namespace Purple.Graphics.Lighting
{
  /// <summary>
  /// All available light types.
  /// </summary>
  public enum LightType {
    /// <summary>
    /// A directional light.
    /// </summary>
    DirectionalLight,
    /// <summary>
    /// A point light.
    /// </summary>
    PointLight,
    /// <summary>
    /// A spot light.
    /// </summary>
    SpotLight
  }

  //=================================================================
  /// <summary>
  /// The base class for all kind of lights.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
	public abstract class Light
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Flag that indicates if light is enabled.
    /// </summary>
    public bool Enabled {
      get {
        return enabled;
      }
      set {
        enabled = value;
      }
    }
    bool enabled = true;

    /// <summary>
    /// The diffuse color of the light.
    /// </summary>
    public int Diffuse {
      get {
        return Color.From(diffuse);
      }
      set {
        diffuse = Color.Vector4(value);
      }
    }

    /// <summary>
    /// The ambient color of the light.
    /// </summary>
    public int Ambient {
      get {
        return Color.From(ambient);
      }
      set {
        ambient = Color.Vector4(value);
      }
    }

    /// <summary>
    /// The diffuse color as a vector4.
    /// </summary>
    public Vector4 DiffuseVector {
      get {
        return diffuse;
      }
      set {
        diffuse = value;
      }
    }
    Vector4 diffuse = Color.Vector4( Color.White );

    /// <summary>
    /// The ambient color as a vector4.
    /// </summary>
    public Vector4 AmbientVector {
      get {
        return ambient;
      }
      set {
        ambient = value;
      }
    }
    Vector4 ambient = Color.Vector4( Color.Black );

    /// <summary>
    /// Returns the light position/direction as a 4d vector.
    /// </summary>
    /// <remarks>The w component defines the the vector is a light position or 
    /// a light direction.</remarks>
    public abstract Vector4 Vector4 { get; }

    /// <summary>
    /// The radius around the light that is influenced by it.
    /// </summary>
    public abstract float MaxRadius { get; }

    /// <summary>
    /// Returns the type of the light.
    /// </summary>
    public abstract LightType LightType { get; }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Binds a shader constant to the light.
    /// </summary>
    /// <param name="constant">The constant to bind to the light.</param>
    public virtual void Bind(IShaderConstant constant) {
      if (constant.Children.Contains("Ambient"))
        constant.Children["Ambient"].Set( AmbientVector );
      if (constant.Children.Contains("Diffuse"))
        constant.Children["Diffuse"].Set( DiffuseVector );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
