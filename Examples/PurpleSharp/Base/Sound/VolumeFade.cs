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

using Purple.Actions;

namespace Purple.Sound
{
  //=================================================================
  /// <summary>
  /// Class for fading the volume of a certain ISoundObject.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
	public class VolumeFade : ActionBase
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    ISoundObject soundObject;

    /// <summary>
    /// The volume at the beginning of the action.
    /// </summary>
    public float StartVolume {
      get {
        return startVolume;
      }
      set {
        startVolume = value;
      }
    }
    float startVolume;


    /// <summary>
    /// The volume at the end of the action.
    /// </summary>
    public float EndVolume {
      get {
        return endVolume;
      }
      set {
        endVolume = value;
      }
    }
    float endVolume;

    /// <summary>
    /// Flag that indicates if playing of the soundObject should be stopped at the end.
    /// </summary>
    public bool StopSound {
      get {
        return stopSound;
      }
      set {
        stopSound = value;
      }
    }
    bool stopSound;

    /// <summary>
    /// Returns the total time of the <see cref="IAction"/>.
    /// </summary>
    public override float TotalTime {
      get {
        return totalTime;
      }
    }
    float totalTime = 0.0f;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new VolumeFade action object.
    /// </summary>
    /// <param name="soundObject">The soundObject to fade.</param>
    /// <param name="startVolume">The start volume.</param>
    /// <param name="endVolume">The target volume.</param>
    /// <param name="time">The fading time.</param>
    /// <param name="stopSound">Flag that indicates if playing of the soundObject should be stopped at the end.</param>
    public VolumeFade(ISoundObject soundObject, float startVolume, float endVolume, float time, bool stopSound) : base("VolumeFadeAction") {
      this.totalTime = time;
      this.soundObject = soundObject;
      this.startVolume = startVolume;
      this.endVolume = endVolume;
      this.totalTime = time;
      this.stopSound = stopSound;
    }

    /*/// <summary>
    /// Creates a new VolumeFade action object. (fade out constructor)
    /// </summary>
    /// <param name="soundObject">The soundObject to fade.</param>
    /// <param name="time">The start volume.</param>
    public VolumeFadeAction(ISoundObject soundObject, float time) : this(soundObject, soundObject.Volume, 0.0f, time, true) {
    }*/

    /// <summary>
    /// Creates a VolumeFade action for fading in a certain soundObject.
    /// </summary>
    /// <param name="soundObject">The soundObject to fade in.</param>
    /// <param name="time">The fading time.</param>
    public static VolumeFade FadeIn(ISoundObject soundObject, float time) {
      VolumeFade fade = new VolumeFade( soundObject, 0, soundObject.Volume, time, false);
      soundObject.Volume = 0.0f;
      soundObject.Play();
      return fade;
    }

    /// <summary>
    /// Creates a VolumeFade action for fading in a certain soundObject.
    /// </summary>
    /// <param name="soundObject">The soundObject to fade in.</param>
    /// <param name="time">The fading time.</param>
    /// <param name="endVolume">The destination volume.</param>
    public static VolumeFade FadeIn(ISoundObject soundObject, float time, float endVolume) {
      VolumeFade fade = new VolumeFade( soundObject, 0, endVolume, time, false);
      soundObject.Volume = 0.0f;
      soundObject.Play();
      return fade;
    }

    /// <summary>
    /// Creates a VolumeFade action for fading out a certain soundObject.
    /// </summary>
    /// <param name="soundObject">The soundObject to fade out.</param>
    /// <param name="time">The fading time.</param>
    public static VolumeFade FadeOut(ISoundObject soundObject, float time) {
      VolumeFade fade = new VolumeFade( soundObject, soundObject.Volume, 0, time, true);
      return fade;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// The method that is called on every update.
    /// </summary>
    /// <param name="t">The time in the range of [0..1].</param>
    protected override void HandleUpdate(float t) {
      soundObject.Volume = startVolume + (endVolume - startVolume)*t;
    }

    /// <summary>
    /// Finishe the current action.
    /// </summary>
    /// <remarks>
    /// The actions time is moved to one. Then it is updated and the the FinishedEvent 
    /// is fired.
    /// </remarks>
    protected override void OnFinish() {
      if (this.stopSound) {
        soundObject.Stop();
        soundObject.Volume = startVolume;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
