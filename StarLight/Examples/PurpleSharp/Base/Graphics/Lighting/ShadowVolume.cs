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
using Purple.Graphics.Effect;
using Purple.Graphics.Lighting;
using Purple.Graphics.States;
using Purple.Graphics.VertexStreams;
using Purple.Graphics.Geometry;

namespace Purple.Graphics.Lighting {
  //=================================================================
  /// <summary>
  /// Create a shadow volume for a mesh.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public class ShadowVolume : IRenderAble {
    //---------------------------------------------------------------
    #region Internal stuff
    //---------------------------------------------------------------
    /// <summary>
    /// The method to use.
    /// </summary>
    public enum StencilMethod {
      /// <summary>
      /// The best method is detected automatically.
      /// </summary>
      Automatic,
      /// <summary>
      /// The ZPass method is used (fast but not so robust).
      /// </summary>
      ZPass,
      /// <summary>
      /// The ZFail method is used (slower but robust).
      /// </summary>
      ZFail
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    // the mesh to calculate shadow volume for
    Mesh mesh;
    // meta info
    Edge[][] edges;
    Vector3[][] faceNormals;
    bool[][] backface;
    // the mesh containing the shadow volume
    Mesh shadowVolume;
    IndexStream indexStream;
    int passTechniqueIndex;
    int failTechniqueIndex;

    /// <summary>
    /// The effect that is used by the shadowVolume.
    /// </summary>
    public IEffect Effect {
      get {
        return effect;
      }
    }
    IEffect effect;

    /// <summary>
    /// The world matrix for the current calculated shadowVolume;
    /// </summary>
    public Matrix4 World {
      get {
        return world;
      }
    }
    Matrix4 world;

