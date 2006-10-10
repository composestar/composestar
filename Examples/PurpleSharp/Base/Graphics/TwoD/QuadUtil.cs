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
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.TwoD
{
  //=================================================================
  /// <summary>
  /// some utility methods for faster creation of Quad and QuadFactory classes
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
	public class QuadUtil
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// fills four vectors into a positionStream at a certain position
    /// </summary>
    /// <param name="positionCache">The cache to fill.</param>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <param name="c">third vector</param>
    /// <param name="d">fourth vector</param>
    public static void FillPosition( Vector2[] positionCache, Vector2 a, Vector2 b, Vector2 c, Vector2 d) {
      positionCache[0] = new Vector2( (a.X - 0.5f)*2, -(a.Y - 0.5f)*2);
      positionCache[1] = new Vector2( (b.X - 0.5f)*2, -(b.Y - 0.5f)*2);
      positionCache[2] = new Vector2( (c.X - 0.5f)*2, -(c.Y - 0.5f)*2);
      positionCache[3] = new Vector2( (d.X - 0.5f)*2, -(d.Y - 0.5f)*2);
    }

    /// <summary>
    /// fills a positionStream with a quad given by position and size
    /// </summary>
    /// <param name="positionCache">The cache to fill with the data.</param>
    /// <param name="quad">quad to use</param>
    public static void FillPosition( Vector2[] positionCache, Quad quad) {
      float pX = quad.Position.X;
      float pY = quad.Position.Y;
      
      float width = quad.Texture.ImageDescription.Width/QuadManager.Instance.TargetSize.X;
      float height = quad.Texture.ImageDescription.Height/QuadManager.Instance.TargetSize.Y;

      QuadUtil.FillPosition(positionCache, new Vector2(pX, pY), new Vector2(pX + width, pY),
        new Vector2(pX + width, pY + height), new Vector2(pX, pY + height));
    }

    /// <summary>
    /// fills a color stream with quad data
    /// </summary>
    /// <param name="colorCache">Cache to fill.</param>
    /// <param name="color">color to use</param>
    public static void FillColor( int[] colorCache, int color) {
      colorCache[0] = color;
      colorCache[1] = color;
      colorCache[2] = color;
      colorCache[3] = color;
    }    

    /// <summary>
    /// fills the texture stream with texture coordinates
    /// </summary>
    /// <param name="textureCache">The textureCache to fill.</param>
    /// <param name="textureCoordinates">texture coordinates to use</param>
    public static void FillTexture(Vector2[] textureCache, RectangleF textureCoordinates) {
      textureCache[0] = new Vector2(textureCoordinates.Left, textureCoordinates.Top);
      textureCache[1] = new Vector2(textureCoordinates.Right, textureCoordinates.Top);
      textureCache[2] = new Vector2(textureCoordinates.Left, textureCoordinates.Bottom);
      textureCache[3] = new Vector2(textureCoordinates.Right, textureCoordinates.Bottom);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
