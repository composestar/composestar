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

namespace Purple.Graphics.VertexStreams {
  //=================================================================
  /// <summary>
  /// A software stream holding an arbitrary number of bone indices.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// This stream is used to store the influencing bones for every vertex.
  /// </remarks>
  //=================================================================
  public class SoftwareBoneIndicesStream : SoftwareStream, IBoneIndicesStream {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    static string name = "SoftwareBoneIndicesStream";

    /// <summary>
    /// Data access via indexer.
    /// </summary>
    public byte[] this[int index] {
      set {
        data[index] = value;
      }
      get {
        return data[index];
      }
    }

    /// <summary>
    /// Array to array data.
    /// </summary>
    public override Array Data { 
      get {
        return data;
      }
    }
    byte[][] data;
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
      VertexElement element = defaultElement;
      element.DeclarationUsage = DeclarationUsage.BoneIndices;
      StreamFactory.Instance.Bind(name, typeof(SoftwareBoneIndicesStream), element);
    }

    /// <summary>
    /// Creates a new instance of a <see cref="SoftwareBoneIndicesStream"/>.
    /// </summary>
    /// <param name="size">number of elements</param>
    public SoftwareBoneIndicesStream(int size) : base(size) {
    }

    /// <summary>
    /// Creates a new instance of a <see cref="SoftwareBoneIndicesStream"/>.
    /// </summary>
    /// <remarks>
    /// Init has to be called!!!
    /// </remarks>
    public SoftwareBoneIndicesStream() : base(){
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Initialises the array with a given size.
    /// </summary>
    /// <param name="size">size of array</param>
    public override void Init(int size) {
      data = new byte[size][];
      used = size;
    }

    /// <summary>
    /// Sets the value of the array.
    /// </summary>
    /// <param name="data">array to set</param>
    protected override void SetData(Array data) {
      this.data = (byte[][])data;
    }

    /// <summary>
    /// Returns the name of the stream.
    /// </summary>
    public override string Name { 
      get {
        return name;
      }
    }

    /// <summary>
    /// Sets the indices for a certain vertex.
    /// </summary>
    /// <param name="vertexIndex">Index of vertex to set bone indices for.</param>
    /// <param name="indices">The array of indices.</param>
    public void SetIndices(int vertexIndex, byte[] indices) {
      data[vertexIndex] = indices;
    }
  
    /// <summary>
    /// Returns the indices for a certain vertex.
    /// </summary>
    /// <param name="vertexIndex">The array of indices.</param>
    /// <returns>The array of indices.</returns>
    public byte[] GetIndices(int vertexIndex) {
      return data[vertexIndex];
    }

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public override IGraphicsStream Clone() {
      IGraphicsStream stream = new SoftwareBoneIndicesStream(this.Size);
      stream.Copy(this);
      return stream;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}