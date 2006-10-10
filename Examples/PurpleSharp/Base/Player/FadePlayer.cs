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
using Purple.Player;

namespace Purple.Graphics.Geometry
{
  //=================================================================
  /// <summary>
  /// This is a class for fading two AnimationPlayers.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// Perhaps I will change the FadePlayer to support fading all kind 
  /// of players later. ;-/
  /// </remarks>
  //=================================================================
	public class FadePlayer : IClipPlayer
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The first player.
    /// </summary>
    public AnimationPlayer PlayerA {
      get {
        return playerA;
      }
    }
    AnimationPlayer playerA;

    /// <summary>
    /// The second player.
    /// </summary>
    public AnimationPlayer PlayerB {
      get {
        return playerB;
      }
    }
    AnimationPlayer playerB;

    /// <summary>
    /// The time to switch/interpolate from one clip to another. 
    /// </summary>
    public float SwitchTime {
      get {
        return switchTime;
      }
      set {
        switchTime = value;
      }
    }
    float switchTime = 0.15f;

    /// <summary>
    /// Returns the index of the current played clip.
    /// </summary>
    public int ClipIndex {
      get {
        return playerA.ClipIndex;
      }
      set {
        playerB.ClipIndex = playerA.ClipIndex;
        playerB.Time = playerA.Time;
        playerA.ClipIndex = value;
        playerA.Time = 0.0f;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an <see cref="FadePlayer"/>.
    /// </summary>
    public FadePlayer(AnimationClip[] clips) {
      playerA = new AnimationPlayer(clips);
      playerB = new AnimationPlayer(clips);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Resets the old player values equal to the current player values.
    /// </summary>
    public void Align() {
      playerB.ClipIndex = playerA.ClipIndex;
      playerB.Time = playerA.Time;
    }

    /// <summary>
    /// Returns the current interpolated skeleton.
    /// </summary>
    /// <param name="target">The array to fill with the joint matrices.</param>
    /// <param name="frames">The animation frames.</param>
    public void Interpolate(Purple.Math.Matrix4[] target, SkeletonFrame[] frames) {
      if (target == null)
        return;
      float switchWeight = Time / switchTime;
      if (playerB.ClipIndex == playerA.ClipIndex || switchWeight >= 1.0f)
        playerA.Interpolate(target, frames);
      else {
        Matrix4[] a = frames[playerA.FrameIndex].JointArray;
        Matrix4[] b = frames[playerA.NextFrameIndex].JointArray;
        Matrix4[] c = frames[playerB.FrameIndex].JointArray;
        Matrix4[] d = frames[playerB.NextFrameIndex].JointArray;

        switchWeight = System.Math.Min(switchWeight, 1.0f);

        for (int i=0; i<target.Length; i++)
          //target[i] = Matrix4.Slerp( c[i], a[i], switchWeight);
          target[i] = Matrix4.Slerp( Matrix4.Slerp( c[i], d[i], playerB.Weight ),
            Matrix4.Slerp(a[i], b[i], playerA.Weight), switchWeight);
      }
    }

    /// <summary>
    /// This method must be called every frame.
    /// </summary>
    /// <param name="deltaTime">Time since the last frame.</param>
    public void OnRender(float deltaTime) {
      playerA.OnRender(deltaTime);
      playerB.OnRender(deltaTime);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------  

    //---------------------------------------------------------------
    #region IClipPlayer Members
    //---------------------------------------------------------------
    /// <summary>
    /// Moves to the last clip.
    /// </summary>
    public void PrevClip() {
      ClipIndex = (ClipIndex + Clips.Length - 1) % Clips.Length;
    }

    /// <summary>
    /// Moves to the next clip.
    /// </summary>
    public void NextClip() {
      ClipIndex = (ClipIndex + 1) % Clips.Length;
    }

    /// <summary>
    /// Returns the clip list.
    /// </summary>
    public IClip[] Clips {
      get {
        return playerA.Clips;
      }
    }

    /// <summary>
    /// Returns the current clip of playerA.
    /// </summary>
    public IClip Clip {
      get {
        return playerA.Clip;
      }
    }

    /// <summary>
    /// The time of the current clip of playerA.
    /// </summary>
    public float ClipTime {
      get {
        return playerA.ClipTime;
      }
    }

    /// <summary>
    /// Returns the clip for a certain name.
    /// </summary>
    /// <param name="name">Name of the clip.</param>
    /// <returns>The clip with the given name.</returns>
    public IClip FindClip(string name) {
      return playerA.FindClip(name);
    }

    /// <summary>
    /// Returns the index of a certain clip.
    /// </summary>
    /// <param name="name">The name of the index to get index for.</param>
    /// <returns>The index of a certain clip.</returns>
    public int GetIndexOf(string name) {
      return playerA.GetIndexOf(name);
    }

    /// <summary>
    /// Sets the time of playerA.
    /// </summary>
    public float Time {
      get {
        return playerA.Time;
      }
      set {
        playerA.Time = value;
      }
    }

    /// <summary>
    /// Returns the state of playerA.
    /// </summary>
    public Purple.Player.PlayerState State {
      get {
        return playerA.State;
      }
    }

    /// <summary>
    /// Set the speed of both players or return the speed of player A.
    /// </summary>
    public float Speed {
      get {
        return playerA.Speed;
      }
      set {
        playerA.Speed = value;
        playerB.Speed = value;
      }
    }

    /// <summary>
    /// Stop both players.
    /// </summary>
    public void Stop() {
      playerA.Stop();
      playerB.Stop();
    }

    /// <summary>
    /// Player both players.
    /// </summary>
    public void Play() {
      playerA.Play();
      playerB.Play();
    }

    /// <summary>
    /// Pause both players.
    /// </summary>
    public void Pause() {
      playerA.Pause();
      playerB.Pause();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
