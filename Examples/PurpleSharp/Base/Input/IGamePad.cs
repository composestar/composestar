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

using Purple.Input.ForceFeedback;

namespace Purple.Input {
  //=================================================================
  /// <summary>
  /// Abstract interface for a gamePad.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public interface IGamePad : IInputDevice {
    /// <summary>
    /// Returns true if a certain button is down.
    /// </summary>
    /// <param name="button">The button to test for.</param>
    /// <returns>True if button is down.</returns>
    bool IsDown(int button);

    /// <summary>
    /// Returns true if a certain button is up. 
    /// </summary>
    /// <param name="button">The button to test for.</param>
    /// <returns>True if button is up.</returns>
    bool IsUp(int button);

    /// <summary>
    /// Returns the angles of the point of view controls of the gamePad or float.NAN if they are centered.
    /// </summary>
    float[] PointOfViews { get; }

    /// <summary>
    /// Returns the positions of the axes of the gamePad.
    /// </summary>
    float[] Axes { get; }

    /// <summary>
    /// Available force feedback effects.
    /// </summary>
    IEffect[] Effects { get; }

    /// <summary>
    /// Searches for an effect with a certain type.
    /// </summary>
    /// <param name="type">Type of effect to search for.</param>
    /// <returns>The first effect with a certain type or null.</returns>
    IEffect FindEffect( Type type);

    /// <summary>
    /// Returns true if the device supports force feedback.
    /// </summary>
    bool SupportsForceFeedback { get; }
  }
}