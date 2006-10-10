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
using System.IO;

namespace Purple.Graphics.Core {
	//=================================================================
	/// <summary>
	/// abstract interface for a TextureLoader
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public interface ITextureLoader {
    /// <summary>
    /// creates an empty texture
    /// </summary>
    /// <param name="width">width of texture in pixel</param>
    /// <param name="height">height of texture in pixel</param>
    /// <param name="mipLevels">number of mip levels</param>
    /// <param name="format">format to use for texture</param>
    /// <param name="usage">special texture usage</param>
    /// <returns>new texture</returns>
    ITexture2d Create(int width, int height, int mipLevels, Purple.Graphics.Format format, Purple.Graphics.TextureUsage usage);

    /// <summary>
    /// Loads a texture from a stream.
    /// </summary>
    /// <param name="stream">Stream to load texture from.</param>
    /// <param name="width">Width of texture or 0 to keep original width.</param>
    /// <param name="height">Height of texture or 0 to keep original height.</param>
    /// <param name="mipLevels">Number of mipmap levels to create or 0 to create whole mipmap chain.</param>
    /// <param name="format">Format of texture.</param>
    /// <param name="resizeFilter">The filter used for resizing the texture or none.</param>
    /// <param name="mipFilter">The filter used for generating the mipMapLevels or none.</param>
    /// <param name="usage">The hints for creating the texture.</param>
    /// <returns>Texture object.</returns>
    ITexture2d Load(Stream stream, int width, int height, int mipLevels, Purple.Graphics.Format format, 
      Filter resizeFilter, Filter mipFilter, TextureUsage usage);

    /// <summary>
    /// loads a cube texture from a stream
    /// </summary>
    /// <param name="stream">stream to load texture from</param>
    /// <returns>texture object</returns>
    ITextureCube LoadCube(Stream stream);
		
    /// <summary>
    /// gets a description of a stream texture without loading it.
    /// </summary>
    /// <param name="stream">the stream to get information about</param>
    /// <returns>a description of the stream texture</returns>
    SurfaceDescription GetSurfaceDescription(Stream stream);

    /// <summary>
    /// Saves a surface to a stream.
    /// </summary>
    /// <param name="stream">Stream to save surfaces to.</param>
    /// <param name="surface">Surface to save.</param>
    void Save(Stream stream, ISurface surface);

    /// <summary>
    /// Returns the available texture memory.
    /// </summary>
    int AvailableMemory { get; }
	}
}
