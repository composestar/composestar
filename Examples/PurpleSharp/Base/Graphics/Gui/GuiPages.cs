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

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// The GuiPages element can hold several pages and switch between them.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class GuiPages : GuiParentElement {
    //---------------------------------------------------------------
    #region Variables & Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The index of the active page.
    /// </summary>
    public int CurrentPage {
      get {
        return currentPage;
      }
      set {
        if (currentPage < pages.Length)
          currentPage = value;
      }
    }
    int currentPage = 0;

    /// <summary>
    /// The pages collection;
    /// </summary>
    public IGuiElement[] Pages {
      get {
        return pages;
      }
    }
    IGuiElement[] pages;

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
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------  
    /// <summary>
    /// The standard constructor of a <see cref="GuiPages"/>.
    /// </summary>
    /// <param name="pages">The number of pages.</param>
    public GuiPages(int pages) {
      this.pages = new IGuiElement[pages];
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds a page to the GuiPages collection.
    /// </summary>
    /// <param name="group">Group to add.</param>
    public void Add(IGuiElement group) {
      Resize(pages.Length + 1);
      pages[pages.Length - 1] = group;
    }

    private void Resize(int count) {
      IGuiElement[] newArray = new IGuiElement[count];
      Array.Copy(pages, 0, newArray, 0, pages.Length);
      pages = newArray;
    }

    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public override void OnRender(float deltaTime) {
      pages[ currentPage ].OnRender(deltaTime);
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="key">The key, whose status was changed.</param>
    /// <param name="pressed">Flag that indicates if the key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnKey(Purple.Input.Key key, bool pressed) {
      return pages[ currentPage ].OnKey(key, pressed);
    }

    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button that is pressed or released.</param>
    /// <param name="pressed">Flag that indicates if button is pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnMouse(Vector3 position, Purple.Input.MouseButton button, bool pressed) {
      return pages[ currentPage ].OnMouse(position, button, pressed);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}