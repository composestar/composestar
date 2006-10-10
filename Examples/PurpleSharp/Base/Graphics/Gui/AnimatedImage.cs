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

using Purple.Graphics;
using Purple.Graphics.Core;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// An image that is animated via multiple textures.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// By playing frame after frame, the impression of an animation can be created.
  /// </remarks>
  //=================================================================
	public class AnimatedImage : Image{
	  //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ITexture2d[] textures;

    /// <summary>
    /// Returns the number of frames.
    /// </summary>
    public int Frames {
      get {
        return textures.Length;
      }
    }

    /// <summary>
    /// Sets or retrieves the current frame index.
    /// </summary>
    public int Frame {
      get {
        return frame;
      }
      set {
        frame = value % Frames;
        Quad.Texture = textures[frame];
      }
    }
    int frame = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisations
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new animated image object.
    /// </summary>
    /// <param name="textures">An array of textures to use.</param>
    public AnimatedImage(ITexture2d[] textures) : base(textures[0]) {
      this.textures = textures;
    }

    /// <summary>
    /// Creates a new animated image object.
    /// </summary>
    /// <param name="fileName">Base name of animation files</param>
    public AnimatedImage(string fileName) {
      string[] files = TextureManager.Instance.GetAnimationFiles(fileName);
      if (files.Length == 0)
        throw new ArgumentException("There must be at least one animation image!");
      this.textures = new ITexture2d[files.Length];
      for (int i=0; i<textures.Length; i++)
        textures[i] = TextureManager.Instance.Load(files[i]);
      Init(textures[0]);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
