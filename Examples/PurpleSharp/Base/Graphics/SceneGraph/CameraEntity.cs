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

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// implementation of a perspective camera	
  /// shows all nodes in its subtree
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class CameraEntity : SceneEntity, ISceneEntity {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    Matrix4 view = Matrix4.Identity;
    Matrix4 projection;
    float fovY, aspect;
    float near, far;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// near clipping plane
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

    /// <summary>
    /// far clipping plane
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
    
    /// <summary>
    /// access to the view matrix
    /// </summary>
    public Matrix4 View {
      get {
        return view;
      }
      set {
        view = value;
      }
    }

    /// <summary>
    /// Hack!
    /// </summary>
    public Matrix4 AnimatedView {
      get {
        return animatedView;
      }
      set {
        animatedView = value;
      }
    }
    Matrix4 animatedView = Matrix4.Identity;

    /// <summary>
    /// access to the projection matrix
    /// </summary>
    public Matrix4 Projection {
      get {
        return projection;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of a "perspective" camera node
    /// </summary>
    public CameraEntity() {
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
    /// <summary>
    /// do preparations - Traverse is called, before entity might get culled!
    /// </summary>
    public void Traverse() {
      Manager.CurrentState.View = view*animatedView;
      Manager.CurrentState.Projection = projection;
    }

    /// <summary>
    /// Before controllers can manipulate the entity and before <c>Traverse</c> is called.
    /// </summary>
    /// <remarks>
    /// This is the right time to save the part of the state that gets changed.
    /// </remarks>
    public void Before() {
    }

    /// <summary>
    /// This method is called when all children were traversed.
    /// </summary>
    /// <remarks>
    /// This is the right time to restore the part of the state that was changed.
    /// </remarks>
    public void After() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Helper Methods
    //---------------------------------------------------------------
    void RecalcProjectionMatrix() {
      projection = Matrix4.PerspectiveFOV(fovY, aspect, near, far);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

  }
}
