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

using Purple.Graphics.Core;
using Purple.Math;
using Purple.Graphics.VertexStreams;
using Purple.Graphics.Effect;
using Purple.Graphics.Lighting;

namespace Purple.Graphics.Geometry {
	//=================================================================
	/// <summary>
	/// A simple class for handling meshes.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.7</para>
  /// 	A <see cref="Mesh"/> is a collection of <see cref="SubSet"/>s that 
  ///   may use different <see cref="Textures"/>. However, the transformation 
  ///   must stay the same for all <see cref="SubSet"/>s.
	/// </remarks>
	//=================================================================
	public class Mesh : IAnimatedMesh {
		//---------------------------------------------------------------
    #region Variables and Properties
		//---------------------------------------------------------------
		/// <summary>
		/// Returns the list of <see cref="SubSet"/>s defined for this mesh.
		/// </summary>
		public SubSets SubSets {
			get {
				return subSets;
			}
			set {
				subSets = value;
			}
		}
    SubSets subSets = new SubSets();	// TODO optimize new SubSets()

    
    /// <summary>
    /// Returns the list of <see cref="Textures"/> objects used for the mesh.
    /// </summary>
    /// <remarks>
    /// If the <c>Textures</c> list is empty, the textures have to be 
    /// set outside the mesh. Otherwise there should be a <see cref="Textures"/> object
    /// for every <see cref="SubSet"/>. But for performance reasons you should reuse 
    /// <see cref="Textures"/> objects for <see cref="SubSet"/> using the same 
    /// <see cref="Purple.Graphics.Core.ITexture"/>s.
    /// <para>If this property just contains one <see cref="Textures"/> object, this 
    /// textures are used for the whole <see cref="Mesh"/>.</para>
    /// </remarks>
    public ArrayList Textures { 
      get {
        return textures;
      }
      set {
        textures = value;
      }
    }
    ArrayList textures = new ArrayList(); // TODO optimize new ArrayList
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// Creates a new instance of a <see cref="Mesh"/>.
		/// </summary>		
		public Mesh() {			
		}

    /// <summary>
    /// Creates a new instance of a <see cref="Mesh"/>.
    /// </summary>
    /// <param name="subSet">SubSet to add to the mesh.</param>
    public Mesh(SubSet subSet) {
      this.SubSets.Add(subSet);
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		/// <summary>
		/// Resets the mesh.
		/// </summary>
		public void Reset() {
			subSets.Clear();
      textures.Clear();
		}

    /// <summary>
    /// Render the object.
    /// </summary>
    /// <param name="effect">The current effect that is used for rendering.</param>
    public void Render(IEffect effect) {
      for (int i=0; i<this.SubSets.Count; i++) {
        DrawSubSet(effect, i);
      }
    }

    /// <summary>
    /// Draws a subset with given index.
    /// </summary>
    /// <remarks>
    /// For drawing a whole mesh, the method <c>Draw</c> should be used for optimized speed.
    /// </remarks>
    /// <param name="effect">The current effect that is used for rendering.</param>
    /// <param name="index">Index of the subSet to draw.</param>
    public void DrawSubSet(IEffect effect, int index) {
      // Apply next texture if there is one in the texture list.
      if (Textures.Count > index) {
        (Textures[index] as Textures).Apply();
      }      

      effect.CommitChanges();
      SubSet s = (SubSet)subSets[index];
      (subSets[index] as SubSet).Draw();
    }

    /// <summary>
    /// Creates a deep copy of the current <see cref="Mesh"/>.
    /// </summary>
    /// <returns>Cloned instance of the mesh.</returns>
    public Mesh Clone() {
      Mesh ret = new Mesh();
      for(int i=0; i<SubSets.Count; i++)
        ret.SubSets.Add( SubSets[i].Clone() );
      for(int i=0; i<Textures.Count; i++)
        ret.Textures.Add( Textures[i] );
      return ret;
    }

    /// <summary>
    /// Returns a new instance of the <see cref="Mesh"/>, where all <see cref="SubSet"/>s 
    /// have the specified <see cref="VertexFormat"/>.
    /// </summary>
    /// <param name="format">Format to convert into.</param>
    /// <returns>Cloned instance of the mesh.</returns>
    public Mesh Clone(VertexFormat format) {
      Mesh ret = new Mesh();
      for(int i=0; i<SubSets.Count; i++)
        ret.SubSets.Add( SubSets[i].Clone(format) );
      for(int i=0; i<Textures.Count; i++)
        ret.Textures.Add( Textures[i] );
      return ret;
    }

    /// <summary>
    /// Clones and converts a given <see cref="Mesh"/> to fit the given <see cref="Semantic"/>s.
    /// </summary>
    /// <remarks>
    /// Clone uses a deep copy for the <see cref="VertexUnit"/>s 
    /// and a shallow copy for the <see cref="Purple.Graphics.VertexStreams.IndexStream"/>s.
    /// </remarks>
    /// <param name="semantics">The array of <see cref="Semantic"/> descriptions to fit.</param>
    /// <returns>The cloned and converted <see cref="Mesh"/>.</returns>
    public Mesh Clone(Semantic[] semantics) {      
      Mesh mesh = new Mesh();
      for (int i=0; i<SubSets.Count; i++)
        mesh.SubSets.Add( SubSets[i].Clone(semantics) );
      for (int i=0; i<Textures.Count; i++)
        mesh.Textures.Add( Textures[i] );
      return mesh;
    }

    /// <summary>
    /// Returns the current mesh.
    /// </summary>
    Mesh IAnimatedMesh.Current { 
      get {
        return this;
      }
    }

    /// <summary>
    /// Updates the mesh with the current animation.
    /// </summary>
    public void Update() {
      // this mesh has no animation to update
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
          shadowImplementation = new StencilShadow(this);
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
      if (this.shadowImplementation != null)
        this.shadowImplementation.UpdateShadow(light, world, recalcNormals);
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
