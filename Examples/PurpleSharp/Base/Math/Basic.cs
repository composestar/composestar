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
  /// Basic functions
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  ///   <para>Last Update: 0.5</para>
  /// </remarks>
  //=================================================================
	public class Basic
	{
    /// <summary>simple PI - no discussion</summary>
    public static float PI = (float)System.Math.PI;

    /// <summary>
    /// calculates the square root of a certain value
    /// </summary>
    /// <param name="value">to calc sqrt from</param>
    /// <returns>sqrt of value</returns>
    public static float Sqrt( float value) {
      return (float)System.Math.Sqrt(value);
    }

    /// <summary>
    /// Calculates the inverse square root of a certain value.
    /// </summary>
    /// <param name="x">Value to calc the inverse sqrt from.</param>
    /// <returns>The inverse sqrt.</returns>
    public unsafe static float InvSqrt( float x ) {
      return 1.0f / Sqrt(x);
    }

    /// <summary>
    /// calculates the absolute value
    /// </summary>
    /// <param name="value">to calc absolute value from</param>
    /// <returns>absolute value</returns>
    public static float Abs( float value) {
      return System.Math.Abs( value );
    }

    /// <summary>
    /// Returns the natural (base e) logarithm of a specified number.
    /// </summary>
    /// <param name="a">The number to calculate the logarithm from.</param>
    /// <returns>The natural (base e) logarithm of a specified number.</returns>
    public static float Log(double a) {
      return (float)System.Math.Log(a);
    }

    /// <summary>
    /// Returns the logarithm of a specified number in a specified base.
    /// </summary>
    /// <param name="a">The number to calculate the logarithm from.</param>
    /// <param name="newBase">The base to use.</param>
    /// <returns>The logarithm of a specified number in a specified base.</returns>
    public static float Log(double a, double newBase) {
      return (float)System.Math.Log(a, newBase);
    }

    /// <summary>
    /// Returns the base 10 logarithm of a specified number.
    /// </summary>
    /// <param name="a">The number to calculate the logarithm from.</param>
    /// <returns>The base 10 logarithm of a specified number.</returns>
    public static float Log10(double a) {
      return (float)System.Math.Log10(a);
    }

    /// <summary>
    /// Returns e raised to the specified power.
    /// </summary>
    /// <param name="power">The power to raise e.</param>
    /// <returns>E raised to the specified power.</returns>
    public static float Exp(double power) {
      return (float)System.Math.Exp(power);
    }

    /// <summary>
    /// Returns a specified number raised to the specified power.
    /// </summary>
    /// <param name="a">A number to be raised to a power.</param>
    /// <param name="power">A number that specifies a power.</param>
    /// <returns>A specified number raised to the specified power.</returns>
    public static float Pow(double a, double power) {
      return (float)System.Math.Pow(a, power);
    }

    /// <summary>
    /// Clamps the value to the given range.
    /// </summary>
    /// <param name="value">The value to clamp.</param>
    /// <param name="min">The minimum value.</param>
    /// <param name="max">The maximum value.</param>
    /// <returns>The clamped value!</returns>
    public static float Clamp(float value, float min, float max) {
      if (value < min)
        value = min;
      else if (value > max)
        value = max;
      return value;
    }

    /// <summary>
    /// Clamps the value to the given range.
    /// </summary>
    /// <param name="value">The value to clamp.</param>
    /// <param name="min">The minimum value.</param>
    /// <param name="max">The maximum value.</param>
    /// <returns>The clamped value!</returns>
    public static int Clamp(int value, int min, int max) {
      if (value < min)
        value = min;
      else if (value > max)
        value = max;
      return value;
    }
	}
}
