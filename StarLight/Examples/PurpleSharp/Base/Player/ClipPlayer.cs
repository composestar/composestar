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
  /// A simple class implementing the <see cref="IClipPlayer"/> interface.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  ///   <para>Last Change: 0.7</para>
  /// <seealso cref="IPlayer"/>
  /// </remarks>
  //=================================================================
  public class ClipPlayer : Player, IClipPlayer {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The index of the current played clip.
    /// </summary>
    public virtual int ClipIndex { 
      get {
        return clipIndex;
      }
    }
    /// <summary>
    /// The index of the current played clip.
    /// </summary>
    [System.CLSCompliant(false)]
    protected int clipIndex;

    /// <summary>
    /// The array of <see cref="Clip"/>s.
    /// </summary>
    public Clip[] Clips {
      get {
        return clips;
      }
    }
    Clip[] clips;

    /// <summary>
    /// Returns the current clip.
    /// </summary>
    public Clip Clip {
      get {
        return clips[ClipIndex];
      }
    }

    /// <summary>
    /// Returns the position within the clip as a float in the range of [0,1]. 
    /// </summary>
    public float ClipTime {
      get {
        return CalcClipTime(Time, Clip);
      }
    }

    /// <summary>
    /// Returns the time left of the current clip.
    /// </summary>
    public float TimeLeft {
      get {
        if (Clip.Looping)
          return float.MaxValue;
        return Clip.Length - Time;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of <see cref="ClipPlayer"/>.
    /// </summary>
    /// <param name="clips">The array of clips to initialize with.</param>
    public ClipPlayer(Clip[] clips) {
      this.clips = clips;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Moves to the previous clip.
    /// </summary>
    public void PrevClip() {    
      if (clips.Length == 0)
        return;
      clipIndex = (ClipIndex + clips.Length - 1) % clips.Length;  
      this.Time = 0.0f;
      dirty = true;
    }

    /// <summary>
    /// Advances to the next clip.
    /// </summary>
    public void NextClip() {
      if (clips.Length == 0)
        return;
      clipIndex = (ClipIndex + 1) % clips.Length;
      this.Time = 0.0f;
      dirty = true;
    }

    /// <summary>
    /// Returns a clip for a certain name.
    /// </summary>
    /// <param name="name">Name of the clip.</param>
    /// <returns>The matching clip or null.</returns>
    public Clip FindClip(string name) {
      for (int i=0; i<clips.Length; i++)
        if (clips[i].Name == name)
          return clips[i];
      return null;
    }

    /// <summary>
    /// Returns the index of a certain clip.
    /// </summary>
    /// <param name="name">The name of the clip.</param>
    /// <returns>The index of the clip or -1.</returns>
    public int GetIndexOf(string name) {
      for (int i=0; i<clips.Length; i++)
        if (clips[i].Name == name)
          return i;
      return -1;
    }

    /// <summary>
    /// Caluclates the relative clipTime in the range of [0..1].
    /// </summary>
    /// <param name="t">The time in seconds.</param>
    /// <param name="clip">The clip to use.</param>
    /// <returns></returns>
    public float CalcClipTime(float t, Clip clip) {
      float time = t / clip.Length;
      if (clip.Looping)
        time = time % 1.0f;
      else
        time = Purple.Math.Basic.Clamp(time, 0.0f, 1.0f);
      return (float)time;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
