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
  //=================================================================
  /// <summary>
  /// The material of a certain object.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class Material {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The ambient color of the material.
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
    Vector4 ambient = Color.Vector4( Color.White );

    /// <summary>
    /// The diffuse color of the material.
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
    /// The emissive color of the material.
    /// </summary>
    public int Emissive {
      get {
        return Color.From(emissive);
      }
      set {
        emissive = Color.Vector4(value);
      }
    }

    /// <summary>
    /// The emissive color as a vector4.
    /// </summary>
    public Vector4 EmissiveVector {
      get {
        return emissive;
      }
      set {
        emissive = value;
      }
    }
    Vector4 emissive = Color.Vector4( Color.Black );

    /// <summary>
    /// The specular color of the material.
    /// </summary>
    public int Specular {
      get {
        return Color.From(specular);
      }
      set {
        specular = Color.Vector4(value);
      }
    }

    /// <summary>
    /// The specular color as a vector4.
    /// </summary>
    public Vector4 SpecularVector {
      get {
        return specular;
      }
      set {
        specular = value;
      }
    }
    Vector4 specular = Color.Vector4( Color.White );


    /// <summary>
    /// The sharpness of the specular highlights.
    /// </summary>
    public int SpecularSharpness {
      get {
        return specularSharpness;
      }
      set {
        specularSharpness = value;
      }
    }
    int specularSharpness = 32;
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
    /// Fills a shaderConstant with the material.
    /// </summary>
    /// <param name="constant">The constant that takes the material.</param>
    public virtual void Fill(IShaderConstant constant) {
      if (constant.Children.Contains("Ambient"))
        constant.Children["Ambient"].Set( AmbientVector );
      if (constant.Children.Contains("Diffuse"))
        constant.Children["Diffuse"].Set( DiffuseVector );
      if (constant.Children.Contains("Emissive"))
        constant.Children["Emissive"].Set( EmissiveVector );
      if (constant.Children.Contains("Specular"))
        constant.Children["Specular"].Set( SpecularVector );
      if (constant.Children.Contains("SpecularSharpness"))
        constant.Children["SpecularSharpness"].Set( SpecularSharpness );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
