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

namespace Purple.Graphics.Particles
{
  //=================================================================
  /// <summary>
  /// The manager for particle systems.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
	public class ParticleManager
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ArrayList particleSystems = new ArrayList();

    /// <summary>
    /// Returns the singleton instance of the <see cref="ParticleManager"/>.
    /// </summary>
    public ParticleManager Instance {
      get {
        return instance;
      }
    }
    ParticleManager instance = new ParticleManager();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    private ParticleManager() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Updates the particle systems.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    public void Update(float deltaTime) {
      ArrayList clone = (ArrayList)particleSystems.Clone();
      for (int i=0; i<particleSystems.Count; i++) {
        IParticleSystem particleSystem = (clone[i] as IParticleSystem);
        if (particleSystem.IsAlive)
          (clone[i] as IParticleSystem).Update(deltaTime);
        else
          Remove(particleSystem);
      }
    }

    /// <summary>
    /// Renders the particle systems.
    /// </summary>
    public void Render() {
    }

    /// <summary>
    /// Adds an <see cref="IParticleSystem"/> to the manager.
    /// </summary>
    /// <param name="particleSystem">The particle System to add.</param>
    public void Add(IParticleSystem particleSystem) {
      particleSystems.Add(particleSystem);
    }

    /// <summary>
    /// Removes an <see cref="IParticleSystem"/> from the list.
    /// </summary>
    /// <param name="particleSystem"></param>
    public void Remove(IParticleSystem particleSystem) {
      particleSystems.Remove(particleSystem);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
