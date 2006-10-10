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
using Purple.Graphics.Geometry;
using Purple.Graphics.Effect;

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// A mesh entity - drawing a mesh
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last change: 0.3</para>
  /// </remarks>
  //=================================================================
  public class MeshEntity : SceneEntity, ISceneEntity {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Get access to the mesh.
    /// </summary>
    public Mesh Mesh {
      get {
        return mesh;
      }
      set {
        this.mesh = mesh;
      }
    }
    Mesh mesh;

    /// <summary>
    /// Get access to the tags.
    /// </summary>
    public Matrix4[] Tags {
      get {
        return tags;
      }
      set {
        tags = value;
      }
    }
    Matrix4[] tags = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// create an instance of a mesh entity
    /// </summary>
    /// <param name="mesh">mesh to render</param>
    public MeshEntity(Mesh mesh) {
      this.mesh = mesh;
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
      if (tags != null) {
        Matrix4 world = Manager.CurrentState.World;
        //Manager.CurrentState.Effect = mesh.Effect;
        for (int i=0; i<mesh.SubSets.Count; i++) {
          Manager.CurrentState.World = tags[i]*world;
          if (i < mesh.Textures.Count)
            Manager.CurrentState.Textures = (Textures)mesh.Textures[i];
          Manager.RegisterForRendering(mesh.SubSets[i]);
        }
        Manager.CurrentState.World = world;
      } else {
        //Manager.CurrentState.Effect = mesh.Effect;
        for (int i=0; i<mesh.SubSets.Count; i++) {
          if (i < mesh.Textures.Count)
            Manager.CurrentState.Textures = (Textures)mesh.Textures[i];
          Manager.RegisterForRendering(mesh.SubSets[i]);
        }
      }
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
  }
}