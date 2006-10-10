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
using System.Collections;

namespace Purple.Sound
{
  //=================================================================
  /// <summary>
  /// Api independent class provinding sound engine functionality.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
	public class SoundEngine
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ISoundEngine engine;
    static SoundEngine instance;
    ArrayList channels = new ArrayList();

    /// <summary>
    /// Returns a channel at a certain index.
    /// </summary>
    public Channel this[int index] {
      get {
        return (Channel)channels[index];
      }
    }

    /// <summary>
    /// Returns a channel via its name.
    /// </summary>
    public Channel this[string name] {
      get {
        for (int i = 0; i < channels.Count; i++) {
          if ((channels[i] as Channel).Name == name)
            return (Channel)channels[i];
        }
        return null;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new instance of a soundEngine object
    /// </summary>
    /// <param name="engine">engine to wrap</param>
    private SoundEngine(ISoundEngine engine) {
      this.engine = engine;
      AddChannel("Sound");
      AddChannel("Music");
    }

    /// <summary>
    /// Returns the singleton instance of <see cref="SoundEngine"/>.
    /// </summary>
    static public SoundEngine Instance {
      get {
        if (instance == null) {
          ISoundEngine engine = (ISoundEngine) Purple.PlugIn.Factory.Instance.Get("SoundEngine");
          instance = new SoundEngine(engine);
        }
        return instance;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds a channel.
    /// </summary>
    /// <param name="name">Name of the channel.</param>
    public void AddChannel(string name) {
      channels.Add(new Channel(name, engine));
    }

    /// <summary>
    /// Removes a channel with a certain name.
    /// </summary>
    /// <param name="name">Name of the channel.</param>
    public void RemoveChannel(string name) {
      for (int i = 0; i < channels.Count; i++) {
        if ((channels[i] as Channel).Name == name) {
          channels.RemoveAt(i);
          return;
        }
      }
    }

    /// <summary>
    /// Initializes the <see cref="SoundEngine"/>.
    /// </summary>
    /// <param name="mixRate">Output rate in Hz</param>
    /// <exception cref="SoundException">Thrown if initialization failed</exception>
    public void Init(int mixRate) {
      engine.Init(mixRate);
    }

    /// <summary>
    /// Initializes the <see cref="SoundEngine"/> with 44100 Hz.
    /// </summary>
    /// <exception cref="SoundException">Thrown if initialization failed.</exception>
    public void Init() {
      Init(44100);
    }

    /// <summary>
    /// Shuts down the <see cref="SoundEngine"/>.
    /// </summary>
    public void Close() {
      engine.Dispose();
    }

    /// <summary>
    /// Returns the <see cref="ICDObject"/> for accessing the main cd drive.
    /// </summary>
    /// <returns>The retrieved sound object.</returns>
    public ICDObject GetCD() {
      return engine.GetCD();
    }

    /// <summary>
    /// Returns the <see cref="ICDObject"/> for accessing a certain cd drive.
    /// </summary>
    /// <param name="drive">Drive "d:", "e:", ...</param>
    /// <returns>The retrieved sound object.</returns>
    public ICDObject GetCD(string drive) {
      return engine.GetCD();
    }

    /// <summary>
    /// Checks if a certain sound file is supported by the <see cref="SoundEngine"/>.
    /// </summary>
    /// <param name="fileName">The whole filename or the extension ("test.mp3" or just ".mp3").</param>
    /// <returns>True if supported - false if not.</returns>
    public bool Supports(string fileName) {
      return engine.Supports(fileName);
    }

    /// <summary>
    /// Checks if streaming is supported for a certain sound file.
    /// </summary>
    /// <param name="fileName">The whole filename or the extension ("test.mp3" or just ".mp3").</param>
    /// <returns>True if supported - false if not.</returns>
    public bool SupportsStreaming(string fileName) {
      return engine.SupportsStreaming(fileName);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
