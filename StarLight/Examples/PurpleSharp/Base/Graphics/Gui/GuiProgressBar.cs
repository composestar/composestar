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
using Purple.Graphics.TwoD;

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// Enumeration over all possible directions.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para> 
  /// </remarks>
  //=================================================================
  public enum Direction {
    /// <summary>
    /// The progress bar moves from right to left.
    /// </summary>
    Left,
    /// <summary>
    /// The progress bar moves from left to right.
    /// </summary>
    Right,
    /// <summary>
    /// The progress bar moves up.
    /// </summary>
    Up,
    /// <summary>
    /// The progress bar moves down.
    /// </summary>
    Down
  }

  //=================================================================
  /// <summary>
  /// The abstract interface for gauge.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para> 
  /// </remarks>
  //=================================================================
  public interface IGuiProgressBar : IGuiElement {
   
    /// <summary>
    /// The direction of the gauge.
    /// </summary>
    Direction Direction { get; set; }

    /// <summary>
    /// The total current for the gauge.
    /// </summary>
    float Total { get; set; }

    /// <summary>
    /// The current current.
    /// </summary>
    float Current { get; set; }
  }

  //=================================================================
  /// <summary>
  /// Implementation of a progress bar.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para> 
  /// </remarks>
  //=================================================================
  public class GuiProgressBar : Image, IGuiProgressBar {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Vector2 offset;
    bool dirty = true;

    /// <summary>
    /// The total current for the gauge.
    /// </summary>
    public float Total {
      get {
        return total;
      }
      set {
        total = value;
        dirty = true;
      }
    }
    float total = 100.0f;

    /// <summary>
    /// The current current.
    /// </summary>
    public float Current {
      get {
        return current;
      }
      set {
        float newVal = Purple.Math.Basic.Clamp(value, 0.0f, total);
        if (current != newVal) {
          current = newVal;
          dirty = true;
        }
      }
    }
    float current = 100.0f;

    /// <summary>
    /// The direction of the gauge.
    /// </summary>
    public Direction Direction { 
      get {
        return direction;
      }
      set {
        direction = value;    
        dirty = true;    
      }
    }
    Direction direction = Direction.Right;

    /// <summary>
    /// Size of element.
    /// </summary>
    public override Vector2 Size {
      get {
        return Quad.TextureSize;
      }
    }

    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="GuiGauge"/>.
    /// </summary>
    /// <param name="texture">The texture to use for the gauge.</param>
    public GuiProgressBar(ITexture2d texture) : base(texture) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    void Update() {
      float factor = System.Math.Min(1.0f, current / Total);
      Direction dir = direction;
      
      switch( direction ) {
        case Direction.Right:
          Quad.TextureRectangle = new RectangleF(0.0f, 0.0f, factor, 1.0f);
          Quad.Size = new Vector2( Quad.TextureSize.X*factor, Quad.TextureSize.Y); 
          offset = new Vector2(0,0);
          break;
        case Direction.Left:
          Quad.TextureRectangle = new RectangleF(1.0f - factor, 0.0f, factor, 1.0f);
          Quad.Size = new Vector2( Quad.TextureSize.X*factor, Quad.TextureSize.Y); 
          offset = new Vector2(Quad.TextureSize.X * (1.0f - factor),0);
          break;
        case Direction.Down:
          Quad.TextureRectangle = new RectangleF(0.0f, 0.0f, 1.0f, factor);
          Quad.Size = new Vector2( Quad.TextureSize.X, Quad.TextureSize.Y * factor);
          offset = new Vector2(0,0);
          break;
        case Direction.Up:
          Quad.TextureRectangle = new RectangleF(0.0f, 1.0f - factor, 1.0f, factor);
          Quad.Size = new Vector2( Quad.TextureSize.X, Quad.TextureSize.Y * factor);
          offset = new Vector2(0, Quad.TextureSize.Y * (1.0f - factor));
          break;
      }      
    }

    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <param name="deltaTime">The current since the last <c>OnRender</c> call.</param>
    public override void OnRender(float deltaTime) {
      if (dirty) {
        Update();
        dirty = false;
      }

      this.Position += offset;
      base.OnRender(deltaTime);
      this.Position -= offset;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
