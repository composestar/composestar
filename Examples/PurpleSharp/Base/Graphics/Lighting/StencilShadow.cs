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
using System.IO;

using Purple.Math;
using Purple.Graphics;
using Purple.Graphics.Effect;
using Purple.Graphics.Geometry;

namespace Purple.Graphics.Lighting
{
  //=================================================================
  /// <summary>
  /// Implementation for a stencil shadow.
  /// </summary>
  /// <remarks>
  ///   <para>Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
  public class StencilShadow : IShadowImplementation {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    static IEffect stencilEffect;
    bool shadowVolumePrepared = false;
    ShadowVolume shadowVolume;
    Mesh mesh;

    /// <summary>
    /// The effect used for rendering the shadow volume;
    /// </summary>
    public static IEffect Effect {
      get {
        return stencilEffect;
      }
    }

    /// <summary>
    /// Turn on and off shadowing.
    /// </summary>
    public bool Shadowed { 
      get {
        return shadowed;
      }
      set {
        if (shadowed && !shadowVolumePrepared)
          PrepareShadowVolume();
        shadowed = value;
      }
    }
    bool shadowed = false;

    /// <summary>
    /// Returns the renderAble object for the shadowImpl.
    /// </summary>
    public IRenderAble RenderAble { 
      get {
        return shadowVolume;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    static StencilShadow() {
      using (Stream stream = Purple.IO.ResourceFileSystem.Instance.Open("Purple/Graphics/Lighting/StencilShadow.fx")) {
        stencilEffect = EffectCompiler.Instance.Compile(stream);
      }
    }

    /// <summary>
    /// Creates a new instance of a stencil shadow object.
    /// </summary>
    /// <param name="mesh">The mesh to cast shadow for.</param>
    public StencilShadow(Mesh mesh) {
      this.mesh = mesh;      
      Shadowed = true;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //--------------------------------------------------------------- 
    /// <summary>
    /// Prepares the current model to be used with stencil shadows.
    /// </summary>
    public void PrepareShadowVolume() {
      // prepare the mesh for shadowing
      if (!shadowVolumePrepared) {
        using (Purple.Profiling.Profiler.Instance.Sample("Prepare")) {
          ShadowVolume.PrepareMesh(mesh);
        }
        using (Purple.Profiling.Profiler.Instance.Sample("ShadowVolume")) {
          shadowVolume = new ShadowVolume(mesh, stencilEffect);
        }
        shadowVolumePrepared = true;
      }
    }

    /// <summary>
    /// Hack
    /// </summary>
    /// <param name="light"></param>
    /// <param name="world"></param>
    /// <param name="recalcNormals"></param>
    public void UpdateShadow(Light light, Matrix4 world, bool recalcNormals) {
      if (this.shadowed) {
        shadowVolume.Calculate(light, world, recalcNormals);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