    /// <summary>
    /// The method to use for stencil shadowing.
    /// </summary>
    public StencilMethod Method {
      get {
        return method;
      }
      set{
        method = value;
      }
    }
    StencilMethod method = StencilMethod.Automatic;
    StencilMethod currentMethod = StencilMethod.Automatic;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Creates a <see cref="ShadowVolume"/> object.
    /// </summary>
    /// <param name="mesh">Mesh to create shadow volume for.</param>
    /// <param name="effect">The effect to use.</param>
    public ShadowVolume(Mesh mesh, IEffect effect) {
      this.mesh = mesh;
      this.effect = effect;
      UpdateMetaInfo();
      FillTechniqueIndices();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private void UpdateMetaInfo() {
      faceNormals = new Vector3[mesh.SubSets.Count][];
      backface = new bool[mesh.SubSets.Count][];
      edges = new Edge[mesh.SubSets.Count][];
      this.shadowVolume = new Mesh();

      int maxIndex = 0;
      for (int iSubSet=0; iSubSet<mesh.SubSets.Count; iSubSet++)
        maxIndex = System.Math.Min(mesh.SubSets[iSubSet].VertexUnit.Size, maxIndex);
      indexStream = IndexStream.Create(1024*16, maxIndex);

      for (int iSubSet=0; iSubSet<mesh.SubSets.Count; iSubSet++) {
        SubSet subSet = mesh.SubSets[iSubSet];
        faceNormals[iSubSet] = subSet.CalcFaceNormals();
        backface[iSubSet] = new bool[subSet.PrimitiveCount];
        edges[iSubSet] = subSet.CalcEdges();
        shadowVolume.SubSets.Add( new SubSet(subSet.VertexUnit, indexStream ) );
      }
    }

    /// <summary>
    /// Calculates the best method to use for stencil shadowing.
    /// </summary>
    /// <remarks>ZPass method is faster than the ZFail method but not as robust. 
    /// This method tests if the viewer is within the shadow volume and decides which method 
    /// works best for the current settings.</remarks>
    /// <param name="light">The light.</param>
    /// <param name="world">The world matrix.</param>
    /// <returns>Returns the best StencilMethod.</returns>
    private StencilMethod CalcMethod(Light light, Matrix4 world) {
      
      //Purple.Log.Spam("CalcMethod not implemented so far!");
      return StencilMethod.ZPass;
    }

    /// <summary>
    /// Calculates a the ShadowVolume for a certain lightPosition.
    /// </summary>
    /// <param name="light">The light that is used for casting the ShadowVolume.</param>
    /// <param name="world">The object to world space matrix.</param>
    /// <param name="recalcFaceNormals">Recalculates the face normals. This is just necessary if the vertices 
    /// of the mesh are manipulated by the CPU like for SoftwareSkinning.</param>
    /// <returns>The ShadowVolume in form of an Mesh.</returns>
    public Mesh Calculate(Light light, Matrix4 world, bool recalcFaceNormals) {
      // Init variables
      int j=0;

      // Calculate the object space light vector
      this.world = world;
      Vector4 osLight = light.Vector4 * Matrix4.Invert(world);

      // calc the method to use
      if (method == StencilMethod.Automatic)
        currentMethod = CalcMethod(light, world);
      else
        currentMethod = method;
      SetTechnique(currentMethod);

      // for all subsets of the mesh add the silohuette
      for(int iSubSet = 0; iSubSet < mesh.SubSets.Count; iSubSet++) {
        SubSet subset = mesh.SubSets[iSubSet];
        SubSet shadow = shadowVolume.SubSets[iSubSet];

        // get indices and positions
        PositionStream positionStream = (PositionStream)subset.VertexUnit[typeof(PositionStream)];

        // recalc face normals
        if (recalcFaceNormals)
          faceNormals[iSubSet] = subset.CalcFaceNormals();

        shadow.IndexBufferStart = j;

        CalcVisibilityInfo( ref j, iSubSet, osLight);

        if (indexStream.ElementSize == 2)
          AddShadowVolume16(ref j, iSubSet, light, positionStream.Size/2);
        else
          AddShadowVolume32(ref j, iSubSet, light, positionStream.Size/2);
        shadow.PrimitiveCount = (j - shadow.IndexBufferStart)/3;
      }

      indexStream.Upload();
      return shadowVolume;
    }

    private void AddShadowVolume16(ref int j, int iSubSet, Light light, int infiniteOffset) {
      ushort[] indexStreamData = (ushort[])indexStream.Data;
      // add the shadow volume
      if (light.LightType == LightType.DirectionalLight) {
        // for directional light we just need triangles for the shadow volume 
        // since the dark cap is a point at infinity
        for (int i=0; i<edges[iSubSet].Length; i++) {
          Edge edge = edges[iSubSet][i];
          if (/*edge.FaceB == 0xFFFF ||*/ backface[iSubSet][edge.FaceA] != backface[iSubSet][edge.FaceB]) {
            ResizeIndexStream(indexStream, j+3);
            if (backface[iSubSet][edge.FaceA]) {
              indexStreamData[j++] = (ushort)(edge.A);
              indexStreamData[j++] = (ushort)(edge.B);
              indexStreamData[j++] = (ushort)(edge.A + infiniteOffset);
            } else {
              indexStreamData[j++] = (ushort)(edge.B);
              indexStreamData[j++] = (ushort)(edge.A);
              indexStreamData[j++] = (ushort)(edge.A + infiniteOffset);
            }
          }
        }
      } else {
        // for point lights and others, we need quads for the shadowVolume
        Edge[] currentEdges = edges[iSubSet];
        bool[] currentBackface = backface[iSubSet];
        for (int i=0; i<currentEdges.Length; i++) {
          Edge edge = currentEdges[i];
          if (/*edge.FaceB == 0xFFFF ||*/ currentBackface[edge.FaceA] != currentBackface[edge.FaceB]) {
            ResizeIndexStream(indexStream, j+6);
            if (currentBackface[edge.FaceA]) {
              indexStreamData[j++] = (ushort)(edge.A);
              indexStreamData[j++] = (ushort)(edge.B);
              indexStreamData[j++] = (ushort)(edge.A + infiniteOffset);
              indexStreamData[j++] = (ushort)(edge.B + infiniteOffset);
              indexStreamData[j++] = (ushort)(edge.A + infiniteOffset);
              indexStreamData[j++] = (ushort)(edge.B);
            } else {
              indexStreamData[j++] = (ushort)(edge.B);
              indexStreamData[j++] = (ushort)(edge.A);
              indexStreamData[j++] = (ushort)(edge.A + infiniteOffset);
              indexStreamData[j++] = (ushort)(edge.B);
              indexStreamData[j++] = (ushort)(edge.A + infiniteOffset);
              indexStreamData[j++] = (ushort)(edge.B + infiniteOffset);
            }
          }
        }
      }
    }

    private void AddShadowVolume32(ref int j, int iSubSet, Light light, int infiniteOffset) {
      int[] indexStreamData = (int[])indexStream.Data;
      // add the shadow volume
      if (light.LightType == LightType.DirectionalLight) {
        // for directional light we just need triangles for the shadow volume 
        // since the dark cap is a point at infinity
        for (int i=0; i<edges[iSubSet].Length; i++) {
          Edge edge = edges[iSubSet][i];
          if (/*edge.FaceB == 0xFFFF ||*/ backface[iSubSet][edge.FaceA] != backface[iSubSet][edge.FaceB]) {
            ResizeIndexStream(indexStream, j+3);
            if (backface[iSubSet][edge.FaceA]) {
              indexStreamData[j++] = (edge.A);
              indexStreamData[j++] = (edge.B);
              indexStreamData[j++] = (edge.A + infiniteOffset);
            } else {
              indexStreamData[j++] = (edge.B);
              indexStreamData[j++] = (edge.A);
              indexStreamData[j++] = (edge.A + infiniteOffset);
            }
          }
        }
      } else {
        // for point lights and others, we need quads for the shadowVolume
        Edge[] currentEdges = edges[iSubSet];
        bool[] currentBackface = backface[iSubSet];
        for (int i=0; i<currentEdges.Length; i++) {
          Edge edge = currentEdges[i];
          if (/*edge.FaceB == 0xFFFF ||*/ currentBackface[edge.FaceA] != currentBackface[edge.FaceB]) {
            ResizeIndexStream(indexStream, j+6);
            if (currentBackface[edge.FaceA]) {
              indexStreamData[j++] = (edge.A);
              indexStreamData[j++] = (edge.B);
              indexStreamData[j++] = (edge.A + infiniteOffset);
              indexStreamData[j++] = (edge.B + infiniteOffset);
              indexStreamData[j++] = (edge.A + infiniteOffset);
              indexStreamData[j++] = (edge.B);
            } else {
              indexStreamData[j++] = (edge.B);
              indexStreamData[j++] = (edge.A);
              indexStreamData[j++] = (edge.A + infiniteOffset);
              indexStreamData[j++] = (edge.B);
              indexStreamData[j++] = (edge.A + infiniteOffset);
              indexStreamData[j++] = (edge.B + infiniteOffset);
            }
          }
        }
      }
    }

    private void CalcVisibilityInfo(ref int j, int iSubSet, Vector4 osLight) {
      IndexStream iStream = mesh.SubSets[iSubSet].IndexStream;
      PositionStream positionStream = (PositionStream)mesh.SubSets[iSubSet].VertexUnit[typeof(PositionStream)];

      // sad but true the following variables are for optimization
      bool[] currentBackFace = backface[iSubSet];
      Vector3[] currentFaceNormals = faceNormals[iSubSet];
      Vector3 osLight3 = osLight.Vector3;
      float w = osLight.W;
      int size = iStream.Size/3;
      Vector3[] posStreamData = (Vector3[])positionStream.Data;
      ushort[] iStreamDataShort = iStream.Data as ushort[];
      int[] iStreamDataInt = iStream.Data as int[];

      // fill backface array with visibility information
      for (int i=0; i<size; i++) {
        int index0 = iStream[i*3];
        Vector3 v0 = posStreamData[index0];

        Vector3 lightDirection = v0 * w - osLight3;
        bool back = currentFaceNormals[i] * lightDirection > 0.0f;
        currentBackFace[i] = back;

        // for the zFail method - add the front and back caps
        if (currentMethod == StencilMethod.ZFail) {
          int index1 = iStream[i*3+1];
          int index2 = iStream[i*3+2];
          if (!back) {
            ResizeIndexStream(indexStream, j+3);
            indexStream[j] = (index0 + positionStream.Size/2);
            indexStream[j+2] = (index1 + positionStream.Size/2);
            indexStream[j+1] = (index2 + positionStream.Size/2);
            j+=3;

            ResizeIndexStream(indexStream, j+3);
            indexStream[j] = index0;
            indexStream[j+1] = index1;
            indexStream[j+2] = index2;
            j+=3;
          }
        }
      }
    }

    private void ResizeIndexStream(IndexStream indexStream, int size) {
      if (indexStream.Size < size) {
        size = System.Math.Max(size, indexStream.Size*2);
        Purple.Log.Spam("ShadowVolume - Resize index stream from: " + indexStream.Size + " to: " + size);
        indexStream.Resize(size);
      }
    }

    /// <summary>
    /// Renders the shadowVolume with a certain effect.
    /// </summary>
    /// <param name="effect">The effect to use for rendering the shadow volume.</param>
    public void Render(IEffect effect) {
      Device.Instance.Transformations.World = World;
      shadowVolume.Render(effect);
    }

    /// <summary>
    /// Prepares a mesh for being used as a shadow Volume.
    /// </summary>
    /// <remarks>In fact the geometry is doubled and an FloatStream is added that contains 1.0f for 
    /// vertices that are used for the near cap and 0.0f for the far cap.</remarks>
    /// <param name="mesh">The mesh to prepare.</param>
    public static void PrepareMesh(Mesh mesh) {
      SubSets subSets = mesh.SubSets;
      for (int iSubSet=0; iSubSet<subSets.Count; iSubSet++) {
        VertexUnit vu = subSets[iSubSet].VertexUnit;

        IGraphicsStream[] streams = new IGraphicsStream[ vu.StreamCount+1 ];
        for (int iStream = 0; iStream < vu.StreamCount; iStream++) {
          IGraphicsStream fromStream = vu[iStream];
          IGraphicsStream toStream = null;
          using(Purple.Profiling.Profiler.Instance.Sample("Clone")) {
            toStream = fromStream.Clone();
          }
          using(Purple.Profiling.Profiler.Instance.Sample("Resize")) {
            toStream.Resize( fromStream.Size*2 );
          }
          using(Purple.Profiling.Profiler.Instance.Sample("CopyTo")) {
            fromStream.Data.CopyTo( toStream.Data, fromStream.Size );
          }
          streams[iStream] = toStream;
        }
        using(Purple.Profiling.Profiler.Instance.Sample("FloatStream")) {
          FloatStream floatStream = new FloatStream( vu.Size * 2);
          for (int i=0; i<vu.Size; i++) {
            floatStream[i] = 1.0f;
            floatStream[i + vu.Size] = 0.0f;
          }
          streams[vu.StreamCount] = floatStream;
        }
        vu = new VertexUnit( streams );
        subSets[iSubSet].VertexUnit = vu;
      }
    }

    private void FillTechniqueIndices() {
      this.passTechniqueIndex = -1;
      this.failTechniqueIndex = -1;
      for (int i=0; i<effect.Techniques.Count; i++) {
        if (effect.IsTechniqueValid(i)) {
          if (effect.Techniques[i].Annotations.ContainsKey("StencilMethod")) {
            string methodString = (string)effect.Techniques[i].Annotations["StencilMethod"];
            if (methodString == StencilMethod.ZFail.ToString() && failTechniqueIndex == -1) {
              failTechniqueIndex = i;
            }
            else if (methodString == StencilMethod.ZPass.ToString() && passTechniqueIndex == -1) {
              passTechniqueIndex = i;
            }
          }
        }
      }
      if (failTechniqueIndex == -1 || passTechniqueIndex == -1)
        Purple.Log.Warning("Couldn't set ShadowVolume technique");
    }

    private void SetTechnique(StencilMethod method) {
      if (method == StencilMethod.ZFail)
        effect.TechniqueIndex = this.failTechniqueIndex;
      else
        effect.TechniqueIndex = this.passTechniqueIndex;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
