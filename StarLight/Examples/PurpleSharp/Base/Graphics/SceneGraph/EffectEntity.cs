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
using Purple.Graphics.Core;
using Purple.Graphics.Effect;

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// An effect entity represents the scene entity for an <see cref="IEffect"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  ///   <para>Last change: 0.3</para>
  /// </remarks>
  //=================================================================
  public class EffectEntity : SceneEntity, ISceneEntity {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    IEffect effect;
    IEffect save;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Property
    //---------------------------------------------------------------
    /// <summary>
    /// The effect that is used for the entity.
    /// </summary>
    public IEffect Effect {
      get {
        return effect;
      }
      set {
        effect = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an <see cref="EffectEntity"/>.
    /// </summary>
    /// <param name="effect">The effect used for the <see cref="EffectEntity"/>.</param>
    public EffectEntity(IEffect effect) {
      this.effect = effect;
    }

    /// <summary>
    /// Creates a new instance of an <see cref="EffectEntity"/>.
    /// </summary>
    /// <param name="vs">Vertex shader to use.</param>
    /// <param name="ps">Pixel shader to use.</param>
    public EffectEntity(IVertexShader vs, IPixelShader ps) {
      throw new NotImplementedException("So far ;-)!");
      //this.effect = new Effect.XmlEffect(vs, ps);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Before controllers can manipulate the entity and before <c>Traverse</c> is called.
    /// </summary>
    /// <remarks>
    /// This is the right time to save the part of the state that gets changed.
    /// </remarks>
    public void Before() {
      save = Manager.CurrentState.Effect;
    }

    /// <summary>
    /// Do preparations - Traverse is called, before entity might get culled!
    /// </summary>
    public void Traverse() {
      Manager.CurrentState.Effect = effect;
    }

    /// <summary>
    /// This method is called when all children were traversed.
    /// </summary>
    /// <remarks>
    /// This is the right time to restore the part of the state that was changed.
    /// </remarks>
    public void After() {
      Manager.CurrentState.Effect = save;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
