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
	/// RenderState CullMode
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public struct CullModeState : IRenderState {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------	
		/// <summary>
		/// Default Value: CounterClockwise
		/// </summary>
		public CullMode Mode;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------	
		/// <summary>
		/// intialisation
		/// </summary>
		/// <param name="cullMode">to set</param>
		public CullModeState( CullMode cullMode) {
			Mode = cullMode;			
		}

    /// <summary>
    /// returns the default renderState
    /// </summary>
    public static CullModeState Default {
      get {
        return new CullModeState( CullMode.CounterClockwise );
      }
    }

		/// <summary>
		/// intialisation
		/// </summary>		
		/// <param name="device">The device from which to fill this RenderState</param>
		public CullModeState( Device device ) 
		{						
			Mode = device.RenderStates.CullMode;
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
			Mode = Device.Instance.RenderStates.CullMode;
    }

    /// <summary>
    /// Clones the current <see cref="IRenderState"/> object.
    /// </summary>
    /// <returns>The cloned object.</returns>
    public IRenderState Clone() {
      return new CullModeState(Mode);
    }

		/// <summary>
		/// applies this renderState
		/// </summary>
		public void Apply() {
			Device.Instance.RenderStates.CullMode = Mode;
		}

    /// <summary>
    /// applies the render state
    /// use this static method if you don't want to allocate a renderState object
    /// </summary>
    /// <param name="cullMode">to set</param>
    public static void Apply( CullMode cullMode ) {
      Device.Instance.RenderStates.CullMode = cullMode;
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	
	}
}
