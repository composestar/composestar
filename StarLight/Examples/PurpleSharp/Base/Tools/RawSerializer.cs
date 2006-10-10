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
using System.IO;
using System.Collections;
using System.Runtime.InteropServices;

namespace Purple.Tools {
	//=================================================================
	/// <summary>
	/// class to load data directly from a stream into structs
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>
	///   <para>Last Update: 0.6</para>
	/// </remarks>
	//=================================================================
	public class RawSerializer {
		/// <summary>
		/// loads data from a stream into a certain type
		/// </summary>
		/// <param name="stream">stream to load data from</param>
		/// <param name="type">to fill with data</param>
		/// <returns>filled object of type with data from stream</returns>
		public static object Deserialize( Stream stream, Type type ) {			
			int rawSize = Marshal.SizeOf( type );
			byte[] rawData = new byte[rawSize];
			stream.Read(rawData,0, rawSize);								
			GCHandle handle = GCHandle.Alloc( rawData, GCHandleType.Pinned );
			IntPtr buffer = handle.AddrOfPinnedObject();
			object ret = Marshal.PtrToStructure( buffer, type );
			handle.Free();
			return ret;
		}

		/// <summary>
		/// loads data from a stream into an array of certain types
		/// </summary>
		/// <param name="stream">stream to load data from</param>
		/// <param name="type">to fill with data</param>
		/// <param name="count">number of instances to create</param>
		/// <returns>filled objects of type with data from stream</returns>
		public static Array DeserializeArray( Stream stream, Type type, int count) {
			// TODO: make faster by reading stream once and not for every instance!!!
			Array objects = Array.CreateInstance(type,count);
			for(int i=0; i<count; i++) {
				objects.SetValue(Deserialize(stream, type), i);
			}
			return objects;
		}

		/// <summary>
		/// returns the rawData of an object
		/// </summary>
		/// <param name="obj">object to get data from</param>
		/// <returns>rawData</returns>
		public static byte[] Serialize( object obj ) {
			int rawSize = Marshal.SizeOf( obj );
			byte[] rawData = new byte[ rawSize ];
			GCHandle handle = GCHandle.Alloc( rawData, GCHandleType.Pinned );
			IntPtr buffer = handle.AddrOfPinnedObject();
			Marshal.StructureToPtr( obj, buffer, false );
			handle.Free();
			return rawData;
		}
	}
}
