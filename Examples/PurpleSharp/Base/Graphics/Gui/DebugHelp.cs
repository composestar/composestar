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

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// The help page of the DebugOverlay.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
	public class DebugHelp : GuiGroup
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    GuiText help;
    GuiText frameRate;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates the DebugHelp page.
    /// </summary>
    internal DebugHelp() : base( Math.Vector2.One ) {
      help = new GuiText( 256, 512, new System.Drawing.Font("Arial", 12), Color.White);
      help.Position = new Math.Vector2(0.0f, 0.05f);
      help.Text = 
        @"DebugOverlay Help" + System.Environment.NewLine + 
        "=================" + System.Environment.NewLine + 
        "F1 ... This help page" + System.Environment.NewLine + 
        "F2 ... Info page" + System.Environment.NewLine + 
        "F3 ... Profiling page" + System.Environment.NewLine + 
        " ----------------" + System.Environment.NewLine +
        "H ... Toggle help" + System.Environment.NewLine + 
        "F ... Toggle framerate" + System.Environment.NewLine +
        "W ... Toggle wireframe mode" + System.Environment.NewLine + 
        "S ... Screenshot" + System.Environment.NewLine + 
        "I ... Increase Time Factor" + System.Environment.NewLine +
        "D ... Decrease Time Factor" + System.Environment.NewLine +
        "Esc ... Close Debug Overlay" + System.Environment.NewLine;
      help.Update(Color.From(0x80, 0, 0, 0));   
      Children.Add(help);
      frameRate = new GuiText( 64, 32, new System.Drawing.Font("Arial", 11), Color.White);
      frameRate.TextAnchor = Anchor.Centered;
      Children.Add(frameRate);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Render the next frame.
    /// </summary>
    /// <param name="deltaTime">Time since the last frame.</param>
    public override void OnRender(float deltaTime) {
      if (frameRate.Visible) {
        frameRate.Text = Engine.Instance.FrameRate.ToString();
        frameRate.Update(Color.From(0x80, 0, 0, 0));
      }

      base.OnRender(deltaTime);
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="key">The key, whose status was changed.</param>
    /// <param name="pressed">Flag that indicates if the key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnKey(Purple.Input.Key key, bool pressed) {
      if (pressed) {
        switch (key) {
          case Purple.Input.Key.W:
            Device.Instance.RenderStates.Wireframe = !Device.Instance.RenderStates.Wireframe;
            return true;
          case Purple.Input.Key.S:
            TextureManager.Instance.Save("ScreenShot.png", Device.Instance.BackBuffer );
            return true;
          case Purple.Input.Key.F:
            frameRate.Visible = !frameRate.Visible;
            return true;
          case Purple.Input.Key.H:
            help.Visible = !help.Visible;
            return true;
          case Purple.Input.Key.I:
            Purple.Engine.Instance.TimeFactor += 0.1f;
            return true;
          case Purple.Input.Key.D:
            Purple.Engine.Instance.TimeFactor -= 0.1f;
            return true;
        }
      }
      return base.OnKey (key, pressed);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
