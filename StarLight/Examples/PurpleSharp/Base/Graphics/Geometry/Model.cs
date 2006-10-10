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
using Purple.Graphics;
using Purple.Graphics.Effect;
using Purple.Graphics.Lighting;

namespace Purple.Graphics.Geometry
{
  //=================================================================
  /// <summary>
  /// A 3d entity like a character, a space ship or whatever.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  ///   - The model class can be used to represent an (animated) character
  ///   - The model class consists of a skeleton, meshes and a player
  ///   - The skeleton can be animated by the player
  ///   - Meshes and other models can be attached to a certain bone of the skeleton
  ///   - Meshes can be animated by using the BlendMesh or the SkinnedMesh implementation  
  /// </remarks>
  //=================================================================
  public class Model : IRenderAble/*, IShadowCaster*/ {
    //---------------------------------------------------------------
    #region Internal structs
    //---------------------------------------------------------------
    /// <summary>
    /// Structure for binding a model to a joint.
    /// </summary>
    public struct ModelBinding {
      /// <summary>
      /// The index of the joint.
      /// </summary>
      public int JointNum;
      /// <summary>
      /// The model to attach.
      /// </summary>
      public Model Model;

      /// <summary>
      /// Binds a model to a certain joint.
      /// </summary>
      /// <param name="jn">The joint to bind model to.</param>
      /// <param name="model">The model to bind.</param>
      public ModelBinding(int jn, Model model) {
        this.JointNum = jn;
        this.Model = model;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the skeleton of the model.
    /// </summary>
    public Skeleton Skeleton {
      get {
        return skeleton;
      }
      set {
        this.skeleton = value;
        if (skeleton != null) {
        }
      }
    }
    Skeleton skeleton;

    /// <summary>
    /// The effect to use with the model.
    /// </summary>
    public IEffect Effect {
      get {
        return effect;
      }
      set {
        effect = value;
      }
    }
    IEffect effect;

    /// <summary>
    /// The mesh used by the model.
    /// </summary>
    public IAnimatedMesh Mesh {
      get {
        return mesh;
      }
      set {
        mesh = value;
      }
    }
    IAnimatedMesh mesh;

    /// <summary>
    /// Returns all bound models.
    /// </summary>
    public ModelBinding[] ModelBindings {
      get {
        return (ModelBinding[])modelBindings.ToArray(typeof(ModelBinding));
      }
    }
    ArrayList modelBindings = new ArrayList();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new model.
    /// </summary>
    /// <param name="mesh">Mesh to use for model.</param>
    /// <param name="skeleton">Skeleton to use for the model or null.</param>
    public Model(IAnimatedMesh mesh, Skeleton skeleton) {
      this.mesh = mesh;
      Skeleton = skeleton;
    }

    /// <summary>
    /// Creates a new empty model.
    /// </summary>
    public Model() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Attaches a model to a cetain joint.
    /// </summary>
    /// <param name="joint">The joint to attach model to.</param>
    /// <param name="model">The model to attach.</param>
    public void AttachModel( Model model, Joint joint) {
      int jointIndex = -1;
      if (joint != null)
        jointIndex = joint.Index;
      modelBindings.Add( new ModelBinding(jointIndex, model) );
    }

    /// <summary>
    /// Attaches a model to a cetain joint.
    /// </summary>
    /// <param name="jointIndex">The joint to attach model to.</param>
    /// <param name="model">The model to attach.</param>
    public void AttachModel( Model model, int jointIndex) {
      modelBindings.Add( new ModelBinding(jointIndex, model) );
    }

    /// <summary>
    /// Updates the model.
    /// </summary>
    public void Update() {

      // animate skeleton
      if (skeleton != null)
        skeleton.Update();

      // Update the mesh
      if (mesh != null)
        mesh.Update();

      // Update the bound models
      foreach( ModelBinding b in modelBindings)
          b.Model.Update();
    }

    /// <summary>
    /// Render the object.
    /// </summary>
    /// <param name="effect">The current effect that is used for rendering.</param>
    public void Render(IEffect effect) {
      /*// animate skeleton
      if (skeleton != null && skeletonAnimation != null)
        player.Interpolate(skeleton, skeletonAnimation);*/

      mesh.Render(effect);

      // Render all attached models.
      foreach( ModelBinding b in modelBindings) {
        Matrix4 world = Device.Instance.Transformations.World;
        if (b.JointNum == -1)
          b.Model.Render(effect);
        else {
          Device.Instance.Transformations.World = skeleton.PreBound[b.JointNum] * world;
          b.Model.Render(effect);
          Device.Instance.Transformations.World = world;
        }
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    #region IShadowCaster Members

    /// <summary>
    /// HACK
    /// </summary>
    /// <param name="effect"></param>
    public void RenderShadow(IEffect effect) {
      if (this.ShadowImplementation != null) {
        mesh.ShadowImplementation.RenderAble.Render(effect);
        foreach( ModelBinding b in modelBindings) {
          Matrix4 world = Device.Instance.Transformations.World;
          Device.Instance.Transformations.World = skeleton.PreBound[b.JointNum] * world;
          b.Model.RenderShadow(effect);
          Device.Instance.Transformations.World = world;
        }
      }
    }

    /// <summary>
    /// The shadow implementation of the current shadow caster.
    /// </summary>
    public IShadowImplementation ShadowImplementation {
      get {
        return mesh.ShadowImplementation;
      }
    }

    /// <summary>
    /// Turn on and off shadowing.
    /// </summary>
    public bool Shadowed {
      get {
        return mesh.Shadowed;
      }
      set {
        mesh.Shadowed = value;
        foreach( ModelBinding b in modelBindings) {
          b.Model.Shadowed = true;
        }
      }
    }

    /// <summary>
    /// Hack
    /// </summary>
    /// <param name="light"></param>
    /// <param name="world"></param>
    /// <param name="recalcNormals"></param>
    public void UpdateShadow(Light light, Purple.Math.Matrix4 world, bool recalcNormals) {
      mesh.UpdateShadow(light, world, recalcNormals);
      // Render all attached models.
      foreach( ModelBinding b in modelBindings) {
        b.Model.UpdateShadow(light, skeleton.PreBound[b.JointNum] * world, recalcNormals);
      }
    }

    #endregion
  }
}
