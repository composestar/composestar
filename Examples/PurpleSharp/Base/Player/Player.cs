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
  /// A simple class that implements the time related methods 
  /// of a multimedia player.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  ///   <para>Last Update: 0.7</para>
  /// This class may be used to calculate the current time for a song, 
  /// animation or whatever.
  /// </remarks>
  //=================================================================
	public class Player : IPlayer
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the current animation time.
    /// </summary>
    public float Time {
      get {
        return time;
      }
      set {
        time = value;
      }
    }
    float time;

    /// <summary>
    /// Flag that indicates if the position, time, clip, ... of the player has changed.
    /// </summary>
    protected bool dirty;

    /// <summary>
    /// Current state of the <see cref="IPlayer"/> object.
    /// </summary>
    public PlayerState State {
      get {
        return state;
      }
    }
    /// <summary>
    /// Current state of the <see cref="IPlayer"/> object.
    /// </summary>
    [System.CLSCompliant(false)]
    protected PlayerState state;

    /// <summary>
    /// Play speed of the animation.
    /// </summary>
    public float Speed {
      get {
        return speed;
      }
      set {
        speed = value;
      }
    }
    float speed = 1.0f;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="Player"/> object.
    /// </summary>
		public Player()
		{
      state = PlayerState.Stopped;
      time = 0.0f;
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Stops playing a certain animation.
    /// </summary>
    public void Stop() {
      state = PlayerState.Stopped;
      time = 0.0f;
      dirty = true;
    }

    /// <summary>
    /// Starts playing a certain animation.
    /// </summary>
    public void Play() {
      state = PlayerState.Playing;
    }

    /// <summary>
    /// Pauses a certain animation.
    /// </summary>
    public void Pause() {
      state = PlayerState.Paused;
    }

    /// <summary>
    /// Updates the player.
    /// </summary>
    /// <param name="deltaTime">The time passed since the last frame.</param>
    public virtual void Update(float deltaTime) {
      if (state == PlayerState.Playing) {
        time += deltaTime*speed;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
