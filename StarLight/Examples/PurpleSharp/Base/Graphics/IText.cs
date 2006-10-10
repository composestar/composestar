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

namespace Purple.Graphics
{
  //=================================================================
  /// <summary>
  /// Abstract interface for a simple text object that allows to 
  /// render a certain string into a texture.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public interface IText
	{
    /// <summary>
    /// Returns the used <see cref="System.Drawing.Font"/>.
    /// </summary>
    Font Font { get; }

    /// <summary>
    /// Returns the texture, where the text is rendered into.
    /// </summary>
    ITexture2d Texture { get; }

    /// <summary>
    /// The color of the font.
    /// </summary>
    int Color { get; set; }

    /// <summary>
    /// The color of the shadow.
    /// </summary>
    int ShadowColor { get; set; }

    /// <summary>
    /// The color of the outline.
    /// </summary>
    int OutlineColor { get; set; }

    /// <summary>
    /// Offset of the shadow and outline.
    /// </summary>
    int Offset { get; set; }

    // TODO: bold, italic, underline, ...

    /// <summary>
    /// Horizontal alignment of the text.
    /// </summary>
    Alignment HAlign { get; set; }

    /// <summary>
    /// Vertical alignment of the text.
    /// </summary>
    Alignment VAlign { get; set; }

    /// <summary>
    /// Draws the text into the specified texture.
    /// </summary>
    /// <param name="text">Text to draw.</param>
    /// <param name="pos">Position of text to draw.</param>
    /// <param name="color">Color to preclear the bitmap with or Color.Transparent.</param>
    void Draw( string text, Vector2 pos, int color );

    /// <summary>
    /// Draws the text into the specified texture.
    /// </summary>
    /// <param name="text">Text to draw.</param>
    /// <param name="pos">The target position.</param>
    void Draw( string text, Vector2 pos );
	}
}
