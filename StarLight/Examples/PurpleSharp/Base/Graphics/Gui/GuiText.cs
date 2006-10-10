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

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// Abstract interface for a gui text.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiText : IGuiElement{
    /// <summary>
    /// Set the text of the <see cref="GuiElement"/>.
    /// </summary>
    string Text { get; set; }

    /// <summary>
    /// The anchor used for the text.
    /// </summary>
    Anchor TextAnchor { get; set; }

    /// <summary>
    /// Renders the text into the texture.
    /// </summary>
    /// <param name="color">Color to preclear the background or Color.Empty.</param>
    void Update(int color);
  }

  //=================================================================
  /// <summary>
  /// This class maps the font functionality to a <see cref="IGuiElement"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para>  
  /// </remarks>
  //=================================================================
	public class GuiText : Image, IGuiText
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the underlying <see cref="Text"/> object.
    /// </summary>
    public IText TextObject {
      get {
        return textObject;
      }
    }
    IText textObject;

    /// <summary>
    /// Set the text of the <see cref="GuiElement"/>.
    /// </summary>
    public string Text {
      get {
        return text;
      }
      set {
        text = value;
      }
    }
    string text = "";

    /// <summary>
    /// The anchor used for the text.
    /// </summary>
    public Anchor TextAnchor {
      get {
        return textAnchor;
      }
      set {
        textAnchor = value;
      }
    }
    Anchor textAnchor = Anchor.TopLeft;

    /// <summary>
    /// The color of the shadow.
    /// </summary>
    public int ShadowColor { 
      get {
        return textObject.ShadowColor;
      }
      set {
        textObject.ShadowColor = value;
      }
    }

    /// <summary>
    /// The color of the outline.
    /// </summary>
    public int OutlineColor { 
      get {
        return textObject.OutlineColor;
      }
      set {
        textObject.OutlineColor = value;
      }
    }

    /// <summary>
    /// Offset of the shadow and outline.
    /// </summary>
    public int Offset { 
      get {
        return textObject.Offset;
      }
      set {
        textObject.Offset = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="GuiText"/>.
    /// </summary>
    /// <param name="width">Width of the texture.</param>
    /// <param name="height">Height of the texture.</param>
    /// <param name="font">Font to use for the text.</param>
    /// <param name="color">Color to use for drawing the text.</param>
		public GuiText(int width, int height, Font font, int color)
		{
      textObject = new Text(width, height, font, color );
      Init( textObject.Texture );
		}

    /// <summary>
    /// Creates a new GuiText object.
    /// </summary>
    /// <param name="text">The initial text.</param>
    /// <param name="width">Width of the texture.</param>
    /// <param name="height">Height of the texture.</param>
    /// <param name="font">Font to use for the text.</param>
    /// <param name="color">Color to use for drawing the text.</param>
    public GuiText(string text, int width, int height, Font font, int color) 
      : this(width, height, font, color) {
      this.Text = text;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private Vector2 UpdatePosition() {
      Vector2 pos = Vector2.Zero;
      if (textAnchor.Horizontal == Alignment.Offset)
        pos.X = textAnchor.X;
      else if (textAnchor.Horizontal == Alignment.Left)
        pos.X = 0;
      else if (textAnchor.Horizontal == Alignment.Center)
        pos.X = Quad.Size.X / 2;
      else if (textAnchor.Horizontal == Alignment.Right)
        pos.X = Quad.Size.X;

      if (textAnchor.Vertical == Alignment.Offset)
        pos.Y = textAnchor.Y;
      else if (textAnchor.Vertical == Alignment.Left)
        pos.Y = 0;
      else if (textAnchor.Vertical == Alignment.Center)
        pos.Y = Quad.Size.Y / 2;
      else if (textAnchor.Vertical == Alignment.Right)
        pos.Y = Quad.Size.Y;
      return pos;
    }

    /// <summary>
    /// Renders the text into the texture.
    /// </summary>
    /// <param name="color">Color to preclear the background or Color.Empty.</param>
    public void Update(int color) {
      Vector2 pos = UpdatePosition();

      textObject.HAlign = textAnchor.Horizontal;
      textObject.VAlign = textAnchor.Vertical;
      textObject.Draw(text, QuadManager.Instance.UnitToPixel( pos ), color);
    }

    /// <summary>
    /// Renders the text into the texture.
    /// </summary>
    public void Update() {
      Vector2 pos = UpdatePosition();

      textObject.HAlign = textAnchor.Horizontal;
      textObject.VAlign = textAnchor.Vertical;
      textObject.Draw(text, QuadManager.Instance.UnitToPixel( pos ));
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}