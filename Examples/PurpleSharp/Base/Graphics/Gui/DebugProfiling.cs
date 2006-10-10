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
using Purple.Profiling;

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// The profiling page of the DebugOverlay.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
  public class DebugProfiling : GuiGroup {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    GuiText profiler;
#if PROFILE
    ProfilingData data;
    int frame = 0;
#endif
    const int ProfilerUpdateRate = 50;    
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates the DebugHelp page.
    /// </summary>
    internal DebugProfiling() : base( Math.Vector2.One ) {
#if PROFILE
      profiler = new GuiText( 1024, 768, new System.Drawing.Font("Lucida Console", 11), Color.White);
      Children.Add( profiler );
      data = new ProfilingData();
#else
      profiler = new GuiText( 1024, 768, new System.Drawing.Font("Arial", 14), Color.White);
      profiler.Text = "Profiling not enabled!";
      profiler.Update(Color.From(0x80, 0, 0, 0));
      Children.Add( profiler );   
#endif
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
#if PROFILE
      if (profiler.Visible) {
        frame--;
        if (frame <= 0) {
          profiler.Text = "FPS: " + Engine.Instance.FrameRate + System.Environment.NewLine + data.CreateString(true);
          profiler.Update( Color.From(0x80, 0, 0, 0) );
          frame += ProfilerUpdateRate;
        }
      }
#endif
      base.OnRender(deltaTime);
    }

    /// <summary>
    /// Method that handles keyboard events.
    /// </summary>
    /// <param name="key">The key, whose status was changed.</param>
    /// <param name="pressed">Flag that indicates if the key was pressed or released.</param>
    /// <returns>True if the key was handled and shouldn't be passed on to the underlaying elements.</returns>
    public override bool OnKey(Purple.Input.Key key, bool pressed) {
#if PROFILE
      if (pressed) {
        switch (key) {
          case Purple.Input.Key.R:
            data.Reset();
            return true;
        }
      }
#endif
      return base.OnKey (key, pressed);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}