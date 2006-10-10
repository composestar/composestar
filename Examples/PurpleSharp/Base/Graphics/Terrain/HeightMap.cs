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
using System.Drawing;
using System.Drawing.Imaging;

using Purple.IO;
using Purple.Math;
using Purple.Graphics;
using Purple.Graphics.Geometry;
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.Terrain
{
  //=================================================================
  /// <summary>
  /// A simple terrain generator that uses a heightmap.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
	public class HeightMap
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the singleton instance of the current heightmap.
    /// </summary>
    public static HeightMap Instance {
      get {
        return instance;
      }
    }
    static HeightMap instance = new HeightMap();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
		private HeightMap()
		{
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Generates a heightmap from a Bitmap.
    /// </summary>
    /// <param name="bmp">The bitmap to use.</param>
    public Mesh Generate(Bitmap bmp) {
      VertexUnit vertexUnit = new VertexUnit(VertexFormat.PositionTexture, bmp.Width*bmp.Height);
      PositionStream positionStream = (PositionStream)vertexUnit[typeof(PositionStream)];
      TextureStream textureStream = (TextureStream)vertexUnit[typeof(TextureStream)];

      BitmapData bitmapData = bmp.LockBits(new Rectangle(0,0,bmp.Width, bmp.Height), ImageLockMode.ReadOnly, PixelFormat.Format32bppArgb);
      byte[] data = new byte[bitmapData.Stride*bitmapData.Height];
      System.Runtime.InteropServices.Marshal.Copy(bitmapData.Scan0, data, 0, data.Length); 
      int quadsX = bmp.Width - 1;
      int quadsY = bmp.Height - 1;
      Vector3 scale = new Vector3(0.1f, 0.02f, 0.1f);
      //Vector3 scale = new Vector3(5.0f, 0.1f, 5.0f);
      for (int y=0; y<bmp.Height; y++) {
        for (int x=0; x<bmp.Width; x++) {
          int byteOffset = x*4+y*bitmapData.Stride;
          byte b = data[byteOffset];
          byte g = data[byteOffset+1];
          byte r = data[byteOffset+2];
          byte a = data[byteOffset+3];

          Vector3 vec = Vector3.Scale(new Vector3(x-quadsX*0.5f, r, -y+quadsY*0.5f), scale);
          positionStream[x + y*bmp.Width] = vec;

          Vector2 texVec = new Vector2((float)x/quadsX, (float)y/quadsY);
          textureStream[x + y*bmp.Width] = texVec;
        }
      }
      bmp.UnlockBits(bitmapData);

      IndexStream indexStream = IndexStream.Create(quadsX*quadsY*6, (quadsX+1)*(quadsY+1));
      int offset = 0;
      for (int y=0; y<quadsY; y++) {
        for (int x=0; x<quadsX; x++) {
          indexStream[offset] = (x + y*bmp.Width);
          indexStream[offset+1] = (x + 1 + y*bmp.Width);
          indexStream[offset+2] = (x + (y+1)*bmp.Width);
          indexStream[offset+3] = (x + 1 + y*bmp.Width);
          indexStream[offset+4] = (x + 1 + (y+1)*bmp.Width);
          indexStream[offset+5] = (x + (y+1)*bmp.Width);
          offset+=6;
        }
      }

      Mesh mesh = new Mesh();
      mesh.SubSets.Add( new SubSet(vertexUnit, indexStream) );
      return mesh;
    }

    /// <summary>
    /// Generates a heightmap from a bitmap file.
    /// </summary>
    /// <param name="fileName">The name of the file.</param>
    public Mesh Generate(string fileName) {
      using (Stream stream = Engine.Instance.FileSystem.Open(fileName)) {
        Bitmap bmp = new Bitmap(stream);
        return Generate(bmp);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
