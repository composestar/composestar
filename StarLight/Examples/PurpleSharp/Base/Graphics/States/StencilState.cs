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
  /// RenderState for stencil stuff.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public struct StencilState : IRenderState {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------			
    /// <summary>
    /// The stencil value of the pixel is compared with the stencil-buffer value. If the stencil value of the pixel passes the comparison function, the pixel is written.
    /// </summary>
    public Compare Function;

    /// <summary>
    /// True to turn on stencil testing.
    /// </summary>
    public bool Enable;

    /// <summary>
    /// The stencil operation to perform when the stencil test fails.
    /// </summary>
    public StencilOperation Fail;

    /// <summary>
    /// The stencil operation to perform when the stencil test passes.
    /// </summary>
    public StencilOperation Pass;

    /// <summary>
    /// The stencil operation to perform when the stencil test passes but the zBuffer test fails.
    /// </summary>
    public StencilOperation ZBufferFail;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Creates a new instance of a <see cref="StencilState"/>.
    /// </summary>
    /// <param name="enable">True to turn on stencil testing.</param>
    /// <param name="function">The compare function to use for the stencil test.</param>
    /// <param name="fail">The stencil operation to perform when the stencil test fails.</param>
    /// <param name="pass">The stencil operation to perform when the stencil test passes.</param>
    /// <param name="zBufferFail">The stencil operation to perform when the stencil test passes but the zBuffer test fails.</param>
    public StencilState( bool enable, Compare function, StencilOperation pass, StencilOperation fail, StencilOperation zBufferFail) {
      Enable = enable;
      Function = function;
      Fail = fail;
      Pass = pass;
      ZBufferFail = zBufferFail;
    }

    /// <summary>
    /// Returns the default <see cref="StencilState"/>.
    /// </summary>
    public static StencilState Default {
      get {
        return new StencilState( true, Compare.Always, StencilOperation.Keep, StencilOperation.Keep, StencilOperation.Keep);
      }
    }

    /// <summary>
    /// Creates a new instance of a <see cref="StencilState"/>.
    /// </summary>		
    /// <param name="device">The device from which to fill this RenderState.</param>
    public StencilState(Device device ) {		
      this.Enable = device.RenderStates.StencilTest;
      this.Function = device.RenderStates.StencilFunction;
      this.Fail = device.RenderStates.StencilFail;
      this.Pass = device.RenderStates.StencilPass;
      this.ZBufferFail = device.RenderStates.StencilZBufferFail;
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
      this.Enable = Device.Instance.RenderStates.StencilTest;
      this.Function = Device.Instance.RenderStates.StencilFunction;
      this.Fail = Device.Instance.RenderStates.StencilFail;
      this.Pass = Device.Instance.RenderStates.StencilPass;
      this.ZBufferFail = Device.Instance.RenderStates.StencilZBufferFail;
    }

    /// <summary>
    /// Clones the current <see cref="IRenderState"/> object.
    /// </summary>
    /// <returns>The cloned object.</returns>
    public IRenderState Clone() {
      return new StencilState(Enable, Function, Pass, Fail, ZBufferFail);
    }

    /// <summary>
    /// Applies the renderState.
    /// </summary>
    public void Apply() {
      Device.Instance.RenderStates.StencilFunction = Function;
      Device.Instance.RenderStates.StencilTest = Enable;
      Device.Instance.RenderStates.StencilFail = Fail;
      Device.Instance.RenderStates.StencilPass = Pass;
      Device.Instance.RenderStates.StencilZBufferFail = ZBufferFail;
    }

    /// <summary>
    /// Applies the <see cref="StencilState"/> defined by the passed parameters.
    /// </summary>
    /// <remarks>
    /// Use this static method if you don't want to allocate a renderState object.
    /// </remarks>
    /// <param name="enable">True to turn on stencil testing.</param>
    /// <param name="function">Stencil compare function.</param>
    /// <param name="pass">The operation to perform if the stencil test passes.</param>
    /// <param name="fail">The operation to perform if the stencil test fails.</param>
    /// <param name="zBufferFail">The operation to perform if the stencil test passes but the zBuffer Test fails.</param>
    public static void Apply( bool enable, Compare function, StencilOperation pass, StencilOperation fail, StencilOperation zBufferFail ) {
      Device.Instance.RenderStates.StencilFunction = function;
      Device.Instance.RenderStates.StencilTest = enable;
      Device.Instance.RenderStates.StencilFail = fail;
      Device.Instance.RenderStates.StencilPass = pass;
      Device.Instance.RenderStates.StencilZBufferFail = zBufferFail;
    }

    /// <summary>
    /// Enables, disables the stencil buffer.
    /// </summary>
    /// <param name="enable">Flag that indicates if stencil testing should be enabled.</param>
    public static void Apply( bool enable ) {
      Device.Instance.RenderStates.StencilTest = enable;
    }

    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	
  }
}