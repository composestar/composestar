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

namespace Purple.Graphics.Particles {
  //=================================================================
  /// <summary>
  /// A standard implementation of an <see cref="IParticleSystem"/> that 
  /// emits 2d particles.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>  
  ///   <para>Last Update: 0.6</para>
  /// </remarks>
  //=================================================================
  public class ParticleSystem2d : ParticleSystemBase {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    VertexUnit vertexUnit;
    IndexStream indexStream;

    /// <summary>
    /// The texture to use for the particles.
    /// </summary>
    public ITexture Texture {
      get {
        return textures["color"];
      }
      set {
        textures["color"] = value;
      }
    }
    Textures textures;

    /// <summary>
    /// The subTextures that should be used for animation.
    /// </summary>
    /// <remarks>All SubTextures must use the same physical texture!</remarks>
    public SubTexture[] SubTextures {
      get {
        return subTextures;
      }
      set {
        subTextures = value;
      }
    }
    SubTexture[] subTextures;
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
    public ParticleSystem2d( int size ) : base( ) {
      
      textures = new Textures();
      particles = new Purple.Collections.FixedBag(size);

      // Create a new format
      VertexFormat format = new VertexFormat( 
        new Type[]{ typeof(PositionStream2), typeof(ColorStream), typeof(TextureStream) } );

      // create VertexUnit and IndexStream
      vertexUnit = new VertexUnit( format, size*4);
      indexStream = IndexStream.FromQuads(size);

      // load shaders from the resource files
      using (System.IO.Stream fxStream = Purple.IO.ResourceFileSystem.Instance.Open("Purple/Graphics/Particles/Particle2d.fx")) {
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
      base.Update(deltaTime);

      // update streams
      PositionStream2 posStream = (PositionStream2)vertexUnit[typeof(PositionStream2)];
      ColorStream colorStream = (ColorStream)vertexUnit[typeof(ColorStream)];
      TextureStream textureStream = (TextureStream)vertexUnit[typeof(TextureStream)];
      // Update all particles
      for (int i=0; i<particles.Count; i++) {
        int offset = i*4;
        IParticle particle = particles[i] as IParticle;
        IParticle2d particle2d = particles[i] as IParticle2d;
        if (particle2d != null) {
          Vector2 position = particle2d.Position;
          posStream[offset] = position + new Vector2( -particle.Size.X, particle.Size.Y );
          posStream[offset+1] = position + new Vector2( particle.Size.X, particle.Size.Y);
          posStream[offset+2] = position + new Vector2( particle.Size.X, -particle.Size.Y);
          posStream[offset+3] = position + new Vector2( -particle.Size.X, -particle.Size.Y);
        }
        IParticleColor particleColor = particles[i] as IParticleColor;
        if (particleColor != null) {
          colorStream[offset] = particleColor.Color;
          colorStream[offset+1] = particleColor.Color;
          colorStream[offset+2] = particleColor.Color;
          colorStream[offset+3] = particleColor.Color;
        }
        IParticleIndex particleIndex = particles[i] as IParticleIndex;
        System.Drawing.RectangleF tc;
        if (particleIndex == null || subTextures == null)
          tc = (Texture as ITexture2d).TextureCoordinates;
        else
          tc = subTextures[(int)particleIndex.TextureIndex % subTextures.Length].TextureCoordinates;
        textureStream[offset] = new Vector2( tc.Left, tc.Top);
        textureStream[offset+1] = new Vector2( tc.Right, tc.Top);
        textureStream[offset+2] = new Vector2( tc.Right, tc.Bottom);
        textureStream[offset+3] = new Vector2( tc.Left, tc.Bottom);
      }
      posStream.Upload();
      colorStream.Upload();
      textureStream.Upload();

      // Render all particles
      Device.Instance.VertexUnit = vertexUnit;
      Device.Instance.IndexStream = indexStream;
      textures.Apply();
      int steps = Effect.Begin();
      for (int i=0; i<steps; i++) {
        Effect.BeginPass(i);
        Effect.CommitChanges(); // Oct Update BUG!!!
        Device.Instance.DrawIndexed( vertexUnit.Position, 0, particles.Count*4, indexStream.Position, particles.Count*2);
        Effect.EndPass();
      }
      Effect.End();
    }

    /// <summary>
    /// Render the particle system.
    /// </summary>
    /// <param name="effect">The effect used for rendering the particle system.</param>
    public override void Render(IEffect effect) {
      if (particles.Count > 1) {
        // Render all particles
        Device.Instance.VertexUnit = vertexUnit;
        Device.Instance.IndexStream = indexStream;
        textures.Apply();
        effect.CommitChanges();
        Device.Instance.DrawIndexed( vertexUnit.Position, 0, particles.Count*4, indexStream.Position, particles.Count*2);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
