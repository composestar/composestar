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
using Purple.Graphics;
using Purple.Graphics.Effect;

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// This object stores the current state of the graphics card.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
  public class SceneState : IMultiPassApply {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the default <see cref="SceneState"/>.
    /// </summary>
    public static SceneState Default {
      get {
        return instance;
      }
    }  
    private static SceneState instance = new SceneState();

    /// <summary>
    /// Returns the current <see cref="IEffect"/>.
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
    /// The world matrix.
    /// </summary>
    public Matrix4 World {
      get {
        return world;
      }
      set {
        world = value;
      }
    }
    Matrix4 world = Matrix4.Identity;

    /// <summary>
    /// The view matrix.
    /// </summary>
    public Matrix4 View {
      get {
        return view;
      }
      set {
        view = value;
      }
    }
    Matrix4 view = Matrix4.Identity;

    /// <summary>
    /// The projection matrix.
    /// </summary>
    public Matrix4 Projection {
      get {
        return projection;
      }
      set {
        projection = value;
      }
    }
    Matrix4 projection = Purple.Math.Matrix4.PerspectiveFOV(Purple.Math.Basic.PI/3, 4/3.0f, 10.0f, 1000.0f);

    /// <summary>
    /// The collection of the set <see cref="Purple.Graphics.Core.ITexture"/> objects.
    /// </summary>
    public Textures Textures {
      get {
        return textures;
      }
      set {
        textures = value;
      }
    }
    Textures textures;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of <see cref="SceneState"/>.
    /// </summary>
    public SceneState() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Starts with applying and returns the number of steps.
    /// </summary>
    public int Begin() {
      return effect.Begin();
    }

    /// <summary>
    /// Applies a certain states
    /// </summary>
    /// <param name="step">The step to apply state for.</param>
    public void BeginPass(int step) {
      Device device = Device.Instance;
      textures.Apply();
      device.Transformations.World = World;
      device.Transformations.View = View;
      device.Transformations.Projection = Projection;
      effect.BeginPass(step);
    }

    /// <summary>
    /// Ends the current pass.
    /// </summary>
    public void EndPass() {
      effect.EndPass();
    }

    /// <summary>
    /// End the current effect.
    /// </summary>
    public void End() {
      effect.End();
    }

    /// <summary>
    /// Commits changes within a pass.
    /// </summary>
    public void CommitChanges() {
      effect.CommitChanges();
    }

    /// <summary>
    /// Copies the current <see cref="SceneState"/> to another state.
    /// </summary>
    public void CopyTo(SceneState state) {
      state.Effect = Effect;
      state.Textures = Textures;
      state.World = World;
      state.View = View;
      state.Projection = Projection;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
