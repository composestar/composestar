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
using System.Windows.Forms;
using Purple.Graphics.Core;

namespace Purple.Graphics {
	//=================================================================
	/// <summary>
	/// advanced functionality based on IGfxEngine
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last Update: 0.7</para>
	/// </remarks>
	//=================================================================
	public class GraphicsEngine {
		//---------------------------------------------------------------
    #region Variables and Properties
		//---------------------------------------------------------------				
		IGraphicsEngine engine;

		/// <summary>
		/// Gets singleton instance of the <see cref="GraphicsEngine"/>.
		/// </summary>
		static public GraphicsEngine Instance {
			get {
				if (instance == null) {
					IGraphicsEngine engine = (IGraphicsEngine) Purple.PlugIn.Factory.Instance.Get("GraphicsEngine");
          if (engine == null)
            return null;
          instance = new GraphicsEngine(engine);
				}
				return instance;
			}
		}
    static GraphicsEngine instance;

    /// <summary>
    /// Flag that indicates if <see cref="GraphicsEngine"/> is initialized.
    /// </summary>
    static public bool Initialized {
      get {
        return instance != null && instance.device != null;
      }
    }

		/// <summary>
		/// Returns a list of available Devices.
		/// </summary>
		/// <returns>IDevice objects.</returns>
		public Device[] Devices {
			get {
        if (devices == null) {
          IList devicesList = engine.GetDevices();
          devices = new Device[devicesList.Count];

          int i=0;
          foreach (IDevice device in devicesList) {
            devices[i++] = new Device(device);
          }
        }
				return devices;
			}
		}
    Device[] devices;

		/// <summary>
		/// The control used for gfx output.
		/// </summary>
		public Control Control {
			get {
				return device.Control;
			}
		}

		/// <summary>
		/// The device used for rendering.
		/// </summary>
		public Device Device {
			get {
				return device;
			}
		}
    Device device;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		private GraphicsEngine(IGraphicsEngine engine) {
			this.engine = engine;
      this.device = Devices[0];
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------			
    /// <summary>
    /// Initializes the GraphicsEngine with the given settings.
    /// </summary>
    /// <param name="control">The control to use for graphics output.</param>
    /// <param name="settings">The settings for initializing the engine.</param>
    /// <returns>Returns the device object if successful - null otherwise.</returns>
    public Device Init(Control control, GraphicsSettings settings) {
      if (Devices.Length <= settings.DeviceIndex)
        throw new IndexOutOfRangeException("The GraphicsSettings.DeviceIndex is out of range!");
      this.device = Devices[settings.DeviceIndex];

	    settings.Control = control;
#if DEBUG
      if (settings.VertexProcessing == VertexProcessing.PureHardware)
        settings.VertexProcessing = VertexProcessing.Hardware;
#endif

      device.Switch(settings);

      // Preclear screens
      for (int i = 0; i < 2; i++) {
        device.BeginScene();
        device.Clear(ClearFlags.All);
        device.EndScene();
        device.Present();
      }
      return device;
    }    

    /// <summary>
    /// Tries to initialize the GraphicsEngine with the given settings and the NVidia Performane HeadUpDisplay.
    /// </summary>
    /// <remarks>Some of the GraphicsSettings may be overriden for using the NVPerfHUD. To use this feature, the 
    /// NVPerfHUD must be installed.</remarks>
    /// <param name="control">The control to use for visualization.</param>
    /// <param name="settings">The GraphicsSettings for initialization.</param>
    /// <returns>Returns the device object if successful - null otherwise.</returns>
    public Device InitNVPerfHud(Control control, GraphicsSettings settings) {
      for (int i=0; i<this.Devices.Length; i++) {
        Device device = this.Devices[i];
        if (device.Description == "NVIDIA NVPerfHUD") {
          if (settings.VertexProcessing == VertexProcessing.PureHardware)
            settings.VertexProcessing = VertexProcessing.Hardware;
          settings.DeviceType = DeviceType.Reference;
          settings.DeviceIndex = i;
          Init(control, settings);
          return device;
        }
      }
      Device dev = Init(control, settings);
      Purple.Log.Warning("Failed to initialize the NVIDIA NVPerfHUD!");
      return dev;
    }

    /// <summary>
    /// Calculates the number of bits per pixel for a given format.
    /// </summary>
    /// <param name="fmt">Format to calc bpp for.</param>
    /// <returns>Number of bits per pixel.</returns>
    static public int BitsPerPixel(Format fmt) {
      switch (fmt) {
        case Format.R8G8B8:
          return 24;
        case Format.A8R8G8B8:
          return 32;
        case Format.X8R8G8B8:
          return 32;
        case Format.R5G6B5:
          return 16;
        case Format.X1R5G5B5:
          return 16;
        case Format.A1R5G5B5:
          return 16;
        case Format.A4R4G4B4:
          return 16;
        case Format.R3G3B2:
          return 8;
        case Format.A8R3G3B2:
          return 16;
        case Format.X4R4G4B4:
          return 16;
        case Format.A2B10G10R10:
          return 32;
        case Format.Dxt1:
          return 4;
        case Format.Dxt2:
          return 8;
        case Format.Dxt3:
          return 8;
        case Format.Dxt4:
          return 8;
        case Format.Dxt5:
          return 8;
        default:
          throw new NotSupportedException("Unknown format: " + fmt);
      }
    }

    /// <summary>
    /// Caluclates the minimum number of channelbits for a given format.
    /// </summary>
    /// <param name="fmt">Format</param>
    /// <returns>Number of minimum bits per channel.</returns>
    static public int ColorChannelBits(Format fmt) {
      switch (fmt) {
        case Format.R8G8B8:
          return 8;
        case Format.A8R8G8B8:
          return 8;
        case Format.X8R8G8B8:
          return 8;
        case Format.R5G6B5:
          return 5;
        case Format.X1R5G5B5:
          return 5;
        case Format.A1R5G5B5:
          return 5;
        case Format.A4R4G4B4:
          return 4;
        case Format.R3G3B2:
          return 2;
        case Format.A8R3G3B2:
          return 2;
        case Format.X4R4G4B4:
          return 4;
        case Format.A2B10G10R10:
          return 10;
        default:
          throw new NotSupportedException("Unknown format: " + fmt);
       }
    }

    /// <summary>
    /// Calculates the number of bits of the alpha channel.
    /// </summary>
    /// <param name="fmt">Format to calculate alpha bits for.</param>
    /// <returns>Number of bits of the alpha channel.</returns>
    static public int AlphaChannelBits(Format fmt) {
      switch (fmt) {
        case Format.R8G8B8:
          return 0;
        case Format.A8R8G8B8:
          return 8;
        case Format.X8R8G8B8:
          return 0;
        case Format.R5G6B5:
          return 0;
        case Format.X1R5G5B5:
          return 0;
        case Format.A1R5G5B5:
          return 1;
        case Format.A4R4G4B4:
          return 4;
        case Format.R3G3B2:
          return 0;
        case Format.A8R3G3B2:
          return 8;
        case Format.X4R4G4B4:
          return 0;
        case Format.A2B10G10R10:
          return 2;
        default:
          throw new NotSupportedException("Unknown format: " + fmt);
      }
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
