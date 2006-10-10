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

using Purple.Math;
using Purple.Graphics.Effect;

namespace Purple.Graphics.Core
{

  //=================================================================
  /// <summary>
  /// abstract interface defining a graphical device
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  ///   <para>Last change: 0.7</para>
  /// </remarks>
  //=================================================================
	public interface IDevice : IDisposable {	
		//---------------------------------------------------------------
    #region Properties
		//---------------------------------------------------------------
    /// <summary>
    /// The RenderStates object.
    /// </summary>
    IRenderStates RenderStates { get; }

    /// <summary>
    /// The vertex shader constants.
    /// </summary>
    IVertexShaderConstants VertexShaderConstants { get; }

    /// <summary>
    /// The pixel shader constants.
    /// </summary>
    IPixelShaderConstants PixelShaderConstants { get; }

    /// <summary>
    /// ptr to OpenGl, DirectX9, ... handle
    /// it's for internal use only
    /// </summary>
    IntPtr InternalObject { get; }

		/// <summary>
		/// Returns a list of supported <see cref="DisplayMode"/> objects
		/// </summary>
		/// <returns>DisplayMode objects.</returns>
		DisplayModes DisplayModes { get; }

    /// <summary>
    /// Returns the current displaymode.
    /// </summary>
    DisplayMode CurrentDisplayMode { get; }

		/// <summary>
		/// name of device
		/// </summary>
		string Name { get; }

		/// <summary>
		/// description of device
		/// </summary>
		string Description { get; }

    /// <summary>
    /// if false, properties are only set if different
    /// if ture, properties are set in any case
    /// </summary>
    bool OverrideProperties { get; set;}
 
    /// <summary>
    /// get the resolution used for the internal buffers
    /// should optimally be the same as the output size
    /// </summary>
    System.Drawing.Size Resolution { get; }

		/// <summary>
		/// returns capabilities of this device
		/// </summary>
		Capabilities Capabilities { get; }

		/// <summary>
		/// get access to samplerStates
		/// </summary>
		ISamplerStates SamplerStates { get; }

		/// <summary>
		/// indexBuffer to set for rendering
		/// </summary>		
		IPhysicalGraphicsBuffer IndexBuffer { set; }
		
		/// <summary>
		/// current active vertex shader
		/// </summary>
		IVertexShader VertexShader { get; set; }

		/// <summary>
		/// get/sets the vertexDeclaration for vertex shader
		/// </summary>
		IVertexDeclaration VertexDeclaration { get; set; } 	
			
		/// <summary>
		/// current active pixel shader
		/// </summary>
		IPixelShader PixelShader { get; set; }	
		
    /// <summary>
    /// The backBuffer used for rendering the scene to.
    /// </summary>
    ISurface BackBuffer { get; }
    
    /// <summary>
    /// The combined depth and stencil buffer surface.
    /// </summary>
    /// <remarks>
    /// The property may return null if no depth and no stencil buffer is used.
    /// </remarks>
    ISurface DepthStencilBuffer { get; }

    /// <summary>
    /// access to the current renderTarget
    /// </summary>
    ISurface RenderTarget { get; set; }

    /// <summary>
    /// The standard effect compiler for this Device.
    /// </summary>
    IEffectCompiler EffectCompiler { get; }
		//---------------------------------------------------------------
    #endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
    #region Methods
		//---------------------------------------------------------------
    /// <summary>
    /// Switches the current graphics mode.
    /// </summary>
    /// <param name="settings">The new settings.</param>
    /// <returns>Returns true if switch was successful.</returns>
    bool Switch(GraphicsSettings settings);

		/// <summary>
		/// starts a new scene for drawing to the control
		/// </summary>
		void BeginScene();

		/// <summary>
		/// end scene
		/// </summary>
		void EndScene();

    /// <summary>
    /// Presents the scene.
    /// </summary>
    /// <returns>Returns false if present fails because the device is lost and needs to be reset.</returns>
    bool Present();

    /// <summary>
    /// Tries to reset the device.
    /// </summary>
    /// <returns>True if resetting the device was successful.</returns>
    bool Reset();

    /// <summary>
    /// Color for clearing the frame buffer.
    /// </summary>
    int ClearColor { get; set; }

    /// <summary>
    /// Value to use for clearing the z-Buffer.
    /// </summary>
    float ClearDepth { get; set; }

    /// <summary>
    /// The clear value for the stencil value;
    /// </summary>
    int ClearStencil { get; set; }

		/// <summary>
		/// Clears the back buffers.
		/// </summary>
		/// <param name="clearFlags">Flags that determine which buffers should be cleared.</param>
		void Clear(ClearFlags clearFlags);

		/// <summary>
		/// creates a new vertex Buffer
		/// </summary>
		/// <param name="vertexFormat">type of custom vertex format</param>
		/// <param name="numVertices">number of vertices to store in vertexBuffer</param>		
		IPhysicalGraphicsBuffer CreateVertexBuffer(Type vertexFormat, int numVertices);

		/// <summary>
		/// creates an index buffer
		/// </summary>
		/// <param name="indexFormat">format of indices</param>
		/// <param name="size">size of buffer in vertices</param>		
		/// <returns>indexBuffer</returns>		
		IPhysicalGraphicsBuffer CreateIndexBuffer(Type indexFormat, int size);

		/// <summary>
		/// creates an instance of a TextureLoader class
		/// </summary>
		ITextureLoader CreateTextureLoader();

		/// <summary>
		/// creates a shader compiler
		/// </summary>
		/// <returns>shader compiler</returns>
		IShaderCompiler CreateShaderCompiler();

    /// <summary>
    /// Creates a hlsl shader compiler.
    /// </summary>
    /// <returns>The hlsl shader compiler.</returns>
    IShaderCompiler CreateHLSLShaderCompiler();

		/// <summary>
		/// creates a vertex declaration object
		/// </summary>
		IVertexDeclaration CreateVertexDeclaration(VertexElement[] elements);

		/// <summary>
		/// vertexBuffer to set for rendering
		/// </summary>
		/// <param name="stream">stream to set vertex buffer for</param>
		/// <param name="buffer">which should be used for rendering</param>
		/// <param name="offset">offset to first element in bytes</param>
		void SetVertexBuffer(int stream, IPhysicalGraphicsBuffer buffer, int offset);		

		/// <summary>
		/// sets a texture for the rendered primitives
		/// </summary>
		/// <param name="stage">to set texture for</param>
		/// <param name="tex">texture to use</param>
		void SetTexture(int stage, ITexture tex);

		/// <summary>
		/// renders the triangles defined by the vertexBuffer
		/// </summary>
		/// <param name="startVertex">vertex of first primitive</param>
		/// <param name="primitiveCount">number of primitives to render</param>
		void Draw(int startVertex, int primitiveCount);

		/// <summary>
		/// renders the triangles defined by the indexBuffer
		/// </summary>	
		/// <param name="baseIndex">realIndex = Index + baseIndex </param>	
		/// <param name="minVertex">min vertexBuffer index</param>
		/// <param name="numVertices">num vertexBuffer indices</param>
		/// <param name="startIndex">index of indexBuffer to start with</param>		
		/// <param name="primitiveCount">Number of primitives to render</param>
		void DrawIndexed(int baseIndex, int minVertex, int numVertices, int startIndex, int primitiveCount);				

    /// <summary>
    /// update the viewport - used by Device
    /// may be used to update internal viewport
    /// </summary>
    /// <param name="viewport">new viewport</param>
    void SetViewport(Viewport viewport);

    /// <summary>
    /// Disposes all managed resources;
    /// </summary>
    void DisposeManagedResources();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

	}
}
