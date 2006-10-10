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

namespace Purple.Graphics.VertexStreams {
  //=================================================================
  /// <summary>
  /// a simple stream containing up to 4 indices
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class BoneIndicesStream : IntStream, IBoneIndicesStream {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    static string name = "BoneIndicesStream";
    static byte[] getArray = new byte[4];
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Binds this type to the <see cref="StreamFactory"/>.
    /// </summary>
    static public new void Bind() {
      VertexElement element = defaultElement;
      element.DeclarationUsage = DeclarationUsage.BoneIndices;
      StreamFactory.Instance.Bind(name, typeof(BoneIndicesStream), element);
    }

    /// <summary>
    /// A simple boneIndicesStream.
    /// </summary>
    public BoneIndicesStream(VertexUnit vertexUnit) : base(vertexUnit) {
      Usage = DeclarationUsage.BoneIndices;
    }

    /// <summary>
    /// constructor
    /// </summary>
    /// <param name="size">number of elements</param>
    public BoneIndicesStream(int size) : base(size) {
      Usage = DeclarationUsage.BoneIndices;
    }

    /// <summary>
    /// Constructor
    /// Init has to be called!!!
    /// </summary>
    public BoneIndicesStream() : base(){
      Usage = DeclarationUsage.BoneIndices;
    }

    /// <summary>
    /// Returns true if the current stream is a <see cref="SoftwareStream"/>.
    /// </summary>
    public override bool IsSoftware { 
      get {
        return false;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// name of stream
    /// </summary>
    public override string Name { 
      get {
        return name;
      }
    }

    /// <summary>
    /// returns the bone index for a certain vertex
    /// </summary>
    /// <param name="vertexIndex">vertex to return weight for</param>
    /// <param name="weightIndex">inded of weight to return for bone</param>
    /// <returns>index of bone</returns>
    public byte GetBoneIndex(int vertexIndex, int weightIndex) {
      return ExtractBoneIndex( IntData[vertexIndex], weightIndex);
    }

    /// <summary>
    /// sets the boneIndex for a certain weight of a certain vertex
    /// </summary>
    /// <param name="vertexIndex">the vertex to set value for</param>
    /// <param name="weightIndex">the index of the weight for the given vertex</param>
    /// <param name="boneIndex">the value to set</param>
    public void SetBoneIndex(int vertexIndex, int weightIndex, byte boneIndex) {
      IntData[vertexIndex] = SetBoneIndex( IntData[vertexIndex], weightIndex, boneIndex);
    }

    /// <summary>
    /// Sets the indices for a certain vertex.
    /// </summary>
    /// <param name="vertexIndex">Index of vertex to set bone indices for.</param>
    /// <param name="indices">The array of indices.</param>
    public void SetIndices(int vertexIndex, byte[] indices) {
      uint value = 0;
      int count = System.Math.Min(4, indices.Length);
      for (int i=0; i < count; i++) {
        value = (value << 8) + indices[ count - i - 1];
      }   
      IntData[vertexIndex] = value;
    }

    /// <summary>
    /// Returns the indices for a certain vertex.
    /// </summary>
    /// <param name="vertexIndex">The array of indices.</param>
    /// <returns>The array of indices.</returns>
    public byte[] GetIndices(int vertexIndex) {
      uint value = IntData[vertexIndex];
      getArray[0] = (byte)(value & 0xFF);
      getArray[1] = (byte)((value >> 8) & 0xFF);
      getArray[2] = (byte)((value >> 16) & 0xFF);
      getArray[3] = (byte)((value >> 24) & 0xFF);
      return getArray;
    }

    /// <summary>
    /// calculates the bone index from an uint containing all 4 weights
    /// </summary>
    /// <param name="weights">uint containg all 4 weights</param>
    /// <param name="weightIndex">extracts a certain weightIndex</param>
    /// <returns>the index of a bone</returns>
    [System.CLSCompliant(false)]
    public static byte ExtractBoneIndex( uint weights, int weightIndex) {
      int shift = 8*weightIndex;
      return (byte)((weights >> shift) & 0x000000FF);
    }

    /// <summary>
    /// incorporates the bone index into an uint containing all 4 weights
    /// </summary>
    /// <param name="initialData">the initial value of the 4 bones</param>
    /// <param name="weightIndex">the index of the weight to set value for</param>
    /// <param name="boneIndex">the value to set</param>
    /// <returns>the new bone index value</returns>
    [System.CLSCompliant(false)]
    public static uint SetBoneIndex( uint initialData, int weightIndex, byte boneIndex) {
      int shift = 8*weightIndex;
      uint mask = (uint)0xFFFFFFFF - ((uint)0x000000FF << shift);
      return ((uint)initialData & mask) + ((uint)boneIndex << shift);
    }

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public override IGraphicsStream Clone() {
      IGraphicsStream stream = new BoneIndicesStream(this.Size);
      stream.Copy(this);
      return stream;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}