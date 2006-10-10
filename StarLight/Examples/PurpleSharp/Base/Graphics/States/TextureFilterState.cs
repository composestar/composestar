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
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public struct TextureFilterState : IRenderState {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------			
		/// <summary>
		/// textureStage to set filters for
		/// </summary>
		public int Stage;

		/// <summary>
		/// Minification filter
		/// </summary>
		public TextureFilter MinFilter;

		/// <summary>
		/// Magnification filter
		/// </summary>
		public TextureFilter MagFilter;

		/// <summary>
		/// Mipmap filter
		/// </summary>
		public TextureFilter MipFilter;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------			
		/// <summary>
		/// initialisation
		/// </summary>
		/// <param name="stage">textureStage to set filters for</param>
		/// <param name="minFilter">Minification filter</param>
		/// <param name="magFilter">Magnifiaction filter</param>
		/// <param name="mipFilter">Mipmap filter</param>
		public TextureFilterState( int stage, TextureFilter minFilter, TextureFilter magFilter, TextureFilter mipFilter) {
			Stage = stage;
			MinFilter = minFilter;
			MagFilter = magFilter;
			MipFilter = mipFilter;
		}

    /// <summary>
    /// returns the default renderState
    /// </summary>
    public static TextureFilterState Default {
      get {
        return new TextureFilterState( 0, TextureFilter.None, TextureFilter.None, TextureFilter.None );
      }
    }

		/// <summary>
		/// intialisation
		/// </summary>		
		/// <param name="stage">The texture stage from which to fill this RenderState</param>
		/// <param name="device">The device from which to fill this RenderState</param>
		public TextureFilterState(int stage,Device device) 
		{	
      Stage = stage;
			MinFilter = device.SamplerStates[stage].MinFilter;
			MagFilter = device.SamplerStates[stage].MagFilter;
			MipFilter = device.SamplerStates[stage].MipFilter;
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
      MinFilter = Device.Instance.SamplerStates[Stage].MinFilter;
      MagFilter = Device.Instance.SamplerStates[Stage].MagFilter;
      MipFilter = Device.Instance.SamplerStates[Stage].MipFilter;
    }

    /// <summary>
    /// Clones the current <see cref="IRenderState"/> object.
    /// </summary>
    /// <returns>The cloned object.</returns>
    public IRenderState Clone() {
      return new TextureFilterState(Stage, MinFilter, MagFilter, MipFilter);
    }

		/// <summary>
		/// applies this renderState
		/// </summary>
		public void Apply() {			
			Device.Instance.SamplerStates[Stage].MinFilter = MinFilter;
			Device.Instance.SamplerStates[Stage].MagFilter = MagFilter;
			Device.Instance.SamplerStates[Stage].MipFilter = MipFilter;
		}

    /// <summary>
    /// applies the render state
    /// use this static method if you don't want to allocate a renderState object
    /// </summary>
    /// <param name="stage">textureStage to set filters for</param>
    /// <param name="minFilter">Minification filter</param>
    /// <param name="magFilter">Magnifiaction filter</param>
    /// <param name="mipFilter">Mipmap filter</param>
    public static void Apply( int stage, TextureFilter minFilter, TextureFilter magFilter, TextureFilter mipFilter) {
      Device.Instance.SamplerStates[stage].MinFilter = minFilter;
      Device.Instance.SamplerStates[stage].MagFilter = magFilter;
      Device.Instance.SamplerStates[stage].MipFilter = mipFilter;
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	
	}
}