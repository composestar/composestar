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
  /// An implementation of the <see cref="IParticle"/> that is used by the 
  /// <see cref="ParticleSystem2d"/> class.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  /// </remarks>
  //=================================================================
  public class Particle2d : ParticleBase, IParticle2d, IParticleColor, IParticleIndex {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The current position of the particle.
    /// </summary>
    public Vector2 Position {
      get {
        return position;
      }
      set {
        position = value;
      }
    }
    Vector2 position;

    /// <summary>
    /// The current speed of the particle.
    /// </summary>
    public Vector2 Speed { 
      get {
        return speed;
      }
      set {
        speed = value;
      }
    }
    Vector2 speed;

    /// <summary>
    /// The color of the particle.
    /// </summary>
    public int Color {
      get {
        return color;
      }
      set {
        color = value;
      }
    }
    int color = Purple.Graphics.Color.White;

    /// <summary>
    /// The alpha value of the particle.
    /// </summary>
    public float Alpha { 
      get {
        return Purple.Graphics.Color.GetAlpha(color)/255.0f;
      }
      set {
        color = Purple.Graphics.Color.SetAlpha(color, (int)(value*255));
      }
    }

    /// <summary>
    /// The index of the texture to use.
    /// </summary>
    public float TextureIndex { 
      get {
        return textureIndex;
      }
      set {
        textureIndex = value;
      }
    }
    float textureIndex;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new particle.
    /// </summary>
    public Particle2d() : this(Vector2.Zero, Vector2.Zero) {
    }

    /// <summary>
    /// Creates a new particle.
    /// </summary>
    /// <param name="position">The inital position of the particle.</param>
    /// <param name="speed">The initial speed.</param>
    public Particle2d(Vector2 position, Vector2 speed) {
      this.position = position;
      this.speed = speed;
      Size = new Vector2(0.05f, 0.05f);
      textureIndex = 0;
    }

    /// <summary>
    /// Creates a new particle.
    /// </summary>
    /// <param name="particle">Particle to copy.</param>
    private Particle2d(Particle2d particle) : base(particle) {
    this.position = particle.Position;
    this.speed = particle.Speed;
    this.color = particle.Color;
    this.textureIndex = particle.textureIndex;
  }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Clones the current particle.
    /// </summary>
    /// <returns>The cloned particle.</returns>
    public override IParticle Clone() {
      return new Particle2d(this);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
