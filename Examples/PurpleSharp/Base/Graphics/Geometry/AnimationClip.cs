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

using Purple.Player;

namespace Purple.Graphics.Geometry
{
  //=================================================================
  /// <summary>
  /// This class contains information about a certain animation clip.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public class AnimationClip : Clip
	{
    /// <summary>
    /// The start frame.
    /// </summary>
    [Purple.Serialization.Serialize(true)]
    public int From {
      get {
        return from;
      }
      set {
        from = value;
        // HACK
        this.length = Count/30.0f;
      }
    }
    int from;

    /// <summary>
    /// The last frame of the clip.
    /// </summary>
    [Purple.Serialization.Serialize(true)]
    public int To {
      get {
        return to;
      } 
      set {
        to = value;
        // HACK
        this.length = Count/30.0f;
      }
    }
    int to;

    /// <summary>
    /// Number of frames.
    /// </summary>
    public int Count {
      get {
        return to - from + 1;
      }
    }

    /// <summary>
    /// Parameterless constructor for serialization.
    /// </summary>
    private AnimationClip() {}

    /// <summary>
    /// Creates a new clip.
    /// </summary>
    /// <param name="name">The name of the clip.</param>
    /// <param name="from">The start frame.</param>
    /// <param name="to">The end frame of the clip, that isn't played anymore.</param>
    /// <param name="length">Length of the clip in seconds.</param>
		public AnimationClip(string name, int from, int to, float length) : base(name, length, false)
		{
      this.from = from;
      this.to = to;
		}

    /// <summary>
    /// Creates a new clip.
    /// </summary>
    /// <param name="name">The name of the clip.</param>
    /// <param name="from">The start frame.</param>
    /// <param name="to">The end frame of the clip, that isn't played anymore.</param>
    /// <param name="length">Length of the clip in seconds.</param>
    /// <param name="looping">Flag that indicates if clip should be looped.</param>
    public AnimationClip(string name, int from, int to, float length, bool looping) : base( name, length, looping) {
      this.from = from;
      this.to = to;
    }

    /// <summary>
    /// Calculates the blend parameters for blending the animation.
    /// </summary>
    /// <param name="clipTime">Thie current clip time.</param>
    /// <param name="frameIndex">The current frameIndex.</param>
    /// <param name="nextFrameIndex">The next frameIndex.</param>
    /// <param name="blendFactor">The blend factor between the two indices.</param>
    public void CalcBlendParameters(float clipTime, out int frameIndex, out int nextFrameIndex, out float blendFactor) {
      float frameTime = clipTime*Count;
      frameIndex = (int)(this.from + frameTime);
      if (this.Looping) {
        nextFrameIndex = this.from + ((int)frameTime+1)%Count;
        blendFactor = frameTime % 1.0f;
      } else {
        int next = (int)frameTime + 1;
        if (next >= Count) {
          nextFrameIndex = this.from + Count;
          blendFactor = 0.0f;
        }
        else {
          nextFrameIndex = this.from + next;
          blendFactor = frameTime % 1.0f;
        }
      }
    }

    /// <summary>
    /// Calculates the time for a given frame.
    /// </summary>
    /// <param name="frameIndex">The absolute frame index.</param>
    /// <returns>The time for the given frameIndex.</returns>
    public float CalcTime(int frameIndex) {
      // HACK
      return (frameIndex - this.from)/30.0f;
    }
	}
}
