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

namespace Purple.Graphics {
	//=================================================================
	/// <summary>
	/// a simple triangle consisting of three indices
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last Update: 0.7</para>
	/// </remarks>
	//=================================================================
	public struct Triangle {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
		/// <summary>first vertex</summary>
		public int A;
		/// <summary>second vertex</summary>
		public int B;
		/// <summary>third vertex</summary>
		public int C;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
		/// <summary>
		/// operator []
		/// </summary>
		public int this[int index] {
			get {					
				switch(index) {
					case 0:
						return A;
					case 1:
						return B;
					case 2:
						return C;
					default:
						throw new IndexOutOfRangeException("TriangleIndex must be between 0..2");
				}
			}

			set {
				switch(index) {
					case 0:
						A = value;
						break;
					case 1:
						B = value;
						break;
					case 2:
						C = value;
						break;
					default:
						throw new IndexOutOfRangeException("TriangleIndex must be between 0..2");
				}
			}
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------		

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// creates a triangle
		/// </summary>
		/// <param name="a">first vertex</param>
		/// <param name="b">second vertex</param>
		/// <param name="c">third vertex</param>
		public Triangle(int a, int b, int c) {
			A = a;
			B = b;
			C = c;
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------		
	}
}
