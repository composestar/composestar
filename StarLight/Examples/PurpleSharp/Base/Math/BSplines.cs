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

namespace Purple.Math {
  //=================================================================
  /// <summary>
  /// B-Spline
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class BSpline {
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// interpolates a vector3
    /// </summary>
    /// <param name="time">current interpolation time (0.0 - 1.0f)</param>
    /// <param name="pre">the weight giving values before the actual interpolation interval</param>
    /// <param name="from">the start of the interpolation interval</param>
    /// <param name="to">the end of the interpolation interval</param>
    /// <param name="post">the weight giving values after the actual interpolation interval</param>
    public static Vector3 Interpolate(float time, Vector3 pre, Vector3 from, Vector3 to, Vector3 post) {
      System.Diagnostics.Debug.Assert(time >= 0.0f && time <= 1.0f, "Time not in the interval 0.0f - 1.0f");
      return Vector3.Zero;
    }

    /// <summary>
    /// interpolates a certain value
    /// </summary>
    /// <param name="time">current interpolation time (0.0 - 1.0f)</param>
    /// <param name="pre">the weight giving value before the actual interpolation interval</param>
    /// <param name="from">the start of the interpolation interval</param>
    /// <param name="to">the end of the interpolation interval</param>
    /// <param name="post">the weight giving value after the actual interpolation interval</param>
    /// <returns>the interpolated value</returns>
    public static float Interpolate(float time, float pre, float from, float to, float post) {
      System.Diagnostics.Debug.Assert(time >= 0.0f && time <= 1.0f, "Time not in the interval 0.0f - 1.0f");

      return 0;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
