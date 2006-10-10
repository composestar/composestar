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

namespace Purple.Input {

  //=================================================================
  /// <summary>
  /// Interface that defines a keyboard handler.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>  
  ///   <para>Since: 0.4</para>
  /// </remarks>
  //=================================================================
  public interface IKeyHandler {
    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="key">The key that was pressed or released.</param>
    /// <param name="pressed">Flag that indicates if key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    bool OnKey(Key key, bool pressed);
  }

  //=================================================================
  /// <summary>
  /// Delegate for handling key events.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>  
  ///   <para>Since: 0.4</para>
  /// </remarks>
  //=================================================================
  public delegate void KeyHandler(Key key, bool pressed);

  //=================================================================
  /// <summary>
  /// Delegate for handling text input.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>  
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public delegate void CharHandler(char keyChar);

	//=================================================================
	/// <summary>
	/// Abstract interface for the keyboard.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.6</para>
	/// </remarks>
	//=================================================================
  public interface IKeyboard: IInputDevice {
    /// <summary>
    /// Event that is fired when a key gets pressed or released.
    /// </summary>
    event KeyHandler OnKey;

    /// <summary>
    /// Event that is fired when an key was pressed. This event handler returns the 
    /// ASCII value.
    /// </summary>
    event CharHandler OnChar;

    /// <summary>
    /// Returns true is key is up at the moment.
    /// </summary>
    /// <param name="key">Key to test for.</param>
    /// <returns>True is key is up (not pressed).</returns>
    bool IsUp(Key key);

    /// <summary>
    /// Returns true if key is down at the moment.
    /// </summary>
    /// <param name="key">Key to test for.</param>
    /// <returns>True if key is down (pressed).</returns>
    bool IsDown(Key key);
  }
}
