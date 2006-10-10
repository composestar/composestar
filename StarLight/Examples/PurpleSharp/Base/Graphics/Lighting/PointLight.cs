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
  /// Implementation for a point light.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public class PointLight : Light {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Position of the spot light.
    /// </summary>
    public Vector3 Position {
      get {
        return position;
      }
      set {
        position = value;
      }
    }
    Vector3 position;

    /// <summary>
    /// Returns the light position/direction as a 4d vector.
    /// </summary>
    /// <remarks>The w component defines the the vector is a light position or 
    /// a light direction.</remarks>
    public override Vector4 Vector4 { 
      get {
        return new Vector4( position, 1.0f );
      }
    }

    /// <summary>
    /// The radius around the light that is influenced by it.
    /// </summary>
    public override float MaxRadius { 
      get {
        return 10.0f;
      }
    }

    /// <summary>
    /// Returns the type of the light.
    /// </summary>
    public override LightType LightType { 
      get {
        return LightType.PointLight;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a point light.
    /// </summary>
    /// <param name="position">World position of the point light.</param>
    public PointLight( Vector3 position ) {
      this.position = position;
    }
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
    public override void Bind(IShaderConstant constant) {
      base.Bind(constant);
      if (constant.Children.Contains("Position"))
        constant.Children["Position"].UpdateHandler += new UpdateHandler( UpdateObjectSpacePosition );
    }

    /// <summary>
    /// Method for updating a shader with the light position in object space.
    /// </summary>
    /// <param name="sc">The shader to update.</param>
    public void UpdateObjectSpacePosition(ShaderConstant sc) {
      Transformations trans = Device.Instance.Transformations;
      sc.Set( position * Matrix4.Invert(trans.World) );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
