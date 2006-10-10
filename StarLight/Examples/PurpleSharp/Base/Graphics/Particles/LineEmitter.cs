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

namespace Purple.Graphics.Particles {
  //=================================================================
  /// <summary>
  /// An emitter that emits particles on a given line.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public class LineEmitter : SimpleEmitter {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The start position of the line.
    /// </summary>
    public Vector3 Start {
      get {
        return start;
      }
      set {
        start = value;
      }
    }
    Vector3 start;

    /// <summary>
    /// The end position of the line.
    /// </summary>
    public Vector3 End {
      get {
        return end;
      }
      set {
        end = value;
      }
    }
    Vector3 end;

    /// <summary>
    /// Flag that indicates if the particle position should be a random position on the line 
    /// or if false, the frameTime parameter should be used for linear interpolation.
    /// </summary>
    public bool Random {
      get {
        return random;
      }
      set {
        random = value;
      }
    }
    bool random = false;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	     
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="LineEmitter"/>.
    /// </summary>
    /// <param name="defaultParticle">The particle that is cloned and emitted.</param>
    public LineEmitter(IParticle defaultParticle) : base(defaultParticle) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new particle.
    /// </summary>
    /// <param name="frameTime">The factor that indicates at which time during the frame, the 
    /// particle gets be emitted. (For interpolation purposes)</param>
    protected override IParticle CreateParticle(float frameTime) {
      IParticle particle = base.CreateParticle(frameTime);
      if (random)
        Particle.SetPosition(particle, start + (end-start)*(float)Engine.Instance.Random.NextDouble());
      else
        Particle.SetPosition(particle, start + (end-start)*frameTime);        
      return particle;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
