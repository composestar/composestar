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

namespace Purple.Graphics {
  //=================================================================
  /// <summary>
  /// A simple perspective camera.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class Camera : IApply {
    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The distance to the near clipping plane.
    /// </summary>
    public float Near {
      get {
        return near;
      }
      set {
        near = value;
        RecalcProjectionMatrix();
      }
    }
    float near;

    /// <summary>
    /// The distance to the far clipping plane.
    /// </summary>
    public float Far {
      get {
        return far;
      }
      set {
        far = value;
        RecalcProjectionMatrix();
      }
    }
    float far;

    /// <summary>
    /// The aspect ration width/height.
    /// </summary>
    public float Aspect {
      get {
        return aspect;
      }
      set {
        aspect = value;
        this.RecalcProjectionMatrix();
      }
    }
    float aspect;

    /// <summary>
    /// Field of view of the y-Axis.
    /// </summary>
    public float FovY {
      get {
        return fovY;
      }
      set {
        fovY = value;
        this.RecalcProjectionMatrix();
      }
    }
    float fovY;
    
    /// <summary>
    /// The view matrix.
    /// </summary>
    public Matrix4 View {
      get {
        return view;
      }
      set {
        view = value;
      }
    }
    Matrix4 view = Matrix4.Identity;
    
    /// <summary>
    /// Projection matrix.
    /// </summary>
    public Matrix4 Projection {
      get {
        return projection;
      }
    }
    Matrix4 projection;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new perspective camera.
    /// </summary>
    public Camera() {
      aspect = 4.0f / 3.0f;
      fovY = Math.Basic.PI / 3;
      near = 2.0f;
      far = 5000.0f;
      RecalcProjectionMatrix();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    void RecalcProjectionMatrix() {
      projection = Matrix4.PerspectiveFOV(fovY, aspect, near, far);
    }

    /// <summary>
    /// Applies the camera.
    /// </summary>
    public void Apply() {
      Device.Instance.Transformations.View = view;
      Device.Instance.Transformations.Projection = projection;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
