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
using Purple.Graphics.Core;

namespace Purple.Graphics
{
  //=================================================================
  /// <summary>
  /// A SubTexture is a part of another texture. A SubTexture simplifies
  /// texture sharing which might result in higher performance.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
	public class SubTexture : ITexture2d
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The usage of the texture.
    /// </summary>
    public TextureUsage Usage { 
      get {
        return parentTexture.Usage;
      }
    }

    /// <summary>
    /// The surfaces of the texture.
    /// </summary>
    public ISurface[] Surfaces { 
      get {
        return surfaces;
      }
    }
    ISurface[] surfaces = null;  

    /// <summary>
    /// returns the description of the current image
    /// </summary>
    public SurfaceDescription ImageDescription {
      get {
        return this.imageDescription;
      }
    }
    SurfaceDescription imageDescription;

    /// <summary>
    /// returns the parentTexture of the current texture
    /// </summary>
    public ITexture2d Parent {
      get {
        return parentTexture;
      }
      set {
        parentTexture = value;
      }
    }
    ITexture2d parentTexture;

    /// <summary>
    /// returns the physical texture
    /// </summary>
    public ITexture2d Root {
      get {
        ITexture2d current = this;
        while (current.Parent != null)
          current = current.Parent;
        return current;
      }
    }

    /// <summary>
    /// absolute texture coordinates
    /// </summary>
    public RectangleF TextureCoordinates {
      get {
        return textureCoordinates;
      }
    }
    RectangleF textureCoordinates;

    /// <summary>
    /// returns the id of the texture
    /// </summary>
    public int Id {
      get {
        return parentTexture.Id;
      }
      set {
        parentTexture.Id = value;
      }
    }

