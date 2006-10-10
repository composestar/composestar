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
using Purple.Math;
using Purple.Serialization;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// This class represents an <see cref="Anchor"/> for <see cref="IGuiElement"/>s.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// <para>An <see cref="Anchor"/> can be positioned somewhere inside a <see cref="IGuiElement"/>. 
  /// The position of the <see cref="Anchor"/> becomes the new origin of the element. For example 
  /// if an <c>Anchor.Centered</c> is assigned, the <see cref="IImage"/>'s center will be used 
  /// for positioning by <c>IGuiElement.Position</c>.</para>
  /// <para>The position of the <see cref="Anchor"/> may be defined by setting the <see cref="Alignment"/> 
  /// of the anchor, or by assigning a certain offset.</para>
  /// <para>It is also possible to give an anchor a certain name. In this case it is possible to link two 
  /// <see cref="IGuiElement"/>s at a certain position. This can be realized by specifying a <see cref="Anchor"/> 
  /// added to the <see cref="IGuiLinkAnchors.LinkAnchors"/> of a certain parent element 
  /// (like a <see cref="GuiGroup"/>) and an <see cref="Anchor"/> for a child <see cref="IGuiElement"/>, where both 
  /// anchors have the same name. </para>
  /// <seealso cref="Anchors"/>
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter(typeof(AnchorConverter))]
  public class Anchor {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private Alignment vertical;
    private Alignment horizontal;
    private float x;
    private float y;
    private string name;

    private static Anchor centered = new Anchor(Alignment.Center, Alignment.Center);
    private static Anchor topLeft  = new Anchor(Alignment.Left, Alignment.Top);
    private static Anchor top = new Anchor(Alignment.Center, Alignment.Top);
    private static Anchor topRight = new Anchor(Alignment.Right, Alignment.Top);
    private static Anchor left = new Anchor(Alignment.Left, Alignment.Center);
    private static Anchor right = new Anchor(Alignment.Right, Alignment.Center);
    private static Anchor bottomLeft = new Anchor(Alignment.Left, Alignment.Bottom);
    private static Anchor bottom = new Anchor(Alignment.Center, Alignment.Bottom);
    private static Anchor bottomRight = new Anchor(Alignment.Right, Alignment.Bottom);
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The name of the <see cref="Anchor"/>.
    /// </summary>
    public string Name {
      get {
        return name;
      }
      set {
        name = value;
      }
    }

    /// <summary>
    /// The vertical <see cref="Alignment"/> of the <see cref="Anchor"/>.
    /// </summary>
    [System.ComponentModel.RefreshProperties(System.ComponentModel.RefreshProperties.Repaint)]
    public Alignment Vertical {
      get {
        return vertical;
      }
      set {
        vertical = value;
      }
    }

    /// <summary>
    /// The horizontal <see cref="Alignment"/> of the <see cref="Anchor"/>.
    /// </summary>
    [System.ComponentModel.RefreshProperties(System.ComponentModel.RefreshProperties.Repaint)]
    public Alignment Horizontal {
      get {
        return horizontal;
      }
      set {
        horizontal = value;
       }
    }

    /// <summary>
    /// X coordinate of the <see cref="Anchor"/>. 
    /// </summary>
    /// <remarks>
    /// When a new value gets assigned to this property, the horizontal <see cref="Alignment"/> is 
    /// automatically set to <c>Offset</c>.
    /// <note type="note">The returned value is always 0 for an Alignment different to Offset (use <c>GetPosition</c>).</note>
    /// </remarks>
    [System.ComponentModel.RefreshProperties(System.ComponentModel.RefreshProperties.Repaint)]
    public float X {
      set {
        x = value;
        horizontal = Alignment.Offset;
      }
      get {
        if (horizontal == Alignment.Offset)
          return x;
        else
          return 0.0f;
      }
    }

    /// <summary>
    /// Y coordinate of the <see cref="Anchor"/>. 
    /// </summary>
    /// <remarks>
    /// When a new value gets assigned to this property, the vertical <see cref="Alignment"/> is 
    /// automatically set to <c>Offset</c>.
    /// <note type="note">The returned value is always 0 for an Alignment different to Offset (use <c>GetPosition</c>).</note>
    /// </remarks>
    [System.ComponentModel.RefreshProperties(System.ComponentModel.RefreshProperties.Repaint)]
    public float Y {
      set {
        y = value;
        vertical = Alignment.Offset;
      }
      get {
        if (vertical == Alignment.Offset)
          return y;
        else
          return 0.0f;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Constructors
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new user defined <see cref="Anchor"/> object.
    /// </summary>
    /// <param name="horizontal">Horizontal <see cref="Alignment"/> of the anchor.</param>
    /// <param name="vertical">Vertical <see cref="Alignment"/> of the anchor.</param>
    public Anchor(Alignment horizontal, Alignment vertical) {
      this.horizontal = horizontal;
      this.vertical = vertical;
      x = 0;
      y = 0;
    }

    /// <summary>
    /// Creates a new user defined <see cref="Anchor"/> object.
    /// </summary>
    /// <param name="horizontal">Horizontal <see cref="Alignment"/> of the anchor.</param>
    /// <param name="vertical">Vertical <see cref="Alignment"/> of the anchor.</param>
    /// <param name="name">Name of anchor.</param>
    public Anchor(Alignment horizontal, Alignment vertical, string name) 
      : this(horizontal, vertical) {
      this.name = name;
    }

    /// <summary>
    /// Creates a new user defined <see cref="Anchor"/> object.
    /// </summary>
    /// <param name="offsetX">Horizontal offset of anchor.</param>
    /// <param name="offsetY">Vertical offset of anchor.</param>
    public Anchor(float offsetX, float offsetY) {
      this.horizontal = Alignment.Offset;
      this.vertical = Alignment.Offset;
      this.x = offsetX;
      this.y = offsetY;
    }

    /// <summary>
    /// Creates a new user defined <see cref="Anchor"/> object.
    /// </summary>
    /// <param name="offsetX">Horizontal offset of anchor.</param>
    /// <param name="offsetY">Vertical offset of anchor.</param>
    /// <param name="name">Name of anchor.</param>
    public Anchor(float offsetX, float offsetY, string name) : this(offsetX, offsetY) {
      this.name = name;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Predefined anchors
    //---------------------------------------------------------------
    /// <summary>
    /// Returns a horizontally and vertically centered <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor Centered {
      get {
        return centered;
      }
    }

    /// <summary>
    /// Returns a top/left aligned <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor TopLeft {
      get {
        return topLeft;
      }
    }

    /// <summary>
    /// Returns a top/centered aligned <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor Top {
      get {
        return top;
      }
    }

    /// <summary>
    /// Returns a top/right aligned <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor TopRight {
      get {
        return topRight;
      }
    }

    /// <summary>
    /// Returns a left/centered aligned <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor Left {
      get {
        return left;
      }
    }

    /// <summary>
    /// Returns a right/centered aligned <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor Right {
      get {
        return right;
      }
    }

    /// <summary>
    /// Returns a bottom/left aligned <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor BottomLeft {
      get {
        return bottomLeft;
      }
    }

    /// <summary>
    /// Returns a bottom/centered aligned <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor Bottom {
      get {
        return bottom;
      }
    }

    /// <summary>
    /// Returns a bottom/right aligned <see cref="Anchor"/> object.
    /// </summary>
    public static Anchor BottomRight {
      get {
        return bottomRight;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Calculates the position of the <see cref="Anchor"/> for an 
    /// <see cref="IGuiElement"/> depending on the <see cref="Alignment"/>,
    /// </summary>
    /// <param name="size">Size of the <see cref="IGuiElement"/>.</param>
    /// <returns>The position as a <see cref="Vector2"/>.</returns>
    public Vector2 GetPosition(Vector2 size) {
      Vector2 ret = new Vector2(x,y);
      switch(horizontal) {
        case Alignment.Left:
          ret.X = 0;
          break;
        case Alignment.Center:
          ret.X = size.X/2;
          break;
        case Alignment.Right:
          ret.X = size.X;
          break;
      }

      switch(vertical) {
        case Alignment.Top:
          ret.Y = 0;
          break;
        case Alignment.Center:
          ret.Y = size.Y/2;
          break;
        case Alignment.Bottom:
          ret.Y = size.Y;
          break;
      }		
	    return ret;
    }

    /// <summary>
    /// Sets the offset of the <see cref="Anchor"/>.
    /// </summary>
    /// <remarks>
    /// The horizontal and vertical <see cref="Alignment"/> is automatically set to 
    /// <see cref="Alignment.Offset"/>.
    /// </remarks>
    /// <param name="pos">The new offset to set.</param>
    public void SetPosition(Vector2 pos) {
      vertical = Alignment.Offset;
      horizontal = Alignment.Offset;
      x = pos.X;
      y = pos.Y;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
