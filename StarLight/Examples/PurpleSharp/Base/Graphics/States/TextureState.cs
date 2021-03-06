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
//   Markus W??
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
  /*//=================================================================
  /// <summary>
  /// RenderState for handling textures.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus W??</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public struct TextureState : IRenderState {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
		/// <summary>
		/// Textures to set.
		/// </summary>
	  public Textures Textures;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Returns the default <see cref="TextureState"/>.
    /// </summary>
    public static TextureState Default {
      get {
        return new TextureState();
      }
    }

    /// <summary>
    /// Creates a new instance of a <see cref="TextureState"/>.
    /// </summary>
    /// <param name="textures">Textures to set</param>
    public TextureState( Textures textures ) {
      Textures = textures;
    }

	  /// <summary>
	  /// Creates a new instance of a <see cref="TextureState"/>.
	  /// </summary>
	  /// <param name="device">The device from which to fill this RenderState</param>
	  public TextureState(Device device) 
	  {
		  Textures = device.Textures;
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
      Textures = Device.Instance.Textures;
    }

    /// <summary>
    /// Clones the current <see cref="IRenderState"/> object.
    /// </summary>
    /// <returns>The cloned object.</returns>
    public IRenderState Clone() {
      return new TextureState(Textures);
    }

    /// <summary>
    /// Applies this <see cref="IRenderState"/> object.
    /// </summary>
    public void Apply() {		
	    Device.Instance.Textures = Textures;
    }

    /// <summary>
    /// Applies the render state.
    /// </summary>
    /// <remarks>
    /// Use this static method if you don't want to allocate a <see cref="IRenderState"/> object.
    /// </remarks>
    /// <param name="textures">Textures to set.</param>
    public static void Apply( Textures textures ) {
      Device.Instance.Textures = textures;
    }
    //---------------------------------------------------------------
		#endregion
    //---------------------------------------------------------------	
  }*/
}
