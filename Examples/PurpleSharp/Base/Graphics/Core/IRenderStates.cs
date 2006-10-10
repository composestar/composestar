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

namespace Purple.Graphics.Core {
	//=================================================================
	/// <summary>
	/// abstract for supported renderstates
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last Change: 0.6</para>
	/// </remarks>
	//=================================================================
	public interface IRenderStates{

    /// <summary>
    /// Sets the source blend factor.
    /// </summary>
    /// <remarks>
    /// <c>Final color = source color * SourceBlend + destination color * DestinationBlend.</c>
    /// </remarks>
    Blend SourceBlend { get; set; }

    /// <summary>
    /// Sets the destination blend factor.
    /// </summary>
    /// <remarks>
    /// <c>Final color = source color * SourceBlend + destination color * DestinationBlend.</c>
    /// </remarks>
    Blend DestinationBlend { get; set; }

		/// <summary>
		/// enable clipping by engine
		/// The default value is true.
		/// </summary>
		bool Clipping { get; set; }

		/// <summary>
		/// Specifies how back-facing triangles are culled, if at all. 
		/// The default value is CounterClockwise.
		/// </summary>
		CullMode CullMode { get; set; }

		/// <summary>
		/// turn ZBuffer on/off
		/// </summary>
		bool ZBuffer { get; set; }

		/// <summary>
		/// function to use for ZBuffer
		/// </summary>
		Compare ZBufferFunction { get; set; }

		/// <summary>
		/// retrieves or enables/disables stencil testing
		/// </summary>
		bool StencilTest { get; set; }			

		/// <summary>
		/// Retrieves or sets the stencil operation to perform if the stencil test fails.
		/// </summary>
		StencilOperation StencilFail { get; set; }
			
		/// <summary>
		/// Stencil operation to perform if both the stencil and the depth (z) tests pass. This can be one member of the D3dstencilop enumerated type. The default value is Keep.
		/// </summary>																																																										
		StencilOperation StencilPass { get; set; }
			
		/// <summary>
		/// Stencil operation to perform if the stencil test passes and the depth test (z-test) fails. This can be one of the members of the D3dstencilop enumerated type. Keep
		/// </summary>
		StencilOperation StencilZBufferFail { get; set; }
			
		/// <summary>
		/// Comparison function for the stencil test. This can be one member of the Compare enumerated type. The default value is Always. 
		/// The comparison function is used to compare the reference value to a stencil buffer entry. 
		/// This comparison applies only to the bits in the reference value and stencil buffer entry that are set in the stencil mask (set by the StencilMask render state). 
		/// If true, the stencil test passes.
		/// </summary>
		Compare StencilFunction { get; set; }			

		/// <summary>
		/// Mask applied to the reference value and each stencil buffer entry to determine the significant bits for the stencil test. 
		/// The default mask is 0xFFFFFFFF.
		/// </summary>
		int StencilMask { get; set; }			

		/// <summary>
		/// Write mask applied to values written into the stencil buffer. The default mask is 0xFFFFFFFF.
		/// </summary>
		int StencilWriteMask { get; set; }			

		/// <summary>
		/// One or more members of the D3dshademode enumerated type. The default value is Gouraud.
		/// </summary>
		ShadeMode ShadeMode { get; set; }

		/// <summary>
		/// true to enable the application to write to the depth buffer.
		/// </summary>
		bool ZBufferWriteEnable { get; set; }

		/// <summary>
		/// An int reference value for the stencil test. The default value is 0.
		/// </summary>
		int StencilReference { get; set; }

		/// <summary>
		/// A bool value. True enables two-sided stenciling, false disables it. 
		/// The application should set CullMode to None to enable two-sided stencil mode. 
		/// If the triangle winding order is clockwise, the Stencil* operations will be used. 
		/// If the winding order is counterclocwise, the CounterClockWiseStencil* operations will be used.
		/// </summary>
		bool TwoSidedStencilMode { get; set; }

		/// <summary>		
		/// The comparison function. ccw stencil test passes if ((ref AND mask) stencil function (stencil AND mask)) is true.
		/// </summary>	
		Compare CounterClockwiseStencilFunction { get; set; }
		
		/// <summary>
		/// Stencil operation to perform if ccw stencil test passes and z-test fails.
		/// </summary>	
		StencilOperation CounterClockwiseStencilZBufferFail { get; set; }
		
		/// <summary>
		/// Stencil operation to perform if ccw stencil test fails.
		/// </summary>
		StencilOperation CounterClockwiseStencilFail { get; set; }

		/// <summary>
		/// Stencil operation to perform if both ccw stencil and z-tests pass.
		/// </summary>
		StencilOperation CounterClockwiseStencilPass { get; set; }

    /// <summary>
    /// Flag that indicates if alpha blending is enabled.
    /// </summary>
    bool AlphaEnable { get; set; }

    /// <summary>
    /// Enable/disable wireframe mode.
    /// </summary>
    bool Wireframe { get; set; }

    /// <summary>
    /// Enables/disables writing the color value to the frame buffer.
    /// </summary>
    ColorChannels ColorWriteEnable { get; set; }

    /// <summary>
    /// Polygons with a high z-bias value appear in front of polygons with a low value, without requiring sorting for drawing order. 
    /// For example, polygons with a value of 1 appear in front of polygons with a value of 0.
    /// </summary>
    float DepthBias { get; set; }

    /// <summary>
    /// Enable dithering.
    /// </summary>
    bool Dither { get; set; }
	}
}
