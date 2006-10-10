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
  /// This class represents a typed collection of <see cref="IGuiElement"/>s.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  ///   <para>Last Update: 0.6</para>
  /// </remarks>
  //=================================================================
  [Purple.Scripting.Resource.ShortCut(typeof(IGuiElement))]
  public class GuiElements {
    //---------------------------------------------------------------
    #region Variables & Properties
    //---------------------------------------------------------------
    ArrayList list = new ArrayList();
    IGuiParentNode parent = null;
    
    /// <summary>
    /// Access to the parent element of the contained <see cref="IGuiElement"/>s.
    /// </summary>
    /// <remarks>
    /// If a new parent is assigned, the parent of all contained gui elements will be 
    /// updated.
    /// </remarks>
    public IGuiParentNode Parent {
      get {
        return parent;
      }
      set {
        if (parent != value) {
          parent = value;
          for (int i=0; i<list.Count; i++)
            (list[i] as IGuiElement).Parent = parent;
        }
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new collection of <see cref="GuiElement"/> objects.
    /// </summary>
    public GuiElements() {
    }

    /// <summary>
    /// Creates a new <see cref="GuiElements"/> object that will be inizialized by 
    /// an array of <see cref="IGuiElement"/> objects.
    /// </summary>
    /// <param name="elements">Array of <see cref="IGuiElement"/> objects.</param>
    public GuiElements(IGuiElement[] elements) {
      list.AddRange(elements);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds a new <see cref="IGuiElement"/> to the list.
    /// </summary>
    /// <remarks>
    /// The parent of the element is changed to the parent 
    /// assigned to the current collection.
    /// </remarks>
    /// <param name="element">Element to add.</param>
    public void Add(IGuiElement element) {
      element.Parent = parent;
      list.Add( element );
    }

    /// <summary>
    /// Adds a collection of gui elements.
    /// </summary>
    /// <remarks>
    /// The parent of the element is changed to the parent 
    /// assigned to the current collection.
    /// </remarks>
    /// <param name="elements">The collection containing the gui elements.</param>
    public void Add(ICollection elements) {
      foreach( IGuiElement element in elements)
        Add(element);
    }

    /// <summary>
    /// Adds a guiElement to the front of a list.
    /// </summary>
    /// <param name="element">The element to add.</param>
    public void Prepend(IGuiElement element) {
      list.Insert(0, element);
    }


    /// <summary>
    /// Removes a certain <see cref="IGuiElement"/> to the list.
    /// </summary>
    /// <param name="element">to remove</param>
    public void Remove(IGuiElement element) {
      list.Remove(element);
    }

    /// <summary>
    /// Replaces a certain guiElement.
    /// </summary>
    /// <param name="element">The old element.</param>
    /// <param name="newElement">The new element.</param>
    public void Replace(IGuiElement element, IGuiElement newElement) {
      int index = Find(element);
      if (index == -1)
        throw new ArgumentOutOfRangeException("Element can't be replaced because it isn't contained by the collection!");
      list[index] = newElement;
    }

    /// <summary>
    /// Returns the index of a certain guiElement.
    /// </summary>
    /// <param name="element">The guiElement to search.</param>
    /// <returns>The index of the specified element or -1 if not found.</returns>
    public int Find(IGuiElement element) {
      return list.IndexOf(element);
    }

    /// <summary>
    /// Inserts a guiElement at a certain index.
    /// </summary>
    /// <param name="element">Element to insert.</param>
    /// <param name="index">Index at which to insert.</param>
    public void Insert(IGuiElement element, int index) {
      list.Insert(index, element);
    }

    /// <summary>
    /// Removes all <see cref="IGuiElement"/>s from the list.
    /// </summary>
    public void Clear() {
      list.Clear();
    }

    /// <summary>
    /// Returns the number of elements contained by the collection.
    /// </summary>
    public int Count {
      get {
        return list.Count;
      }
    }

    /// <summary>
    /// Returns the <see cref="IGuiElement"/> at a certain index.
    /// </summary>
    public IGuiElement this[int index] {
      get {
        return (IGuiElement)list[index];
      }
    }

    /// <summary>
    /// Returns the <see cref="IGuiElement"/> by name.
    /// </summary>
    public IGuiElement this[string name] {
      get {
        string[] names = name.Split('.');
        IGuiElement element = Find(names[0]);
        for (int i=1; i<names.Length; i++) {
          IGuiGroupNode group = (IGuiGroupNode)element;
          element = group[names[i]];
        }
        return element;
      }
    }

    /// <summary>
    /// Converts the <see cref="GuiElements"/> collection to an array of 
    /// <see cref="IGuiElement"/> objects.
    /// </summary>
    /// <returns>The array of <see cref="IGuiElement"/> objects.</returns>
    public IGuiElement[] ToArray() {
      return (IGuiElement[])list.ToArray(typeof(IGuiElement));
    }

    /// <summary>
    /// Returns the child with the specified name or null if not found.
    /// </summary>
    /// <param name="name">Name of child.</param>
    /// <returns>The child with the specified name or null if not found.</returns>
    public IGuiElement Find(string name) {
      // Todo: optimize
      for (int i=0; i<list.Count; i++) {
        IGuiElement element = (IGuiElement)list[i];
        if (element.Name != null && element.Name.Equals(name))
          return element;
      }
      return null;
    }

    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public void OnRender(float deltaTime) {
      if (parent == null || (parent as IGuiElement).AbsoluteVisible) {
        for (int i=0; i<list.Count; i++) {
          IGuiElement element = (IGuiElement)list[i];
          if (element.Visible)
            element.OnRender(deltaTime);
        }
      }
    }

    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button that is pressed or released.</param>
    /// <param name="pressed">Flag that indicates if button is pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public bool OnMouse(Vector3 position, Purple.Input.MouseButton button, bool pressed) {
      ArrayList cloned = (ArrayList)list.Clone();
      for (int i=cloned.Count-1; i>=0; i--) {
        IGuiElement element = (cloned[i] as IGuiElement);
        if (element.Enabled && element.OnMouse(position, button, pressed))
          return true;
      }
      return false;
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="key">The current key that was pressed or released.</param>
    /// <param name="pressed">Flagthat indicates if key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public bool OnKey(Purple.Input.Key key, bool pressed) {
      ArrayList cloned = (ArrayList)list.Clone();
      for (int i=cloned.Count-1; i>=0; i--) {
        IGuiElement element = (cloned[i] as IGuiElement);
        if (element.Enabled && element.OnKey(key, pressed))
          return true;
      }
      return false;
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="character">The character that was entered.</param>
    public bool OnChar(char character) {
      ArrayList cloned = (ArrayList)list.Clone();
      for (int i=cloned.Count-1; i>=0; i--) {
        IGuiElement element = (cloned[i] as IGuiElement);
        if (element.Enabled && element.OnChar(character))
          return true;
      }
      return false;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
