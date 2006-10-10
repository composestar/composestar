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
using Purple.Math;
using Purple.Graphics.Core;

namespace Purple.Graphics {
	//=================================================================
	/// <summary>
	/// abstract interface for a graphics stream
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
  public interface IGraphicsStream : IDisposable {

    /// <summary>
    /// access to data of stream (fixed size)
    /// </summary>
    Array Data { get;}

    /// <summary>
    /// name of stream
    /// </summary>
    string Name { get; }

    /// <summary>
    /// the size of the stream
    /// </summary>
    int Size { get; }

    /// <summary>
    /// the number of used elements in the stream
    /// is set to size on construction, Init(size) and Resize(size)
    /// </summary>
    int Used { get; }

    /// <summary>
    /// returns the size in bytes of one array element
    /// </summary>
    short ElementSize { get; }

    /// <summary>
    /// type of vertices
    /// </summary>
    Type Type {get;}

    /// <summary>
    /// uploads from offline to online data
    /// </summary>
    void Upload();

    /// <summary>
    /// uploads from offline to online data
    /// </summary>
    /// <param name="start">The start index for uploading.</param>
    /// <param name="length">The number of elements to upload</param>
    void Upload(int start, int length);

    /// <summary>
    /// downloads online to offline data
    /// </summary>
    void Download();

    /// <summary>
    /// start position in physical graphics buffer
    /// </summary>
    int Position { get; set; }		

    /// <summary>
    /// returns the physical buffer
    /// </summary>		
    IPhysicalGraphicsBuffer PhysicalBuffer { get; set; }

    /// <summary>
    /// returns the used BufferManager
    /// </summary>
    BufferManager BufferManager { get; }

    /// <summary>
    /// Returns true if the current stream is a <see cref="Purple.Graphics.VertexStreams.SoftwareStream"/>.
    /// </summary>
    bool IsSoftware { get; }

    /// <summary>
    /// initialises the array with a given size
    /// </summary>
    /// <param name="size">size of array</param>
    void Init(int size);

    /// <summary>
    /// resizes the stream
    /// creates new stream, copies the old one to the new one
    /// !!!don't uses this method in speed critical sections!!!
    /// !!!this methods works with offline buffers only!!!
    /// </summary>
    /// <param name="size">new size</param>
    void Resize(int size);

    /// <summary>
    /// test if buffer has offline data
    /// </summary>
    bool HasOfflineData();

    /// <summary>
    /// test if buffer has online data
    /// </summary>
    bool HasOnlineData();

    /// <summary>
    /// disposes online data
    /// </summary>
    /// <returns></returns>
    void DisposeOnlineData();

    /// <summary>
    /// disposes offline data
    /// </summary>
    /// <returns></returns>
    void DisposeOfflineData();

    /// <summary>
    /// copies the data of the stream to this stream
    /// </summary>
    /// <param name="stream">stream to take data from</param>
    void Copy(IGraphicsStream stream);

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    IGraphicsStream Clone();
  }

	//=================================================================
	/// <summary>
	/// abstract interface
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public interface IVertexStream : IGraphicsStream {
		/// <summary>
		/// gets the description of the vertex element
		/// </summary>
		VertexElement VertexElement { get; set; }

    // TODO: find a way to use vertexStream in more than one vertexUnit
		/// <summary>
		/// returns vertexUnit of VertexStream
		/// </summary>
		VertexUnit VertexUnit { set; get; }
	}
}
