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

using Purple.Serialization;

namespace Purple.Graphics
{
  /// <summary>
  /// Defines flags that describe the relationship between the adapter refresh rate and the rate at which Device.Present operations are completed.
  /// </summary>
  public enum PresentInterval {
    /// <summary>
    /// The window area gets updated immediately.
    /// </summary>
    Immediate,
    /// <summary>
    /// The window area gets updated during the next vertical retrace.
    /// </summary>
    One
  }

  /// <summary>
  /// Defines how vertices are processed.
  /// </summary>
  public enum VertexProcessing {
    /// <summary>
    /// Vertices are process in software.
    /// </summary>
    Software,
    /// <summary>
    /// Vertices are processed in soft and hardware.
    /// </summary>
    Mixed,
    /// <summary>
    /// Vertices are processed in hardware.
    /// </summary>
    Hardware,
    /// <summary>
    /// Vertices are processed in pure hardware.
    /// </summary>
    PureHardware
  }

  /// <summary>
  /// The type of the device to use.
  /// </summary>
  public enum DeviceType {
    /// <summary>
    /// Use the hardware device.
    /// </summary>
    Hardware,
    /// <summary>
    /// Use the reference device.
    /// </summary>
    Reference,
    /// <summary>
    /// Use the null device.
    /// </summary>
    Null
  }

  //=================================================================
  /// <summary>
  /// The graphics settings for initializing the GraphicsEngine.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public struct GraphicsSettings {
    /// <summary>
    /// The index of the Device to use.
    /// </summary>
    [Serialize(true)]
    public int DeviceIndex;
    /// <summary>
    /// The width of the game window.
    /// </summary>
    [Serialize(true)]
    public int Width;
    /// <summary>
    /// The height of the game window.
    /// </summary>
    [Serialize(true)]
    public int Height;
    /// <summary>
    /// Number of bits per pixel.
    /// </summary>
    [Serialize(true)]
    public int BitsPerPixel;
    /// <summary>
    /// Flag that indicates if the game should run in fullscreen mode.
    /// </summary>
    [Serialize(true)]
    public bool FullScreen;
    /// <summary>
    /// The target refresh rate.
    /// </summary>
    [Serialize(true)]
    public int RefreshRate;
    /// <summary>
    /// The control to use for graphics output.
    /// </summary>
    public Control Control;
    /// <summary>
    /// Defines how vertices are processed.
    /// </summary>
    [Serialize(true)]
    public VertexProcessing VertexProcessing;
    /// <summary>
    /// Defines the present interval.
    /// </summary>
    [Serialize(true)]
    public PresentInterval PresentInterval;
    /// <summary>
    /// The number of bits for the z-Buffer.
    /// </summary>
    [Serialize(true)]
    public int DepthBits;
    /// <summary>
    /// The number of bits for the stencil buffer.
    /// </summary>
    [Serialize(true)]
    public int StencilBits;
    /// <summary>
    /// The type of the device to use for rendering.
    /// </summary>
    [Serialize(true)]
    public DeviceType DeviceType;

    /// <summary>
    /// The standard windowed settings.
    /// </summary>
    public static GraphicsSettings WindowedSettings {
      get {
        return windowedSettings;
      }
    }
    static GraphicsSettings windowedSettings = new GraphicsSettings(800, 600, 16, false);

    /// <summary>
    /// The standard fullScreen settings.
    /// </summary>
    public static GraphicsSettings FullScreenSettings {
      get {
        return fullScreenSettings;
      }
    }
    static GraphicsSettings fullScreenSettings = new GraphicsSettings(800, 600, 16, true);

    /// <summary>
    /// Creates a GraphicsSettings object with the necessary information.
    /// </summary>
    /// <param name="width">The width of the control.</param>
    /// <param name="height">The height of the control.</param>
    /// <param name="bpp">The color depth.</param>
    /// <param name="fullScreen">Flag that indicates if game should be started in fullscreen mode.</param>
    public GraphicsSettings(int width, int height, int bpp, bool fullScreen) {
      this.Control = null;
      this.DeviceIndex = 0;
      this.Width = width;
      this.Height = height;
      this.BitsPerPixel = bpp;
      this.FullScreen = fullScreen;
      this.VertexProcessing = VertexProcessing.PureHardware;
      RefreshRate = 70;
      PresentInterval = PresentInterval.Immediate;
      DepthBits = 16;
      StencilBits = 8;
      DeviceType = DeviceType.Hardware;
    }

    /// <summary>
    /// Loads the settings from an xml configuration file.
    /// </summary>
    /// <param name="fileName">Name of file to load settings from.</param>
    /// <returns>The loaded GraphicsSettings object.</returns>
    public static GraphicsSettings Load(string fileName) {
      return (GraphicsSettings)Purple.Serialization.XmlSerializeCodec.Load(fileName);
    }

    /// <summary>
    /// Loads the settings from a stream.
    /// </summary>
    /// <param name="stream">Stream to load settings from.</param>
    /// <returns>The GraphicsSettings object.</returns>
    public static GraphicsSettings Load(System.IO.Stream stream) {
      return (GraphicsSettings)Purple.Serialization.XmlSerializeCodec.Load(stream);
    }
	}
}
