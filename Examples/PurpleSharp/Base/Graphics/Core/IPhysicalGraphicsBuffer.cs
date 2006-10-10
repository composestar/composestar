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
using System.Collections;

namespace Purple.Graphics.Core {
	//=================================================================
	/// <summary>
	/// Buffer holding all vertices/indices of a certain mesh	
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para> 
	///   <para>Last Update: 0.7</para> 
	/// </remarks>
	//=================================================================
	public interface IPhysicalGraphicsBuffer : IDisposable {
    /// <summary>
    /// uploads the buffer data
    /// </summary>
    /// <param name="vertices">array of vertices/indices</param>
    /// <param name="offset">to start in buffer</param>
    /// <param name="start">The index of the vertices array to start with.</param>
    /// <param name="size">The number of element of the vertices array to upload.</param>
    void Upload(Array vertices, int offset, int start, int size);

    /// <summary>
    /// downloads the buffer data
    /// not recommended - slow
    /// </summary>
    /// <param name="size">size of array to download</param>
    /// <param name="offset">to start in buffer</param>
    Array Download(int size, int offset);

		/// <summary>
		/// returns number of vertices/indices
		/// </summary>
		int Size { get; }

		/// <summary>
		/// type of vertex/index
		/// </summary>
		Type Type { get; }	

    /// <summary>
    /// This property contains how often the graphics buffer was (re)created.
    /// That way streams can test if their data was destroyed and has to be uploaded again.
    /// </summary>
    int ChangeCounter { get; }
	}
}
