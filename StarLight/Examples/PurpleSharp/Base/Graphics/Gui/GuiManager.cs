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

using Purple.Graphics.TwoD;
using Purple.Graphics.States;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// This class manages drawing <see cref="GuiElements"/>. 
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para>  
  ///   <para>Last Update: 0.6</para>
  /// <para>A <see cref="GuiManager"/> is a special kind of <see cref="GuiGroup"/> that 
  /// shouldn't be added to other groups or gui managers.</para>
  /// </remarks>
  //=================================================================
	public class GuiManager : GuiGroup
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The element that has the focus.
    /// </summary>
    public IGuiElement FocusElement {
      get {
        return focusElement;
      }
      set {
        focusElement = value;
      }
    }
    IGuiElement focusElement = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
		/// <summary>
		/// Creates a new instance of a <see cref="GuiManager"/>.
		/// </summary>
    public GuiManager()
		{ 
      size = QuadManager.Instance.TargetSize;
		}

    /// <summary>
    /// Returns the default instance of a <see cref="GuiManager"/>.
    /// </summary>
    public static GuiManager Instance {
      get {
        return instance;
      }
    }
    static GuiManager instance = new GuiManager();
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
      // fill quad manager with quads from gui elements
      base.OnRender(deltaTime);

      // draw all quads
      QuadManager.Instance.OnRender(deltaTime);
    }

    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button that is pressed or released.</param>
    /// <param name="pressed">Flag that indicates if button is pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnMouse(Purple.Math.Vector3 position, Purple.Input.MouseButton button, bool pressed) {
      return base.OnMouse (position, button, pressed);
    }

    /// <summary>
    /// Method that handles characters entered via the keyboard.
    /// </summary>
    /// <param name="keyChar">The entered character.</param>
    public override bool OnChar(char keyChar) {
      if (focusElement != null) {
        focusElement.OnChar( keyChar );
        return true;
      }
      return false;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
