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
using System.Windows.Forms;

using Purple.Graphics;
using Purple.Graphics.Core;
using Purple.Graphics.Gui;

namespace Purple.Input {
  //=================================================================
  /// <summary>
  /// InputEngine using an inputEngine plugIn
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.5</para>
  /// </remarks>
  //=================================================================
  public class InputEngine: IDisposable {		

    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private static InputEngine instance;
    private IInputEngine engine;

    /// <summary>
    /// Returns the standard mouse cursor;
    /// </summary>
    public IImage StandardMouseCursor {
      get {
        if (cursor == null)
          cursor = LoadMouseCursor();
        return cursor;
      }
    }
    IImage cursor;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// gets singleton instance of InputEngine
    /// </summary>
    static public InputEngine Instance {
      get {
        if (instance == null) {
          IInputEngine engine = (IInputEngine) Purple.PlugIn.Factory.Instance.Get("InputEngine");
          if (engine == null)
            return null;
          instance = new InputEngine(engine);
        }
        return instance;
      }
    }

    /// <summary>
    /// Flag that indicates if <see cref="InputEngine"/> is initialized.
    /// </summary>
    static public bool Initialized {
      get {
        return instance != null && instance.engine.Initialized;
      }
    }

    /// <summary>
    /// get the keyboard device
    /// </summary>
    public IKeyboard Keyboard { 
      get {
        return engine.Keyboard;
      }
    }
			
    /// <summary>
    /// get the mouse device
    /// </summary>
    public IMouse Mouse { 
      get {
        return engine.Mouse;
      }
    }		

    /// <summary>
    /// Returns the array of gamepads.
    /// </summary>
    public IGamePad[] GamePads { 
      get {
        return engine.GamePads;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    private InputEngine(IInputEngine engine) {
      this.engine = engine;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// inits the input engine
    /// </summary>
    /// <param name="control">main window</param>
    public void Init(Control control) {
      engine.Init(control);
    }	

    /// <summary>
    /// updates all input devices
    /// is called by engine once fore every frame
    /// </summary>
    public void Update() {
      engine.Update();
    }

    /// <summary>
    /// dispose the engine
    /// </summary>
    public void Dispose() {
      engine.Dispose();
      instance = null;
    }

    /// <summary>
    /// Loads the standard mouse cursor.
    /// </summary>
    /// <returns>The standard mouse cursor.</returns>
    private IImage LoadMouseCursor() {

      using (Purple.Profiling.Profiler.Instance.Sample("LoadMouseCursor")) {
        if (GraphicsEngine.Initialized) {
          string name = "Purple/Input/Cursor.png";
          ITexture2d texture = TextureManager.Instance.Load( Purple.IO.ResourceFileSystem.Instance.Open(name));
          IImage img = new Image(texture);
          img.Position = new Purple.Math.Vector2(0.5f, 0.5f);
          return img;
        } else
          return null;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
