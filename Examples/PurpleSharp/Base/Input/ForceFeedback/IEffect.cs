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

namespace Purple.Input.ForceFeedback {
  //=================================================================
  /// <summary>
  /// Abstract interface for a force feedback effect.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public interface IEffect {
    /// <summary>
    /// Name of the effect.
    /// </summary>
    string Name { get; }

    /// <summary>
    /// The duration of the effect.
    /// </summary>
    /// <remarks>The total duration of the effect, in microseconds. 
    /// If this value is INFINITE, the effect has infinite duration. 
    /// If an envelope has been applied to the effect, the attack is applied, followed by an infinite sustain. </remarks>
    float Duration { get; set; }
    /// <summary>
    /// The gain to be applied to the effect, in the range from 0.0f through 1.0f; 
    /// The gain is a scaling factor applied to all magnitudes of the effect and its envelope. 
    /// </summary>
    float Gain { get; set; }
    /// <summary>
    /// Returns the number of force feedback axes of the effect.
    /// </summary>
    int AxesCount { get; }

    /// <summary>
    /// Starts playing an effect.
    /// </summary>
    /// <param name="iterations">Number of times to play the effect.</param>
    void Start(int iterations);
    /// <summary>
    /// Stops playing an effect;
    /// </summary>
    void Stop();
    /// <summary>
    /// Returns true if the effect is currently playing.
    /// </summary>
    bool IsPlaying { get; }
    /// <summary>
    /// Sets the direction of the effect for a certain axis.
    /// </summary>
    /// <param name="index">The index of the axis.</param>
    /// <param name="dir">The direction of the axis.</param>
    void SetDirection(int index, float dir);
  }

  //=================================================================
  /// <summary>
  /// Constant force effect.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public interface IConstantEffect : IEffect {
    /// <summary>
    /// The magnitude of the effect, in the range from –1.0f through 1.0f 
    /// If an envelope is applied to this effect, the value represents the magnitude of the sustain. 
    /// If no envelope is applied, the value represents the amplitude of the entire effect. 
    /// </summary>
    float Magnitude { get; set; }
  }

  //=================================================================
  /// <summary>
  /// Ramp force effect.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// The <c>Duration</c> of an IRampEffect mustn't be infinte!
  /// </remarks>
  //=================================================================
  public interface IRampEffect: IEffect {
    /// <summary>
    /// Magnitude at the start of the effect, in the range from –1.0f through 1.0f. 
    /// </summary>
    float Begin { get; set; }
    /// <summary>
    /// Magnitude at the end of the effect, in the range from –1.0f through 1.0f.
    /// </summary>
    float End { get; set; }
  }

  //=================================================================
  /// <summary>
  /// Periodic force effect.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public interface IPeriodicEffect : IEffect {
    /// <summary>
    /// The magnitude of the effect, in the range from –1.0f through 1.0f 
    /// If an envelope is applied to this effect, the value represents the magnitude of the sustain. 
    /// If no envelope is applied, the value represents the amplitude of the entire effect. 
    /// </summary>
    float Magnitude { get; set; }    
    /// <summary>
    /// Offset of the effect. The range of forces generated by the effect is Offset minus Magnitude to Offset plus Magnitude.
    /// The value of the lOffset member is also the baseline for any envelope that is applied to the effect. 
    /// </summary>
    float Offset { get; set; }
    /// <summary>
    /// Position in the cycle of the periodic effect at which playback begins, in the range from 0 through 2 Pi.
    /// </summary>
    /// <remarks>
    /// A device driver cannot provide support for all values in the Phase member. 
    /// In this case, the value is rounded off to the nearest supported value.
    /// </remarks>
    float Phase { get; set; }
    /// <summary>
    /// Period of the effect, in seconds. 
    /// </summary>
    float Period { get; set; }
  }
}