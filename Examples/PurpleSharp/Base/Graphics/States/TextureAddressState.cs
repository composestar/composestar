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

namespace Purple.Graphics.States {
  //=================================================================
  /// <summary>
  /// RenderState TextureFilter
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public struct TextureAddressState : IRenderState {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------			
    /// <summary>
    /// TextureStage to set address mode for.
    /// </summary>
    public int Stage;

    /// <summary>
    /// Address for the first coordinate.
    /// </summary>
    public TextureAddress U;

    /// <summary>
    /// Address for the second coordinate.
    /// </summary>
    public TextureAddress V;

    /// <summary>
    /// Address for the third coordinate.
    /// </summary>
    public TextureAddress W;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------			
    /// <summary>
    /// Creates a new state object.
    /// </summary>
    /// <param name="stage">TextureStage to set filters for.</param>
    /// <param name="u">Address type for the first texture coordinate.</param>
    /// <param name="v">Address type for the second texture coordinate.</param>
    /// <param name="w">Address type for the third texture coordinate.</param>
    public TextureAddressState( int stage, TextureAddress u, TextureAddress v, TextureAddress w) {
      Stage = stage;
      this.U = u;
      this.V = v;
      this.W = w;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------	
    /// <summary>
    /// Saves the current state into the current <see cref="IRenderState"/> object.
    /// </summary>
    public void Save() {
      U = Device.Instance.SamplerStates[Stage].AddressU;
      V = Device.Instance.SamplerStates[Stage].AddressV;
      W = Device.Instance.SamplerStates[Stage].AddressW;
    }

    /// <summary>
    /// Clones the current <see cref="IRenderState"/> object.
    /// </summary>
    /// <returns>The cloned object.</returns>
    public IRenderState Clone() {
      return new TextureAddressState(Stage, U, V, W);
    }

    /// <summary>
    /// Applies this renderState.
    /// </summary>
    public void Apply() {			
      Device.Instance.SamplerStates[Stage].AddressU = U;
      Device.Instance.SamplerStates[Stage].AddressV = V;
      Device.Instance.SamplerStates[Stage].AddressW = W;
    }

    /// <summary>
    /// Applies the render state.
    /// </summary>
    /// <remarks>
    /// Use this static method if you don't want to allocate a renderState object.
    /// </remarks>
    /// <param name="stage">TextureStage to set address mode for.</param>
    /// <param name="u">Address type for the first texture coordinate.</param>
    /// <param name="v">Address type for the second texture coordinate.</param>
    /// <param name="w">Address type for the third texture coordinate.</param>
    public static void Apply( int stage, TextureAddress u, TextureAddress v, TextureAddress w) {
      Device.Instance.SamplerStates[stage].AddressU = u;
      Device.Instance.SamplerStates[stage].AddressV = v;
      Device.Instance.SamplerStates[stage].AddressW = w;
    }

    /// <summary>
    /// Applies the render state.
    /// </summary>
    /// <remarks>
    /// Use this static method if you don't want to allocate a renderState object.
    /// </remarks>
    /// <param name="stage">TextureStage to set address mode for.</param>
    /// <param name="mode">Address mode for all texture coordinates.</param>
    public static void Apply( int stage, TextureAddress mode) {
      Device.Instance.SamplerStates[stage].AddressU = mode;
      Device.Instance.SamplerStates[stage].AddressV = mode;
      Device.Instance.SamplerStates[stage].AddressW = mode;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	
  }
}
