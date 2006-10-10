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
using Purple.Math;
using Purple.Input;
using Purple.Graphics.Geometry;
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// A controller for vertex skinning in software.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para> 
  ///   <para>Last change: 0.3</para> 
  /// </remarks>
  //=================================================================
  public class SkinningController : IEntityController {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    Channel anim;
    ISceneEntity entity;
    Vector3[][] source;
    SubSet[] subSets;
    IBoneIndicesStream[] boneIndicesStream;
    IBoneWeightsStream[] boneWeightsStream;
    Matrix4[] tags;
    //Matrix4[] animatedJoints;
    int[] bindings;
    Skeleton skeleton;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the used skeleton
    /// </summary>
    public Skeleton Skeleton {
      get {
        return skeleton;
      }
    }

    /// <summary>
    /// Returns the skeleton animation.
    /// </summary>
    public Channel Animation {
      get {
        return anim;
      }
    }

    /// <summary>
    /// entity to operate on
    /// </summary>
    public ISceneEntity Entity {
      get {
        return entity;
      }
      set {
        if (value is MeshEntity)
          SetMeshEntity( (MeshEntity) value );
        else if (value is SubSetEntity)
          SetSubSetEntity( (SubSetEntity) value );
        else
          throw new Exception("SkinningController currently just supports MeshEntities and SubSetEntities!");
      }
    }

    private void SetSubSetEntity(SubSetEntity subSetEntity){
      subSets = new SubSet[1];
      boneIndicesStream = new IBoneIndicesStream[1];
      boneWeightsStream = new IBoneWeightsStream[1];
      source = new Vector3[1][];
      SetSubSet( subSetEntity.SubSet, 0 );
      entity = subSetEntity;
    }

    private void SetMeshEntity(MeshEntity meshEntity) {
      int subSetCount = meshEntity.Mesh.SubSets.Count;
      subSets = new SubSet[subSetCount];
      boneIndicesStream = new IBoneIndicesStream[subSetCount];
      boneWeightsStream = new IBoneWeightsStream[subSetCount];
      source = new Vector3[subSetCount][];
      for (int i=0; i<subSetCount; i++) {
        SubSet subSet = meshEntity.Mesh.SubSets[i];
        SetSubSet(subSet, i);
      }
      tags = new Matrix4[subSetCount];
      meshEntity.Tags = tags;
      entity = meshEntity;

    }

    private void SetSubSet(SubSet subSet, int i) {
      if (subSet.VertexUnit.Format.Contains(Semantic.BoneIndices)) {
        PositionStream posStream = (PositionStream)subSet.VertexUnit[typeof(PositionStream)];
        boneIndicesStream[i] = (IBoneIndicesStream)subSet.VertexUnit[typeof(IBoneIndicesStream)];
        boneWeightsStream[i] = (IBoneWeightsStream)subSet.VertexUnit[typeof(IBoneWeightsStream)];
        source[i] = (Vector3[])posStream.Data.Clone();
        subSets[i] = subSet;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of a soft skinning Controller
    /// </summary>
    /// <param name="skeleton">the binding pose skeleton</param>
    /// <param name="anim">the animation</param>
    /// <param name="bindings">Binds a <see cref="SubSet"/> to an animated <see cref="Joint"/>.</param>
    public SkinningController(Skeleton skeleton, Channel anim, int[] bindings) {
      this.anim = anim;
      this.skeleton = skeleton;
      this.bindings = bindings;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Does the manipulation.
    /// </summary>
    public void Do() {
      for (int iSubSet = 0; iSubSet<subSets.Length; iSubSet++) {
        //Interpolate(animatedJoints, anim.Frames[player.FrameIndex].JointArray, 
        //  anim.Frames[player.NextFrameIndex].JointArray, player.Weight);
        //player.Interpolate(animatedJoints, anim.Frames);
        
        if (bindings[iSubSet] == -1)
          tags[iSubSet] = Matrix4.Identity;
        else
          tags[iSubSet] = Matrix4.Invert(anim.Frames[0].JointArray[bindings[iSubSet]]) * animatedJoints[bindings[iSubSet]];

        if (subSets[iSubSet] != null) {
          PositionStream posStream = (PositionStream)subSets[iSubSet].VertexUnit[typeof(PositionStream),0];
      
          Skinning.PreBind(animatedJoints, animatedJoints, skeleton.InvertedBindingPose);
          Skinning.SoftSkin(posStream, source[iSubSet], boneIndicesStream[iSubSet], boneWeightsStream[iSubSet], animatedJoints, false);

          posStream.Upload();
        }
      }
    
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
