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
using System.Collections;
using Purple.Math;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// An abstract interface for a <see cref="IGuiGroup"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  ///   <para>Last Update: 0.6</para>
  /// <para>A gui group is an <see cref="IGuiParentElement"/>, that exposes the 
  /// underlaying child objects to the public. </para>
  /// </remarks>
  //=================================================================
  public interface IGuiGroup : IGuiParentElement, IGuiGroupNode {
  }

  //=================================================================
  /// <summary>
  /// A standard implementation of the <see cref="IGuiGroup"/> inteface.
  /// </summary>
  /// <remarks>
  /// <para>This clas represents a simple gui group. You can use this class 
  /// for grouping <see cref="IGuiElement"/>s and for creating more complex 
  /// gui elements based on a gui group.</para>
  /// <para>If you want to create a gui element that is based on several other 
  /// gui elements, but you don't want to use the standard grouping implementation (<c>Children</c>, ...) of 
  /// this class, use the <see cref="GuiParentElement"/> class.</para>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public class GuiGroup : GuiParentElement, IGuiGroup
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Access to the list of children for a certain <see cref="IGuiGroupNode"/>.
    /// </summary>
    public GuiElements Children { 
      get {
        return children;
      }
      set {
        if (children != value) {
          children = value;
          children.Parent = this;
        }
      }
    }
    private GuiElements children;

    /// <summary>
    /// Returns the <see cref="IGuiElement"/> with the specified index.
    /// </summary>
    public IGuiElement this[int index] {
      get {
        return children[index];
      }
    }

    /// <summary>
    /// Returns the size of the current <see cref="IGuiElement"/>.
    /// </summary>
    public override Vector2 Size {
      get {
        if (size == Vector2.Zero)
          Log.Warning("Size of GuiGroup shouldn't be zero!");
        return size;
      }
    }
    /// <summary>
    /// Size of the current <see cref="GuiGroup"/>.
    /// </summary>
    [System.CLSCompliant(false)]
    protected Vector2 size = Vector2.Zero;

    /// <summary>
    /// Returns the number of children.
    /// </summary>
    public int Count {
      get {
        return children.Count;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="GuiGroup"/>.
    /// </summary>
    /// <remarks><note type="note">Don't forget to set size!</note>
    /// </remarks>
    public GuiGroup() {    
      Children = new GuiElements();
    }

    /// <summary>
    /// Creates a new instance of a <see cref="GuiGroup"/>.
    /// </summary>
    /// <param name="size">Size of <see cref="GuiGroup"/>.</param>
    public GuiGroup(Vector2 size) {
      Children = new GuiElements();
      this.size = size;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public override void OnRender(float deltaTime) {
      children.OnRender(deltaTime);
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="key">The key, whose status was changed.</param>
    /// <param name="pressed">Flag that indicates if the key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnKey(Purple.Input.Key key, bool pressed) {
      return children.OnKey(key, pressed);
    }

    /// <summary>
    /// Returns the <see cref="IGuiElement"/> by name.
    /// </summary>
    public IGuiElement this[string name] {
      get {
        return children[name];
      }
    }

    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button that is pressed or released.</param>
    /// <param name="pressed">Flag that indicates if button is pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnMouse(Vector3 position, Purple.Input.MouseButton button, bool pressed) {
      return children.OnMouse(position, button, pressed);
    }

    /// <summary>
    /// Adds a guiElement to the front of a list.
    /// </summary>
    /// <param name="element">The element to add.</param>
    public void Prepend(IGuiElement element) {
      Children.Prepend(element);
    }

    /// <summary>
    /// Adds a guiElement to the list.
    /// </summary>
    /// <param name="element">Element to add.</param>
    public void Add(IGuiElement element) {
      Children.Add(element);
    }

    /// <summary>
    /// Adds a collection of gui elements.
    /// </summary>
    /// <param name="elements">The collection containing the gui elements.</param>
    public void Add(ICollection elements) {
      Children.Add( elements );
    }

    /// <summary>
    /// Removes a certain guiElement from the list.
    /// </summary>
    /// <param name="element">Element to remove.</param>
    public void Remove(IGuiElement element) {
      Children.Remove(element);
    }

    /// <summary>
    /// Replaces a certain guiElement.
    /// </summary>
    /// <param name="element">The old element.</param>
    /// <param name="newElement">The new element.</param>
    public void Replace(IGuiElement element, IGuiElement newElement) {
      Children.Replace(element, newElement);
    }

    /// <summary>
    /// Returns the index of a certain guiElement.
    /// </summary>
    /// <param name="element">The guiElement to search.</param>
    /// <returns>The index of the specified element or -1 if not found.</returns>
    public int Find(IGuiElement element) {
      return Children.Find(element);
    }

    /// <summary>
    /// Inserts a guiElement at a certain index.
    /// </summary>
    /// <param name="element">Element to insert.</param>
    /// <param name="index">Index at which to insert.</param>
    public void Insert(IGuiElement element, int index) {
      Children.Insert(element, index);
    }

    /// <summary>
    /// Removes all <see cref="IGuiElement"/>s.
    /// </summary>
    public void Clear() {
      Children.Clear();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
