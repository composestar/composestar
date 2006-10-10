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
using System.Windows.Forms;

namespace Purple.Input
{
	//=================================================================
	/// <summary>
	/// Abstract interface for an input engine.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.4</para>
	/// </remarks>
	//=================================================================
	public interface IInputEngine: IDisposable
	{		

		/// <summary>
		/// Initializes the input engine.
		/// </summary>
		/// <param name="control">Instance of control to render into.</param>
		void Init(Control control);		

    /// <summary>
    /// Returns true if engine is initialized.
    /// </summary>
    bool Initialized { get; }

		/// <summary>
		/// Returns the keyboard object.
		/// </summary>
		IKeyboard Keyboard { get; }
			
		/// <summary>
		/// Returns the mouse object.
		/// </summary>
		IMouse Mouse { get; }			

    /// <summary>
    /// Returns the the array of gamepads.
    /// </summary>
    IGamePad[] GamePads { get; }

		/// <summary>
		/// Updates all input devices.
		/// </summary>
		/// <remarks>
		/// Should be called once per frame.
		/// </remarks>
		void Update();
	}
}
