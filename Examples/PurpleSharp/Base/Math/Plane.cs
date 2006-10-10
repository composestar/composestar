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
//
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
// *****************************************************************************
using System;

namespace Purple.Math
{
  //=================================================================
  /// <summary>
  /// A simple plane in 3d space.	
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
	public class Plane
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The normal vector of the plane.
    /// </summary>
    public Vector3 Normal {
      get {
        return normal;
      }
      set {
        normal = value;
      }
    }
    Vector3 normal;

    /// <summary>
    /// The d constant of the plane where Normal*X = d
    /// </summary>
    public float D {
      get {
        return d;
      }
      set {
        d = value;
      }
    }
    float d;
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
    /// Calculates the signed distance between a point and the plane.
    /// </summary>
    /// <remarks>The sign indicates if the point is on the front or back side of the plane.
    /// The front side is the side, the normal vector points to.</remarks>
    /// <param name="p">The point to calculate the width for.</param>
    /// <returns>The signed distance between a point and the plane.</returns>
    public float SignedDistance(Vector3 p) {
      return normal*p - d;
    }

    /// <summary>
    /// Calculates the distance between a point and the plane.
    /// </summary>
    /// <param name="p">The point to calculate distance for.</param>
    /// <returns>The distance between a point and the plane.</returns>
    public float Distance(Vector3 p) {
      return Basic.Abs( SignedDistance(p) );
    }

    /// <summary>
    /// Method to test if a certain point is in front of the plane.
    /// </summary>
    /// <param name="p">Point to test.</param>
    /// <returns>True if the point is in front of the plane.</returns>
    public bool IsInFront(Vector3 p) {
      return SignedDistance(p) >= 0.0f;
    }

    /// <summary>
    /// Method to test if a point lies on the plane.
    /// </summary>
    /// <param name="p">The point to test.</param>
    /// <returns>True if the point lies on the plane.</returns>
    public bool ContainsPoint(Vector3 p) {
      return SignedDistance(p) == 0.0f;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
