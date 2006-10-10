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
  /// transform entity applys a transform before rendering of a subtree
  /// and reset the old afterwards
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last change: 0.3</para>
  /// </remarks>
  //=================================================================
  public class TransformEntity : SceneEntity, ISceneEntity {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    Matrix4 world;
    Matrix4 save;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Access to the world matrix of the transform
    /// </summary>
    public Matrix4 World {
      get {
        return world;
      }
      set {
        world = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of a transform entity
    /// </summary>
    /// <param name="world">world matrix</param>
    public TransformEntity(Matrix4 world) {
      this.world = world;
    }

    /// <summary>
    /// creates an instance of a transform entity (identity matrix)
    /// </summary>
    public TransformEntity() {
      this.world = Matrix4.Identity;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Before controllers can manipulate the entity and before <c>Traverse</c> is called.
    /// </summary>
    /// <remarks>
    /// This is the right time to save the part of the state that gets changed.
    /// </remarks>
    public void Before() {
      save = Manager.CurrentState.World;
    }

    /// <summary>
    /// do preparations - Traverse is called, before entity might get culled!
    /// </summary>
    public void Traverse() {
      Manager.CurrentState.World *= world;
    }

    /// <summary>
    /// This method is called when all children were traversed.
    /// </summary>
    /// <remarks>
    /// This is the right time to restore the part of the state that was changed.
    /// </remarks>
    public void After() {
      Manager.CurrentState.World = save;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
