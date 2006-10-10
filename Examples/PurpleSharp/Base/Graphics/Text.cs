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
using System.Drawing.Imaging;

using Purple.Math;
using Purple.Graphics.Core;

namespace Purple.Graphics {
  //=================================================================
  /// <summary>
  /// A standard text implementation, that supports unicode, but might 
  /// not be too fast.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public class Text : IText {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Bitmap bitmap;
    StringFormat format;
    Brush brush;
    Brush shadowBrush;
    Brush outlineBrush;

    /// <summary>
    /// Returns the used <see cref="System.Drawing.Font"/>.
    /// </summary>
    public Font Font { 
      get {
        return font;
      }
    }
    Font font;

    /// <summary>
    /// Returns the texture, where the text is rendered into.
    /// </summary>
    public ITexture2d Texture { 
      get {
        return texture;
      }
    }
    ITexture2d texture;

    /// <summary>
    /// The color of the font.
    /// </summary>
    public int Color { 
      get {
        return color;
      }
      set {
        color = value;
        brush = new System.Drawing.SolidBrush( System.Drawing.Color.FromArgb(color) );
      }
    }
    int color;

    /// <summary>
    /// The color of the shadow.
    /// </summary>
    public int ShadowColor {
      get {
        return shadowColor;
      }
      set {
        shadowColor = value;
        shadowBrush = new System.Drawing.SolidBrush( System.Drawing.Color.FromArgb(shadowColor) );
      }
    }
    int shadowColor;

    /// <summary>
    /// The color of the outline.
    /// </summary>
    public int OutlineColor {
      get {
        return outlineColor;
      }
      set {
        outlineColor = value;
        outlineBrush = new System.Drawing.SolidBrush( System.Drawing.Color.FromArgb(outlineColor) );
      }
    }
    int outlineColor;

    /// <summary>
    /// Offset of the shadow and outline.
    /// </summary>
    public int Offset {
      get {
        return offset;
      }
      set {
        offset = value;
      }
    }
    int offset = 1;

    /// <summary>
    /// Vertical alignment of the text.
    /// </summary>
    public Alignment VAlign {
      get {
        return vAlign;
      }
      set {
        vAlign = value;
        switch (vAlign) {
          case Alignment.Left:
            format.LineAlignment = StringAlignment.Near;
            break;
          case Alignment.Center:
            format.LineAlignment = StringAlignment.Center;
            break;
          case Alignment.Right:
            format.LineAlignment = StringAlignment.Far;
            break;
          case Alignment.Offset:
            format.LineAlignment = StringAlignment.Near;
            break;
        }
      }
    }
    Alignment vAlign = Alignment.Near;

    /// <summary>
    /// Horizontal alignment of the text.
    /// </summary>
    public Alignment HAlign {
      get {
        return hAlign;
      }
      set {
        hAlign = value;
        switch (hAlign) {
          case Alignment.Left:
            format.Alignment = StringAlignment.Near;
            break;
          case Alignment.Center:
            format.Alignment = StringAlignment.Center;
            break;
          case Alignment.Right:
            format.Alignment = StringAlignment.Far;
            break;
          case Alignment.Offset:
            format.Alignment = StringAlignment.Near;
            break;
        }
      }
    }
    Alignment hAlign = Alignment.Left;

    /// <summary>
    /// Wraps text between lines.
    /// </summary>
    public bool WordWrap {
      get {
        return wordWrap;
      }
      set {
        wordWrap = value;
        if (!wordWrap)
          format.FormatFlags |= StringFormatFlags.NoWrap;
        else
          format.FormatFlags &= ~StringFormatFlags.NoWrap;
      }
    }
    bool wordWrap = true;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new text object.
    /// </summary>
    /// <param name="width">Size of texture to create.</param>
    /// <param name="height">Size of texture to create.</param>
    /// <param name="font">The font to use for rendering the text.</param>
    /// <param name="color">The color of the text.</param>
    public Text(int width, int height, Font font, int color) {
      if (width <= 0 || height <= 0)
        throw new ArgumentException("Size of Text is invalid! (" + width + ":" + height + ")");
     
      bitmap = new Bitmap(width, height, System.Drawing.Imaging.PixelFormat.Format32bppArgb);
      this.texture = TextureManager.Instance.Create(width, height, 1, Format.A8R8G8B8, TextureUsage.Normal); //dynamic?

      this.font = font;
      this.Color = color;
      format = new StringFormat( StringFormat.GenericDefault );
    }

