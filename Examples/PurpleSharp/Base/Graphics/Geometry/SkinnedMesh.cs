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
using Purple.Graphics.Lighting;
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.Geometry {
  //=================================================================
  /// <summary>
  /// This class handles skinned mesh.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class SkinnedMesh : IAnimatedMesh {
    //---------------------------------------------------------------
    #region Internal SubSetData class
    //---------------------------------------------------------------
    class SubSetData {
      public Vector3[] Position;
      public Vector3[] Normal;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    SubSetData[] data;

    /// <summary>
    /// Returns the current mesh.
    /// </summary>
    Mesh IAnimatedMesh.Current { 
      get {
        return mesh;
      }
    }
    Mesh mesh;

    /// <summary>
    /// The assosicated textures.
    /// </summary>
    ArrayList IAnimatedMesh.Textures { 
      get {
        return mesh.Textures;
      }
    }

    /// <summary>
    /// The skeleton that is used for the current skinned mesh.
    /// </summary>
    public Skeleton Skeleton {
      get {
        return skeleton;
      }
      set {
        skeleton = value;
      }
    }
    Skeleton skeleton;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new skinned mesh object.
    /// </summary>
    /// <param name="sourceMesh">The mesh to use for skinning.</param>
    /// <param name="skeleton">The skeleton that is used for skinning</param>
    public SkinnedMesh(Mesh sourceMesh, Skeleton skeleton) {
      // Copy original data for software skinning
      SetMesh(sourceMesh);
      this.skeleton = skeleton;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Render the object.
    /// </summary>
    /// <param name="effect">The current effect that is used for rendering.</param>
    public void Render(Purple.Graphics.Effect.IEffect effect) {
      this.mesh.Render(effect);
    }

    /// <summary>
    /// Updates the model.
    /// </summary>
    /// <remarks>Ensure that the skeleton gets updated before Update of the SkinnedMesh is called.</remarks>
    public void Update() {
      SoftSkin(skeleton.PreBound);
    }

    void SetMesh(Mesh sourceMesh) {
      this.mesh = sourceMesh;
      data = new SubSetData[ sourceMesh.SubSets.Count ];
      for (int i=0; i<data.Length; i++)
        data[i] = CreateSubSetData( sourceMesh.SubSets[i] );
    }

    void SetSubSet(SubSet subSet) {
      data = new SubSetData[ 1 ];
      data[0] = CreateSubSetData( subSet );
    }

    private SubSetData CreateSubSetData( SubSet subSet ) {
      VertexUnit vu = subSet.VertexUnit;

      if ( !vu.Format.Contains(Semantic.BoneIndices) ||
        !vu.Format.Contains(Semantic.BoneIndices) ||
        !vu.Format.Contains(Semantic.Position))
        return null;

      SubSetData ssData = new SubSetData();
      PositionStream posStream = (PositionStream)vu[typeof(PositionStream)];
      ssData.Position = (Vector3[])posStream.Data.Clone();
      if (vu.Format.Contains(typeof(NormalStream))) {
        NormalStream normalStream = (NormalStream)vu[typeof(NormalStream)];
        ssData.Normal = (Vector3[])normalStream.Data.Clone();
      }

      return ssData;
    }

    void SoftSkin(Matrix4[] preBound) {
      // Apply skinning on all subSets
      for (int iSubSet = 0; iSubSet<data.Length; iSubSet++) {
        SubSetData ssData = data[iSubSet];
        if (ssData != null) {
          VertexUnit vu = mesh.SubSets[iSubSet].VertexUnit;
          PositionStream ps = (PositionStream)vu[typeof(PositionStream)];
          IBoneIndicesStream bis = (IBoneIndicesStream)vu[typeof(IBoneIndicesStream)];
          IBoneWeightsStream bws = (IBoneWeightsStream)vu[typeof(IBoneWeightsStream)];
          // currently just the position stream is skinned!
          Skinning.SoftSkin( ps, ssData.Position, bis, bws, preBound, Shadowed);
          ps.Upload();
        }
      }
    }

    /// <summary>
    /// The shadow implementation of the current shadow caster.
    /// </summary>
    public IShadowImplementation ShadowImplementation { 
      get {
        return shadowImplementation;
      }
      set {
        shadowImplementation = value;
      }
    }
    IShadowImplementation shadowImplementation = null;
    
    /// <summary>
    /// Turn on and off shadowing.
    /// </summary>
    public bool Shadowed { 
      get {
        if (shadowImplementation == null)
          return false;
        return shadowImplementation.Shadowed;
      }
      set {
        if (value && shadowImplementation == null)
          shadowImplementation = new StencilShadow(this.mesh);
        if (shadowImplementation != null)
          shadowImplementation.Shadowed = value;
      }
    }

    /// <summary>
    /// Hack
    /// </summary>
    /// <param name="light"></param>
    /// <param name="world"></param>
    /// <param name="recalcNormals"></param>
    public void UpdateShadow(Light light, Matrix4 world, bool recalcNormals) {
      this.shadowImplementation.UpdateShadow(light, world, recalcNormals);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}

