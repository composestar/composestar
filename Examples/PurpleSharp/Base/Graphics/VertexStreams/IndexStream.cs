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

namespace Purple.Graphics.VertexStreams {
  //=================================================================
  /// <summary>
  /// Abstract base class for an index stream.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public abstract class IndexStream : GraphicsStream {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// returns the used BufferManager
    /// </summary>
    public override BufferManager BufferManager { 
      get {
        return IndexBufferManager.Instance;
      }
    }

    /// <summary>
    /// Returns true if the current stream is a <see cref="SoftwareStream"/>.
    /// </summary>
    public override bool IsSoftware { 
      get {
        return false;
      }
    }

    /// <summary>
    /// data access via indexer
    /// </summary>
    public abstract int this[int index] { get; set; }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="size">size of indexstream in number of indices</param>
    public IndexStream(int size) : base(size) {
    }

    /// <summary>
    /// Constructor
    /// Init has to be called!!!
    /// </summary>
    public IndexStream() : base() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new IndexStream depending on the size of the maxIndex.
    /// </summary>
    /// <param name="indexStreamSize">The number of indices to create.</param>
    /// <param name="maxIndex">The maximum index to handle (create IndexStream16 or IndexStream32).</param>
    /// <returns>The created indexStream.</returns>
    public static IndexStream Create(int indexStreamSize, int maxIndex) {
      if (maxIndex > ushort.MaxValue)
        return new IndexStream32(indexStreamSize);
      return new IndexStream16(indexStreamSize);
    }

    /// <summary>
    /// Creates a new IndexStream object.
    /// </summary>
    /// <param name="indices">The list of indices.</param>
    /// <param name="maxIndex">The maximum index to handle (create IndexStream16 or IndexStream32).</param>
    /// <returns>The created indexStream.</returns>
    public static IndexStream Create( IList indices, int maxIndex) {
      IndexStream stream = IndexStream.Create( indices.Count, maxIndex );
      for (int i=0; i<indices.Count; i++)
        stream[i] = (int)indices[i];
      return stream;
    }

    /// <summary>
    /// Creates a new IndexStream depending on the size of the maxIndex.
    /// </summary>
    /// <param name="indexStreamSize">The number of indices to create.</param>
    /// <param name="type">Type of the indexStream to create.</param>
    /// <returns>The created indexStream.</returns>
    public static IndexStream Create(int indexStreamSize, Type type) {
      if (type == typeof(IndexStream16))
        return new IndexStream16(indexStreamSize);
      else if (type == typeof(IndexStream32))
        return new IndexStream32(indexStreamSize);
      throw new ArgumentException("IndexStream type: " + type.ToString() + " unknown!", "type");
    }

    /// <summary>
    /// Creates an IndexStream from a chain of quads.
    /// </summary>
    /// <param name="quadNum">The number of quads.</param>
    /// <returns>The created indexStream.</returns>
    public static IndexStream FromChain(int quadNum) {
      IndexStream indexStream;
      if (quadNum*6 > ushort.MaxValue)
        indexStream = new IndexStream32(quadNum*6);
      else
        indexStream = new IndexStream16(quadNum*6);

      int iIndex = 0;
      int iVertex = 0;
      for (int i=0; i<quadNum; i++, iIndex+=6, iVertex+=2) {
        indexStream[iIndex+0] = iVertex;
        indexStream[iIndex+1] = iVertex + 3;
        indexStream[iIndex+2] = iVertex + 1;
        indexStream[iIndex+3] = iVertex;
        indexStream[iIndex+4] = iVertex + 2;
        indexStream[iIndex+5] = iVertex + 3;
      }
      return indexStream;
    }

    /// <summary>
    /// Creates an IndexStream from a certain number of vertices that are organised in quads.
    /// </summary>
    /// <param name="quadNum">The number of quads.</param>
    /// <returns>The created indexStream.</returns>
    public static IndexStream FromQuads(int quadNum) {
      IndexStream indexStream;
      if (quadNum*6 > ushort.MaxValue)
        indexStream = new IndexStream32(quadNum*6);
      else
        indexStream = new IndexStream16(quadNum*6);

      int iIndex = 0;
      int iVertex = 0;
      for (int i=0; i<quadNum; i++, iIndex+=6, iVertex+=4) {
        indexStream[iIndex+0] = iVertex;
        indexStream[iIndex+1] = (iVertex + 1);
        indexStream[iIndex+2] = (iVertex + 3);
        indexStream[iIndex+3] = (iVertex + 3);
        indexStream[iIndex+4] = (iVertex + 1);
        indexStream[iIndex+5] = (iVertex + 2);
      }
      return indexStream;
    }

    /// <summary>
    /// returns the triangle for the given index
    /// !!! assumes triangle list format !!!
    /// </summary>
    /// <param name="index">index of triangle</param>
    /// <returns>triangle</returns>
    public Triangle GetTriangle(int index) {
      return new Triangle( this[index*3],
        this[index*3 + 1],
        this[index*3 + 2]);
    }

    /// <summary>
    /// sets a triangle for a given index
    /// </summary>
    /// <param name="tri">triangle to set</param>
    /// <param name="index">index to use</param>
    public void SetTriangle(Triangle tri, int index) {
      this[index*3] = tri.A;
      this[index*3 + 1] = tri.B;
      this[index*3 + 2] = tri.C;
    }

    /// <summary>
    /// copies a source index stream into a certain location of a destination vertex stream		
    /// </summary>
    /// <param name="source">index stream to take data from</param>
    /// <param name="sourceIndex">start index</param>
    /// <param name="dest">index to put data into</param>
    /// <param name="destIndex">start index</param>
    /// <param name="length">number of elements</param>
    /// <param name="vertexOffset">used if vertices are copied to another location</param>
    public static void Copy(IndexStream source, int sourceIndex, 
      IndexStream dest, int destIndex, int length, int vertexOffset) {
      if (source.GetType() != dest.GetType() || vertexOffset != 0 ) {
        for (int i=0; i<length; i++) {
          dest[destIndex] = (ushort)(source[sourceIndex] + vertexOffset);
        }
      } else
        Array.Copy(source.Data, sourceIndex, dest.Data, destIndex, length);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}