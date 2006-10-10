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

namespace Purple.Sound {
  //=================================================================
  /// <summary>
  /// abstract interface for a certain soundEngine (fMod, Miles Sound System, BASS, ..)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public interface ISoundEngine : IDisposable{
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// initializes the soundEngine
    /// </summary>
    /// <param name="mixRate">Output rate in Hz</param>
    /// <exception cref="SoundException">thrown if initialization failed</exception>
    void Init(int mixRate);

    /// <summary>
    /// loads a specific sound file into memory
    /// </summary>
    /// <param name="fileName">path of file</param>
    /// <returns>the loaded sound object</returns>
    /// <exception cref="Purple.Exceptions.StreamException">if file couldn't be loaded</exception>
    /// <exception cref="SoundException">if sound format isn't supported (extension)</exception>
    ISampleObject Load(string fileName);

    /// <summary>
    /// loads a specific sound file into memory
    /// </summary>
    /// <param name="stream">stream containing data</param>
    /// <param name="fileName">fileName or extension => "Test.mp3" or ".mp3"</param>
    /// <returns>the loaded sound object</returns>
    /// <exception cref="Purple.Exceptions.StreamException">if stream is invalid</exception>
    /// <exception cref="SoundException">if sound format isn't supported (extension)</exception>
    ISampleObject Load(System.IO.Stream stream, String fileName);

    /// <summary>
    /// loads a specific sound file part by part (on demand)
    /// </summary>
    /// <param name="fileName">path of file</param>
    /// <returns>the loaded sound object</returns>
    /// <exception cref="Purple.Exceptions.StreamException">if file couldn't be loaded</exception>
    /// <exception cref="SoundException">if sound format isn't supported (extension)</exception>
    IStreamObject LoadStream(string fileName);

    /// <summary>
    /// loads a specific sound file part by part (on demand)
    /// </summary>
    /// <param name="stream">stream containing data</param>
    /// <param name="fileName">fileName or extension => "Test.mp3" or ".mp3"</param>
    /// <returns>the loaded sound object</returns>
    /// <exception cref="Purple.Exceptions.StreamException">if stream is invalid</exception>
    /// <exception cref="SoundException">if sound format isn't supported (extension)</exception>
    IStreamObject LoadStream(System.IO.Stream stream, String fileName);

    /// <summary>
    /// get the cd object for accessing the main cd drive
    /// </summary>
    /// <returns>the retrieved sound object</returns>
    ICDObject GetCD();

    /// <summary>
    /// get the cd object for accessing a certain cd drive
    /// </summary>
    /// <param name="drive">drive "d:", "e:", ...</param>
    /// <returns>the retrieved sound object</returns>
    ICDObject GetCD(string drive);   

    /// <summary>
    /// checks if a certain sound file is supported by this SoundEngine
    /// </summary>
    /// <param name="fileName">the whole filename or the extension ("test.mp3" or just ".mp3")</param>
    /// <returns>true if supported - false if not</returns>
    bool Supports(string fileName);

    /// <summary>
    /// checks if streaming is supported for a certain sound file
    /// </summary>
    /// <param name="fileName">the whole filename or the extension ("test.mp3" or just ".mp3")</param>
    /// <returns>true if supported - false if not</returns>
    bool SupportsStreaming(string fileName);
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
