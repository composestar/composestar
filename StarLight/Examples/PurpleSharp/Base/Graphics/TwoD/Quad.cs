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
using System.Drawing;

using Purple.Math;
using Purple.Graphics.Core;
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.TwoD
{
  //=================================================================
  /// <summary>
  /// a simple quad (billboard)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
	public class Quad : IExtendedQuad
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The cache for the positions;
    /// </summary>
    protected Vector2[] positionCache;
    /// <summary>
    /// The cache for the colors;
    /// </summary>
    protected int[] colorCache;
    /// <summary>
    /// The cache for the textures;
    /// </summary>
    protected Vector2[] textureCache;
    /// <summary>
    /// Returns true if the quad was changed since the last update.
    /// </summary>
    protected bool dirty = true;

    /// <summary>
    /// returns the factory used for creating the quad
    /// </summary>
    public IQuadFactory QuadFactory {
      get {
        return factory;
      }
    }
    /// <summary>
    /// The factory used for creating the quad.
    /// </summary>
    protected QuadFactory factory;

    /// <summary>
    /// Returns true if the quad is visible and should be rendered.
    /// </summary>
    public bool Visible { 
      get {
        return alpha > float.MinValue;
      }
    }

    /// <summary>
    /// Returns the number of vertices.
    /// </summary>
    public virtual int IndexCount { 
      get {
        return 6;
      }
    }

    /// <summary>
    /// Returns the number of vertices.
    /// </summary>
    public virtual int VertexCount { 
      get {
        return 4;
      }
    }

    /// <summary>
    /// position of the billboard (upper-left corner)
    /// range of vector components goes from [0..1]
    /// </summary>
    public Purple.Math.Vector2 Position {
      get {
        return position;
      }
      set {
        position = value;
        dirty = true;
      }
    }
    Vector2 position = Vector2.Zero;

    /// <summary>
    /// the used texture
    /// </summary>
    public Purple.Graphics.Core.ITexture2d Texture {
      get {
        return texture;
      }
      set {
        texture = value;
      }
    }
    ITexture2d texture = null;

    /// <summary>
    /// alpha value of the quad
    /// </summary>
    public float Alpha {
      get {
        return alpha;
      }
      set {
        alpha = value;
        dirty = true;
      }
    }
    float alpha = 1.0f;

    /// <summary>
    /// the scale ratio of the quad
    /// </summary>
    public Purple.Math.Vector2 Scale {
      get {
        return scale;
      }
      set {
        scale = value;
        dirty = true;
      }
    }
    Vector2 scale = new Vector2(1.0f, 1.0f);

    /// <summary>
    /// rotation angles for the quad
    /// </summary>
    public float RotationZ {
      get {
        return rotationZ;
      }
      set {
        rotationZ = value;
        dirty = true;
      }
    }
    float rotationZ;

    /// <summary>
    /// size of the quad
    /// </summary>
    public Purple.Math.Vector2 Size {
      get {
        return size;
      }
      set {
        size = value;
      }
    }
    Vector2 size = Vector2.Zero;

    /// <summary>
    /// size of texture
    /// </summary>
    public Purple.Math.Vector2 TextureSize {
      get {
        if (texture == null)
          return Vector2.Zero;
        return QuadManager.Instance.PixelToUnit( 
          new Vector2((float)texture.ImageDescription.Width, (float)texture.ImageDescription.Height) );
      }
    }

    /// <summary>
    /// The texture rectangle to use.
    /// </summary>
    public RectangleF TextureRectangle { 
      get {
        return textureRectangle;
      }
      set {
        textureRectangle = value;
      }
    }
    RectangleF textureRectangle = new RectangleF(0, 0, 1, 1);
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new instance of a billboard
    /// </summary>
		internal Quad(QuadFactory factory)
		{
      this.factory = factory;
      positionCache = new Vector2[4];
      colorCache = new int[4];
      textureCache = new Vector2[4];
		}

    /// <summary>
    /// Empty constructor.
    /// </summary>
    protected Quad() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Fills the indexStream with the data.
    /// </summary>
    /// <param name="indexStream">Fills the quad data into the passed indexStream.</param>
    /// <param name="index">The start index in the index buffer.</param>
    /// <param name="vertexIndex">The start in the vertex buffer.</param>
    public virtual void FillIndexStream(IndexStream indexStream, int index, int vertexIndex) {
      indexStream[index] = (vertexIndex + 0);
      indexStream[index+1] = (vertexIndex + 1);
      indexStream[index+2] = (vertexIndex + 2);
      indexStream[index+3] = (vertexIndex + 2);
      indexStream[index+4] = (vertexIndex + 1);
      indexStream[index+5] = (vertexIndex + 3);
    }

    /// <summary>
    /// Fills the vertexUnit with the quad data.
    /// </summary>
    /// <param name="vertexUnit">The vertexUnit to fill with the quad data.</param>
    /// <param name="index">The start index in the index buffer.</param>
    public virtual void FillVertexUnit(Purple.Graphics.VertexUnit vertexUnit, int index) {
      if (dirty) {
        Vector2 right = QuadManager.Instance.RotateUnit( new Vector2( size.X * this.scale.X, 0 ), this.rotationZ);
        Vector2 down = QuadManager.Instance.RotateUnit( new Vector2( 0, size.Y * this.scale.Y), this.rotationZ);

        Vector2 a = this.position;
        Vector2 b = this.position + right;
        Vector2 c = this.position + down;
        Vector2 d = this.position + down + right;

        RectangleF texCoords = new RectangleF(
          texture.TextureCoordinates.Left + texture.TextureCoordinates.Width * textureRectangle.Left,
          texture.TextureCoordinates.Top + texture.TextureCoordinates.Height * textureRectangle.Top,
          texture.TextureCoordinates.Width * textureRectangle.Width,
          texture.TextureCoordinates.Height * textureRectangle.Height);

        QuadUtil.FillPosition(positionCache, a, b, c, d);
        QuadUtil.FillColor(colorCache, Color.From((int)(alpha*255+0.5f), 0xff, 0xff, 0xff));
        QuadUtil.FillTexture(textureCache, texCoords);
        dirty = false;
      }

      positionCache.CopyTo(vertexUnit[typeof(PositionStream2)].Data, index);
      colorCache.CopyTo(vertexUnit[typeof(ColorStream)].Data, index);
      textureCache.CopyTo(vertexUnit[typeof(TextureStream)].Data, index);
    }

    /// <summary>
    /// tests if two quads can be put into the same SubSet
    /// </summary>
    /// <param name="quad">quad tot test with</param>
    /// <returns>true if quad can be put into same subSet</returns>
    public bool IsCompatible(IQuad quad) {
      if (quad == null || quad.Texture.Id != this.Texture.Id)
        return false;
      return true;
    }

    /// <summary>
    /// Starts with applying and returns the number of passes.
    /// </summary>
    public void Apply() {
      ShaderConstants.Instance["color"].Set( Texture.Root );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
