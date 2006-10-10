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
using Purple.Graphics.Core;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// The abstract interface for a gui slider.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
	public class GuiListBox : GuiGroup
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ThinButtonLogic logic = new ThinButtonLogic();
    ButtonState buttonState = ButtonState.Normal;
    Image[] images;
    ButtonState[] states;
    ITexture2d background;
    ITexture2d hover;
    ITexture2d selected;

    /// <summary>
    /// Flag that indicates if multiple items can be selected.
    /// </summary>
    public bool MultiSelect {
      get {
        return multiSelect;
      }
      set {
        multiSelect = value;
      }
    }
    bool multiSelect = false;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new GuiListBox object.
    /// </summary>
    /// <param name="items">The number of items.</param>
    /// <param name="background">The background texture.</param>
    /// <param name="hover">The hover texture.</param>
    /// <param name="selected">The selected texture.</param>
    public GuiListBox(int items, ITexture2d background, ITexture2d hover, ITexture2d selected) {
      this.background = background;
      this.hover = hover;
      this.selected = selected;

      this.images = new Image[items];
      states = new ButtonState[items];
      for (int i=0; i<images.Length; i++) {
        SubTexture subTexture = SubTexture.Create(background, i, items, 1);
        images[i] = new Image( subTexture );
        images[i].Position = Vector2.MultiplyElements( images[i].Size, new Vector2(0.0f, i) );
        this.Children.Add( images[i] );
      }
      this.size = Vector2.MultiplyElements( images[0].Size, new Vector2(1, items) );
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
    public override bool OnMouse(Vector3 position, Purple.Input.MouseButton button, bool pressed) {
      base.OnMouse (position, button, pressed);
      ButtonState newState = logic.Update(this, position, button, pressed);

      UpdateState(newState, position.Vector2);
      return false;
    }

    private void UpdateState(ButtonState newState, Vector2 position) {
      switch (newState) {
        case ButtonState.Normal:
          if (buttonState != ButtonState.Normal) {
            for (int i=0; i<images.Length; i++)
              SetState( i, ButtonState.Normal );
          }
          break;
        case ButtonState.Hover:
          for (int i=0; i<images.Length; i++) {
            if (images[i].ContainsPoint(position))
              SetState( i, ButtonState.Hover);
            else
              SetState( i, ButtonState.Normal);
          }
          break;
        case ButtonState.PressedHover:
          for (int i=0; i<images.Length; i++) {
            if (images[i].ContainsPoint(position) || states[i] == ButtonState.Pressed)
              SetState( i, ButtonState.PressedHover);
          }
          break;
        case ButtonState.Pressed:
          for (int i=0; i<images.Length; i++) {
            if (states[i] == ButtonState.PressedHover)
              SetState( i, ButtonState.Pressed);
          }
          break;
        default:
          throw new NotSupportedException("ButtonState not supported: " + newState.ToString());
      }
      buttonState = newState;
    }

    private void SetState(int index, ButtonState state) {
      if (states[index] != state) {
        switch(state) {
          case ButtonState.Normal:
            (images[index].Quad.Texture as SubTexture).Parent = background;
            break;
          case ButtonState.Hover:
            (images[index].Quad.Texture as SubTexture).Parent = hover;
            break;
          case ButtonState.PressedHover:
            (images[index].Quad.Texture as SubTexture).Parent = selected;
            break;
          case ButtonState.Pressed:
            (images[index].Quad.Texture as SubTexture).Parent = hover;
            break;
          default:
            throw new NotSupportedException("ButtonState not supported: " + state.ToString());
        }
        this.states[index] = state;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