    /// <summary>
    /// description of the texture
    /// </summary>
    public SurfaceDescription Description {
      get {
        return parentTexture.Description;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Creates an instance of a SubTexture.
    /// </summary>
    /// <param name="textureCoordinates">New texture coordinates relativ to parent texture [0;1].</param>
    /// <param name="parentTexture">The texture to create the subTexture from.</param>
    public SubTexture(RectangleF textureCoordinates, ITexture2d parentTexture)
		{
      this.parentTexture = parentTexture;
      this.textureCoordinates = CalcSubCoordinates( textureCoordinates, parentTexture.TextureCoordinates);
		  this.imageDescription = CalcImageDescription();
    }

    /// <summary>
    /// Creates an instance of a SubTexture.
    /// </summary>
    /// <param name="tc">New texture coordinates in pixels.</param>
    /// <param name="parentTexture">The texture to create the subTexture from.</param>
    public SubTexture(Rectangle tc, ITexture2d parentTexture) : 
      this( new RectangleF( (float)tc.Left / parentTexture.Root.ImageDescription.Width, (float)tc.Top / parentTexture.Root.ImageDescription.Height,
      (float)tc.Width / parentTexture.Root.ImageDescription.Width, (float)tc.Height / parentTexture.Root.ImageDescription.Height), parentTexture)
    {
    }

    /// <summary>
    /// Returns true if the texture was already uploaded.
    /// </summary>
    /// <returns></returns>
    bool ITexture.HasOnlineData {
      get {
        throw new InvalidOperationException("HasOnlineData shouldn't be called on SubTextures!");
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private SurfaceDescription CalcImageDescription() {
      return new SurfaceDescription(
        (int)(textureCoordinates.Width * Description.Width + 0.5f),
        (int)(textureCoordinates.Height * Description.Height + 0.5f), parentTexture.Description.Format);
    }

    /// <summary>
    /// create the new sub texture coordinates from the texture coordinates provided relative to the
    /// parent texture and parent coordinates
    /// </summary>
    /// <param name="textureCoordinates">new texture coordinates relativ to parent texture</param>
    /// <param name="parentCoordinates">absolute coordinates of the parent texture</param>
    /// <returns>absolute texture coordinates</returns>
    public static RectangleF CalcSubCoordinates( RectangleF textureCoordinates, RectangleF parentCoordinates) {
      return new RectangleF( 
        parentCoordinates.Left + textureCoordinates.Left * (parentCoordinates.Right - parentCoordinates.Left),
        parentCoordinates.Top + textureCoordinates.Top * (parentCoordinates.Bottom - parentCoordinates.Top),
        textureCoordinates.Width * (parentCoordinates.Right - parentCoordinates.Left),
        textureCoordinates.Height * (parentCoordinates.Bottom - parentCoordinates.Top)
     );
    }

    /// <summary>
    /// Creates a certain number of subTextures by splitting up a texture into regions of same size.
    /// </summary>
    /// <param name="parent">The parent texture to split up.</param>
    /// <param name="totalNum">Total number of subTextures.</param>
    /// <param name="columns">The number of columns.</param>
    /// <returns>The array of subTextures.</returns>
    public static SubTexture[] Create( ITexture2d parent, int totalNum, int columns) {
      SubTexture[] subTextures = new SubTexture[totalNum];
      int rows = (totalNum + columns - 1)/ columns;
      float stepX = 1.0f / columns;
      float stepY = 1.0f / rows;
      for (int y=0; y<rows; y++) {
        for (int x=0; x<columns; x++) {
          int index = x + y*columns;
          if (index >= totalNum)
            return subTextures;
          subTextures[index] = new SubTexture( new System.Drawing.RectangleF( stepX*x, stepY*y, stepX, stepY ), parent);
        }
      }
      return subTextures;
    }

    /// <summary>
    /// Creates a new subTexture.
    /// </summary>
    /// <param name="parent">The parent texture to split up.</param>
    /// <param name="index">The current index of the subTexture to create.</param>
    /// <param name="totalNum">Total number of subTextures.</param>
    /// <param name="columns">The number of columns.</param>
    /// <returns>The subTexture.</returns>
    public static SubTexture Create( ITexture2d parent, int index, int totalNum, int columns) {
      int column = index % columns;
      int row = index / columns;
      int rows = (totalNum + columns - 1)/ columns;
      return new SubTexture(new System.Drawing.RectangleF( 
        (float)column/columns, (float)row/rows, 1.0f/columns, 1.0f/rows), parent);
    }

    /// <summary>
    /// Copies a rectangular area of the bitmap to the texture.
    /// </summary>
    /// <param name="bitmap">The bitmap to copy.</param>
    /// <param name="source">The source rectangle (Rectangle.Empty to copy the whole bitmap).</param>
    /// <param name="target">The target position.</param>
    public void CopyBitmap(Bitmap bitmap, Rectangle source, Point target) {
      Root.CopyBitmap(bitmap, source, 
        new Point((int)(textureCoordinates.Left*Description.Width + target.X),
                  (int)(textureCoordinates.Top*Description.Height + target.Y)));
    }

    /// <summary>
    /// Clears the texture to a certain color.
    /// </summary>
    /// <param name="color">Color to use for clearing texture.</param>
    public void Clear(System.Drawing.Color color) {
      Root.Clear(color);
    }

    /// <summary>
    /// compares two texture
    /// </summary>
    /// <param name="obj">2nd texture</param>
    /// <returns>0 if same physical texture</returns>
    public int CompareTo(object obj) {
      ITexture tex = (ITexture)obj;
      return parentTexture.CompareTo(obj);
    }

    /// <summary>
    /// Uploads the offline data to the gpu.
    /// </summary>
    void ITexture.Upload() {
      throw new InvalidOperationException("Upload shouldn't be called on SubTextures!");
    }

    /// <summary>
    /// Disposes the online data.
    /// </summary>
    void ITexture.DisposeOnlineData() {
      throw new InvalidOperationException("DisposeOnlineData shouldn't be called on SubTextures!");
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
