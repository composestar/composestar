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
//   Markus W��
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

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// The info page of the DebugOverlay.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus W��</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
  public class DebugInfo : GuiGroup {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    const int UpdateRate = 100;
    int frame = 0;
    GuiText info;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates the DebugHelp page.
    /// </summary>
    internal DebugInfo() : base( Math.Vector2.One ) {
      info = new GuiText( 512, 768, new System.Drawing.Font("Arial", 12), Color.White);
      info.Position = new Math.Vector2(0.0f, 0.0f);
      Children.Add(info);
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
      frame--;
      if (frame <= 0) {
        DisplayMode mode = Device.Instance.CurrentDisplayMode;
        info.Text = 
          @"DebugOverlay Info" + System.Environment.NewLine + 
          "=================" + System.Environment.NewLine + 
          "GfxCard: " + Device.Instance.Name + " - " + Device.Instance.Description + System.Environment.NewLine + 
          "Mode: " + mode.Width + '*' + mode.Height + '*' + mode.BitsPerPixel + " (" + mode.RefreshRate + " Hz)" + System.Environment.NewLine + 
          "FPS: " + Engine.Instance.FrameRate + System.Environment.NewLine + 
          "Tris (/s): " + Device.Instance.TrianglesRendered + " (" + Device.Instance.TrianglesRendered * Engine.Instance.FrameRate + ')' + System.Environment.NewLine +
          "VidMem: " + TextureManager.Instance.AvailableMemory + System.Environment.NewLine + 
          "Textures (online): " + TextureManager.Instance.TexturesAlive + " (" + TextureManager.Instance.TexturesOnline + ")" + System.Environment.NewLine;

        info.Update(Color.From(0x80, 0, 0, 0));   
        frame += UpdateRate;
      }
      Device.Instance.TrianglesRendered = 0;
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
          case Purple.Input.Key.G:
            System.GC.Collect();
            System.GC.WaitForPendingFinalizers();
            return true;
          case Purple.Input.Key.D:
            TextureManager.Instance.DisposeOnlineData();
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