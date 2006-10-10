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
using Purple.Graphics.TwoD;
using Purple.Graphics.Core;
using Purple.Math;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// Abstract interface for a gui element that shows a simple image.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IImage : IGuiElement {
    /// <summary>
    /// Returns the <see cref="IExtendedQuad"/> that should be shown 
    /// by a gui element.
    /// </summary>
    IExtendedQuad Quad { get; }
  }

  //=================================================================
  /// <summary>
  /// A simple gui element that shows an image.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public class Image : GuiElement, IImage
	{    
    //---------------------------------------------------------------
    #region Properties and Variables
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the <see cref="IExtendedQuad"/> that should be shown 
    /// by a gui element.
    /// </summary>
    public IExtendedQuad Quad {
      get {
        return quad;
      }
      set {
        quad = value;
      }
    }
    private IExtendedQuad quad;

    /// <summary>
    /// Returns the size of the gui element.
    /// </summary>
    public override Vector2 Size { 
      get {
        return quad.Size;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an <see cref="Image"/>. 
    /// <note type="note">Don't forget to set the <c>quad</c>.</note>
    /// </summary>
    internal Image() {
    }

    /// <summary>
    /// Creates a new instance of an <see cref="Image"/>.
    /// </summary>
    /// <param name="quad">Quad to visualize in image.</param>
		public Image(IExtendedQuad quad)
		{
      this.quad = quad;
		}

    /// <summary>
    /// Creates a new instance of an <see cref="Image"/>.
    /// </summary>
    /// <param name="texture">Texture to use for quad.</param>
    public Image(ITexture2d texture) {
      Init(texture);
    }

    /// <summary>
    /// Create a new instance of an <see cref="Image"/>
    /// </summary>
    /// <param name="fileName">FileName of texture to use for quad.</param>
    public Image(string fileName) :
      this(TextureManager.Instance.Load(fileName)) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Initializes the image with a given texture.
    /// </summary>
    /// <param name="texture">Texture to initialize image with.</param>
    protected void Init(ITexture2d texture) {
      if (texture == null)
        throw new ArgumentNullException("texture");
      quad = (IExtendedQuad)QuadFactory.Instance.CreateQuad();
      quad.Texture = texture;
      quad.Size = quad.TextureSize;
    }

    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public override void OnRender(float deltaTime) {
      quad.Position = this.AbsolutePosition;
      quad.RotationZ = this.AbsoluteRotation;
      quad.Scale = this.AbsoluteScale;
      quad.Alpha = this.AbsoluteAlpha;
      QuadManager.Instance.Draw(quad);
    }

    /// <summary>
    /// Creates a list of images from a list of textures.
    /// </summary>
    /// <param name="textures">The list of textures.</param>
    /// <returns>The list of images.</returns>
    public static IImage[] From(ITexture2d[] textures) {
      IImage[] images = new IImage[textures.Length];
      for (int i = 0; i < textures.Length; i++) {
        images[i] = new Image(textures[i]);        
      }
      return images;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
