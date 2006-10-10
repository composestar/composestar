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

namespace Purple.Tools {
	//=================================================================
	/// <summary>
	/// Subarray is a sub part of an array
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>
	/// </remarks>
	//=================================================================
	public class SubArray {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
		Array array;
		int length;
		int offset;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// constructor
		/// </summary>
		/// <param name="array">parent array to use</param>
		public SubArray(Array array) {			
			this.array = array;
			length = 0;
			offset = 0;
		}

		/// <summary>
		/// constructor
		/// </summary>
		/// <param name="array">parent array to use</param>
		/// <param name="offset">to start in parent array</param>
		/// <param name="length">length of subArray</param>
		public SubArray(Array array, int offset, int length) {
			this.array = array;
			this.offset = offset;
			this.length = length;
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods and Properties
		//---------------------------------------------------------------
		/// <summary>
		/// length of subArray
		/// </summary>
		public int Length {
			get {
				return length;
			}
			set {
				length = value;
			}
		}

		/// <summary>
		/// offset of subArray
		/// </summary>
		public int Offset {
			get {
				return offset;
			}
			set {
				offset = value;
			}
		}

		/// <summary>
		/// whole array
		/// </summary>
		public Array Array {
			get {
				return array;
			}
		}

		/// <summary>
		/// offseted indexer
		/// </summary>
		public object this[int index] {
			get {
				if (index < 0 ||index >= length)
					throw new IndexOutOfRangeException("Index invalid!");
				return array.GetValue(index + offset);
			}
			set {
				if (index < 0 || index >= length)
					throw new IndexOutOfRangeException("Index invalid!");
				array.SetValue(value, index + offset);
			}
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
