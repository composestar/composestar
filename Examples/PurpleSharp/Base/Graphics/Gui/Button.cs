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
  /// Enumeration of all possible button states.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para> 
  /// </remarks>
  //=================================================================
  public enum ButtonState {
    /// <summary>
    /// The button is in its normal state and can be pressed.
    /// </summary>
    Normal,
    /// <summary>
    /// The button isn't pressed, but the mouse cursor is currently over the button.
    /// </summary>
    Hover,
    /// <summary>
    /// The button is currently pressed and the mouse cursor is over the button.
    /// </summary>
    PressedHover,
    /// <summary>
    /// The button is pressed but the mouse cursor is not over the button.
    /// </summary>
    Pressed
  }

  //=================================================================
  /// <summary>
  /// The abstract interface for a button.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para> 
  ///   <para>Last Update: 0.6</para>
  /// </remarks>
  //=================================================================
  public interface IButton: IGuiElement, IMouseHandler {
    /// <summary>
    /// Current state of the button.
    /// </summary>
    ButtonState ButtonState { get; }

    /// <summary>
    /// Event that is fired when button is clicked.
    /// </summary>
    event VoidEventHandler Clicked;

    /// <summary>
    /// Event that is fired when button is pressed down.
    /// </summary>
    event VoidEventHandler Down;

    /// <summary>
    /// Event that is fired when button is released.
    /// </summary>
    event VoidEventHandler Up;

    /// <summary>
    /// Event that is fired when button is hovered.
    /// </summary>
    event VoidEventHandler Hover;

    /// <summary>
    /// Event that is fired when button gets left.
    /// </summary>
    event VoidEventHandler Leave;
  }

  
  //=================================================================
  /// <summary>
  /// Reusable logic for a button.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
  internal struct ButtonLogic {
    /// <summary>
    /// Flag that indicates if the button was pressed down.
    /// </summary>
    public bool ButtonDown;
    /// <summary>
    /// Flag that indicates if the mouse was over the button.
    /// </summary>
    public bool WasOver;

    /// <summary>
    /// Event that is fired when button is pressed down.
    /// </summary>
    public event VoidEventHandler Down;

    /// <summary>
    /// Event that is fired when button is released.
    /// </summary>
    public event VoidEventHandler Up;

    /// <summary>
    /// Event that is fired when button is hovered.
    /// </summary>
    public event VoidEventHandler Hover;

    /// <summary>
    /// Event that is fired when button gets left.
    /// </summary>
    public event VoidEventHandler Leave;

    /// <summary>
    /// Updates the button, eventually fires the events and returns the new ButtonState.
    /// </summary>
    /// <param name="element">The element the buttonLogic is calculated for.</param>
    /// <param name="position">The position of the mouse cursor.</param>
    /// <param name="button">The buttons of the mouse.</param>
    /// <param name="pressed">Flag that indicates if mouse was pressed.</param>
    /// <returns>The new buttonState.</returns>
    public ButtonState Update(IGuiElement element, Vector3 position, MouseButton button, bool pressed) {
      bool isOver = element.ContainsPoint(new Vector2(position.X, position.Y));
      if (isOver && !WasOver) {
        if (Hover != null)
          Hover();
      } else if (WasOver && !isOver) {
        if (Leave != null)
          Leave();
      }
      WasOver = isOver;

      if (button == MouseButton.Left && pressed && isOver) {
        ButtonDown = true;
        if (Down != null)
          Down();
      }
      else if (button == MouseButton.Left && !pressed && ButtonDown) {
        ButtonDown = false;
        if (Up != null)
          Up();
      }

      ButtonState newState = ButtonState.Normal;
      if (ButtonDown) {
        if (isOver)
          newState = ButtonState.PressedHover;
        else
          newState = ButtonState.Pressed;
      } else {
        if (isOver)
          newState = ButtonState.Hover;
        else
          newState = ButtonState.Normal;
      }
      return newState;
    }
  }

  //=================================================================
  /// <summary>
  /// Reusable logic for a button.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
  internal struct ThinButtonLogic {
    /// <summary>
    /// Flag that indicates if the button was pressed down.
    /// </summary>
    public bool ButtonDown;
    /// <summary>
    /// Flag that indicates if the mouse was over the button.
    /// </summary>
    public bool WasOver;

    /// <summary>
    /// Updates the button, eventually fires the events and returns the new ButtonState.
    /// </summary>
    /// <param name="element">The element the buttonLogic is calculated for.</param>
    /// <param name="position">The position of the mouse cursor.</param>
    /// <param name="button">The buttons of the mouse.</param>
    /// <param name="pressed">Flag that indicates if mouse was pressed.</param>
    /// <returns>The new buttonState.</returns>
    public ButtonState Update(IGuiElement element, Vector3 position, MouseButton button, bool pressed) {
      bool isOver = element.ContainsPoint(new Vector2(position.X, position.Y));
      WasOver = isOver;

      if (button == MouseButton.Left && pressed && isOver)
        ButtonDown = true;
      else if (button == MouseButton.Left && !pressed && ButtonDown)
        ButtonDown = false;

      ButtonState newState = ButtonState.Normal;
      if (ButtonDown) {
        if (isOver)
          newState = ButtonState.PressedHover;
        else
          newState = ButtonState.Pressed;
      } else {
        if (isOver)
          newState = ButtonState.Hover;
        else
          newState = ButtonState.Normal;
      }
      return newState;
    }
  }

  //=================================================================
  /// <summary>
  /// Implementation of a standard button.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para> 
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
  public class Button : Image, IButton {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ButtonLogic logic = new ButtonLogic();
    ITexture2d normal;
    ITexture2d hover;
    ITexture2d pressed;
    ITexture2d disabled;

    /// <summary>
    /// Current state of the button.
    /// </summary>
    public ButtonState ButtonState { 
      get {
        return buttonState;
      }
    }
    ButtonState buttonState;

    /// <summary>
    /// Event that is fired when button is clicked.
    /// </summary>
    public event VoidEventHandler Clicked;

    /// <summary>
    /// Event that is fired when button is pressed down.
    /// </summary>
    public event VoidEventHandler Down {
      add {
        logic.Down += value;
      }
      remove {
        logic.Down -= value;
      }
    }

    /// <summary>
    /// Event that is fired when button is released.
    /// </summary>
    public event VoidEventHandler Up {
      add {
        logic.Up += value;
      }
      remove {
        logic.Up -= value;
      }
    }

    /// <summary>
    /// Event that is fired when button is hovered.
    /// </summary>
    public event VoidEventHandler Hover {
      add {
        logic.Hover += value;
      }
      remove {
        logic.Hover -= value;
      }
    }

    /// <summary>
    /// Event that is fired when button gets left.
    /// </summary>
    public event VoidEventHandler Leave {
      add {
        logic.Leave += value;
      }
      remove {
        logic.Leave -= value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="Button"/>.
    /// </summary>
    /// <param name="normal">The image to use for normal state.</param>
    /// <param name="pressed">The image to use for pressed state.</param>
    /// <param name="hover">The image to use for mouseOver state.</param>
    /// <param name="disabled">The image to use for disabled state.</param>
    public Button(ITexture2d normal, ITexture2d pressed, ITexture2d hover, ITexture2d disabled) : base(normal){
      if (normal == null)
        throw new ArgumentNullException("normal");
      if (pressed == null)
        throw new ArgumentNullException("pressed");

      this.normal = normal;
      this.pressed = pressed;
      this.disabled = disabled;
      this.hover = hover;
      buttonState = ButtonState.Normal;
    }

    /// <summary>
    /// Creates a new instance of a <see cref="Button"/>.
    /// </summary>
    /// <param name="normal">The image to use for normal state.</param>
    /// <param name="pressed">The image to use for pressed state.</param>
    /// <param name="hover">The image to use for mouseOver state.</param>
    public Button(ITexture2d normal, ITexture2d pressed, ITexture2d hover) :
      this( normal, pressed, hover, normal) {
    }

    /// <summary>
    /// Creates a new instance of a <see cref="Button"/>.
    /// </summary>
    /// <param name="normal">The image to use for normal state.</param>
    /// <param name="pressed">The image to use for pressed state.</param>
    public Button(ITexture2d normal, ITexture2d pressed) :
      this( normal, pressed, normal, normal) {
    }

    /// <summary>
    /// Creates a new instance of a <see cref="Button"/>.
    /// </summary>
    /// <param name="normal">The image to use for normal state.</param>
    public Button(ITexture2d normal) :
      this( normal, normal, normal, normal) {
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
    public override bool OnMouse(Vector3 position, MouseButton button, bool pressed) {    
      ButtonState newState = logic.Update(this, position, button, pressed);
      UpdateState(newState);
      return false;
    }

    private void UpdateState(ButtonState newState) {
      if (!this.Enabled)
        Quad.Texture = disabled;
      else if (newState != buttonState) {
        switch (newState) {
          case ButtonState.Normal:
            Quad.Texture = normal;
            break;
          case ButtonState.Pressed:
            Quad.Texture = hover;
            break;
          case ButtonState.Hover:
              Quad.Texture = hover;

            if (buttonState == ButtonState.PressedHover && Clicked != null)
              Clicked();
            break;
          case ButtonState.PressedHover:
            Quad.Texture = pressed;
            break;
          default:
            throw new NotSupportedException("Unknown ButtonState: " + newState.ToString());
        }
        buttonState = newState;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
