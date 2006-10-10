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

namespace Purple.Graphics.States
{
  //=================================================================
  /// <summary>
  /// RenderState for alpha handling.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public struct AlphaState : IRenderState {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------	
    bool alphaEnable;
    Blend sourceBlend;
    Blend destinationBlend;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Creates a new <see cref="AlphaState"/> object.
    /// </summary>
    /// <param name="alphaEnable">Flag that indicates if alpha blending is enabled.</param>
    /// <param name="sourceBlend">Sets the source blend factor.</param>
    /// <param name="destinationBlend">Sets the destination blend factor.</param>
    public AlphaState( bool alphaEnable, Blend sourceBlend, Blend destinationBlend ) {
      this.alphaEnable = alphaEnable;		
      this.sourceBlend = sourceBlend;
      this.destinationBlend = destinationBlend;
    }

    /// <summary>
    /// Creates a new <see cref="AlphaState"/> object.
    /// </summary>		
    /// <param name="device">The <see cref="Device"/> from which to fill this <see cref="IRenderState"/>.</param>
    public AlphaState( Device device ) {						
      alphaEnable = device.RenderStates.AlphaEnable;
      sourceBlend = device.RenderStates.SourceBlend;
      destinationBlend = device.RenderStates.DestinationBlend;
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
      alphaEnable = Device.Instance.RenderStates.AlphaEnable;
      sourceBlend = Device.Instance.RenderStates.SourceBlend;
      destinationBlend = Device.Instance.RenderStates.DestinationBlend;
    }

    /// <summary>
    /// Clones the current <see cref="IRenderState"/> object.
    /// </summary>
    /// <returns>The cloned object.</returns>
    public IRenderState Clone() {
      return new AlphaState(alphaEnable, sourceBlend, destinationBlend );
    }

    /// <summary>
    /// Applies this <see cref="IRenderState"/>.
    /// </summary>
    public void Apply() {
      Apply( alphaEnable, sourceBlend, destinationBlend);
    }

    /// <summary>
    /// Applies this <see cref="IRenderState"/>.
    /// </summary>
    /// <remarks>
    /// Use this static method if you don't want to allocate a whole 
    /// <see cref="IRenderState"/> object.
    /// </remarks>
    /// <param name="alphaEnable">Flag that indicates if alpha blending is enabled.</param>
    /// <param name="sourceBlend">Sets the source blend factor.</param>
    /// <param name="destinationBlend">Sets the destination blend factor.</param>
    public static void Apply(bool alphaEnable, Blend sourceBlend, Blend destinationBlend ) {
      Device.Instance.RenderStates.AlphaEnable = alphaEnable;
      Device.Instance.RenderStates.SourceBlend = sourceBlend;
      Device.Instance.RenderStates.DestinationBlend = destinationBlend;
    }

    /// <summary>
    /// Applies this <see cref="IRenderState"/>.
    /// </summary>
    /// <remarks>
    /// Use this static method if you don't want to allocate a whole 
    /// <see cref="IRenderState"/> object.
    /// </remarks>
    /// <param name="alphaEnable">Flag that indicates if alpha blending is enabled.</param>
    public static void Apply(bool alphaEnable) {
      Device.Instance.RenderStates.AlphaEnable = alphaEnable;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	
  }
}
