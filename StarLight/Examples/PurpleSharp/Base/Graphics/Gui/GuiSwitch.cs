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
using Purple.Input;
using Purple.Graphics;
using Purple.Graphics.Core;
using Purple.Graphics.TwoD;

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// The abstract interface for a gui switch (checkbox).
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
  public interface IGuiSwitch: IGuiElement, IMouseHandler {
    /// <summary>
    /// Current state of the switch.
    /// </summary>
    bool Checked { get; set; }

    /// <summary>
    /// Event that is fired when the switch gets changed.
    /// </summary>
    event VoidEventHandler Changed;

    /// <summary>
    /// Event that is fired when switch is hovered.
    /// </summary>
    event VoidEventHandler Hover;

    /// <summary>
    /// Event that is fired when switch gets left.
    /// </summary>
    event VoidEventHandler Leave;
  }

  //=================================================================
  /// <summary>
  /// Implementation of a gui switch (checkbox).
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
  public class GuiSwitch : Image, IGuiSwitch {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ButtonLogic buttonLogic;
    ITexture2d textureOn;
    ITexture2d textureOff;
    ITexture2d textureDisabledOn;
    ITexture2d textureDissabledOff;
    ITexture2d textureHoverOn;
    ITexture2d textureHoverOff;

    /// <summary>
    /// Current buttonState of the guiSwitch.
    /// </summary>
    public ButtonState ButtonState { 
      get {
        return buttonState;
      }
    }
    ButtonState buttonState;

    /// <summary>
    /// Current state of the switch.
    /// </summary>
    public bool Checked { 
      get {
        return checkedValue;
      }
      set {
        checkedValue = value;
        this.UpdateState(buttonState);
      }
    }
    bool checkedValue = true;

    /// <summary>
    /// Event that is fired when the switch gets changed.
    /// </summary>
    public event VoidEventHandler Changed;

    /// <summary>
    /// Event that is fired when button is pressed down.
    /// </summary>
    public event VoidEventHandler Down {
      add {
        buttonLogic.Down += value;
      }
      remove {
        buttonLogic.Down -= value;
      }
    }

    /// <summary>
    /// Event that is fired when button is released.
    /// </summary>
    public event VoidEventHandler Up {
      add {
        buttonLogic.Up += value;
      }
      remove {
        buttonLogic.Up -= value;
      }
    }

    /// <summary>
    /// Event that is fired when button is hovered.
    /// </summary>
    public event VoidEventHandler Hover {
      add {
        buttonLogic.Hover += value;
      }
      remove {
        buttonLogic.Hover -= value;
      }
    }

    /// <summary>
    /// Event that is fired when button gets left.
    /// </summary>
    public event VoidEventHandler Leave {
      add {
        buttonLogic.Leave += value;
      }
      remove {
        buttonLogic.Leave -= value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new gui switch object.
    /// </summary>
    /// <param name="textureOn">Texture to use if switch is turned on.</param>
    /// <param name="textureOff">Texture to use if switch is turned off.</param>
    public GuiSwitch(ITexture2d textureOn, ITexture2d textureOff) 
      : this(textureOn, textureOff, textureOn, textureOff, textureOn, textureOff) {
    }

    /// <summary>
    /// Creates a new gui switch object.
    /// </summary>
    /// <param name="textureOn">Texture to use if switch is turned on.</param>
    /// <param name="textureOff">Texture to use if switch is turned off.</param>
    /// <param name="textureHoverOn">Texture to use if switch is hovered and turned on.</param>
    /// <param name="textureHoverOff">Texture to use if switch is hovered and turned off.</param>
    public GuiSwitch(ITexture2d textureOn, ITexture2d textureOff, 
      ITexture2d textureHoverOn, ITexture2d textureHoverOff)
    : this(textureOn, textureOff, textureHoverOn, textureHoverOff, textureOn, textureOff) {
    }

    /// <summary>
    /// Creates a new gui switch object.
    /// </summary>
    /// <param name="textureOn">Texture to use if switch is turned on.</param>
    /// <param name="textureOff">Texture to use if switch is turned off.</param>
    /// <param name="textureHoverOn">Texture to use if switch is hovered and turned on.</param>
    /// <param name="textureHoverOff">Texture to use if switch is hovered and turned off.</param>
    /// <param name="textureDisabledOn">Texture to use if switch is disabled and turned on.</param>
    /// <param name="textureDisabledOff">Texture to use if switch is disabled and turned off.</param>
    public GuiSwitch(ITexture2d textureOn, ITexture2d textureOff, ITexture2d textureHoverOn, 
      ITexture2d textureHoverOff, ITexture2d textureDisabledOn, ITexture2d textureDisabledOff) 
    : base(textureOn) {

      if (textureOn == null)
        throw new ArgumentNullException("textureOn");
      if (textureOff == null)
        throw new ArgumentNullException("textureOff");

      this.textureOn = textureOn;
      this.textureOff = textureOff;
      this.textureDisabledOn = textureDisabledOn;
      this.textureDissabledOff = textureDisabledOff;
      this.textureHoverOn = textureHoverOn;
      this.textureHoverOff = textureHoverOff;

      buttonLogic = new ButtonLogic();
      checkedValue = true;
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
      return false;
    }

    private void UpdateState(ButtonState newState) {
      if (!Enabled) {
        if (checkedValue)
          Quad.Texture = this.textureDisabledOn;
        else
          Quad.Texture = this.textureDissabledOff;
      } else {
        switch (newState) {
          case ButtonState.Hover:
            if (buttonState == ButtonState.PressedHover) {
              checkedValue = !checkedValue;
              if (this.Changed != null)
                this.Changed();       
            }
            if (checkedValue)
              Quad.Texture = this.textureHoverOn;
            else
              Quad.Texture = this.textureHoverOff;
            break;
          case ButtonState.PressedHover:
            if (checkedValue)
              Quad.Texture = this.textureHoverOff;
            else 
              Quad.Texture = this.textureHoverOn;
            break;
          case ButtonState.Pressed:
            if (checkedValue)
              Quad.Texture = this.textureHoverOn;
            else 
              Quad.Texture = this.textureHoverOff;
            break;
          case ButtonState.Normal:
            if (checkedValue)
              Quad.Texture = this.textureOn;
            else 
              Quad.Texture = this.textureOff;
            break;
        }
      }
      this.buttonState = newState;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
