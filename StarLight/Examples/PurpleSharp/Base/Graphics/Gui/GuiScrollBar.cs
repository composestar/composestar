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

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// A scroll bar.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
  public class GuiScrollBar : GuiGroup, IGuiProgressBar {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Button left;
    Button right;
    Button handle;
    Image background;
    float minPos;
    float maxPos;
    float pickPosition;

    /// <summary>
    /// Event that is fired if the current position of the progressbar gets changed.
    /// </summary>
    public event SliderChanged Changed;

    /// <summary>
    /// The direction of the gauge.
    /// </summary>
    public Direction Direction { 
      get {
        return direction;
      }
      set {
        direction = value;
      }
    }
    Direction direction = Direction.Left;

    /// <summary>
    /// Flag that defines if <see cref="IGuiProgressBar"/> increments or decrements.
    /// </summary>
    public bool Increment { 
      get {
        return increment;
      }
      set {
        increment = value;
      }
    }
    bool increment = true;

    /// <summary>
    /// The total time for the gauge.
    /// </summary>
    public float Total { 
      get {
        return total;
      }
      set {
        total = value;
      }
    }
    float total = 100.0f;

    /// <summary>
    /// The current time.
    /// </summary>
    public float Current { 
      get {
        return current;
      }
      set {
        float newVal = Math.Basic.Clamp( value, 0, Total );
        if (current != newVal) {
          current = newVal;
          if (this.Changed != null)
            Changed(this);
        }
      }
    }
    float current = 100.0f;

    /// <summary>
    /// The size of the line.
    /// </summary>
    public float LineStep {
      get {
        return lineStep;
      }
      set {
        lineStep = value;
      }
    }
    float lineStep = 5.0f;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new GuiScrollBar object.
    /// </summary>
    public GuiScrollBar(Image background, Button handle, Button left, Button right) {
      this.background = background;
      this.Children.Add( background );

      this.left = left;
      this.left.Clicked += new VoidEventHandler(left_Clicked);
      this.Children.Add( left );

      this.right = right;
      this.right.Clicked += new VoidEventHandler(right_Clicked);
      this.Children.Add( right );

      this.handle = handle;
      this.Children.Add( handle );
      
      this.size = background.Size;
      this.direction = Direction.Down;
      UpdateLayout();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private void UpdateLayout() {
      if (this.direction == Direction.Left || this.direction == Direction.Right) {
        left.Position = new Vector2(0.0f, (Size.Y - left.Size.Y)/2);
        right.Position = new Vector2(Size.X - right.Size.X, (Size.Y - right.Size.Y)/2);
        handle.Position = new Vector2(0, (Size.Y - handle.Size.Y)/2);
        minPos = left.Size.X;
        maxPos = Size.X - right.Size.X - handle.Size.X;
      } else if (this.direction == Direction.Up || this.direction == Direction.Down) {
        left.Position = new Vector2((Size.X - left.Size.X)/2, 0.0f);
        right.Position = new Vector2((Size.X - right.Size.X)/2, Size.Y - right.Size.Y);
        handle.Position = new Vector2((Size.X - handle.Size.X)/2, 0);
        float scale = 1.0f;
        minPos = left.Size.Y;
        maxPos = Size.Y - right.Size.Y - handle.Size.Y*scale;
        this.handle.Scale = new Vector2(this.handle.Scale.X, scale);
      }
      Update();
    }

    /// <summary>
    /// Method that handles mouse events.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button that is pressed or released.</param>
    /// <param name="pressed">Flag that indicates if button is pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnMouse(Vector3 position, Purple.Input.MouseButton button, bool pressed) {
      base.OnMouse (position, button, pressed);
      if (handle.ButtonState == ButtonState.Pressed || handle.ButtonState == ButtonState.PressedHover) {
        if (pressed)
          pickPosition = this.InverseTransform( position.Vector2 ).Y*this.Size.Y - handle.Position.Y; // - handle.AbsolutePosition.Y;
        float pos = this.InverseTransform( position.Vector2 ).Y*this.Size.Y - pickPosition;
        SetPos(pos);
      }
      return false;
    }

    private void SetPos(float pos) {
      pos = Math.Basic.Clamp(pos, minPos, maxPos);
      Current = Total * ((pos-minPos)/(maxPos-minPos));
      Update();
    }

    private void Update() {
      float pos = minPos + (maxPos - minPos)*Current/Total;
      handle.Position = new Vector2( handle.Position.X, pos);
    }

    private void left_Clicked() {
      Current -= lineStep;
      Update();
    }

    private void right_Clicked() {
      Current += lineStep;
      Update();  
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
