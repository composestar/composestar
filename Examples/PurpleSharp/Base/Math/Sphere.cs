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
  //=================================================================
  /// <summary>
  /// Class representing a simple 3d sphere.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// The sphere may be used for collision spheres, bounding spheres and more...
  /// </remarks>
  //=================================================================
	public class Sphere
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The center of the sphere.
    /// </summary>
    public Vector3 Center {
      get {
        return center;
      }
      set {
        center = value;
      }
    }
    Vector3 center;

    /// <summary>
    /// The radius of the sphere.
    /// </summary>
    public float Radius {
      get {
        return radius;
      }
      set {
        radius = value;
      }
    }
    float radius;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a sphere.
    /// </summary>
    /// <param name="center">Origin of the sphere.</param>
    /// <param name="radius">Radius of the sphere.</param>
		public Sphere(Vector3 center, float radius)
		{
      this.center = center;
      this.radius = radius;
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Transforms the current sphere with a transformation matrix.
    /// </summary>
    /// <param name="matrix">The matrix to transform sphere with.</param>
    public void Transform(Matrix4 matrix) {
      // no scaling handled so far.
      center.Mul( ref matrix);
    }

    /// <summary>
    /// Returns true if the given vector is inside the sphere.
    /// </summary>
    /// <param name="vec">Vector to test if inside.</param>
    /// <returns>True if the vector is inside the sphere.</returns>
    public bool IsInside(Vector3 vec) {
      Vector3 dist = vec - center;
      return dist.Length() <= radius;
    }

    /// <summary>
    /// Enlarges the sphere to make the point fit into the <see cref="Sphere"/>.
    /// </summary>
    /// <param name="vec"></param>
    public void Grow(Vector3 vec) {
      Vector3 dist = vec - center;
      if (dist.LengthSquared() > radius*radius)
        radius = dist.Length();
    }

    /// <summary>
    /// Tests if the current sphere intersects with another sphere.
    /// </summary>
    /// <param name="sphere">Sphere to test with.</param>
    /// <returns>True if the current sphere intersects with another sphere.</returns>
    public bool Intersects(Sphere sphere) {
      Vector3 dist = this.center - sphere.center;
      return dist.LengthSquared() >= (this.radius + sphere.radius)*(this.radius + sphere.radius);
    }

    /// <summary>
    /// Calculates the distance between the current and another sphere.
    /// </summary>
    /// <param name="sphere">The sphere to use for calculation.</param>
    /// <returns>The distance between the current and another sphere.</returns>
    public float Distance(Sphere sphere) {
      return Basic.Sqrt( DistanceSquared(sphere) );
    }

    /// <summary>
    /// Calculates the squared distance between the current and another sphere.
    /// </summary>
    /// <param name="sphere">The sphere to use for calculation.</param>
    /// <returns>The squared distance between the current and another sphere.</returns>
    public float DistanceSquared(Sphere sphere) {
      return (sphere.center - center).LengthSquared();
    }

    /// <summary>
    /// Calculates the CollisionTime between the current moving sphere and a static sphere.
    /// </summary>
    /// <param name="sphere">The static sphere to use.</param>
    /// <param name="moveVector">The move vector of the current sphere.</param>
    /// <returns>The time of the collision relative to the length of the move Vector or float.MaxValue if there was no collision.</returns>
    public float CollisionTime(Vector3 moveVector, Sphere sphere) {
      // early escape if the distance between the spheres is greater than the moveVector plus the two radii of the spheres
      float sumRadii = radius + sphere.radius;
      if (this.Distance(sphere) > moveVector.Length() + sumRadii)
        return float.MaxValue; 
     
      // early escape if sphere moves away from the other sphere
      Vector3 ab = sphere.center - this.center;
      if (moveVector * ab < 0)
        return float.MaxValue;

      // calc the nearest point
      float moveVectorLength = moveVector.Length();
      Vector3 m0 = moveVector / moveVectorLength;
      float nearestPointFactor = m0 * ab;
      Vector3 nearestPoint = this.center + m0*nearestPointFactor;

      // early (actually quite late) escape if nearestPoint isn't near enough
      float nearestPointDistSquared = (nearestPoint - sphere.center).LengthSquared();
      if ( nearestPointDistSquared > sumRadii*sumRadii)
        return float.MaxValue;

      // starting from the nearestPoint, calculate the point on the moveVector whose distance between
      // the two spheres is equal to the sum of the radii
      float scale = nearestPointFactor - Basic.Sqrt(sumRadii*sumRadii - nearestPointDistSquared);
      return scale / moveVectorLength;
    }
        
    /// <summary>
    /// Returns the collision time of the current moving sphere with another sphere.
    /// </summary>
    /// <param name="moveVector">The move vector of the current sphere.</param>
    /// <param name="sphere">The other moving sphere.</param>
    /// <param name="otherMoveVector">The move vector of the other sphere.</param>
    /// <returns>The collision time of the current moving sphere with a another sphere.</returns>
    public float CollisionTime(Vector3 moveVector, Sphere sphere, Vector3 otherMoveVector) {
      return CollisionTime((moveVector - otherMoveVector), sphere);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
