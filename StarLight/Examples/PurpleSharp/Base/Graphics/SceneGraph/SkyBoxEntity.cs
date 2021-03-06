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
//   Markus W??
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
using Purple.Graphics.VertexStreams;
using Purple.Graphics.Geometry;

namespace Purple.Graphics.SceneGraph
{
  //=================================================================
  /// <summary>
  /// A simple sky box entity
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus W??</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
	public class SkyBoxEntity : SceneEntity, ISceneEntity {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    SubSet subSet;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a sky box entity.
    /// </summary>
    /// <param name="size">Size of sky box.</param>
    /// <param name="format"><see cref="VertexFormat"/> to use for skybox.</param>
    public SkyBoxEntity(Vector3 size, VertexFormat format) {
      // create streams
      VertexUnit vertexUnit = new VertexUnit(format, 8);
      PositionStream position = (PositionStream)vertexUnit[ typeof(PositionStream) ];
      //TextureStream texture = (TextureStream)vertexUnit[ typeof(TextureStream) ];
      IndexStream index = new IndexStream16(24);

      // fill position data
      position[0] = new Vector3(-size.X, -size.Y, size.Z);
      position[1] = new Vector3( size.X, -size.Y, size.Z);
      position[2] = new Vector3( size.X, -size.Y,-size.Z);
      position[3] = new Vector3(-size.X, -size.Y,-size.Z);
      position[4] = new Vector3(-size.X,  size.Y, size.Z);
      position[5] = new Vector3( size.X,  size.Y, size.Z);
      position[6] = new Vector3( size.X,  size.Y,-size.Z);
      position[7] = new Vector3(-size.X,  size.Y,-size.Z);

      subSet = new SubSet(vertexUnit, index);
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
      Manager.RegisterForRendering(subSet);
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
