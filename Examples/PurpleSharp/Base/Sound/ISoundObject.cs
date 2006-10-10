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

namespace Purple.Sound {
  //=================================================================
  /// <summary>
  /// abstract interface for a certain SoundObject (MOD, WAV, MP3, MIDI, CD, ...)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public interface ISoundObject : IDisposable {

    //---------------------------------------------------------------
    #region Stuff
    //---------------------------------------------------------------
    /// <summary>
    /// plays a certain soundObject from it's current position
    /// </summary>
    /// <exception cref="SoundException">if music can't be played</exception>
    void Play();

		/// <summary>
		/// Stops playing of a certain soundObject
		/// </summary>
    void Stop();

    /// <summary>
    /// Pause or unpause the sound
    /// </summary>
    /// <param name="paused">flag indicating of sound should be paused</param>
    void Pause(bool paused);

    /// <summary>
    /// returns true if song is paused
    /// </summary>
    bool Paused { get; }

    /// <summary>
    /// Returns true if the sound object is currently playing.
    /// </summary>
    /// <returns></returns>
    bool IsPlaying();

    /// <summary>
    /// Gets or sets the stereo panning value of this sound.
    /// </summary>
    /// <value>A value between -1.0 (left) and 1.0 (right), </value>
    float Pan { get; set; }
    
    /// <summary>
    /// Gets or sets the volume value of this sound.
    /// </summary>
    /// <value>A value between 0.0 (silent) and 1.0f (full volume), </value>
    float Volume { get; set; }

    /// <summary>
    /// Returns the current channel, the sound object belongs to.
    /// </summary>
    Channel Channel { get; set; }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
