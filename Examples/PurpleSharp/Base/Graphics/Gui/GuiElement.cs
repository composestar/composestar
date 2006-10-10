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
using Purple.Graphics.TwoD;
using Purple.Input;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// An abstract interface for a gui element.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  ///   <para>Last Change: 0.7</para>
  /// <para>The <see cref="IGuiElement"/> represents the base interface for all gui elements. For faster 
  /// creation of specialised gui elements, the abstract class <see cref="GuiElement"/> is provided, that 
  /// implements some standard functionatliy of an <see cref="IGuiElement"/>.
  /// </para>  
  /// <para>The functionality of the gui element is based on a number of smaller, more 
  /// special interfaces. This makes it possible to reuse code that is just based on a certain 
  /// part interface for objects that don't inherit the whole <see cref="IGuiElement"/> interface.</para>
  /// <seealso cref="GuiElement"/>
  /// <seealso cref="IGuiGroup"/>
  /// <seealso cref="IGuiParentElement"/>
  /// </remarks>
  //=================================================================
  public interface IGuiElement : 
    IGuiPosition, IGuiSize, IGuiNode, IGuiAnchor, IGuiRotatable, IGuiVisible, IGuiEnabled,
    IGuiScaleable, IGuiAlpha, IGuiName, IMouseHandler, IKeyboardHandler, IRender {
    /// <summary>
    /// Tests if the given guiElement contains the given point.
    /// </summary>
    /// <param name="point">The point to test for.</param>
    /// <returns>True if the point is within the guiElement.</returns>
    bool ContainsPoint(Vector2 point);
  }

  //=================================================================
  /// <summary>
  /// The abstract standard implementation of an <see cref="IGuiElement"/>.
  /// </summary>
  /// <remarks>
  /// <para>This class implements some standard behaviour of an <see cref="IGuiElement"/> 
  /// and may be used for convenience to create other, more specialised gui elements.</para>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  ///   <para>Last Change: 0.7</para>
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter(typeof(GuiElementConverter))]
  public abstract class GuiElement : IGuiElement {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Name of the current gui element.
    /// </summary>
    public string Name {
      get {
        return name;
      }
      set {
        name = value;
      }
    }
    private string name = null;

    /// <summary>
    /// The position of the element relative to its parent element.
    /// </summary>
    public Vector2 Position { 
      get {
        return position;
      }
      set {
        position = value;
      }
    }
    private Vector2 position = Vector2.Zero;

    /// <summary>
    /// Visibility of the gui element.
    /// </summary>
    public bool Visible { 
      get {
        return visible;
      }
      set {
        visible = value;
      }
    }
    bool visible = true;

    /// <summary>
    /// Is gui element visible by taking in account its parents.
    /// </summary>
    public bool AbsoluteVisible { 
      get {
        bool curVis = visible;
        IGuiParentElement node = (IGuiParentElement)Parent;
        while (curVis && node != null) {
          curVis = node.Visible;
          node = (IGuiParentElement)node.Parent;
        }
        return curVis;
      }
    }
    /// <summary>
    /// Flag that indicates if the element is enabled or not.
    /// </summary>
    public bool Enabled { 
      get {
        return enabled;
      }
      set {
        enabled = value;
      }
    }
    bool enabled = true;

    /// <summary>
    /// Flag that indicates if the element is enabled taking its parents into account.
    /// </summary>
    public bool AbsoluteEnabled { 
      get {
        bool curEnabled = enabled;
        IGuiParentElement node = (IGuiParentElement)Parent;
        while (curEnabled && node != null) {
          curEnabled = node.Enabled;
          node = (IGuiParentElement)node.Parent;
        }
        return curEnabled;
      }
    }

    /// <summary>
    /// Returns the absolute position of the gui element.
    /// </summary>
    public Vector2 AbsolutePosition {
      get {
        return this.CalcAbsolutePosition( Vector2.Zero );
      }
    }

    /// <summary>
    /// Returns the parent of the current element.
    /// </summary>
    /// <remarks>
    /// If this element is the root node and therefore has no parent, null is returned.
    /// </remarks>
    //[System.ComponentModel.TypeConverter(typeof(GuiElementConverter))]
    public IGuiParentNode Parent { 
      get {
        return parent;
      }
      set {
        parent = value;
      }
    }
    IGuiParentNode parent = null;

    /// <summary>
    /// Returns the root node of the current element.
    /// </summary>
    /// <remarks>
    /// If the current element is the root, the current element is returned.
    /// </remarks>
    public IGuiNode Root { 
      get {
        if (this.Parent == null)
          return this;
        return this.Parent.Root;
      }
    }

    /// <summary>
    /// Returns the size of the element relative to its parent element.
    /// </summary>
    public abstract Vector2 Size { get; }

    /// <summary>
    /// Returns the absolute size of the current element.
    /// </summary>
    public Vector2 AbsoluteSize {
      get {
        return Vector2.MultiplyElements(Size, AbsoluteScale);
      }
    }

    /// <summary>
    /// Access to the used <see cref="Anchor"/> for the current element.
    /// </summary>
    public Anchor Anchor {
      get {
        return anchor;
      }
      set {
        anchor = value;
      }
    }
    Anchor anchor = Anchor.TopLeft;

    /// <summary>
    /// Access to the relative rotation of the element.
    /// </summary>
    /// <remarks>
    /// The rotation is specified in radians and represents a rotation around the z-axis.
    /// </remarks>
    public float Rotation {
      get {
        return rotation;
      }
      set {
        rotation = value;
      }
    }
    private float rotation;

    /// <summary>
    /// Calculates the absolute rotation of the element.
    /// </summary>
    /// <remarks>
    /// The rotation is specified in radians and represents a rotation around the z-axis.
    /// </remarks>
    public float AbsoluteRotation {
      get {
        float rot = rotation;
        IGuiParentElement node = (IGuiParentElement)Parent;
        while (node != null) {
          rot += node.Rotation;
          node = (IGuiParentElement)node.Parent;
        }
        return rot;
      }
    }

    /// <summary>
    /// The relative scale of the gui element.
    /// </summary>
    /// <remarks>
    /// The default value is: <c>Vector2(1.0f, 1.0f)</c>.
    /// </remarks>
    public Vector2 Scale {
      get {
        return scale;
      }
      set {
        scale = value;
      }
    }
    private Vector2 scale = new Vector2(1.0f, 1.0f);

    /// <summary>
    /// The absolute scale of the gui element.
    /// </summary>
    public Vector2 AbsoluteScale {
      get {
        Vector2 scl = Scale;
        IGuiParentElement node = (IGuiParentElement)Parent;
        while (node != null) {
          scl = Vector2.MultiplyElements(scl, node.Scale);
          node = (IGuiParentElement)node.Parent;
        }
        return scl;
      }
    }

    /// <summary>
    /// The relative transparency of the gui element.
    /// </summary>
    /// <remarks>
    /// The default value is: <c>1.0f</c>.
    /// </remarks>
    public float Alpha {
      get {
        return alpha;
      }
      set {
        alpha = value;
      }
    }
    float alpha = 1.0f;

    /// <summary>
    /// Calculates the absolute transparency of the current gui element.
    /// </summary>
    public float AbsoluteAlpha {
      get {
        float al = alpha;
        IGuiParentElement node = (IGuiParentElement)Parent;
        while (node != null) {
          al = al*node.Alpha;
          node = (IGuiParentElement)node.Parent;
        }
        return al;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="GuiElement"/>.
    /// </summary>
		public GuiElement()
		{
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Moves the element a certain amount.
    /// </summary>
    /// <param name="vector">Vector which is added to the current position.</param>
    public void Move(Vector2 vector) {
      Position += vector;
    }

    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button that is pressed or released.</param>
    /// <param name="pressed">Flag that indicates if button is pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public virtual bool OnMouse(Vector3 position, MouseButton button, bool pressed) {
      return false;
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="key">The key, whose status was changed.</param>
    /// <param name="pressed">Flag that indicates if the key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public virtual bool OnKey(Key key, bool pressed) {
      return false;
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="keyChar">The character that was entered via the keyboard.</param>
    public virtual bool OnChar(char keyChar) {
      return false;
    }

    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public abstract void OnRender(float deltaTime);

    /// <summary>
    /// Tests if a given point is within the gui window.
    /// </summary>
    /// <param name="point">Point to test for.</param>
    /// <returns>True if the point is within the gui window.</returns>
    public bool ContainsPoint(Vector2 point) {
      Vector2 pt = InverseTransform(point);

      return (
        pt.X >= 0.0f && pt.Y >= 0.0f &&
        pt.X <= 1.0f &&
        pt.Y <= 1.0f );
    }

    /// <summary>
    /// Transforms a given point back into the space of the gui element.
    /// </summary>
    /// <param name="point">Point on screen to transform back.</param>
    /// <returns>The point on the screen transformed into the space of the gui element.</returns>
    public Vector2 InverseTransform(Vector2 point) {
      Vector2 absPos = this.AbsolutePosition;
      Vector2 absSize = this.AbsoluteSize;
      Vector2 p = new Vector2((point.X - absPos.X), (point.Y - absPos.Y));
      
      // transform point back to unrotated position
      float angle = -this.AbsoluteRotation;
      Vector2 dx = QuadManager.Instance.RotateUnit(p, angle);

      return Vector2.DivideElements(dx, absSize);
    }

    /// <summary>
    /// Calculates the absolute position from a relative position within the guiElement.
    /// </summary>
    /// <param name="relativePosition">The relative position within the guiElement.</param>
    /// <returns>The absolute position.</returns>
    public Vector2 CalcAbsolutePosition(Vector2 relativePosition) {
      Vector2 scaledSize = Vector2.MultiplyElements(Size, scale);
      Vector2 anchorPosition = QuadManager.Instance.RotateUnit(anchor.GetPosition(scaledSize), rotation);
      Vector2 relativePos = (position + relativePosition) - anchorPosition;
      string anchorName = this.anchor.Name;
      IGuiParentElement node = (IGuiParentElement)Parent;
      while (node != null) {
        if (node.LinkAnchorCount != 0 && node.LinkAnchors[anchorName] != null)
          anchorPosition = Vector2.MultiplyElements(relativePos - node.Anchor.GetPosition(node.Size) + node.LinkAnchors[anchorName].GetPosition(node.Size), node.Scale);
        else
          anchorPosition = Vector2.MultiplyElements(relativePos - node.Anchor.GetPosition(node.Size), node.Scale);
        anchorPosition = QuadManager.Instance.RotateUnit(anchorPosition, node.Rotation);
        relativePos = node.Position + anchorPosition;
        anchorName = node.Anchor.Name;
        node = (IGuiParentElement)node.Parent;
      }
      return relativePos;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
