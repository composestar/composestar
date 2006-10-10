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
  /// The 3d extension to an <see cref="IParticle"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  /// </remarks>
  //=================================================================
  public interface IParticle3d {
    /// <summary>
    /// The current position of the particle.
    /// </summary>
    Vector3 Position { get; set; }

    /// <summary>
    /// The current speed of the particle.
    /// </summary>
    Vector3 Speed { get; set; }
  }

  //=================================================================
  /// <summary>
  /// The 2d extension to an <see cref="IParticle"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  /// </remarks>
  //=================================================================
  public interface IParticle2d {
    /// <summary>
    /// The current position of the particle.
    /// </summary>
    Vector2 Position { get; set; }

    /// <summary>
    /// The current speed of the particle.
    /// </summary>
    Vector2 Speed { get; set; }
  }

  //=================================================================
  /// <summary>
  /// The color extension to an <see cref="IParticle"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  /// </remarks>
  //=================================================================
  public interface IParticleColor {
    /// <summary>
    /// The color of the particle.
    /// </summary>
    int Color { get; set; }

    /// <summary>
    /// The alpha value of the particle.
    /// </summary>
    float Alpha { get; set; }
  }

  //=================================================================
  /// <summary>
  /// The texture index of the particle.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  /// </remarks>
  //=================================================================
  public interface IParticleIndex {
    /// <summary>
    /// The index of the texture to use.
    /// </summary>
    float TextureIndex { get; set; }
  }

  //=================================================================
  /// <summary>
  /// Orientation of a chain particle.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public interface IParticleOrientation {
    /// <summary>
    /// Orientation of the particle.
    /// </summary>
    Vector3 Orientation { get; set; }
  }

  //=================================================================
  /// <summary>
  /// An abstract interface for a particle.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  /// </remarks>
  //=================================================================
  public interface IParticle : ICloneable{
    /// <summary>
    /// The size of the particle.
    /// </summary>
    Vector2 Size { set; get; }

    /// <summary>
    /// Returns true if particle is still alive.
    /// </summary>
    bool IsAlive { get; }

    /// <summary>
    /// Returns the age of the particle.
    /// </summary>
    float Age { get; set; }

    /// <summary>
    /// The age at which the particle dies.
    /// </summary>
    float LifeTime { get; }

    /// <summary>
    /// Clones the current particle.
    /// </summary>
    /// <returns>The cloned particle.</returns>
    new IParticle Clone();
  }
}
