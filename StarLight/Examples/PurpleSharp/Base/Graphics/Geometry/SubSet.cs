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
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.Geometry
{
	//=================================================================
	/// <summary>
	/// A <see cref="SubSet"/> is the smallest geometric unit of Purple#.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para> 
  ///   <para>A subset can be drawn at once by one render call. For best 
  ///   performance a subset should contain as much data as possible, that way
  ///   the total number of subsets should be kept rather low.</para>
  ///   <para>However, there are some constraints that make it hard 
  ///   to keep the number of subsets low!</para>
  ///   <para>All data of a subset must be drawn with the same renderstates. That 
  ///   also means, that it must use the same textures, the same transformations, the 
  ///   same shaders, the same vertex and index buffers and all the other render and sampler states.</para> 
	/// </remarks>
	//=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
  public class SubSet {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    /// <summary>
    /// The <see cref="IndexStream"/> that contains the indices for the current subset.
    /// </summary>
    public IndexStream IndexStream {
      get {
        return indexStream;
      }
      set {
        indexStream = value;
      }
    }
    IndexStream indexStream;

    /// <summary>
    /// The <see cref="VertexUnit"/> that is used by the current subset.
    /// </summary>
    public VertexUnit VertexUnit {
      get {
        return vertexUnit;
      }
      set {
        vertexUnit = value;
      }
    }
    VertexUnit vertexUnit;

    /// <summary>
    /// Index of first vertex in <see cref="IndexStream"/>.
    /// </summary>
    public int IndexBufferStart {
      get {
        return indexBufferStart;
      }
      set {
        indexBufferStart = value;
      }
    }
    int indexBufferStart;

    /// <summary>
    /// Number of triangles to draw.
    /// </summary>
    public int PrimitiveCount {
      get {
        return primitiveCount;
      }
      set {
        primitiveCount = value;
      }
    }
    int primitiveCount;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="SubSet"/> and prefills it with data.
    /// </summary>
    /// <param name="vertexUnit">Vertex data to use.</param>
    /// <param name="indexStream">Indices to use.</param>
    /// <param name="indexBufferStart">Start index for <see cref="IndexStream"/>.</param>
    /// <param name="primitiveCount">Number of primitives to draw.</param>
    public SubSet(VertexUnit vertexUnit, IndexStream indexStream, 
      int indexBufferStart, int primitiveCount) {
      this.indexStream = indexStream;
      this.vertexUnit = vertexUnit;			
      this.indexBufferStart = indexBufferStart;
      this.primitiveCount = primitiveCount;
    }

    /// <summary>
    /// Creates a new instance of a <see cref="SubSet"/>.
    /// </summary>
    /// <remarks>
    /// Assumes usage of complete VertexUnit/IndexSteram.
    /// </remarks>
    /// <param name="vertexUnit">Vertex data to use.</param>
    /// <param name="indexStream">Indices to use.</param>
    public SubSet(VertexUnit vertexUnit, IndexStream indexStream) :
      this(vertexUnit, indexStream, 0, indexStream.Size/3) {
    }

    /// <summary>
    /// Creates a new instance of a <see cref="SubSet"/>.
    /// </summary>
    /// <param name="vertexUnit">The vertex unit containing the data.</param>
    public SubSet(VertexUnit vertexUnit) : this(vertexUnit, null, 0, vertexUnit.Size/3) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Draws the subSet.
    /// </summary>
    public void Draw() {

      Device device = Device.Instance;

      device.VertexUnit  = VertexUnit;
      device.IndexStream = IndexStream;

      if (IndexStream != null) {
        device.DrawIndexed( VertexUnit.Position, 0, VertexUnit.Size,
          IndexBufferStart + IndexStream.Position, PrimitiveCount);
      } else {
        device.Draw(VertexUnit.Position, PrimitiveCount);
      }
    }

    /// <summary>
    /// Changes the format of the current <see cref="SubSet"/>.
    /// </summary>
    /// <param name="format">The new format to use.</param>		
    internal void ChangeFormat(VertexFormat format) {
      // Creates a new vertexUnit in the new format.
      VertexUnit newUnit = (VertexUnit)VertexUnit.Clone(format);
      VertexUnit.Dispose();
      VertexUnit = newUnit;      
    }

    /// <summary>
    /// Changes the format to fit the given semantics.
    /// </summary>
    /// <param name="semantics">Semantics to fit.</param>
    internal void ChangeFormat(Semantic[] semantics) {
      VertexUnit.ChangeFormat(semantics);
    }

    /// <summary>
    /// Returns true if two subsets can be merged.
    /// </summary>
    /// <remarks>Same material and same vertex format.</remarks>
    /// <param name="s2">Subset to merge.</param>
    /// <returns>True if it is possible.</returns>
    public bool CanMerge(SubSet s2) {
      if (VertexUnit.Format != s2.VertexUnit.Format)
        return false;
      return true;
    }

    /// <summary>
    /// Merges two subsets.
    /// </summary>
    /// <param name="subSet">Subset to merge with.</param>
    public void Merge(SubSet subSet) {
      if (!CanMerge(subSet))
        throw new GraphicsException("Subsets can't be merged!");

      // Merge vertex unit
      VertexUnit unit = new VertexUnit(VertexUnit.Format, VertexUnit.Size + subSet.VertexUnit.Size);						
      VertexUnit.Copy(VertexUnit, 0, unit, 0, VertexUnit.Size);			
      VertexUnit.Copy(subSet.VertexUnit, 0, unit, VertexUnit.Size, subSet.VertexUnit.Size);							

      VertexUnit.Dispose();
      VertexUnit = unit;

      // Merge index streams
      IndexStream index = IndexStream.Create(IndexStream.Size + subSet.IndexStream.Size, IndexStream.GetType());
      VertexStreams.IndexStream.Copy(IndexStream, 0, index, 0, IndexStream.Size, 0);
      VertexStreams.IndexStream.Copy(subSet.IndexStream, 0, index, IndexStream.Size, subSet.IndexStream.Size, VertexUnit.Size);
      IndexStream.Dispose();
      IndexStream = index;
    }

    /// <summary>
    /// Create a <see cref="SubSet"/> from an unindexed <see cref="VertexUnit"/>.
    /// </summary>
    /// <param name="vertexUnit"><see cref="VertexUnit"/> to indexify.</param>
    /// <returns>The created <see cref="SubSet"/>.</returns>
    public static SubSet Indexify(VertexUnit vertexUnit) {
      VertexUnit vUnit;
      IndexStream indexStream;
      vertexUnit.Indexify(out vUnit, out indexStream);
      SubSet result = new SubSet(vUnit, indexStream);
      return result;
    }

    /// <summary>
    /// Clones the current <see cref="SubSet"/>.
    /// </summary>
    /// <remarks>
    /// Uses deep copy (just <see cref="VertexUnit"/> is cloned but not <see cref="IndexStream"/>).
    /// </remarks>
    /// <returns>Clone of the current <see cref="SubSet"/>.</returns>
    public SubSet Clone() {
      SubSet ret = new SubSet(VertexUnit.Clone(), IndexStream, IndexBufferStart, PrimitiveCount);
      return ret;
    }

    /// <summary>
    /// Clones the current <see cref="SubSet"/>, whereby the format of the <see cref="VertexUnit"/> 
    /// is changed to the given <see cref="VertexFormat"/>.
    /// </summary>
    /// <remarks>
    /// Uses deep copy (just <see cref="VertexUnit"/> is cloned but not <see cref="IndexStream"/>).
    /// </remarks>
    /// <param name="format"></param>
    /// <returns></returns>
    public SubSet Clone(VertexFormat format) {
      SubSet ret = new SubSet(VertexUnit.Clone(format), IndexStream, IndexBufferStart, PrimitiveCount);
      return ret;
    }

    /// <summary>
    /// Clones the current <see cref="SubSet"/>, whereby the format is changed to the fit the semantics given.
    /// </summary>
    /// <remarks>
    /// Uses deep copy (just <see cref="VertexUnit"/> is cloned but not <see cref="IndexStream"/>).
    /// </remarks>
    /// <param name="semantics"></param>
    /// <returns></returns>
    public SubSet Clone(Semantic[] semantics) {
      SubSet ret = new SubSet(VertexUnit.Clone(semantics), IndexStream, IndexBufferStart, PrimitiveCount);
      return ret;
    }  

    /// <summary>
    /// Calculates the face normals for the current subSet in object space.
    /// </summary>
    /// <returns>The face normals for the current subSet.</returns>
    public Vector3[] CalcFaceNormalsOld() {
      Vector3[] faceNormals = new Vector3[ this.PrimitiveCount ];
      PositionStream positionStream = (PositionStream)vertexUnit[typeof(PositionStream)];

      for (int i=0; i<this.PrimitiveCount; i++) {
        int index0 = indexStream[i*3];
        int index1 = indexStream[i*3+1];
        int index2 = indexStream[i*3+2];
        Vector3 v0 = positionStream[index0];
        Vector3 v1 = positionStream[index1];
        Vector3 v2 = positionStream[index2];

        faceNormals[i] = Vector3.Unit(Vector3.Cross( v2 - v0, v1 - v0));
      }
      return faceNormals;
    }

    /// <summary>
    /// Calculates the face normals for the current subSet in object space.
    /// </summary>
    /// <returns>The face normals for the current subSet.</returns>
    public unsafe Vector3[] CalcFaceNormals() {
      Vector3[] faceNormals = new Vector3[ this.PrimitiveCount ];
      PositionStream positionStream = (PositionStream)vertexUnit[typeof(PositionStream)];
      Vector3[] positionStreamData = (Vector3[])positionStream.Data;
      if (indexStream.Type == typeof(ushort)) {
        ushort[] indexStreamData = (ushort[])indexStream.Data;
          for (int i=0, j=0; i<this.primitiveCount; i++) {
            Vector3 v0 = positionStreamData[indexStreamData[j++]];
            Vector3 v1 = positionStreamData[indexStreamData[j++]];
            Vector3 v2 = positionStreamData[indexStreamData[j++]];

            faceNormals[i] = Vector3.Cross( v2 - v0, v1 - v0);//Vector3.CrossUnit( v2 - v0, v1 - v0);
          }
          return faceNormals;
      } else {
        int[] indexStreamData = (int[])indexStream.Data;
        for (int i=0, j=0; i<this.primitiveCount; i++) {
          Vector3 v0 = positionStreamData[indexStreamData[j++]];
          Vector3 v1 = positionStreamData[indexStreamData[j++]];
          Vector3 v2 = positionStreamData[indexStreamData[j++]];

          faceNormals[i] = Vector3.Cross( v2 - v0, v1 - v0);//Vector3.CrossUnit( v2 - v0, v1 - v0);
        }
        return faceNormals;
      }
    }

    /// <summary>
    /// Calcs the edges from the current subSet.
    /// </summary>
    /// <remarks>This method is rather slow and should just be called for offline processing.</remarks>
    /// <returns>The edges from the current subSet.</returns>
    [System.CLSCompliant(false)]
    public Edge[] CalcEdges() {
      // The stream containing the positions
      PositionStream positionStream = (PositionStream)vertexUnit[typeof(PositionStream)];

      int[] indexMapping = new int[ positionStream.Size ];
      Hashtable positions = new Hashtable( );

      // Find equal vertices but with different index (may be different because of other texture coordinates ...
      for (int i=0; i<positionStream.Size; i++) {
        Vector3 pos = positionStream[i];
        if (positions.Contains(pos)) {
          indexMapping[i] = (int)positions[pos];
        } else {
          positions.Add(pos, i);
          indexMapping[i] = i;
        }
      }

      Hashtable edges = new Hashtable();
      for (int i=0; i<indexStream.Size/3; i++) {
        int index0 = indexMapping[indexStream[i*3]];
        int index1 = indexMapping[indexStream[i*3+1]];
        int index2 = indexMapping[indexStream[i*3+2]];

        AddEdge(edges, index0, index1, i);
        AddEdge(edges, index1, index2, i);
        AddEdge(edges, index2, index0, i);
      }

      int oneSided = 0;
      foreach(Edge e in edges.Values)
        if (e.FaceB == -1 || e.FaceA == -1)
          oneSided++;

      Edge[] retEdges = new Edge[edges.Count-oneSided];
      int l=0;
      foreach(Edge e in edges.Values)
        if (e.FaceB != -1 && e.FaceA != -1)
          retEdges[l++] = e;
      return retEdges;  
    }
    
    void AddEdge(Hashtable edges, int a, int b, int triIndex) {
      int other = -1;
      if (a < b) {
        SimpleEdge se = new SimpleEdge(a,b);
        if (edges.Contains(se)) {
          Edge e = (Edge)edges[se];
          other = e.FaceB;
          if (e.FaceA != -1) // HACK
            triIndex = e.FaceA;
        }    
        edges[se] = new Edge(a, b, triIndex, other);  
      } else {
        SimpleEdge se = new SimpleEdge(b, a);
        if (edges.Contains(se)) {
          Edge e = (Edge)edges[se];
          other = e.FaceA;
          if (e.FaceB != -1)  // HACK
            triIndex = e.FaceB;
        }    
        edges[se] = new Edge(b, a, other, triIndex);  
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
