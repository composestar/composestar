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
  /// Index stream
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class IndexStream32 : IndexStream {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    static string name = "Index32";
    uint[] data = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// data access via indexer
    /// </summary>
    public override int this[int index] { 
      get {
        return (int)data[index];
      }
      set {
        data[index] = (uint)value;
      }
    }

    /// <summary>
    /// name of stream
    /// </summary>
    public override string Name { 
      get {
        return name;
      }
    }

    /// <summary>
    /// access to array data
    /// </summary>
    public override Array Data { 
      get {
        return data;
      }
    }
		
    /// <summary>
    /// type of vertices
    /// </summary>
    public override Type Type {
      get {
        return typeof(uint);
      }
    }

		
    /// <summary>
    /// returns the size in bytes of one array element
    /// </summary>
    public override short ElementSize { 
      get {
        return 4;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Binds this type to the <see cref="StreamFactory"/>.
    /// </summary>
    static public void Bind() {
      StreamFactory.Instance.Bind(name, typeof(IndexStream32), VertexElement.None);
    }

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="size">size of indexstream in number of indices</param>
    public IndexStream32(int size) : base(size) {
    }

    /// <summary>
    /// Constructor
    /// Init has to be called!!!
    /// </summary>
    public IndexStream32() : base() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Creates an IndexStream from a list of triangles
    /// </summary>
    /// <param name="triangles"></param>
    public static IndexStream32 FromTriangles(System.Collections.ICollection triangles) {
      IndexStream32 index = new IndexStream32(triangles.Count * 3);
      int i=0;
      foreach(Triangle tri in triangles) {
        index[i++] = tri.A;
        index[i++] = tri.B;
        index[i++] = tri.C;
      }
      return index;
    }

/*    /// <summary>
    /// returns the triangle for the given index
    /// !!! assumes triangle list format !!!
    /// </summary>
    /// <param name="index">index of triangle</param>
    /// <returns>triangle</returns>
    [System.CLSCompliant(false)]
    public Triangle32 GetTriangle(int index) {
      return new Triangle32( this[index*3],
        this[index*3 + 1],
        this[index*3 + 2]);
    }

    /// <summary>
    /// sets a triangle for a given index
    /// </summary>
    /// <param name="tri">triangle to set</param>
    /// <param name="index">index to use</param>
    [System.CLSCompliant(false)]
    public void SetTriangle(Triangle tri, int index) {
      this[index*3] = tri.A;
      this[index*3 + 1] = tri.B;
      this[index*3 + 2] = tri.C;
    }*/

    /// <summary>
    /// initialises the array with a given size
    /// </summary>
    /// <param name="size">size of array</param>
    public override void Init(int size) {
      data = new uint[size];
      used = size;
    }

    /// <summary>
    /// sets the value of the array
    /// </summary>
    /// <param name="data">array to set</param>
    protected override void SetData(Array data) {
      this.data = (uint[])data;
    }

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public override IGraphicsStream Clone() {
      IGraphicsStream stream = new IndexStream32(this.Size);
      stream.Copy(this);
      return stream;
    }

    /// <summary>
    /// Sorts the indexStream.
    /// </summary>
    /// <param name="posStream">The positions to use for sorting.</param>
    /// <param name="frontToBack">Flag that indicates if stream should be sorted 
    /// front to back or the way around.</param>
    public void Sort(PositionStream posStream, bool frontToBack) {
      int[] triIndices = new int[Size/3];
      float[] zValues = new float[Size/3];

      // fill triIndices, with the current triangle indices, and the zValues with their
      // accumulated z values.
      for (int i=0; i<triIndices.Length; i++) {
        triIndices[i] = i;
        if (frontToBack)
          zValues[i] = -posStream[(int)data[i*3]].Z - posStream[(int)data[i*3+1]].Z - posStream[(int)data[i*3+2]].Z;
        else
          zValues[i] = posStream[(int)data[i*3]].Z + posStream[(int)data[i*3+1]].Z + posStream[(int)data[i*3+2]].Z;
      }

      // sort indices and reorder the index buffer
      Array.Sort(zValues, triIndices);
      uint[] newData = new uint[data.Length];
      for (int i=0; i<triIndices.Length; i++) {
        int newIndex = triIndices[i];
        newData[ i*3 ] = data[ newIndex*3 ];
        newData[ i*3+1 ] = data[ newIndex*3+1 ];
        newData[ i*3+2 ] = data[ newIndex*3+2 ];
      }
      data = newData;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}