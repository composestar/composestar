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
  /// A software bone weights stream holding an arbitrary number of weights.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// This stream is used to store the influencing weights for every vertex.
  /// </remarks>
  //=================================================================
  public class SoftwareBoneWeightsStream : SoftwareStream, IBoneWeightsStream {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the name of the stream.
    /// </summary>
    public override string Name { 
      get {
        return name;
      }
    }
    static string name = "SoftwareBoneWeightsStream";

    /// <summary>
    /// Data access via indexer.
    /// </summary>
    public float[] this[int index] {
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
    float[][] data;
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
      element.DeclarationUsage = DeclarationUsage.BoneWeights;
      StreamFactory.Instance.Bind(name, typeof(SoftwareBoneWeightsStream), element);
    }

    /// <summary>
    /// Creates a new instance of a <see cref="SoftwareBoneWeightsStream"/>.
    /// </summary>
    /// <param name="size">number of elements</param>
    public SoftwareBoneWeightsStream(int size) : base(size) {
  }

    /// <summary>
    /// Creates a new instance of a <see cref="SoftwareBoneWeightsStream"/>.
    /// </summary>
    /// <remarks>
    /// Init has to be called!!!
    /// </remarks>
    public SoftwareBoneWeightsStream() : base(){
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
      data = new float[size][];
      used = size;
    }

    /// <summary>
    /// Sets the value of the array.
    /// </summary>
    /// <param name="data">array to set</param>
    protected override void SetData(Array data) {
      this.data = (float[][])data;
    }

    /// <summary>
    /// Sets the weights for a certain vertex.
    /// </summary>
    /// <param name="vertexIndex">Index of vertex to set bone weights for.</param>
    /// <param name="weights">The array of weights.</param>
    public void SetWeights(int vertexIndex, float[] weights) {
      data[vertexIndex] = weights;
    }

    /// <summary>
    /// Returns the weights for a certain vertex.
    /// </summary>
    /// <param name="vertexIndex">The array of weights.</param>
    /// <returns>The array of weights.</returns>
    public float[] GetWeights(int vertexIndex) {
      return data[vertexIndex];
    }

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public override IGraphicsStream Clone() {
      IGraphicsStream stream = new SoftwareBoneWeightsStream(this.Size);
      stream.Copy(this);
      return stream;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
