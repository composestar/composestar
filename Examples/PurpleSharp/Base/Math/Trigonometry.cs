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
	/// Trigonmetry functions
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public class Trigonometry {
		/// <summary>
		/// cosinus function
		/// </summary>
		/// <param name="angle">angle in radians</param>
		/// <returns>the cos of the specified angle</returns>
		public static float Cos(float angle) {
			return (float)System.Math.Cos(angle);
		}

    /// <summary>
    /// Returns the angle whose cosine is the specified number.
    /// </summary>
    /// <param name="number">The number.</param>
    /// <returns>The angle whose cosine is the specified number.</returns>
    public static float Acos(float number) {
      return (float)System.Math.Acos(number);
    }

		/// <summary>
		/// sinus function
		/// </summary>
		/// <param name="angle">angle in radians</param>
		/// <returns>the sin of the specified angle</returns>
		public static float Sin(float angle) {
			return (float)System.Math.Sin(angle);
		}

		/// <summary>
		/// co-tangens function (cos/sin)
		/// </summary>
		/// <param name="angle">angle in radians</param>
		/// <returns>co-tangens of specified angle</returns>
		public static float Cot(float angle) {
			return Cos(angle)/Sin(angle);
		}
	}
}
