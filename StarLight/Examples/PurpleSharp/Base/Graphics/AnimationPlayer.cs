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

namespace Purple.Graphics.Geometry {
  //=================================================================
  /// <summary>
  /// A specialised player for animating models.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class AnimationPlayer : ClipPlayer {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    int lastClipIndex = -1;
    float lastTime = 0.0f;
    float currentFadeTime = 0.0f;

    /// <summary>
    /// Returns the current AnimationClip.
    /// </summary>
    public AnimationClip AnimationClip {
      get {
        return (AnimationClip)Clip;
      }
    }

    /// <summary>
    /// The frameIndex of the last clip.
    /// </summary>
    public int LastFrameIndex {
      get {
        return lastFrameIndex;
      }
    }
    int lastFrameIndex = 0;

    /// <summary>
    /// The next frameIndex of the last clip.
    /// </summary>
    public int LastFrameIndex2 {
      get {
        return lastFrameIndex2;
      }
    }
    int lastFrameIndex2 = 0;

    /// <summary>
    /// The blend factor of the last clip.
    /// </summary>
    public float LastBlendFactor {
      get {
        return lastBlendFactor;
      }
    }
    float lastBlendFactor = 0.0f;

    /// <summary>
    /// The frameIndex of the current clip.
    /// </summary>
    public int FrameIndex {
      get {
        return frameIndex;
      }
    }
    int frameIndex = 0;

    /// <summary>
    /// The next frameIndex of the current clip.
    /// </summary>
    public int FrameIndex2 = 0;

    /// <summary>
    /// The blend factor of the current clip.
    /// </summary>
    public float BlendFactor = 0.0f;

    /// <summary>
    /// The blend factor between the last and the current clip.
    /// </summary>
    public float ClipBlendFactor = 1.0f;

    /// <summary>
    /// The time in seconds to fade from one clip to another.
    /// </summary>
    public float FadeTime = 0.10f;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an <see cref="AnimationPlayer"/>.
    /// </summary>
    public AnimationPlayer(AnimationClip[] clips) : base(clips) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------


    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Fades to the clip with a given index.
    /// </summary>
    /// <param name="clipIndex">The index to fade to.</param>
    public void Fade(int clipIndex) {
      if (this.ClipTime >= 0.99f)
        SetClip(clipIndex);
      else if (this.currentFadeTime > this.FadeTime*0.50f) {
        lastClipIndex = this.clipIndex;
        this.lastTime = this.Time;
      }
      this.clipIndex = clipIndex;
      this.Time = 0.0f;
      this.currentFadeTime = 0.0f;
    }

    /// <summary>
    /// Set the clip with a given index.
    /// </summary>
    /// <param name="clipIndex">The clip to set.</param>
    public void SetClip(int clipIndex) {
      this.clipIndex = clipIndex;
      this.lastClipIndex = -1;
      this.Time = 0.0f;
      dirty = true;
    }

    /// <summary>
    /// Sets a frame with a given index.
    /// </summary>
    /// <param name="frameIndex">The frame to set.</param>
    public void SetFrame(int frameIndex) {
      SetClip( CalcClipIndex(frameIndex) );
      this.Time = AnimationClip.CalcTime( frameIndex );
    }

    /// <summary>
    /// Updates the player.
    /// </summary>
    /// <param name="deltaTime">The time passed since the last frame.</param>
    public override void Update(float deltaTime) {
      base.Update (deltaTime);
      if (state == PlayerState.Playing || dirty) {
        AnimationClip clip = AnimationClip;
        this.currentFadeTime += deltaTime*Speed;
        this.lastTime += deltaTime*Speed;
        clip.CalcBlendParameters(this.ClipTime, out frameIndex, out this.FrameIndex2, out this.BlendFactor);      
        if (lastClipIndex == -1 || this.currentFadeTime >= this.FadeTime) {
          ClipBlendFactor = 1.0f;
        }
        else {
          AnimationClip lastClip = (AnimationClip)this.Clips[lastClipIndex];
          lastClip.CalcBlendParameters( this.CalcClipTime(this.lastTime, lastClip), out lastFrameIndex, 
            out lastFrameIndex2, out lastBlendFactor);
          ClipBlendFactor = this.currentFadeTime / this.FadeTime;        
        }
        dirty = false;
      }
    }

    /// <summary>
    /// Calculates the clipIndex from a given frameIndex.
    /// </summary>
    /// <param name="frameIndex">The frameIndex to calculate the clipIndex for.</param>
    /// <returns>The clipIndex or -1 if the frameIndex was invalid.</returns>
    public int CalcClipIndex(int frameIndex) {
      for (int i=0; i<Clips.Length; i++) {
        AnimationClip clip = (AnimationClip)Clips[i];
        if (clip.From <= frameIndex && clip.To >= frameIndex)
          return i;
      }
      return -1;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
