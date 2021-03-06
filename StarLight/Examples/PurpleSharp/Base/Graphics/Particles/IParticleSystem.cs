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
//   Markus W??
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

namespace Purple.Graphics.Particles
{
  //=================================================================
  /// <summary>
  /// An abstract interface of a particle system.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus W??</para>
  ///   <para>Since: 0.5</para>  
  /// </remarks>
  //=================================================================
	public interface IParticleSystem : IRenderAble
	{
    /// <summary>
    /// Updates the particle system.
    /// </summary>
    /// <param name="deltaTime">The time since the last update.</param>
    void Update(float deltaTime);

    /// <summary>
    /// Returns true if the particle system is still alive.
    /// </summary>
    bool IsAlive { get; }

    /// <summary>
    /// The affectors that affect the particle system.
    /// </summary>
    ArrayList Affectors { get; }

    /// <summary>
    /// A list of <see cref="IParticleEmitter"/> objects that emit particles.
    /// </summary>
    ArrayList Emitters { get;}

    /// <summary>
    /// Emits a new particle.
    /// </summary>
    void Emit(IParticle particle);
	}
}
