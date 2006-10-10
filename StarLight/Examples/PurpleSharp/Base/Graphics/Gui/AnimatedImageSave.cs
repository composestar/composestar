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
using System.IO;

using Purple.Math;
using Purple.Graphics.TwoD;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// The abstract interface for animated images.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para> 
  /// <para>An <see cref="IAnimatedImage"/> can be considered as an animated sprite.
  /// For every frame of the animation, an <see cref="IImage"/> object can be added.</para>
  /// <para>By playing frame after frame, the impression of an animation can be created.</para> 
  /// <para></para>
  /// </remarks>
  //=================================================================
  public interface IAnimatedImage : IGuiParentElement, IImage {
    /// <summary>
    /// Access to the frames of the <see cref="IAnimatedImage"/>.
    /// </summary>
    Images Frames {get; set;}

    /// <summary>
    /// Returns the current frame of the <see cref="IAnimatedImage"/>.
    /// </summary>
    IImage CurrentFrame {get;}
  }

  //=================================================================
  /// <summary>
  /// Standard implementation of the <see cref="IAnimatedImage"/> interface.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// <para>An <see cref="IAnimatedImage"/> can be considered as an animated sprite.
  /// For every frame of the animation, an <see cref="IImage"/> object can be added.</para>
  /// <para>By playing frame after frame, the impression of an animation can be created.</para>
  /// </remarks>
  //=================================================================
	public class AnimatedImageSave : GuiParentElement, IAnimatedImage
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    Images frames;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Access to the frames of the <see cref="AnimatedImage"/>.
    /// </summary>
    public Images Frames {
      get {
        return frames;
      }
      set {
        frames = value;
        frames.Parent = this;
      }
    }

    /// <summary>
    /// Returns the current frames quad object.
    /// </summary>
    public IExtendedQuad Quad { 
      get {
        return frames.Quad;
      }
    }

    /// <summary>
    /// Returns the current frame.
    /// </summary>
    public IImage CurrentFrame {
      get {
        return frames.CurrentFrame;
      }
    }

    /// <summary>
    /// Returns the size of the gui element, which is the max size of all frames.
    /// </summary>
    public override Vector2 Size {
      get {
        return frames.Size;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an <see cref="AnimatedImage"/>.
    /// </summary>
		public AnimatedImageSave()
		{
      Frames = new Images();
		}

    /// <summary>
    /// Creates a new instance of an <see cref="AnimatedImage"/>.
    /// </summary>
    /// <param name="fileName">The animation images to load.</param>
    public AnimatedImageSave(string fileName) {
      this.Frames = new Images(fileName);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public override void OnRender(float deltaTime) {
      frames.OnRender(deltaTime);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
