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

namespace Purple.Math {
  //=================================================================
  /// <summary>
  /// Implementation of an axis aligned bounding box.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.5</para>
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
  public struct AABB {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    /// <summary>
    /// Lower/left/front point.
    /// </summary>
    Vector3 min;

    /// <summary>
    /// Upper/right/back point.
    /// </summary>
    Vector3 max;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Access to the lower, left, front <see cref="Vector3"/>.
    /// </summary>
    [Purple.Serialization.Serialize()]
    public Vector3 Min {
      get {
        return min;
      }
      set {
        min = value;
      }
    }

    /// <summary>
    /// Access to the upper, right, back <see cref="Vector3"/>.
    /// </summary>
    [Purple.Serialization.Serialize()]
    public Vector3 Max {
      get {
        return max;
      }
      set {
        max = value;
      }
    }

    /// <summary>
    /// Returns the size of the bounding box.
    /// </summary>
    public Vector3 Size {
      get {
        return max - min;
      }
    }

    /// <summary>
    /// Center of the bounding box.
    /// </summary>
    public Vector3 Center {
      get {
        return min + (max-min)*0.5f;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates an axis aligned bound box.
    /// </summary>
    /// <param name="min">Lower, left, front point.</param>
    /// <param name="max">Upper, right, back point.</param>
    public AABB(Vector3 min, Vector3 max) {
      this.min = min;
      this.max = max;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methdos
    //---------------------------------------------------------------
    /// <summary>
    /// Tests if point is inside box.
    /// </summary>
    /// <param name="vec">Point to test if it is inside.</param>
    /// <returns>True if point is inside box.</returns>
    public bool IsInside(Vector3 vec) {
      return Vector3.AllLessOrEqual(vec, max) && Vector3.AllLessOrEqual(min, vec);
    }

    /// <summary>
    /// Enlarge the box to make the point fit into the <see cref="AABB"/>.
    /// </summary>
    /// <param name="vec">Point to add.</param>
    public void Grow(Vector3 vec) {
      if (vec.X > max.X) max.X = vec.X;
      if (vec.Y > max.Y) max.Y = vec.Y;
      if (vec.Z > max.Z) max.Z = vec.Z;

      if (vec.X < min.X) min.X = vec.X;
      if (vec.Y < min.Y) min.Y = vec.Y;
      if (vec.Z < min.Z) min.Z = vec.Z;
    }

    /// <summary>
    /// Enlarge the box to make the point fit into the <see cref="AABB"/>.
    /// </summary>
    /// <param name="x">The x part of the point to add.</param>
    /// <param name="y">The y part of the point to add.</param>
    /// <param name="z">The z part of the point to add.</param>
    public void Grow(float x, float y, float z) {
      if (x > max.X) max.X = x;
      if (y > max.Y) max.Y = y;
      if (z > max.Z) max.Z = z;

      if (x < min.X) min.X = x;
      if (y < min.Y) min.Y = y;
      if (z < min.Z) min.Z = z;
    }

    /// <summary>
    /// Enlarge the box to make the passed box fit into our <see cref="AABB"/>.
    /// </summary>
    /// <param name="aabb">Box to add.</param>
    public void Grow(AABB aabb) {
      Grow(aabb.Min);
      Grow(aabb.Max);
    }

    /// <summary>
    /// Test if two <see cref="AABB"/> intersect.
    /// </summary>
    /// <param name="b">Second <see cref="AABB"/>.</param>
    /// <returns>True if they overlap.</returns>
    public bool Intersects(AABB b) {
      return !(Vector3.OneLess(this.Max, b.Min) ||
             Vector3.OneLess(b.Max, this.Min));
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
