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

namespace Purple.Player
{
  //=================================================================
  /// <summary>
  /// A simple clip.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
	public class Clip
	{
    /// <summary>
    /// The name of the clip.
    /// </summary>
    [Purple.Serialization.Serialize(true)]
    public string Name {
      get {
        return name;
      }
      set {
        name = value;
      }
    }
    string name;

    /// <summary>
    /// Returns the duration of the clip.
    /// </summary>
    public float Length { 
      get {
        return length;
      }
    }
    /// <summary>
    /// The duration of the clip.
    /// </summary>
    [System.CLSCompliant(false)]
    protected float length = 1.0f;

    /// <summary>
    /// Returns true if the clip should be looped.
    /// </summary>
    [Purple.Serialization.Serialize(true)]
    public bool Looping { 
      get {
        return looping;
      }
      set {
        looping = value;
      }
    }
    bool looping;

    /// <summary>
    /// Parameterless constructor for serialization.
    /// </summary>
    protected Clip() {}

    /// <summary>
    /// Creates a new clip.
    /// </summary>
    /// <param name="name">Name of the clip.</param>
    /// <param name="length">Length of the clip in seconds.</param>
    /// <param name="looping">Flag that indicates if the clip should be looped.</param>
    public Clip(string name, float length, bool looping) {
      this.name = name;
      this.length = length;
      this.looping = looping;
    }
	}
}
