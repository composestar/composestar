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

using Purple.Input;
using Purple.Graphics;
using Purple.Graphics.Core;
using Purple.Profiling;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// A gui element that contains some cool debugging functionality.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
	public class DebugOverlay : GuiGroup
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    //GuiText frameRate;
    //GuiText profiler;
    //ProfilingData data;
    //const int ProfilerUpdateRate = 50;
    //int frame = 0;

    /// <summary>
    /// The pages of the DebugOverlay.
    /// </summary>
    public GuiPages Pages {
      get {
        return pages;
      }
    }
    GuiPages pages;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new DebugOverlay object.
    /// </summary>
    public DebugOverlay() {
      pages = new GuiPages(3);
      Children.Add(pages);

      pages.Pages[0] = new DebugHelp();
      pages.Pages[1] = new DebugInfo();
      pages.Pages[2] = new DebugProfiling();
      
      this.Visible = false;
      this.size = new Math.Vector2(1.0f, 1.0f);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Called for every frame.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    public override void OnRender(float deltaTime) {
      Profiler.Instance.Begin("OnRender DebugOverlay");
      base.OnRender(deltaTime);
      Profiler.Instance.End("OnRender DebugOverlay");
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="key">The key, whose status was changed.</param>
    /// <param name="pressed">Flag that indicates if the key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnKey(Key key, bool pressed) {
      if (key == Key.D && pressed && InputEngine.Instance.Keyboard.IsDown(Key.LeftControl)) {
        this.Visible = !this.Visible;
        return true;
      }
      if (this.Visible) {
        if (pressed) {
          if (key >= Key.F1 && key <= Key.F15) {
            pages.CurrentPage = (int)key - (int)Key.F1;
            return true;
          }
          if (key == Key.Esc) {
            this.Visible = false;
            return true;
          }
        }
        return base.OnKey(key, pressed);
      }   
      return false;
    }      
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
