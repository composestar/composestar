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
  /// <summary>
  /// Delegate that may be used to modify particles before they get emitted.
  /// </summary>
  /// <param name="particle">The particle to emit.</param>
  /// <param name="frameTime">The factor that indicates at which time during the frame, the 
  /// particle gets be emitted. (For interpolation purposes)</param>
  public delegate void EmitHandler(IParticle particle, float frameTime);

  //=================================================================
  /// <summary>
  /// An emitter that emits cloned defaultParticle elements.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// This emitter is intended to be used as base emitter class. It clones the defaultParticle, that 
  /// may be modified by the inherited emitter and than emitted.
  /// </remarks>
  //=================================================================
	public class SimpleEmitter : IParticleEmitter
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    float time;

    /// <summary>
    /// Flag that indicates if the emitter is enabled.
    /// </summary>
    public bool Enabled { 
      get {
        return enabled;
      }
      set {
        enabled = value;
      }
    }
    bool enabled = true;

    /// <summary>
    /// The particle that is cloned than modified and emitted.
    /// </summary>
    public IParticle DefaultParticle { 
      get {
        return defaultParticle;
      }
      set {
        defaultParticle = value;
      }
    }
    IParticle defaultParticle = null;

    /// <summary>
    /// Number of particles that should be emitted per second or frame depending if the 
    /// <c>TimeBased</c> flag is set true.
    /// </summary>
    public float ParticleCount {
      get {
        return particleCount;
      }
      set {
        particleCount = value;
      }
    }
    float particleCount = 1;

    /// <summary>
    /// Flag that indicates if <c>ParticleCount</c> particles should be emitted per second 
    /// or per frame.
    /// </summary>
    public bool TimeBased {
      get {
        return timeBased;
      }
      set {
        timeBased = value;
      }
    }
    bool timeBased = true;

    /// <summary>
    /// Event that gets fired when a new particle gets emitted.
    /// </summary>
    public EmitHandler OnEmit;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="SimpleEmitter"/>.
    /// </summary>
    /// <param name="defaultParticle">The particle that is cloned and emitted.</param>
    public SimpleEmitter(IParticle defaultParticle) {
      this.defaultParticle = defaultParticle;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Updates the particle emitter object.
    /// </summary>
    /// <param name="particleSystem">The particleSystem that manages the emitted particles.</param>
    /// <param name="deltaTime">The time since the last frame.</param>
    public virtual void Update(IParticleSystem particleSystem, float deltaTime) {
      if (enabled) {
        if (timeBased)
          EmitTimeParticles(particleSystem, deltaTime);
        else
          EmitFrameParticles(particleSystem, deltaTime);
      }
    }

    private void EmitTimeParticles(IParticleSystem particleSystem, float deltaTime) {
      float timePerParticle = 1.0f/particleCount;
      while(timePerParticle + time <= deltaTime) {
        time += timePerParticle;
        IParticle particle = CreateParticle( time / deltaTime );
        particle.Age = deltaTime - time;
        Emit(particleSystem, particle, time / deltaTime);
      }
      time -= deltaTime;
    }

    private void EmitFrameParticles(IParticleSystem particleSystem, float deltaTime) {
      float framesPerParticle = 1.0f/particleCount;
      while(framesPerParticle + time <= 1.0f) {
        time += framesPerParticle;
        IParticle particle = CreateParticle( time );
        Emit(particleSystem, particle, time);
      }
      time -= 1.0f;
    }

    /// <summary>
    /// Creates a new particle.
    /// </summary>
    /// <param name="frameTime">The factor that indicates at which time during the frame, the 
    /// particle gets emitted. (For interpolation purposes)</param>
    protected virtual IParticle CreateParticle(float frameTime) {
      return defaultParticle.Clone();
    }

    /// <summary>
    /// Emits the particle.
    /// </summary>
    /// <param name="particleSystem">The particleSystem that manages the emitted particles.</param>
    /// <param name="particle">Particle to emit.</param>
    /// <param name="frameTime">The factor that indicates at which time during the frame, the 
    /// particle gets emitted. (For interpolation purposes)</param>
    protected void Emit(IParticleSystem particleSystem, IParticle particle, float frameTime) {
      if (OnEmit != null)
        OnEmit(particle, frameTime);
      particleSystem.Emit(particle);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
