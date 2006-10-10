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

using System.Diagnostics;
using System.Collections;
using System.Windows.Forms;

using Purple.Graphics.Core;
using Purple.Graphics.Effect;
using Purple.Math;

namespace Purple.Graphics {
	//=================================================================
	/// <summary>
	/// adds some advanced functionality to IDevice
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.7</para>
	/// </remarks>
	//=================================================================
	public class Device : IDisposable {
		//---------------------------------------------------------------
    #region Variables and Properties
		//---------------------------------------------------------------
		IDevice device;
		IShaderCompiler shaderCompiler = null;
    IShaderCompiler hlslShaderCompiler = null;
		Transformations transformations = new Transformations();
    ShaderConstants shaderConstants = null;
    bool deviceLost = false;
    Viewport viewport;
    TextureStages textureStages;

    /// <summary>
    /// TrianglesRendered in the last frame.
    /// </summary>
    public int TrianglesRendered {
      get {
        return trianglesRendered;
      }
      set {
        trianglesRendered = value;
      }
    }
    int trianglesRendered = 0;

    /// <summary>
    /// Returns the current target control of the device.
    /// </summary>
    public Control Control {
      get {
        return control;
      }
    }
    Control control;
		
    /// <summary>
    /// Get pixel shader constant registers.
    /// </summary>
    public IPixelShaderConstants PixelShaderConstants {
      get {
        return device.PixelShaderConstants;
      }
    }

    /// <summary>
    /// Get vertex shader constant registers.
    /// </summary>
    public IVertexShaderConstants VertexShaderConstants {
      get {
        return device.VertexShaderConstants;
      }
    }

    /// <summary>
    /// Get the render states object.
    /// </summary>
    public IRenderStates RenderStates {
      get {
        return device.RenderStates;
      }
    }

    /// <summary>
    /// access to the viewport
    /// viewport is overwritten on reset, RenderTarget, ..
    /// </summary>
    public Viewport Viewport {
      get {
        return viewport;
      }
      set {
        viewport = value;
        device.SetViewport(viewport);
      }
    }

		/// <summary>
		/// returns currently used device
		/// </summary>
		static public Device Instance {
			get {
				return GraphicsEngine.Instance.Device;
			}
		}

    /// <summary>
    /// dispose the device
    /// </summary>
    public void Dispose() {
      Purple.Profiling.Profiler.Instance.Begin("Device.Dispose");
      //DisposeObjects();
      /*for (int i=0; i<16; i++)
        device.SetVertexBuffer(i, null, 0);
      for (int i=0; i<8; i++)
        device.SetTexture(i, null);*/
      // ddispose plugIn
      device.Dispose();
      Purple.Profiling.Profiler.Instance.End("Device.Dispose");
    }

    /// <summary>
    /// is device currently lost (try reset)
    /// </summary>
    public bool IsLost {
      get {
        return deviceLost;
      }
    }

    /// <summary>
    /// Returns a list of <see cref="DisplayMode"/> objects.
    /// </summary>
    public DisplayModes DisplayModes {
      get {
        return device.DisplayModes;
      }
    }

    /// <summary>
    /// Returns the current displaymode.
    /// </summary>
    public DisplayMode CurrentDisplayMode {
      get {
        return device.CurrentDisplayMode;
      }
    }

    /// <summary>
    /// OpenGl, DirectX IDevice Plugin
    /// </summary>
    public Core.IDevice PlugIn { 
      get {
        return device;
      }
    }

		/// <summary>
		/// name of device
		/// </summary>
		public string Name { 
			get {
				return device.Name;
			}
		}

		/// <summary>
		/// returns capabilities of this device
		/// </summary>
		public Capabilities Capabilities { 
			get {
				return device.Capabilities;
			}
		}

		/// <summary>
		/// description of device
		/// </summary>
		public string Description { 
			get {
				return device.Description;
			}
		}  

    /// <summary>
    /// The backBuffer used for rendering the scene to.
    /// </summary>
    public ISurface BackBuffer {
      get {
        return device.BackBuffer;
      }
    }
    
    /// <summary>
    /// The combined depth and stencil buffer surface.
    /// </summary>
    /// <remarks>
    /// The property may return null if no depth and no stencil buffer is used.
    /// </remarks>
    public ISurface DepthStencilBuffer {
      get {
        return device.DepthStencilBuffer;
      }
    }

    /// <summary>
    /// texture to render to
    /// </summary>
    public ISurface RenderTarget {
      get {
        return device.RenderTarget;
      }
      set {
        device.RenderTarget = value;
        viewport = new Viewport(0,0, device.Resolution.Width, device.Resolution.Height);
      }
    }

    /// <summary>
    /// get the resolution used for the internal buffers
    /// should optimally be the same as the output size
    /// </summary>
    public System.Drawing.Size Resolution { 
      get {
        return device.Resolution;
      }
    }

