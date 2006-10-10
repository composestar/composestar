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
using System.Collections;

using Purple.Math;

namespace Purple.Graphics.Geometry
{
  //=================================================================
  /// <summary>
  /// One frame of the skeleton.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last change: 0.7</para>
  /// </remarks>
  //=================================================================
  public class SkeletonFrame {
    /// <summary>
    /// Array of joints for this frame.
    /// </summary>
    public Matrix4[] JointArray;

    /// <summary>
    /// Create a new instance of a SkeletonFrame.
    /// </summary>
    /// <param name="jointArray">Joints of the skeleton for this frame.</param>
    public SkeletonFrame(Matrix4[] jointArray) {
      JointArray = jointArray;
    }
  }

  //=================================================================
  /// <summary>
  /// A channel is a certain jointset of a skeleton, which can be 
  /// animated seperately.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Last Update: 0.7</para>
  /// Replaces the old SkeletonAnimation class.
  /// </remarks>
  //=================================================================
  public class Channel {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The native frameRate of the stored skeleton frames.
    /// </summary>
    public int FrameRate {
      get {
        return frameRate;
      }
    }
    int frameRate = 30;

    /// <summary>
    /// The mapping of the channel bones to the original bones.
    /// </summary>
    public int[] Bones {
      get {
        return bones;
      }
    }
    int[] bones;

    /// <summary>
    /// Stores the animated frames of the skeleton.
    /// </summary>
    public SkeletonFrame[] Frames {
      get {
        return frames;
      }
    }
    SkeletonFrame[] frames;

    /// <summary>
    /// The player for animating the skeleton.
    /// </summary>
    public AnimationPlayer Player {
      get {
        return player;
      }
      set {
        player = value;
      }
    }
    AnimationPlayer player;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates an instance of a <see cref="Channel"/>.
    /// </summary>
    /// <param name="frames">Stores the animated frames of the skeleton.</param>
    /// <param name="bones">Mapping of the channel bones to the original bones.</param>
    /// <param name="frameRate">The default frameRate of the animation.</param>
    public Channel(SkeletonFrame[] frames, int[] bones, int frameRate) : 
      this(frames, bones, frameRate, new AnimationPlayer( new AnimationClip[] { new AnimationClip("All", 0, frames.Length-1, (float)frames.Length/frameRate) } )) {
    }

    /// <summary>
    /// Creates an instance of a <see cref="Channel"/>.
    /// </summary>
    /// <param name="frames">Stores the animated frames of the skeleton.</param>
    /// <param name="bones">Mapping of the channel bones to the original bones.</param>
    /// <param name="frameRate">The default frameRate of the animation.</param>
    /// <param name="player">The animation player to use for this channel.</param>
    public Channel(SkeletonFrame[] frames, int[] bones, int frameRate, AnimationPlayer player) {
      this.frames = frames;
      this.frameRate = frameRate;
      this.bones = bones;
      this.player = player;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Interpolates two frames of a channel and stores the result into the target array.
    /// </summary>
    /// <param name="target">The target array.</param>
    /// <param name="from">The first frame.</param>
    /// <param name="to">The second frame.</param>
    /// <param name="factor">The interpolation factor in the range of [0,1].</param>
    public void Blend(Matrix4[] target, int from, int to, float factor) {
      // target[i] = Slerp( a[i], b[i], time);
      // Interpolation of prebound joints: 
      // preBoundTarget[i].RM = Slerp( invA[i].RM, invB[i].RM, time);
      // preBoundTarget[i].TV = -pose.TranslationLerp*pBT[i].RM + Lerp( A[i].RM, B[i].RM, time);
      Matrix4[] a = frames[from].JointArray;
      Matrix4[] b = frames[to].JointArray;
      System.Diagnostics.Debug.Assert(a.Length == b.Length);

      if (bones != null) {
        for (int i=0; i<bones.Length; i++)    
          target[bones[i]] = Matrix4.Slerp( a[i], b[i], factor);
      } else {
        for (int i=0; i<target.Length; i++)    
          target[i] = Matrix4.Slerp( a[i], b[i], factor);
      }
    }

    /// <summary>
    /// CrossInterpolates between two clips. 
    /// </summary>
    /// <param name="target">The target array.</param>
    /// <param name="fromA">The first frame of clip A.</param>
    /// <param name="toA">The target frame of clip A.</param>
    /// <param name="factorA">The time factor between fromA and toA.</param>
    /// <param name="fromB">The first frame of clip B.</param>
    /// <param name="toB">The target frame of clipB.</param>
    /// <param name="factorB">The time factor between fromB and toB.</param>
    /// <param name="factor">The interpolation factor between clip A and B.</param>
    public void Blend(Purple.Math.Matrix4[] target, int fromA, int toA, float factorA,
      int fromB, int toB, float factorB, float factor ) {
      if (target == null)
        return;
      if (factor >= 1.0f - float.Epsilon)
        Blend(target, fromB, toB, factorB);
      else if (factor <= float.Epsilon)
        Blend(target, fromA, toA, factorA);
      else {
        Matrix4[] a = frames[fromA].JointArray;
        Matrix4[] b = frames[toA].JointArray;
        Matrix4[] c = frames[fromB].JointArray;
        Matrix4[] d = frames[toB].JointArray;

        for (int i=0; i<target.Length; i++)
          target[i] = Matrix4.Slerp( Matrix4.Slerp(a[i], b[i], factorA),
            Matrix4.Slerp( c[i], d[i], factorB ), factor);
      }
    }

    /// <summary>
    /// Updates the cue target array with the animated matrices.
    /// </summary>
    /// <param name="target">The target array.</param>
    public void Update(Purple.Math.Matrix4[] target) {
      Blend(target, player.LastFrameIndex, player.LastFrameIndex2, player.LastBlendFactor,
        player.FrameIndex, player.FrameIndex2, player.BlendFactor, player.ClipBlendFactor);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