    /// <summary>
    /// Creates a new text object.
    /// </summary>
    /// <param name="texture">The texture object to render the text into.</param>
    /// <param name="font">The font to use for rendering the text.</param>
    /// <param name="color">The color of the text.</param>
    public Text(ITexture2d texture, Font font, int color) {
      bitmap = new Bitmap(texture.Description.Width, texture.Description.Height, System.Drawing.Imaging.PixelFormat.Format32bppArgb);
      this.texture = texture;
      this.font = font;
      this.Color = color;
      format = new StringFormat( StringFormat.GenericDefault );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Draws the text into the specified texture.
    /// </summary>
    /// <param name="text">Text to draw.</param>
    /// <param name="pos">The target position.</param>
    /// <param name="color">Color to preclear the bitmap with.</param>
    public void Draw( string text, Vector2 pos, int color ) {
      System.Drawing.Graphics g = System.Drawing.Graphics.FromImage(bitmap);
      // Begin Instead of g.Clear()
      BitmapData data = bitmap.LockBits( new Rectangle(0,0, bitmap.Width, bitmap.Height), ImageLockMode.WriteOnly, PixelFormat.Format32bppArgb);
      if (data.Stride == data.Width*4)
        Purple.Tools.Memory.BlockCopy( data.Scan0, color, (data.Stride*data.Height)/4);
      else
        g.Clear( System.Drawing.Color.FromArgb(color) );
      bitmap.UnlockBits(data);
      // End Instead of g.Clear()
      //g.Clear( System.Drawing.Color.FromArgb(color) );
      Draw(g, text, pos);
      g.Dispose();
      texture.CopyBitmap( bitmap, Rectangle.Empty, new Point(0,0) );
    }

    private void Draw(System.Drawing.Graphics g, string text, Vector2 pos) {
      //if (highQuality) {
      //g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.HighQuality;
      //g.TextRenderingHint = System.Drawing.Text.TextRenderingHint.ClearTypeGridFit;
      //} else {
      g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.HighSpeed;
      g.TextRenderingHint = System.Drawing.Text.TextRenderingHint.AntiAlias;
      // }
      if (outlineColor != 0) {
        g.DrawString( text, font, outlineBrush, pos.X-offset, pos.Y-offset, format );
        g.DrawString( text, font, outlineBrush, pos.X-offset, pos.Y+offset, format );
        g.DrawString( text, font, outlineBrush, pos.X+offset, pos.Y-offset, format );
        g.DrawString( text, font, outlineBrush, pos.X+offset, pos.Y+offset, format );
      }
      if (shadowColor != 0)
        g.DrawString( text, font, shadowBrush, pos.X+offset, pos.Y+offset, format);
      g.DrawString( text, font, brush, pos.X, pos.Y, format);
    }

    /// <summary>
    /// Draws the text into the specified texture.
    /// </summary>
    /// <param name="text">Text to draw.</param>
    /// <param name="pos">The target position.</param>
    public void Draw( string text, Vector2 pos ) {
      System.Drawing.Graphics g = System.Drawing.Graphics.FromImage(bitmap);
      Draw(g, text, pos);
      g.Dispose();
      texture.CopyBitmap( bitmap, Rectangle.Empty, new Point(0,0) );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Some static helper methods
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a texture from a certain number of text strings.
    /// </summary>
    /// <param name="items">The text strings to render.</param>
    /// <param name="width">The width of one text item.</param>
    /// <param name="height">The height of one text item.</param>
    /// <param name="font">The font to use for the text.</param>
    /// <param name="textColor">The color of the text.</param>
    /// <param name="outlineColor">The color of the outline or 0.</param>
    /// <returns>The final texture.</returns>
    public static ITexture2d Create(string[] items, int width, int height, System.Drawing.Font font, int textColor, int outlineColor) {
      Vector2 size = new Vector2(width, height);
      IText text = new Text(width, height*items.Length, font, textColor);
      text.OutlineColor = outlineColor;
      text.HAlign = Alignment.Center;
      text.VAlign = Alignment.Center;
      for (int i = 0; i < items.Length; i++) {
        Vector2 pos = new Vector2( size.X/2, size.Y/2 + size.Y*i);
        text.Draw(items[i], pos);
      }
      return text.Texture;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}