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

namespace Purple.Math
{
  /// <summary>
  /// Planes of a Frustum.
  /// </summary>
  public enum FrustumPlanes {
    /// <summary></summary>
    Left,
    /// <summary></summary>
    Right,
    /// <summary></summary>
    Top,
    /// <summary></summary>
    Bottom,
    /// <summary></summary>
    Near,
    /// <summary></summary>
    Far
  }

  //=================================================================
  /// <summary>
  /// A class that represents the viewing frustum.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
	public class Frustum
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The planes of the Frustum.
    /// </summary>
    public Plane[] Planes {
      get {
        return planes;
      }
    }
    Plane[] planes = new Plane[6];
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new frustum from a projection matrix.
    /// </summary>
    public Frustum(Matrix4 projection) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Tests if a certain point is within the frustum.
    /// </summary>
    /// <param name="vec">Vector to test.</param>
    /// <returns>True if the point is within the frustum.</returns>
    public bool Contains(Vector3 vec) {
      // Todo: do some early rejection test with a bounding sphere or AABB.
      for (int i=0; i<planes.Length; i++) {
        if (planes[i].IsInFront(vec))
          return false;
      }
      return true;
    }

    /// <summary>
    /// Tests if a sphere is at least partly contained by the Frustum.
    /// </summary>
    /// <param name="sphere">The sphere to test for.</param>
    /// <returns>True if the sphere is at least partly contained by the Frustum.</returns>
    public bool Contains(Sphere sphere) {
      // Todo: some early rejection test with a bounding sphere or AABB
      for (int i=0; i<planes.Length; i++) {
        if (planes[i].SignedDistance(sphere.Center) > sphere.Radius)
          return false;
      }
      return true;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
