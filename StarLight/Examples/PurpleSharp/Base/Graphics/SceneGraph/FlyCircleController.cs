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
using Purple.Input;
using Purple.Graphics.Geometry;
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// a Controller for flying a circle
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class FlyCircleController : IEntityController {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    TransformEntity entity;
    float radius;
    float rotation;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// entity to operate on
    /// </summary>
    public ISceneEntity Entity {
      get {
        return entity;
      }
      set {
        if (!(value is TransformEntity))
          throw new Exception("FlyCircleController currently just supports TransformEntities!");
        entity = (TransformEntity)value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of a soft skinning Controller
    /// </summary>
    /// <param name="radius">the radious of the circly</param>
    public FlyCircleController(float radius) {
      this.radius = radius;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Does the manipulation
    /// </summary>
    public void Do() {
      // Todo: Add some real timing
      rotation += 0.005f;
      entity.World += Matrix4.Translation( new Vector3(radius, 0, 0)) * Matrix4.RotationY(rotation) ;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
