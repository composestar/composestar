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

namespace Purple.Graphics.TwoD {
  /// <summary>
  /// Structure that holds one vertex of a MeshQuad.
  /// </summary>
  public struct MeshVertex {
    /// <summary>
    /// The relative position of the vertex within the quad [0;1].
    /// </summary>
    public Vector2 Position;

    /// <summary>
    /// The relative texture coordinate of the vertex within the quad [0;1].
    /// </summary>
    public Vector2 Texture;

    /// <summary>
    /// The color the vertex.
    /// </summary>
    public int Color;
    
    /// <summary>
    /// Creates a new MeshVertex object.
    /// </summary>
    /// <param name="position">The relative position of the vertex within the quad [0;1].</param>
    /// <param name="texture">The relative texture coordinate of the vertex within the quad [0;1].</param>
    /// <param name="color">The color the vertex.</param>
    public MeshVertex(Vector2 position, Vector2 texture, int color) {
      this.Position = position;
      this.Texture = texture;
      this.Color = color;
    }
  }

  //=================================================================
  /// <summary>
  /// A quad that contains of a 2d mesh.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class MeshQuad : Quad, IMeshQuad {
    //---------------------------------------------------------------
    #region Variables and PRoperties
    //---------------------------------------------------------------
    /// <summary>
    /// Access to the all mesh vertices.
    /// </summary>
    public MeshVertex[,] Vertices {
      get {
        return vertices;
      }
    }
    MeshVertex[,] vertices;

    /// <summary>
    /// Returns the number of vertices.
    /// </summary>
    public override int IndexCount { 
      get {
        return (vertices.GetLength(0)-1)*(vertices.GetLength(1)-1)*6;
      }
    }

    /// <summary>
    /// Returns the number of vertices.
    /// </summary>
    public override int VertexCount { 
      get {
        return vertices.Length;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a mesh quad.
    /// </summary>
    /// <param name="columns">The number of columns.</param>
    /// <param name="rows">The number of rows.</param>
    /// <param name="factory">The factory that created the quad.</param>
    internal MeshQuad(int columns, int rows, QuadFactory factory) {
      this.factory = factory;
      Create(columns, rows);      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Creates the array of MeshVertex objects.
    /// </summary>
    /// <param name="columns">The number of columns.</param>
    /// <param name="rows">The number of rows.</param>
    private void Create(int columns, int rows) {
      vertices = new MeshVertex[columns, rows];
      for (int y=0; y<rows; y++) {
        for (int x=0; x<columns; x++) {
          Vector2 position = new Vector2( (float)x/(columns-1), (float)y/(rows-1) );
          Vector2 texture = new Vector2( (float)x/(columns-1), (float)y/(rows-1) );
          vertices[x,y] = new MeshVertex( position, texture, Color.White);
        }
      }
      positionCache = new Vector2[vertices.Length];
      colorCache = new int[vertices.Length];
      textureCache = new Vector2[vertices.Length];
    }

    /// <summary>
    /// Fills the vertexUnit with the quad data.
    /// </summary>
    /// <param name="vertexUnit">The vertexUnit to fill with the quad data.</param>
    /// <param name="index">The start index in the index buffer.</param>
    public override void FillVertexUnit(Purple.Graphics.VertexUnit vertexUnit, int index) {

      Vector2 a = this.Position;
      Vector2 right = QuadManager.Instance.RotateUnit( new Vector2( Size.X * this.Scale.X, 0 ), this.RotationZ);
      Vector2 down = QuadManager.Instance.RotateUnit( new Vector2( 0, Size.Y * this.Scale.Y), this.RotationZ);

      if (dirty) {
        PositionStream2 ps = PositionStream2.From(QuadFactory.VertexUnit);
        ColorStream cs = ColorStream.From(QuadFactory.VertexUnit);
        TextureStream ts = TextureStream.From(QuadFactory.VertexUnit);
        RectangleF tc = Texture.TextureCoordinates;

        for (int y=0; y<vertices.GetLength(1); y++) {
          for (int x=0; x<vertices.GetLength(0); x++) {
            MeshVertex vertex = vertices[ x, y ];
            Vector2 pos = a + right*vertex.Position.X + down*vertex.Position.Y;
            ps[index] = new Vector2( (pos.X - 0.5f)*2, -(pos.Y - 0.5f)*2);
            cs[index] = Color.SetAlpha(vertex.Color, (int)(Color.GetAlpha(vertex.Color)*Alpha));
            Vector2 texture = new Vector2(
              tc.Left + tc.Width * (TextureRectangle.Left + TextureRectangle.Width*vertex.Texture.X),
              tc.Top + tc.Height * (TextureRectangle.Top + TextureRectangle.Height*vertex.Texture.Y));
            ts[index] = texture;
            index++;
          }
        }
        dirty = false;
      }
      positionCache.CopyTo(vertexUnit[typeof(PositionStream2)].Data, index);
      colorCache.CopyTo(vertexUnit[typeof(ColorStream)].Data, index);
      textureCache.CopyTo(vertexUnit[typeof(TextureStream)].Data, index);
    }

    /// <summary>
    /// Fills the indexStream with the data.
    /// </summary>
    /// <param name="indexStream">Fills the quad data into the passed indexStream.</param>
    /// <param name="index">The start index in the index buffer.</param>
    /// <param name="vertexIndex">The start in the vertex buffer.</param>
    public override void FillIndexStream(IndexStream indexStream, int index, int vertexIndex) {
      for (int y=0; y<vertices.GetLength(1)-1; y++) {
        for (int x=0; x<vertices.GetLength(0)-1; x++) {
          int offset = x + y*vertices.GetLength(0);
          indexStream[index] = (vertexIndex + x+0 + (y+0)*vertices.GetLength(0));
          indexStream[index+1] = (vertexIndex + x+1 + (y+0)*vertices.GetLength(0));
          indexStream[index+2] = (vertexIndex + x+0 + (y+1)*vertices.GetLength(0));
          indexStream[index+3] = (vertexIndex + x+0 + (y+1)*vertices.GetLength(0));
          indexStream[index+4] = (vertexIndex + x+1 + (y+0)*vertices.GetLength(0));
          indexStream[index+5] = (vertexIndex + x+1 + (y+1)*vertices.GetLength(0));
          index += 6;
        }
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