		/// <summary>
		/// get access to transformation matrices
		/// </summary>
		public Transformations Transformations { 
			get {
				return transformations;
			}
		}  

		/// <summary>
		/// get access to samplerStates
		/// </summary>
		public ISamplerStates SamplerStates { 
			get {
				return device.SamplerStates;
			}
		}  
	
    /// <summary>
    /// Access to the textures.
    /// </summary>
    public TextureStages TextureStages {
      get {
        return textureStages;
      }
    }

		/*/// <summary>
		/// Access to the currently set textures object.
		/// </summary>
		public Textures Textures { 
			get {
				return textures;
			}
			set {
        if (textures != value || (textures != null) ) {		
			    textures = value;   

          int stage = 0;
          if (textures != null) {
            foreach(ITexture tex in textures) {
              this.SetTexture(stage, tex);
              stage++;
            }
          }
          // <TODO> get current TexStageNum!!!
          while (stage < 8) {
            this.SetTexture(stage, null);					
            stage ++;
          }
        }
			}
		} */ 

		/// <summary>
		/// virtual indexBuffer to set for rendering
		/// </summary>
		public IGraphicsStream IndexStream {			
			set {
        if (value == null) {
          device.IndexBuffer = null;
          return;
        }
				if (!value.HasOnlineData())
					value.Upload();					
				device.IndexBuffer = value.PhysicalBuffer;
			}
		}	
	
		/// <summary>
		/// VertexUnit to set for rendering
		/// </summary>
		public VertexUnit VertexUnit {
			set {
				device.VertexDeclaration = value.VertexDeclaration;
				for (int i=0; i<value.StreamCount; i++) {
					IGraphicsStream stream = value[i];
          if (!stream.IsSoftware) {
            if (!stream.HasOnlineData())
              stream.Upload();
            device.SetVertexBuffer( i, stream.PhysicalBuffer, 0);
          }
				}								
			}
		}
		
		/// <summary>
		/// current active vertex shader
		/// </summary>
		public IVertexShader VertexShader { 
      get {
        return device.VertexShader;
      }
      set {
        device.VertexShader = value;
      }
		}

		/// <summary>
		/// returns the shader compiler
		/// </summary>
		public IShaderCompiler ShaderCompiler {
			get {
				if (shaderCompiler == null)
					shaderCompiler = device.CreateShaderCompiler();
				return shaderCompiler;
			}
		}	

    /// <summary>
    /// Returns the shader compiler for the high level shader language.
    /// </summary>
    public IShaderCompiler HLSLShaderCompiler {
      get {
        if (hlslShaderCompiler == null)
          hlslShaderCompiler = device.CreateHLSLShaderCompiler();
        return hlslShaderCompiler;
      }
    }
	
    /// <summary>
    /// returns the shader consant table
    /// </summary>
    public ShaderConstants ShaderConstants {
      get {
        if (shaderConstants == null)
          shaderConstants = ShaderConstants.Instance;
        return shaderConstants;
      }
    }
			
