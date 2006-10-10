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

using Purple.Graphics.Core;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// An overlay that can wrap another gui element and passes on mouse events.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
	public class Overlay : Image
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ThinButtonLogic buttonLogic;
    ITexture2d normal;
    ITexture2d hover;
    ITexture2d pressed;
    ITexture2d disabled;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new overlay element.
    /// </summary>
    /// <param name="normal">The image to use for normal state.</param>
    /// <param name="pressed">The image to use for pressed state.</param>
    /// <param name="hover">The image to use for mouseOver state.</param>
    /// <param name="disabled">The image to use for disabled state.</param>
    public Overlay(ITexture2d normal, ITexture2d pressed, ITexture2d hover, ITexture2d disabled) : base(normal) {
      buttonLogic = new ThinButtonLogic();
      this.normal = normal;
      this.pressed = pressed;
      this.hover = hover;
      this.disabled = disabled; 
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button that is pressed or released.</param>
    /// <param name="pressed">Flag that indicates if button is pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnMouse(Purple.Math.Vector3 position, Purple.Input.MouseButton button, bool pressed) {
      ButtonState newState = buttonLogic.Update(this, position, button, pressed);
      UpdateState(newState);
      return false;
    }

    private void UpdateState(ButtonState newState) {
      if (!this.Enabled)
        Quad.Texture = disabled;
      else /*if (newState != buttonState)*/ {
        switch (newState) {
          case ButtonState.Normal:
            Quad.Texture = normal;
            break;
          case ButtonState.Pressed:
            Quad.Texture = hover;
            break;
          case ButtonState.Hover:
            Quad.Texture = hover;
            break;
          case ButtonState.PressedHover:
            Quad.Texture = pressed;
            break;
          default:
            throw new NotSupportedException("Unknown ButtonState: " + newState.ToString());
        }
      }
    }    

    /// <summary>
    /// Calculates the position of the overlay by using the guiElement that should be covered.
    /// </summary>
    /// <param name="element">The element to cover.</param>
    public void Cover( IGuiElement element) {
      this.Position = element.Position;
      this.Anchor = element.Anchor;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
