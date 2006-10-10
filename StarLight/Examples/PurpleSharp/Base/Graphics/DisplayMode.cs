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

namespace Purple.Graphics
{
  //=================================================================
  /// <summary>
  /// The struct defining a certain DisplayMode.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
	public struct DisplayMode {
	 	//---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Width of screen in pixel.
    /// </summary>
    public int Width;
      
    /// <summary>
    /// Height of screen in pixel.
    /// </summary>
    public int Height;    

    /// <summary>
    /// The color format.
    /// </summary>
    public Format Format;     

    /// <summary>
    /// Refresh rate of screen.
    /// </summary>
    public int RefreshRate;     

    /// <summary>
    /// Returns the number of BitsPerPixel for the current DisplayMode.
    /// </summary>
    public int BitsPerPixel {
      get {
        return GraphicsEngine.BitsPerPixel(Format);
      }
    }

    /// <summary>
    /// The aspect ratio of this DisplayMode.
    /// </summary>
    public float AspectRatio {
      get {
        return (float)Width/Height;
      }
    }

    /// <summary>
    /// An invalid DisplayMode. May be used to express that no DisplayMode was found or whatever.
    /// </summary>
    public static DisplayMode None {
      get {
        return new DisplayMode(0,0, Format.A16B16G16R16, 0);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Create a new DisplayMode object.
    /// </summary>
    /// <param name="width">Width of screen in pixel.</param>
    /// <param name="height">Height of screen in pixel.</param>
    /// <param name="format">Color format.</param>
    /// <param name="refreshRate">Refresh rate of screen.</param>
    public DisplayMode(int width, int height, Format format, int refreshRate) {
      this.Width = width;
      this.Height = height;
      this.Format = format;
      this.RefreshRate = refreshRate;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary> 
    /// Returns the Displaymode as a string.
    /// </summary>
    /// <returns></returns>
    public override string ToString() {
      return Width + "*" + Height + "*" + Format + " (" + RefreshRate + " Hz)";
    }

    /// <summary>
    /// Returns true if the current mode has the given aspect ratio.
    /// </summary>
    /// <param name="width">Width.</param>
    /// <param name="height">Height.</param>
    /// <returns>True if the current mode has the given aspect ratio.</returns>
    public bool HasAspectRatio(int width, int height) {
      float aspect = (float)width/height;
      float diff = this.AspectRatio - aspect;
      return (diff < 0.0001f && diff > -0.0001f);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	
	}
}
