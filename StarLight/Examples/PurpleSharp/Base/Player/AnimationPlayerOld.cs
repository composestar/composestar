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
  /// A specialised player for animating models.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public class AnimationPlayer : ClipPlayer
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the current clip. 
    /// </summary>
    public new AnimationClip Clip {
      get {
        return (AnimationClip)base.Clip;
      }
    }

    /// <summary>
    /// Returns true if the end of the clip is reached.
    /// </summary>
    /// <remarks>
    /// This property returns always false if the clip gets looped.
    /// </remarks>
    public bool EndOfClip {
      get {
        if (Clip.Looping)
          return false;
        return (Clip.From + frameIndex) > Clip.To;
      }
    }

    /// <summary>
    /// Returns the number of frames that should be player in one second.
    /// </summary>
    public float FrameRate {
      get {
        return frameRate;
      }
    }
    float frameRate = 30.0f;

    /// <summary>
    /// Returns the index of the current frame of the current clip.
    /// </summary>
    public int FrameIndex {
      get {
        int index;
        if (Clip.Looping)
          index = Clip.From + (int)frameIndex % Clip.Count;
        else
          index = System.Math.Min(Clip.From + (int)frameIndex, Clip.To);
        return index;
      }
      set {
        int clipIndex = CalcClipIndex(value);
        if (clipIndex == -1)
          throw new ArgumentOutOfRangeException("FrameIndex", "The FrameIndex is out of range!");
        AnimationClip clip = (AnimationClip)Clips[clipIndex];
        ClipIndex = clipIndex;
        Time = (value - clip.From)/FrameRate;
        frameIndex = value - clip.From;
      }
    }
    private float frameIndex;

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

    /// <summary>
    /// Returns the index of the next frame of the current clip.
    /// </summary>
    public int NextFrameIndex {
      get {
        int frames = 1 + (int)frameIndex;
        if (Clip.Looping)
          return Clip.From + frames%Clip.Count;
        else
          return System.Math.Min(Clip.From + frames, Clip.To);
      }
    }

    /// <summary>
    /// The current weight between FrameIndex and NextFrameIndex.
    /// </summary>
    public float Weight {
      get {
        return frameIndex % 1.0f;
      }
    }
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
    /// Returns the current interpolated skeleton.
    /// </summary>
    /// <param name="target">The array to fill with the joint matrices.</param>
    /// <param name="frames">The animation frames.</param>
    public virtual void Interpolate(Matrix4[] target, SkeletonFrame[] frames) {
      // target[i] = Slerp( a[i], b[i], time);
      // Interpolation of prebound joints: 
      // preBoundTarget[i].RM = Slerp( invA[i].RM, invB[i].RM, time);
      // preBoundTarget[i].TV = -pose.TranslationLerp*pBT[i].RM + Lerp( A[i].RM, B[i].RM, time);
      Matrix4[] a = frames[FrameIndex].JointArray;
      Matrix4[] b = frames[NextFrameIndex].JointArray;
      System.Diagnostics.Debug.Assert(target.Length == a.Length && a.Length == b.Length);

      for (int i=0; i<target.Length; i++)    
        target[i] = Matrix4.Slerp( a[i], b[i], Weight);
    }

    /// <summary>
    /// This method must be called every frame.
    /// </summary>
    /// <param name="deltaTime">Time since the last frame.</param>
    public override void OnRender(float deltaTime) {
      base.OnRender(deltaTime);
      frameIndex = this.Time*this.FrameRate;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
