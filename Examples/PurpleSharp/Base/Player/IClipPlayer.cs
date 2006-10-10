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

namespace Purple.Player {
  //=================================================================
  /// <summary>
  /// An abstract interface that defines multi clip functionality for 
  /// a multimedia player.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  ///   <para>Last Update: 0.7</para>
  /// <seealso cref="IPlayer"/>
  /// </remarks>
  //=================================================================
  public interface IClipPlayer : IPlayer {
    /// <summary>
    /// Moves to the previous clip.
    /// </summary>
    void PrevClip();

    /// <summary>
    /// Advances to the next clip.
    /// </summary>
    void NextClip();

    /// <summary>
    /// The index of the current clip.
    /// </summary>
    int ClipIndex { get; }

    /// <summary>
    /// The collection of <see cref="Clip"/>s.
    /// </summary>
    Clip[] Clips {get;}

    /// <summary>
    /// Returns the current clip.
    /// </summary>
    Clip Clip {get;}

    /// <summary>
    /// Returns the position within the clip as a float in the range of [0,1].
    /// </summary>
    float ClipTime {get;}

    /// <summary>
    /// Returns a clip for a certain name.
    /// </summary>
    /// <param name="name">Name of the clip.</param>
    /// <returns>The matching clip or null.</returns>
    Clip FindClip(string name);

    /// <summary>
    /// Returns the index of a certain clip.
    /// </summary>
    /// <param name="name">The name of the clip.</param>
    /// <returns>The index of the clip or -1.</returns>
    int GetIndexOf(string name);
  }
}