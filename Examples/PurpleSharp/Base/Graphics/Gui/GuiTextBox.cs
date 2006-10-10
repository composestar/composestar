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
using Purple.Graphics.Core;
using Purple.Graphics.TwoD;

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// This class is a GuiText that handles user input via the keyboard.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class GuiTextBox : GuiText {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The maximum length of the entered text.
    /// </summary>
    public int MaxLength {
      get {
        return maxLength;
      }
      set {
        maxLength = value;
      }
    }
    int maxLength = 64;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="GuiTextBox"/>.
    /// </summary>
    /// <param name="width">Width of the texture.</param>
    /// <param name="height">Height of the texture.</param>
    /// <param name="font">Font to use for the text.</param>
    /// <param name="color">Color to use for drawing the text.</param>
    public GuiTextBox(int width, int height, Font font, int color) : base(width, height, font, color) {

    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Method to check if the entered keyChar is a valid char and will be added to the text.
    /// </summary>
    /// <param name="keyChar">The char to test for.</param>
    /// <returns>True if the entered character is valid.</returns>
    protected bool IsValidChar(char keyChar) {
      return (char.IsLetterOrDigit(keyChar) || char.IsWhiteSpace(keyChar) || char.IsSymbol(keyChar) 
        || char.IsPunctuation(keyChar) || keyChar == '-' || keyChar =='_');    
    }

    /// <summary>
    /// The method that handles entered characters.
    /// </summary>
    /// <param name="keyChar">The character that was entered via the keyboard.</param>
    public override bool OnChar(char keyChar) {
      if (this.Text.Length <= MaxLength && IsValidChar(keyChar)) {
        Text += keyChar;
        Update(Color.Empty);
      } else if (keyChar == '\b' && Text.Length > 0) {
        Text = Text.Substring(0, Text.Length-1);
        Update(Color.Empty);
      } else {
        //TODO: Signal
      }
      return true;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}