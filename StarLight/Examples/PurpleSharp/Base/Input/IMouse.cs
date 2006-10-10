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

using Purple.Math;
using Purple.Graphics.Gui;

namespace Purple.Input {
  //=================================================================
  /// <summary>
  /// Enumeration of all mouse buttons.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>  
  ///   <para>Since: 0.4</para>
  /// </remarks>
  //=================================================================
  public enum MouseButton {
    /// <summary>
    /// The left mouse button.
    /// </summary>
    Left,
    /// <summary>
    /// The right mouse button.
    /// </summary>
    Right,
    /// <summary>
    /// The middle mouse button.
    /// </summary>
    Middle,
    /// <summary>
    /// Mouse button 4.
    /// </summary>
    Button4,
    /// <summary>
    /// Mouse button 5.
    /// </summary>
    Button5,
    /// <summary>
    /// Mouse button 6.
    /// </summary>
    Button6,
    /// <summary>
    /// Mouse button 7.
    /// </summary>
    Button7,
    /// <summary>
    /// Mouse button 8.
    /// </summary>
    Button8,
    /// <summary>
    /// No mouse button was pressed.
    /// </summary>
    None
  }

  //=================================================================
  /// <summary>
  /// Interface that defines a mouse handler.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>  
  ///   <para>Since: 0.4</para>
  /// </remarks>
  //=================================================================
  public interface IMouseHandler {
    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button that is pressed or released.</param>
    /// <param name="pressed">Flag that indicates if button is pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    bool OnMouse(Vector3 position, MouseButton button, bool pressed);
  }

  //=================================================================
  /// <summary>
  /// Interface that defines a keyboard handler.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>  
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public interface IKeyboardHandler {
    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="key">The key, whose status was changed.</param>
    /// <param name="pressed">Flag that indicates if the key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    bool OnKey(Key key, bool pressed);

    /// <summary>
    /// Method that character events.
    /// </summary>
    /// <param name="keyChar">The character that was entered via the keyboard.</param>
    bool OnChar(char keyChar);
  }

  //=================================================================
  /// <summary>
  /// Delegate for handling mouse events.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>  
  ///   <para>Since: 0.4</para>
  /// </remarks>
  //=================================================================
  public delegate void MouseHandler(Vector3 position, MouseButton button, bool pressed);

	//=================================================================
	/// <summary>
	/// Abstract interface for the mouse.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.6</para>
	/// </remarks>
	//=================================================================
	public interface IMouse: IInputDevice
	{
    /// <summary>
    /// Event that is fired in case of mouse events.
    /// </summary>
    event MouseHandler OnMouse;

    /// <summary>
    /// Movement of the mouse.
    /// </summary>
    Vector3 Delta { get; }

    /// <summary>
    /// Returns the position of the mouse.
    /// </summary>
    Vector3 Position { get; set; }

    /// <summary>
    /// Returns true if a certain mouse button is down. 
    /// </summary>
    /// <param name="mouseButton">The mouse button to test for.</param>
    /// <returns>True if mouse button is down.</returns>
    bool IsDown(MouseButton mouseButton);

    /// <summary>
    /// Returns true if a certain mouse button is up. 
    /// </summary>
    /// <param name="mouseButton">The mouse button to test for.</param>
    /// <returns>True if mouse button is up.</returns>
    bool IsUp(MouseButton mouseButton);

    /// <summary>
    /// Returns the number of mouse buttons.
    /// </summary>
    int ButtonNum { get; }

    /// <summary>
    /// Flag that indicates if the mouse position is clamped to the screen.
    /// </summary>
    /// <remarks>The default value of this flag is true.</remarks>
    bool Clamp { get; set; }
    
    /// <summary>
    /// Gets or sets the flag which indicates if the native mouse
    /// cursor should be hidden when inside the window.
    /// </summary>
    bool HideNativeCursor { get; set; }
	}
}
