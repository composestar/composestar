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
  /// An <see cref="IParticleSystem"/> that emits particles in form of a polygon chain.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para> 
  ///   <para>Last Update: 0.6</para>
  /// </remarks>
  //=================================================================
  public class LineParticleSystem : ParticleSystemBase {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    VertexUnit vertexUnit;
    IndexStream indexStream;

        /// <summary>
    /// The texture to use for the particles.
    /// </summary>
    public ITexture2d Texture {
      get {
        return (ITexture2d)textures["color"];
      }
      set {
        textures["color"] = value;
      }
    }
    Textures textures;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a particle system.
    /// </summary>
    /// <param name="size">The max number of particles.</param>
    public LineParticleSystem( int size ) : base( ) {
      
      textures = new Textures();
      particles = new FixedRoundBuffer(size);

      // Create a new format
      VertexFormat format = new VertexFormat( 
        new Type[]{ typeof(PositionStream), typeof(ColorStream), typeof(TextureStream) } );

      // create VertexUnit and IndexStream
      vertexUnit = new VertexUnit( format, 2+size*2);
      indexStream = IndexStream.FromChain(size-1);

      // load shaders from the resource files
      using (System.IO.Stream fxStream = Purple.IO.ResourceFileSystem.Instance.Open("Purple/Graphics/Particles/LineParticle.fx")) {
        Effect = EffectCompiler.Instance.Compile(fxStream);
      }
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
    public override void Update(float deltaTime) {
      UpdateAge(deltaTime);
      // delete dead particles
      FixedRoundBuffer buffer = particles as FixedRoundBuffer;
      while (buffer.Count > 0 && !(buffer[0] as IParticle).IsAlive)
        buffer.RemoveFirst();

      UpdateParticles(deltaTime);
      EmitParticles(deltaTime);

      ITexture2d texture = Texture;
      if (particles.Count >= 2) {
        // update streams
        PositionStream posStream = (PositionStream)vertexUnit[typeof(PositionStream)];
        ColorStream colorStream = (ColorStream)vertexUnit[typeof(ColorStream)];
        TextureStream textureStream = (TextureStream)vertexUnit[typeof(TextureStream)];
        // Update all particles
        for (int i=0; i<particles.Count; i++) {
          int offset = i*2;
          IParticle particle = particles[i] as IParticle;
          IParticle3d particle3d = particles[i] as IParticle3d;
          if (particle3d != null) {
            Vector3 position = particle3d.Position;
            posStream[offset] = position + particle.Size.X * (particle as IParticleOrientation).Orientation;
            posStream[offset+1] = position - particle.Size.X * (particle as IParticleOrientation).Orientation;
          }
          IParticleColor particleColor = particles[i] as IParticleColor;
          if (particleColor != null) {
            colorStream[offset] = particleColor.Color;
            colorStream[offset+1] = particleColor.Color;
          }
          float tx = texture.TextureCoordinates.Left + texture.TextureCoordinates.Width * particle.Age / particle.LifeTime;
          textureStream[offset] = new Vector2(tx, texture.TextureCoordinates.Top);
          textureStream[offset+1] = new Vector2(tx, texture.TextureCoordinates.Bottom);
        }
        posStream.Upload();
        colorStream.Upload();
        textureStream.Upload();
      }
    }

    /// <summary>
    /// Render the particle system.
    /// </summary>
    /// <param name="effect">The effect used for rendering the particle system.</param>
    public override void Render(IEffect effect) {
      if (particles.Count > 2) {
        // Render all particles
        Device.Instance.VertexUnit = vertexUnit;
        Device.Instance.IndexStream = indexStream;
        textures.Apply();
        effect.CommitChanges();
        Device.Instance.DrawIndexed( vertexUnit.Position, 0, particles.Count*2, indexStream.Position, particles.Count*2-2);
      }
    }

    /*public override string ToString() {
      string ret = "";
      for (int i=0; i<particles.Count; i++)
        ret += (particles[i] as IParticle).Age.ToString() + " - " + (particles[i] as IParticle3d).Position.ToString() + Environment.NewLine;
      return ret;
    }*/
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
