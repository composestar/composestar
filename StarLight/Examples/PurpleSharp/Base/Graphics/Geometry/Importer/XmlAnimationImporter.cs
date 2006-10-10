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

namespace Purple.Graphics.Geometry.Importer
{
  //=================================================================
  /// <summary>
  ///importing animation data from a xml file
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  ///   <para>Update: 0.5</para> 
  /// </remarks>
  //=================================================================
	public class XmlAnimationImporter : IAnimationImporter
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    System.Globalization.CultureInfo culture = new System.Globalization.CultureInfo("en-US");
    SkeletonFrame[] frames;
    int frameRate;
    int currentFrame;

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
    /// constructor
    /// </summary>
    /// <param name="fileName">name of file</param>
    public XmlAnimationImporter(string fileName) {
      Import(fileName);
    }

    /// <summary>
    /// constructor
    /// </summary>
    /// <param name="stream">stream containing animation</param>
    public XmlAnimationImporter(Stream stream) {
      Import(stream);
    }

    /// <summary>
    /// constructor
    /// </summary>
    public XmlAnimationImporter() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// import a mesh from a file
    /// </summary>
    /// <param name="fileName">name of file</param>
    public void Import(string fileName) {
      using(Stream stream = fileSystem.Open(fileName)) {
        Import(stream);
      }
    }

    /// <summary>
    /// imports an IMeshAnimtion from a stream
    /// </summary>
    /// <param name="stream"></param>
    /// <returns></returns>
    public void Import(Stream stream) {
      // rest skeletonAnimation object
      channels = null;

      // parse the xml file
      XmlTextReader reader = new XmlTextReader(stream);
      ArrayList joints = new ArrayList();
      
      while(reader.Read()) {
        if (reader.NodeType == XmlNodeType.Element) {
          switch (reader.Name) {

              // <animation>
            case "animation":
              //animationName = reader.GetAttribute("name");
              string frameCountString = reader.GetAttribute("frameCount");
              int frameCount = int.Parse(frameCountString);
              frameRate = int.Parse(reader.GetAttribute("frameRate"));
              currentFrame = 0;
            	frames = new SkeletonFrame[frameCount];				
              break;

              //<frame>
            case "frame": {
              if (currentFrame != int.Parse(reader.GetAttribute("frame")))
                throw new Exception("Frames missing!");
						  joints.Clear();
            }
              break;

              //<joint>
            case "joint": {
              string jointName = reader.GetAttribute("name");
              Matrix4 m = new Matrix4(  float.Parse( reader.GetAttribute("a1"), culture),
                float.Parse( reader.GetAttribute("a2"), culture),
                float.Parse( reader.GetAttribute("a3"), culture),
                float.Parse( reader.GetAttribute("a4"), culture),
                float.Parse( reader.GetAttribute("b1"), culture),
                float.Parse( reader.GetAttribute("b2"), culture),
                float.Parse( reader.GetAttribute("b3"), culture),
                float.Parse( reader.GetAttribute("b4"), culture),
                float.Parse( reader.GetAttribute("c1"), culture),
                float.Parse( reader.GetAttribute("c2"), culture),
                float.Parse( reader.GetAttribute("c3"), culture),
                float.Parse( reader.GetAttribute("c4"), culture),
                float.Parse( reader.GetAttribute("d1"), culture),
                float.Parse( reader.GetAttribute("d2"), culture),
                float.Parse( reader.GetAttribute("d3"), culture),
                float.Parse( reader.GetAttribute("d4"), culture) );
              joints.Add( m );
            }
              break;
          }
        }

        if (reader.NodeType == XmlNodeType.EndElement) {
          if (reader.Name.Equals("frame")) {
            frames[currentFrame] = new SkeletonFrame((Matrix4[])joints.ToArray(typeof(Matrix4)));
            currentFrame++;
          }
          if (reader.Name.Equals("animation")) {
            channels = new Channel[] { new Channel(frames, null, frameRate) };
          }
        }
      }
      reader.Close();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