		/// <summary>
		/// current active pixel shader
		/// </summary>
		public IPixelShader PixelShader { 
			get {
				return device.PixelShader;
			}
      set {
        device.PixelShader = value;
      }
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		internal Device(IDevice device) {
			this.device = device;
      Transformations.World = Matrix4.Identity;
      Transformations.View = Matrix4.Identity;
      Transformations.Projection = Matrix4.PerspectiveFOV(4.0f/3, 1.0f, 1.0f, 1000.0f);
      textureStages = new TextureStages(this);
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		/// <summary>
		/// starts a new scene for drawing to the control
		/// </summary>
		public void BeginScene() {
			device.BeginScene();
		}

		/// <summary>
		/// end scene
		/// </summary>
		public void EndScene() {
			device.EndScene();
		}

		/// <summary>
		/// present scene to control
		/// </summary>
		public void Present() {
      //Present( System.Drawing.Rectangle.FromLTRB(0,0, device.Resolution.Width, device.Resolution.Height));
      deviceLost = !device.Present();
		}

    /// <summary>
    /// Switches the current graphics mode.
    /// </summary>
    /// <param name="settings">The new settings.</param>
    public void Switch(GraphicsSettings settings) {
      // Is this our first time we initialize the graphics device? If not cleanup stuff.
      bool wasInitialized = Control != null;

      // remove resizing events
      if (wasInitialized) {
        Form oldForm = (Control.TopLevelControl as Form);
        oldForm.Resize -= new EventHandler(OnResizeControl);
        control.Resize -= new EventHandler(OnResizeControl);
      }

      // do we have assigned a new target control?
      if (settings.Control != null)
        this.control = settings.Control;
        // if not assign the current one
      else
        settings.Control = this.control;

      Form form = (Control.TopLevelControl as Form);
      form.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;

      if (wasInitialized)
        TextureManager.Instance.Release();
      if (device.Switch(settings)) {
        if (wasInitialized)
          TextureManager.Instance.Recreate();

        // reset fullScreen/windowed stuff   
        Control targetControl;
        if (settings.FullScreen) {
          targetControl = form;
          form.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
        } else {
          targetControl = control;
          form.FormBorderStyle = System.Windows.Forms.FormBorderStyle.Sizable;
        }
        if (!form.Visible)
          form.Show(); // necessay to prevent OutOfMemoryException when assigning: pp.DeviceWindow = form;
        form.Left = 0;
        form.Top = 0;
        form.ClientSize = new System.Drawing.Size(settings.Width, settings.Height);
        targetControl.Resize += new EventHandler(OnResizeControl);
        Viewport = new Viewport(0,0, targetControl.ClientSize.Width, targetControl.ClientSize.Height);

        Purple.Log.Spam("Device was (re)created - groovy!");
      } else {
        Purple.Log.Spam("Couldn't creat device!");
      }
    }

    /// <summary>
    /// reset the device
    /// </summary>
    public void Reset() {
      TextureManager.Instance.Release();
      // Try to reset the device and our viewport.
      if (device.Reset()) {
        viewport = new Viewport(0,0, device.Resolution.Width, device.Resolution.Height);
        TextureManager.Instance.Recover();
        deviceLost = false;
        Purple.Log.Spam("Device was reset - groovy!");
      } else // device couldn't be reset
        deviceLost = true;
    }

    /// <summary>
    /// Color for clearing the frame buffer.
    /// </summary>
    public int ClearColor { 
      get {
        return device.ClearColor;
      }
      set {
        device.ClearColor = value;
      }
    }

    /// <summary>
    /// Value to use for clearing the z-Buffer.
    /// </summary>
    public float ClearDepth { 
      get {
        return device.ClearDepth;
      }
      set {
        device.ClearDepth = value;
      }
    }
    /// <summary>
    /// The clear value for the stencil value;
    /// </summary>
    public int ClearStencil { 
      get {
        return device.ClearStencil;
      }
      set {
        device.ClearStencil = value;
      }
    }

    /// <summary>
    /// Clears the back buffers.
    /// </summary>
    /// <param name="clearFlags">Flags that determine which buffers should be cleared.</param>
    public void Clear(ClearFlags clearFlags) {
      device.Clear(clearFlags);
    }

		/// <summary>
		/// creates an vertex declaration object
		/// </summary>
		public IVertexDeclaration CreateVertexDeclaration(VertexElement[] elements) {
      // filter for software
      int software = 0;
      for (int i=0; i<elements.Length; i++)
        if (elements[i].DeclarationType == DeclarationType.Software)
          software++;

      int filled = 0;
      VertexElement[] newElements = new VertexElement[elements.Length - software];
      for (int i=0; i<elements.Length; i++) {
        if (elements[i].DeclarationType != DeclarationType.Software) {
          newElements[ filled ] = elements[i];
          filled++;
        }
      }

			return device.CreateVertexDeclaration(newElements);
		}


		/// <summary>
		/// sets a texture for the rendered primitives
		/// </summary>
		/// <param name="stage">to set texture for</param>
		/// <param name="tex">texture to use</param>
		void SetTexture(int stage, ITexture tex) {
			device.SetTexture(stage, tex);
		}

		/// <summary>
		/// renders the triangles defined by the vertexBuffer
		/// </summary>
		/// <param name="startVertex">vertex of first primitive</param>
		/// <param name="primitiveCount">number of primitives to render</param>
		public void Draw(int startVertex, int primitiveCount) {
			device.Draw(startVertex, primitiveCount);
      this.trianglesRendered += primitiveCount;
		}

		/// <summary>
		/// renders the triangles defined by the indexBuffer
		/// </summary>
		/// <param name="baseIndex">realIndex = Index + baseIndex </param>	
		/// <param name="minVertex">min vertexBuffer index</param>
		/// <param name="numVertices">num vertexBuffer indices</param>
		/// <param name="startIndex">index of indexBuffer to start with</param>		
		/// <param name="primitiveCount">Number of primitives to render</param>
		public void DrawIndexed(int baseIndex, int minVertex, int numVertices, int startIndex, int primitiveCount) {
			device.DrawIndexed(baseIndex, minVertex, numVertices, startIndex, primitiveCount);
      this.trianglesRendered += primitiveCount;
		}

    internal void OnResizeControl(object source, EventArgs args) {
      Reset();
    }

    /// <summary>
    /// Disposes all managed resources;
    /// </summary>
    public void DisposeManagedResources() {
      TextureManager.Instance.DisposeOnlineData();
      device.DisposeManagedResources();
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
