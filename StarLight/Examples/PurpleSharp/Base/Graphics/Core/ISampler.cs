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
	/// interface for SamplerStates
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public interface ISamplerStates {		
		/// <summary>
		/// access Sampler by Index
		/// </summary>
		ISampler this[int index] { get; }					

		/// <summary>
		/// The number of ISamplers contained within this object;
		/// </summary>
		int Length {get;}
	}

	//=================================================================
	/// <summary>
	/// abstract interface for a sampler
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public interface ISampler {
		/// <summary>
		/// Texture-address mode for the u coordinate.
		/// </summary>
		TextureAddress AddressU { get; set; }
			
		/// <summary>
		/// Texture-address mode for the v coordinate.
		/// </summary>
		TextureAddress AddressV  { get; set; }

		/// <summary>
		/// Texture-address mode for the w coordinate.
		/// </summary>
		TextureAddress AddressW  { get; set; }

		/// <summary>
		/// Border color or type Color.
		/// </summary>
		int BorderColor  { get; set; }

		/// <summary>
		/// Vertex offset in the presampled displacement map. This is a constant used by the tessellator, its value is 256.
		/// </summary>
		int DMapOffset  { get; set; }

		/// <summary>
		/// When a multi-element texture is assigned to the sampler, this indicates which element index to use. 
		/// The default value is 0.
		/// </summary>
		int ElementIndex { get; set; }

		/// <summary>
		/// Magnification filter
		/// </summary>
		TextureFilter MagFilter { get; set; }

		/// <summary>
		/// Minification filter
		/// </summary>
		TextureFilter MinFilter { get; set; }

		/// <summary>
		/// Mipmap filter to use during minification.
		/// </summary>
		TextureFilter MipFilter { get; set; }

		/// <summary>
		/// 
		/// </summary>
		int MaxAnisotropy  { get; set; }

		/// <summary>
		/// LOD index of largest map to use. Values range from 0 to (n-1) where 0 is the largest.
		/// </summary>
		int MaxMipLevel { get; set; }

		/// <summary>
		/// Mipmap level of detail bias.
		/// </summary>
		float MipMapLevelOfDetailBias  { get; set; }

		
		/// <summary>Gamma correction value. The default value is 0, which means gamma is 1.0 and no correction is required. 
		/// Otherwise, this value means that the sampler should assume gamma of 2.2 on the content and convert it to linear (gamma 1.0) before presenting it to the pixel shader.</summary>		
		bool SrgbTexture    { get; set; }
	}
}
