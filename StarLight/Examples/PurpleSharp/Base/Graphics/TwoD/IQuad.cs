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

namespace Purple.Graphics.TwoD
{
  //=================================================================
  /// <summary>
  /// abstract interface for an extended quad
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public interface IExtendedQuad : IQuad {
    /// <summary>
    /// opacity of the current sprite
    /// </summary>
    float Alpha { get; set; }

    /// <summary>
    /// the scale ratio of the quad
    /// </summary>
    Purple.Math.Vector2 Scale { get; set; }

    /// <summary>
    /// rotation angles for the quad
    /// </summary>
    float RotationZ { get; set; }

    /// <summary>
    /// size of the quad
    /// </summary>
    Purple.Math.Vector2 Size { get; set; }

    /// <summary>
    /// size of texture
    /// </summary>
    Purple.Math.Vector2 TextureSize { get; }

    /// <summary>
    /// The texture rectangle to use.
    /// </summary>
    RectangleF TextureRectangle { get; set; }
  }

  //=================================================================
  /// <summary>
  /// abstract interface for a simple quad
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
	public interface IQuad : IApply
	{
    /// <summary>
    /// Returns true if the quad is visible and should be rendered.
    /// </summary>
    bool Visible { get; }

    /// <summary>
    /// Access to the position of the sprite.
    /// </summary>
    Vector2 Position { get; set; }

    /// <summary>
    /// The used texture.
    /// </summary>
    ITexture2d Texture { get; set;}

    /// <summary>
    /// Returns the factory used for creating the quad.
    /// </summary>
    IQuadFactory QuadFactory {get;}

    /// <summary>
    /// Tests if two quads can be put into the same SubSet.
    /// </summary>
    /// <param name="quad">Quad tot test with.</param>
    /// <returns>True if quad can be put into same subSet.</returns>
    bool IsCompatible(IQuad quad);

    /// <summary>
    /// Returns the number of indices.
    /// </summary>
    int IndexCount { get; }

    /// <summary>
    /// Returns the number of vertices.
    /// </summary>
    int VertexCount { get; }

    /// <summary>
    /// Fills the indexStream with the data.
    /// </summary>
    /// <param name="indexStream">Fills the quad data into the passed indexStream.</param>
    /// <param name="index">The start in the index buffer.</param>
    /// <param name="vertexIndex">The start in the vertex buffer.</param>
    void FillIndexStream(Purple.Graphics.VertexStreams.IndexStream indexStream, int index, int vertexIndex);

    /// <summary>
    /// Fills the vertexUnit with the quad data.
    /// </summary>
    /// <param name="vertexUnit">The vertexUnit to fill with the quad data.</param>
    /// <param name="index">The start index in the index buffer.</param>
    void FillVertexUnit(Purple.Graphics.VertexUnit vertexUnit, int index);
	}
}
