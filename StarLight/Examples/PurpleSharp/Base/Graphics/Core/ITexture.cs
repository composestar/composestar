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
using System.Drawing;

namespace Purple.Graphics.Core {
	//=================================================================
	/// <summary>
	/// abstract interface for a texture
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para> 
	///   <para>Last Change: 0.7</para> 
	/// </remarks>
	//=================================================================
	public interface ITexture : IComparable {

    /// <summary>
    /// The usage of the texture.
    /// </summary>
    TextureUsage Usage { get; }

		/// <summary>
		/// Returns the id of the texture.
		/// </summary>
		int Id { get; set;}	

    /// <summary>
    /// Returns the description of the surface.
    /// </summary>
    Purple.Graphics.SurfaceDescription Description {get; }

    /// <summary>
    /// Uploads the offline data to the gpu.
    /// </summary>
    void Upload();

    /// <summary>
    /// Returns true if the texture was already uploaded.
    /// </summary>
    /// <returns></returns>
    bool HasOnlineData { get; }

    /// <summary>
    /// Disposes the online data.
    /// </summary>
    void DisposeOnlineData();
	}

  //=================================================================
  /// <summary>
  /// abstract interface for a 2d texture
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public interface ITexture2d : ITexture {

    /// <summary>
    /// get the absolute texture coordinates of this texture
    /// </summary>
    RectangleF TextureCoordinates { get; }

    /// <summary>
    /// returns the parent texture
    /// </summary>
    ITexture2d Parent { get; }

    /// <summary>
    /// returns the physical texture
    /// </summary>
    ITexture2d Root { get; }

    /// <summary>
    /// returns a description of the image
    /// </summary>
    SurfaceDescription ImageDescription { get; }

    /// <summary>
    /// Copies a rectangular area of the bitmap to the texture.
    /// </summary>
    /// <param name="bitmap">The bitmap to copy.</param>
    /// <param name="source">The source rectangle (Rectangle.Empty to copy the whole bitmap).</param>
    /// <param name="target">The target position.</param>
    void CopyBitmap(Bitmap bitmap, Rectangle source, Point target);

    /// <summary>
    /// Clears the texture to a certain color.
    /// </summary>
    /// <param name="color">Color to use for clearing texture.</param>
    void Clear(System.Drawing.Color color);
  }

  //=================================================================
  /// <summary>
  /// abstract interface for a 3d texture
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public interface ITexture3d : ITexture {
  }

  //=================================================================
  /// <summary>
  /// abstract interface for a cube texture
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public interface ITextureCube : ITexture {
  }
}
