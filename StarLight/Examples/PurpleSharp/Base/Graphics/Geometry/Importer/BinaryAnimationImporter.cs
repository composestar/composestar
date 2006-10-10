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
using System.Xml;
using System.IO;
using System.Collections;

using Purple.IO;
using Purple.Math;
using Purple.Profiling;

namespace Purple.Graphics.Geometry.Importer {
  //=================================================================
  /// <summary>
  /// Imports an animation from a binary file.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  ///   <para>Update: 0.5</para>  
  /// </remarks>
  //=================================================================
  public class BinaryAnimationImporter : IAnimationImporter {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    System.Globalization.CultureInfo culture = new System.Globalization.CultureInfo("en-US");
    SkeletonFrame[] frames;
    int frameRate;

    /// <summary>
    /// Retturns the animation channels.
    /// </summary>
    public Channel[] Channels { 
      get {
        return channels;
      }
      set {
        channels = value;
      }
    }
    Channel[] channels;

    /// <summary>
    /// The <see cref="IFileSystem"/> that is used by the <see cref="BinaryAnimationImporter"/>.
    /// </summary>
    public IFileSystem FileSystem {
      get {
        return fileSystem;
      }
      set {
        fileSystem = value;
      }
    }
    IFileSystem fileSystem = RefFileSystem.Instance;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new <see cref="BinaryAnimationImporter"/>.
    /// </summary>
    /// <param name="fileName">Name of file.</param>
    public BinaryAnimationImporter(string fileName) {
      Import(fileName);
    }

    /// <summary>
    /// Creates a new <see cref="BinaryAnimationImporter"/>.
    /// </summary>
    /// <param name="stream">Stream containing animation.</param>
    public BinaryAnimationImporter(Stream stream) {
      Import(stream);
    }

    /// <summary>
    /// Creates a new <see cref="BinaryAnimationImporter"/>.
    /// </summary>
    public BinaryAnimationImporter() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Import skeletal animation data from a file.
    /// </summary>
    /// <param name="fileName">Name of file.</param>
    public void Import(string fileName) {
      using(Stream stream = fileSystem.Open(fileName)) {
        Import(stream);
      }
    }

    /// <summary>
    /// Import an animation from a stream.
    /// </summary>
    /// <param name="stream"></param>
    /// <returns></returns>
    public void Import(Stream stream) {
      Profiler.Instance.Begin("Import binary animation");
      // rest skeletonAnimation object
      channels = null;

      // Header
      BinaryReader reader = new BinaryReader( stream );
      if (ReadString(reader) != "anim" || ReadString(reader) != "v0.3")
        throw new NotSupportedException("Can't load mesh, file not supported!");

      string animationName = ReadString(reader); // can be removed in future versions
      int frameCount = ReadInt(reader);
      frameRate = ReadInt(reader);
      frames = new SkeletonFrame[frameCount];		

      byte[] matrixBytes = new byte[64];
      for(int i=0; i<frameCount; i++) {
        int jointNum = ReadInt(reader);
        if (matrixBytes.Length < jointNum*64)
          matrixBytes = new byte[jointNum*64];        

        Matrix4[] matrices = new Matrix4[jointNum];
        reader.Read(matrixBytes, 0, jointNum*64);
        matrices = Matrix4.From( matrixBytes, 0, jointNum );
        frames[i] = new SkeletonFrame(matrices);
      }
      reader.Close();

      channels = new Channel[] { new Channel(frames, null, frameRate) };
      Profiler.Instance.End("Import binary animation");
    }

    string ReadString(BinaryReader reader) {
      int length = ReadInt(reader);
      return new string(reader.ReadChars(length));
    }

    int ReadInt(BinaryReader reader) {
      return reader.ReadInt32();
    }

    float ReadFloat(BinaryReader reader) {
      return reader.ReadSingle();
    }

    Vector3 ReadVector3(BinaryReader reader) {
      return new Vector3( ReadFloat(reader), ReadFloat(reader), ReadFloat(reader));
    }

    Vector2 ReadVector2(BinaryReader reader) {
      return new Vector2( ReadFloat(reader), ReadFloat(reader));
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}