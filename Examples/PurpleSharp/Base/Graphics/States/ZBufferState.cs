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
	/// RenderState ZBufferState
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public struct ZBufferState : IRenderState {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------			
		/// <summary>
		/// The depth value of the pixel is compared with the depth-buffer value. If the depth value of the pixel passes the comparison function, the pixel is written.
		/// </summary>
		public Compare Function;

		/// <summary>
		///  true to turn on depth-buffering state, false to disable depth buffering. 
		/// </summary>
		public bool Enable;

		/// <summary>
		/// true to enable the application to write to the depth buffer.
		/// </summary>
		public bool WriteEnable;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------	
		/// <summary>
		/// initialisation
		/// </summary>
		/// <param name="enable">true to turn on ZBuffer</param>
		/// <param name="function">to use for deciding to draw or reject pixel</param>
		/// <param name="writeEnable">true to turn on writting to the zBuffer</param>
		public ZBufferState( bool enable, Compare function, bool writeEnable) {
			Enable = enable;
			Function = function;
			WriteEnable = writeEnable;
		}

    /// <summary>
    /// returns the default renderState
    /// </summary>
    public static ZBufferState Default {
      get {
        return new ZBufferState( true, Compare.Less, true );
      }
    }

		/// <summary>
		/// intialisation
		/// </summary>		
		/// <param name="device">The device from which to fill this RenderState</param>
		public ZBufferState(Device device ) 
		{						
			this.Enable = device.RenderStates.ZBuffer;
			this.Function = device.RenderStates.ZBufferFunction;
			this.WriteEnable = device.RenderStates.ZBufferWriteEnable;
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
      this.Enable = Device.Instance.RenderStates.ZBuffer;
      this.Function = Device.Instance.RenderStates.ZBufferFunction;
      this.WriteEnable = Device.Instance.RenderStates.ZBufferWriteEnable;
    }

    /// <summary>
    /// Clones the current <see cref="IRenderState"/> object.
    /// </summary>
    /// <returns>The cloned object.</returns>
    public IRenderState Clone() {
      return new ZBufferState(Enable, Function, WriteEnable);
    }

		/// <summary>
		/// applies this renderState
		/// </summary>
		public void Apply() {
			Device.Instance.RenderStates.ZBufferFunction = Function;
			Device.Instance.RenderStates.ZBuffer = Enable;
			Device.Instance.RenderStates.ZBufferWriteEnable = WriteEnable;
		}

    /// <summary>
    /// applies the render state
    /// use this static method if you don't want to allocate a renderState object
    /// </summary>
    /// <param name="enable">true to turn on ZBuffer</param>
    /// <param name="function">to use for deciding to draw or reject pixel</param>
    /// <param name="writeEnable">true to turn on writting to the zBuffer</param>
    public static void Apply( bool enable, Compare function, bool writeEnable ) {
      Device.Instance.RenderStates.ZBufferFunction = function;
      Device.Instance.RenderStates.ZBuffer = enable;
      Device.Instance.RenderStates.ZBufferWriteEnable = writeEnable;
    }

    /// <summary>
    /// applies the render state
    /// </summary>
    /// <param name="enable">true to turn zBuffer</param>
    public static void Apply( bool enable ) {
      Device.Instance.RenderStates.ZBuffer = enable;
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	
	}
}
