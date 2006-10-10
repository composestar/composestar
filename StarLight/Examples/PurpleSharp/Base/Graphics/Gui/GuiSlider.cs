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
using Purple.Input;
using Purple.Graphics.Core;
using Purple.Graphics.TwoD;

namespace Purple.Graphics.Gui {
  /// <summary>
  /// Changed event for sliders.
  /// </summary>
  public delegate void SliderChanged(IGuiProgressBar progressBar);

  //=================================================================
  /// <summary>
  /// The abstract interface for a gui slider.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
  public interface IGuiSlider : IGuiProgressBar {
    /// <summary>
    /// Event that is fired if the slider gets changed.
    /// </summary>
    event SliderChanged Changed;
  }

  //=================================================================
  /// <summary>
  /// A simple slider input control.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class GuiSlider : GuiProgressBar, IGuiSlider {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ButtonLogic buttonLogic = new ButtonLogic();
    ITexture2d textureNormal;
    ITexture2d textureHovered;
    ITexture2d texturePressed;
    ITexture2d textureDisabled;

    /// <summary>
    /// Event that is fired if the slider gets changed.
    /// </summary>
    public event SliderChanged Changed;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Create a new guiSlider object.
    /// </summary>
    /// <param name="normal">The standard texture.</param>
    /// <param name="hovered">The texture used if the slider is hovered.</param>
    /// <param name="pressed">The texture used if the slider is pressed.</param>
    /// <param name="disabled">The texture used if the slider is disabled.</param>
    public GuiSlider(ITexture2d normal, ITexture2d hovered, ITexture2d pressed, ITexture2d disabled) 
    : base(normal) {
      this.textureNormal = normal;
      this.textureHovered = hovered;
      this.texturePressed = pressed;
      this.textureDisabled = disabled;
      this.Direction = Direction.Left;
    }

    /// <summary>
    /// Create a new guiSlider object.
    /// </summary>
    /// <param name="normal">The standard texture.</param>
    /// <param name="hovered">The texture used if the slider is hovered.</param>
    public GuiSlider(ITexture2d normal, ITexture2d hovered) 
      : this( normal, hovered, normal, normal) {
    }

    /// <summary>
    /// Create a new guiSlider object.
    /// </summary>
    /// <param name="normal">The standard texture.</param>
    public GuiSlider(ITexture2d normal) 
      : this( normal, normal, normal, normal) {
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
    public override bool OnMouse(Vector3 position, MouseButton button, bool pressed) {
      ButtonState newState = buttonLogic.Update(this, position, button, pressed);
      UpdateState(newState);
      if (newState == ButtonState.Pressed || newState == ButtonState.PressedHover) {
        Vector2 p = this.InverseTransform(position.Vector2);
        float newVal = this.Total*p.X;
        if (Current != newVal) {
          Current = newVal;
          if (this.Changed != null)
            Changed(this);
        }
      }
      return false;
    }

    private void UpdateState(ButtonState newState) {
      if (!Enabled) {
        Quad.Texture = this.textureDisabled;
      } else {
        switch (newState) {
          case ButtonState.Hover:
            Quad.Texture = this.textureHovered;
            break;
          case ButtonState.PressedHover:
            Quad.Texture = this.texturePressed;
            break;
          case ButtonState.Pressed:
            this.Quad.Texture = this.textureHovered;
            break;
          case ButtonState.Normal:
            this.Quad.Texture = this.textureNormal;
            break;
        }
      }
      //this.buttonState = newState;
    }
    /// <summary>
    /// Returns the size of the gui element.
    /// </summary>
    public override Vector2 Size { 
      get {
        return Quad.TextureSize;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}


