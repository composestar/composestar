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
  /// This class handles blended meshes.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// There are many ways to improve the performance of this class. 
  /// Currently streams like TextureStreams are stored for every mesh, 
  /// I don't check if the tempMesh can be reused, DynamicMesh, ...
  /// </remarks>
  //=================================================================
  public class BlendMesh : IAnimatedMesh {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Mesh tempMesh = null;

    /// <summary>
    /// The meshes used for blending.
    /// </summary>
    public Mesh[] Meshes {
      get {
        return meshes;
      }
      set {
        meshes = value;
      }
    }
    Mesh[] meshes;

    /// <summary>
    /// Returns the number of frames.
    /// </summary>
    public int Frames {
      get {
        return meshes.Length;
      }
    }

    /// <summary>
    /// Returns the current mesh.
    /// </summary>
    public Mesh Current { 
      get {
        return tempMesh;
      }
    }

    /// <summary>
    /// The assosicated textures.
    /// </summary>
    ArrayList IAnimatedMesh.Textures { 
      get {
        return meshes[0].Textures;
      }
    }

    /// <summary>
    /// The player for the current BlendMesh.
    /// </summary>
    public AnimationPlayer Player {
      get {
        return player;
      }
      set {
        player = value;
      }
    }
    AnimationPlayer player;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new BlendMesh object from a collection of meshes.
    /// </summary>
    /// <param name="meshes">The meshes to use.</param>
    public BlendMesh(Mesh[] meshes) {
      this.meshes = meshes;
    }

    /// <summary>
    /// Creates a new BlendMesh.
    /// </summary>
    /// <param name="frameCount">Number of frames of the BlendMesh.</param>
    public BlendMesh(int frameCount) {
      this.meshes = new Mesh[frameCount];
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Updates the blend mesh.
    /// </summary>
    public void Update() {
      if (player == null)
        player = new AnimationPlayer( new AnimationClip[]{new AnimationClip("null", 0, 0, 0.0f)});
      Blend(player.LastFrameIndex, player.LastFrameIndex2, player.LastBlendFactor,
            player.FrameIndex, player.FrameIndex2, player.BlendFactor, player.ClipBlendFactor);
    }

    /// <summary>
    /// Render the object.
    /// </summary>
    /// <param name="effect">The current effect that is used for rendering.</param>
    public void Render(Purple.Graphics.Effect.IEffect effect) {
      if (tempMesh == null)
        meshes[0].Render(effect);
      else
        Current.Render(effect);
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
          shadowImplementation = new StencilShadow(meshes[0]);
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

    /// <summary>
    /// Blends two meshes.
    /// </summary>
    /// <param name="fromIndex">The index of the mesh to blend from.</param>
    /// <param name="toIndex">The index of the mesh to blend to.</param>
    /// <param name="factor">The blend factor.</param>
    private void Blend(int fromIndex, int toIndex, float factor) {
      CreateMesh();
      if (fromIndex == toIndex || factor < float.Epsilon)
        Fill(fromIndex);
      else if (factor >= 1.0f - float.Epsilon)
        Fill(toIndex);
      else {
        for (int i=0; i<tempMesh.SubSets.Count; i++) {
          SubSet ssTarget = (SubSet)tempMesh.SubSets[i];
          PositionStream psTarget = (PositionStream)ssTarget.VertexUnit[typeof(PositionStream)];
          SubSet ssSourceA = (SubSet)meshes[fromIndex].SubSets[i];
          PositionStream psSourceA = (PositionStream)ssSourceA.VertexUnit[typeof(PositionStream)];
          SubSet ssSourceB = (SubSet)meshes[toIndex].SubSets[i];
          PositionStream psSourceB = (PositionStream)ssSourceB.VertexUnit[typeof(PositionStream)];
          for (int l=0; l<psTarget.Size; l++)
            psTarget[l] = Vector3.Lerp( psSourceA[l], psSourceB[l], factor );
          psTarget.Upload();
        }
      }
    }

    /// <summary>
    /// Blends two meshes.
    /// </summary>
    /// <param name="fromIndexA">The index of the first mesh to blend from.</param>
    /// <param name="toIndexA">The index of the first mesh to blend to.</param>
    /// <param name="factorA">The blend factor of the first mesh.</param>
    /// <param name="fromIndexB">The index of the second mesh to blend from.</param>
    /// <param name="toIndexB">The index of the second mesh to blend to.</param>
    /// <param name="factorB">The blend factor of the second mesh.</param>
    /// <param name="factor">The blend factor between meshA and meshB.</param>
    public void Blend(int fromIndexA, int toIndexA, float factorA, 
      int fromIndexB, int toIndexB, float factorB, float factor) {
      CreateMesh();
      if (factor < float.Epsilon)
        Blend( fromIndexA, toIndexA, factorA );
      else if (factor >= 1.0f - float.Epsilon)
        Blend( fromIndexB, toIndexB, factorB );
      else {
        for (int i=0; i<tempMesh.SubSets.Count; i++) {
          SubSet ssTarget = (SubSet)tempMesh.SubSets[i];
          PositionStream psTarget = (PositionStream)ssTarget.VertexUnit[typeof(PositionStream)];
          SubSet ssSourceA = (SubSet)meshes[fromIndexA].SubSets[i];
          PositionStream psSourceA = (PositionStream)ssSourceA.VertexUnit[typeof(PositionStream)];
          SubSet ssSourceB = (SubSet)meshes[toIndexA].SubSets[i];
          PositionStream psSourceB = (PositionStream)ssSourceB.VertexUnit[typeof(PositionStream)];
          SubSet ssSourceC = (SubSet)meshes[fromIndexB].SubSets[i];
          PositionStream psSourceC = (PositionStream)ssSourceC.VertexUnit[typeof(PositionStream)];
          SubSet ssSourceD = (SubSet)meshes[toIndexB].SubSets[i];
          PositionStream psSourceD = (PositionStream)ssSourceD.VertexUnit[typeof(PositionStream)];
          for (int l=0; l<psTarget.Size; l++)
            psTarget[l] = Vector3.Lerp( Vector3.Lerp( psSourceA[l], psSourceB[l], factorA ),
              Vector3.Lerp(psSourceC[l], psSourceD[l], factorB), factor);
          psTarget.Upload();
        }
      }
    }


    /// <summary>
    /// Fills the temporary mesh with the mesh at a certain index.
    /// </summary>
    /// <param name="index">Index of source mesh.</param>
    /// <returns>The temporary mesh.</returns>
    private void Fill(int index) {
      CreateMesh();
      for (int i=0; i<tempMesh.SubSets.Count; i++) {
        SubSet ssTarget = (SubSet)tempMesh.SubSets[i];
        PositionStream psTarget = (PositionStream)ssTarget.VertexUnit[typeof(PositionStream)];
        SubSet ssSource = (SubSet)meshes[index].SubSets[i];
        PositionStream psSource = (PositionStream)ssSource.VertexUnit[typeof(PositionStream)];
        Array.Copy(psSource.Data, 0, psTarget.Data, 0, psTarget.Size);
        psTarget.Upload();
      }
    }

    private void CreateMesh() {
      if (tempMesh == null) {// || ?? security checks
        tempMesh = meshes[0].Clone();
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}

