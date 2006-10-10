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
using Purple.Graphics;
using Purple.Graphics.Core;
using Purple.Graphics.Effect;
using Purple.Graphics.VertexStreams;
using Purple.Collections;

namespace Purple.Graphics.Particles {
  //=================================================================
  /// <summary>
  /// A base class for an <see cref="IParticleSystem"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
  public abstract class ParticleSystemBase : IParticleSystem {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The collection containing the particles.
    /// </summary>
    protected IList particles;

    /// <summary>
    /// The effect used for the particle system.
    /// </summary>
    public IEffect Effect {
      get {
        return effect;
      }
      set {
        effect = value;
      }
    }
    IEffect effect;

    /// <summary>
    /// The affectors that affect the particle system.
    /// </summary>
    public ArrayList Affectors {
      get {
        return affectors;
      }
    }
    ArrayList affectors = new ArrayList();

    /// <summary>
    /// A list of <see cref="IParticleEmitter"/> objects that emit particles.
    /// </summary>
    public ArrayList Emitters {
      get {
        return emitters;
      }
    }
    ArrayList emitters = new ArrayList();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a particle system.
    /// </summary>
    public ParticleSystemBase( ) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Updates the particle system.
    /// </summary>
    /// <param name="deltaTime">The time since the last update.</param>
    public virtual void Update(float deltaTime) {

      UpdateAge(deltaTime);
      RemoveDeadParticles();
      UpdateParticles(deltaTime);
      EmitParticles(deltaTime);

    }

    /// <summary>
    /// Update the age of the particles.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    protected void UpdateAge(float deltaTime) {
      for (int i=0; i<particles.Count; i++) {
        (particles[i] as IParticle).Age += deltaTime;
      }
    }

    /// <summary>
    /// Remove all particles that have have reached their maximum age.
    /// </summary>
    protected void RemoveDeadParticles() {
      int j=particles.Count - 1;
      while (j >= 0) {
        if (!(particles[j] as IParticle).IsAlive)
          particles.RemoveAt(j);
        j--;
      }
    }

    /// <summary>
    /// Apply all affectors.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    protected void UpdateParticles(float deltaTime) {
      // update particles
      for (int i=0; i<affectors.Count; i++) {
        IParticleAffector affector = (IParticleAffector)affectors[i];
        affector.Affect(particles, particles.Count, deltaTime);
      }
    }

    /// <summary>
    /// Emit new particles.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    protected void EmitParticles(float deltaTime) {
      // emit new particles
      for (int i=0; i<emitters.Count; i++) {
        IParticleEmitter emitter = (IParticleEmitter)emitters[i];
        emitter.Update(this, deltaTime);
      }
    }

    /// <summary>
    /// Render the particle system.
    /// </summary>
    /// <param name="effect">The effect used for rendering the particle system.</param>
    public abstract void Render(IEffect effect);

    /// <summary>
    /// Returns true if the particle system is still alive.
    /// </summary>
    public bool IsAlive {
      get {
        return isAlive;
      }
      set {
        isAlive = value;
      }
    }
    bool isAlive = true;

    /// <summary>
    /// Emits a new particle.
    /// </summary>
    public virtual void Emit(IParticle particle) {
      particles.Add(particle);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
