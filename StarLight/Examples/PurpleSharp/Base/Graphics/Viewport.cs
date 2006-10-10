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

namespace Purple.Graphics {
  //=================================================================
  /// <summary>
  /// view port
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public struct Viewport {
    private System.Drawing.Rectangle rectangle;

    /// <summary>
    /// upper left corner of the viewport on the render target
    /// </summary>
    public int Left {
      get {
        return rectangle.X;
      }
    }

    /// <summary>
    /// upper left corner of the viewport on the render target
    /// </summary>
    public int Top {
      get {
        return rectangle.Y;
      }
    }

    /// <summary>
    /// lower right corner of the viewport on the render target
    /// </summary>
    public int Right {
      get {
        return rectangle.Right;
      }
    }

    /// <summary>
    /// lower right corner of the viewport on the render target
    /// </summary>
    public int Bottom {
      get {
        return rectangle.Bottom;
      }
    }

    /// <summary>
    /// width of the viewport
    /// </summary>
    public int Width {
      get {
        return rectangle.Width;
      }
    }

    /// <summary>
    /// height of the viewport
    /// </summary>
    public int Height {
      get {
        return rectangle.Height; 
      }
    }

    /// <summary>
    /// return viewport as rectangle
    /// </summary>
    public System.Drawing.Rectangle Rectangle {
      get {
        return rectangle;
      }
    }

    /// <summary>
    /// creates an viewport object
    /// </summary>
    /// <param name="left">upper left corner of the viewport on the render target</param>
    /// <param name="top">upper left corner of the viewport on the render target</param>
    /// <param name="right">lower right corner of the viewport on the render target</param>
    /// <param name="bottom">lower right corner of the viewport on the render target</param>
    public Viewport(int left, int top, int right, int bottom) {
      rectangle = new System.Drawing.Rectangle(left, top, right-left, bottom-top);
    }
  }
}
