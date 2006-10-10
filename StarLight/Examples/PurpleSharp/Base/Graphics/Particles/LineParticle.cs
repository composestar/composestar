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

namespace Purple.Graphics.Particles
{
  //=================================================================
  /// <summary>
  /// An implementation of the <see cref="IParticle"/> that is used by the 
  /// <see cref="LineParticleSystem"/> class.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public class LineParticle : ParticleBase, IParticle3d, IParticleColor, IParticleOrientation  {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The current position of the particle.
    /// </summary>
    public Vector3 Position {
      get {
        return position;
      }
      set {
        position = value;
      }
    }
    Vector3 position;

    /// <summary>
    /// The current speed of the particle.
    /// </summary>
    public Vector3 Speed { 
      get {
        return speed;
      }
      set {
        speed = value;
      }
    }
    Vector3 speed;

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
    /// Orientation of the particle.
    /// </summary>
    public Vector3 Orientation { 
      get {
        return orientation;
      }
      set {
        orientation = value;
      }
    }
    Vector3 orientation = new Vector3(1,0,0);
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new particle.
    /// </summary>
    public LineParticle() : this( Vector3.Zero, Vector3.Zero) {
    }

    /// <summary>
    /// Creates a new particle.
    /// </summary>
    /// <param name="position">The inital position of the particle.</param>
    /// <param name="speed">The initial speed.</param>
    public LineParticle(Vector3 position, Vector3 speed) {
      this.Position = position;
      this.speed = speed;
      Size = new Vector2(0.5f, 0.5f);
      LifeTime = 0.30f;
    }

    /// <summary>
    /// Creates a new particle.
    /// </summary>
    /// <param name="particle">Particle to copy.</param>
    private LineParticle(LineParticle particle) : base(particle) {
      this.position = particle.Position;
      this.speed = particle.Speed;
      this.color = particle.Color;
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
      return new LineParticle(this);
    }

    /// <summary>
    /// Sets the position of the particle.
    /// </summary>
    /// <param name="particle">Particle for which to set position.</param>
    /// <param name="position">Position to set.</param>
    public static void SetPosition(IParticle particle, Vector3 position) {
      IParticle3d p3d = (particle as IParticle3d);
      if (p3d != null)
        p3d.Position = position;
      else {
        IParticle2d p2d = (particle as IParticle2d);
        if (p2d != null)
          p2d.Position = position.Vector2;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
