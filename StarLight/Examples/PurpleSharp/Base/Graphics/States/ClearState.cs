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
  /// RenderState for clearing the buffers.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public class ClearState : IRenderState
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------	
    int clearColor;
    float clearDepth;
    int clearStencil;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Creates a new instance of a <see cref="ClearState"/>.
    /// </summary>
    /// <param name="clearColor">The color for clearing the framebuffer.</param>
    /// <param name="clearDepth">The value to clear the zBuffer with.</param>
    /// <param name="clearStencil">The value to clear the stencil buffer with.</param>
		public ClearState(int clearColor, float clearDepth, int clearStencil)
		{
      this.clearColor = clearColor;
      this.clearDepth = clearDepth;
      this.clearStencil = clearStencil;
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
      clearColor = Device.Instance.ClearColor;
      clearDepth = Device.Instance.ClearDepth;
      clearStencil = Device.Instance.ClearStencil;
    }

    /// <summary>
    /// Clones the current <see cref="IRenderState"/> object.
    /// </summary>
    /// <returns>The cloned object.</returns>
    public IRenderState Clone() {
      return new ClearState(clearColor, clearDepth, clearStencil);
    }

    /// <summary>
    /// Applies this <see cref="IRenderState"/>.
    /// </summary>
    public void Apply() {
      Apply( clearColor, clearDepth, clearStencil );
    }

    /// <summary>
    /// Applies this <see cref="IRenderState"/>.
    /// </summary>
    /// <remarks>
    /// Use this static method if you don't want to allocate a whole 
    /// <see cref="IRenderState"/> object.
    /// </remarks>
    /// <param name="clearColor">The color to use for clearing the frameBuffer.</param>
    /// <param name="clearDepth">The value to use for clearing the depthBuffer.</param>
    /// <param name="clearStencil">The value to use for clearing the clearStencil.</param>
    public static void Apply( int clearColor, float clearDepth, int clearStencil ) {
      Device.Instance.ClearColor = clearColor;
      Device.Instance.ClearDepth = clearDepth;
      Device.Instance.ClearStencil = clearStencil;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	
	}
}
