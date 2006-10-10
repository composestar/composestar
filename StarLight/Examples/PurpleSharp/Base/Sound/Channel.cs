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
  /// Class that represents a certain sound channel. Certain attributes like 
  /// volume, pan and more can be applied on a per channel basis.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
  public class Channel {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    ISoundEngine engine;
    ArrayList soundObjects = new ArrayList();

    /// <summary>
    /// Name of the channel.
    /// </summary>
    public string Name {
      get {
        return name;
      }
    }
    string name;

    /// <summary>
    /// Gets or sets the stereo panning value of this sound.
    /// </summary>
    /// <value>A value between -1.0 (left) and 1.0 (right), </value>
    public float Pan { 
      get {
        return pan;
      } 
      set {
        if (pan != value) {
          pan = value;
          for (int i = 0; i < soundObjects.Count; i++) {
            (soundObjects[i] as ISoundObject).Pan = (soundObjects[i] as ISoundObject).Pan;          
          }
        }
      }
    }
    float pan = 0.0f;
    
    /// <summary>
    /// Gets or sets the volume value of this sound.
    /// </summary>
    /// <value>A value between 0.0 (silent) and 1.0f (full volume), </value>
    public float Volume { 
      get {
        return volume;
      } 
      set {
        if (volume != value) {
          volume = value;
          for (int i = 0; i < soundObjects.Count; i++) {
            (soundObjects[i] as ISoundObject).Volume = (soundObjects[i] as ISoundObject).Volume;          
          }
        }
      }
    }
    float volume = 1.0f;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new channel.
    /// </summary>
    /// <param name="name">Name of the channel.</param>
    /// <param name="engine">The sound engine of the current channel.</param>
    internal Channel(string name, ISoundEngine engine) {
      this.name = name;
      this.engine = engine;
    }

    /// <summary>
    /// Loads a specific sound file into memory.
    /// </summary>
    /// <param name="fileName">Path of file.</param>
    /// <returns>The loaded sound object.</returns>
    /// <exception cref="Purple.Exceptions.StreamException">If file couldn't be loaded.</exception>
    /// <exception cref="SoundException">If sound format isn't supported (extension).</exception>
    public ISampleObject Load(string fileName) {
      ISampleObject obj = engine.Load(fileName);
      obj.Channel = this;
      return obj;
    }

    /// <summary>
    /// Loads a specific sound file into memory.
    /// </summary>
    /// <param name="stream"><see cref="System.IO.Stream"/> containing data.</param>
    /// <param name="fileName">FileName or extension => "Test.mp3" or ".mp3".</param>
    /// <returns>The loaded sound object.</returns>
    /// <exception cref="Purple.Exceptions.StreamException">Thrown if stream is invalid.</exception>
    /// <exception cref="SoundException">Thrown if sound format isn't supported (extension).</exception>
    public ISampleObject Load(System.IO.Stream stream, String fileName) {
      ISampleObject obj = engine.Load(stream, fileName);
      obj.Channel = this;
      return obj;
    }

    /// <summary>
    /// Loads a specific sound file part by part (on demand).
    /// </summary>
    /// <param name="fileName">Path of file.</param>
    /// <returns>The loaded sound object.</returns>
    /// <exception cref="Purple.Exceptions.StreamException">If file couldn't be loaded.</exception>
    /// <exception cref="SoundException">If sound format isn't supported (extension).</exception>
    public IStreamObject LoadStream(string fileName) {
      IStreamObject obj = engine.LoadStream(fileName);
      obj.Channel = this;
      return obj;
    }

    /// <summary>
    /// Loads a specific sound file part by part (on demand).
    /// </summary>
    /// <param name="stream">Stream containing data.</param>
    /// <param name="fileName">FileName or extension => "Test.mp3" or ".mp3".</param>
    /// <returns>The loaded sound object.</returns>
    /// <exception cref="Purple.Exceptions.StreamException">Thrown if stream is invalid.</exception>
    /// <exception cref="SoundException">Thrown if sound format isn't supported (extension).</exception>
    public IStreamObject LoadStream(System.IO.Stream stream, String fileName) {
      IStreamObject obj = engine.LoadStream(stream, fileName);
      obj.Channel = this;
      return obj;
    }

    /// <summary>
    /// Adds a soundobject to the channel.
    /// </summary>
    /// <param name="soundObject">The sound object to add.</param>
    public void Add(ISoundObject soundObject) {
      soundObjects.Add( soundObject );
    }

    /// <summary>
    /// Removes a soundobject from the channel.
    /// </summary>
    /// <param name="soundObject">SoundObject to remove.</param>
    public void Remove(ISoundObject soundObject) {
      soundObjects.Remove( soundObject );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
