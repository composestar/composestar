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
using System.Collections;
using System.Runtime.InteropServices;

using Purple.IO;
using Purple.Tools;
using Purple.Math;
using Purple.Graphics.Core;
using Purple.Graphics.VertexStreams;
using Purple.Graphics.Geometry;

namespace Purple.Graphics.Geometry {
  internal struct MD2_Header {
    /// <summary>Magic number must be equal to "IPD2".</summary>
    public int     Ident;
    /// <summary>Md2 version must be equal to 8.</summary>
    public int     Version;
    /// <summary>Width of the texture.</summary>
    public int     SkinWidth;
    /// <summary>Height of the texture.</summary>
    public int     SkinHeight;
    /// <summary>Size of one frame in bytes.</summary>
    public int     FrameSize;
    /// <summary>Number of textures.</summary>
    public int     NumSkins;
    /// <summary>Number of vertices.</summary>
    public int     NumVertices;
    /// <summary>Number of texture coordinates.</summary>
    public int     NumTextureCoordinates;
    /// <summary>Number of triangles.</summary>
    public int     NumTris;
    /// <summary>Number of openGL commands.</summary>
    public int     NumGLCommands;
    /// <summary>Total number of frames.</summary>
    public int     NumFrames;
    /// <summary>Offset to skin names (64 bytes each).</summary>
    public int     OffsetSkins;
    /// <summary>Offset to texture coordinates.</summary>
    public int     OffsetTextureCoordinates;
    /// <summary>Offset to triangles.</summary>
    public int     OffsetTriangles;
    /// <summary>Offset to frame data.</summary>
    public int     OffsetFrames;
    /// <summary>Offset to OpenGL commands.</summary>
    public int     OffsetGLCommands;
    /// <summary>Offset to end of file.</summary>
    public int     OffsetEnd;

    /// <summary>
    /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
    /// </summary>
    /// <param name="x"></param>
    private MD2_Header(float x) {
      Ident = Version = SkinWidth = SkinHeight = FrameSize = 0;
      NumSkins = NumVertices = NumTextureCoordinates = NumTris = NumGLCommands = NumFrames = 0;
      OffsetSkins = OffsetTextureCoordinates = OffsetTriangles = OffsetFrames = OffsetGLCommands = OffsetEnd = 0;
    }
  }

  internal struct MD2_Vertex {
    /// <summary>Compressed vertex (x, y, z) coordinates.</summary>
    public byte X;
    /// <summary>Compressed vertex (x, y, z) coordinates.</summary>
    public byte Y;
    /// <summary>Compressed vertex (x, y, z) coordinates.</summary>
    public byte Z;
    /// <summary>Index to a normal vector for the lighting.</summary>
    public byte LightNormalIndex;

    /// <summary>
    /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
    /// </summary>
    /// <param name="x"></param>
    private MD2_Vertex(float x) {
      X=Y=Z=LightNormalIndex=0;
    }
  }

  internal struct MD2_TextureCoordinate {
    /// <summary>Compressed texture coordinate.</summary>
    public short S;
    /// <summary>Compressed texture coordinate.</summary>
    public short T;

    /// <summary>
    /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
    /// </summary>
    /// <param name="x"></param>
    private MD2_TextureCoordinate(float x) {
      S=T=0;
    }
  }

  internal struct MD2_Frame {
    /// <summary>Scale Values.</summary>
    [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 3 )]
    public float[] Scale;
    /// <summary>Translation Vector.</summary>
    [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 3 )]
    public float[] Translate;
    /// <summary>Frame name.</summary>
    [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 16 )]
    public byte[] Name;

    /// <summary>
    /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
    /// </summary>
    /// <param name="x"></param>
    private MD2_Frame(float x) {
      Scale=Translate = null;
      Name=null;
    }
  }

  internal struct MD2_Skin {
    /// <summary>Pathname of Skin in PK3 file</summary>
    [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 64 )]
    public byte[] Name;

    /// <summary>
    /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
    /// </summary>
    /// <param name="x"></param>
    private MD2_Skin(float x) {
      Name = null;
    }
  }

  internal struct MD2_Triangle {
    /// <summary>Indexes to triangle's vertices.</summary>
    [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 3 )]
    public ushort[] VertexIndex;
    /// <summary>Indexes to vertices' texture coorinates.</summary>
    [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 3 )]
    public ushort[] TriangleIndex;

    
    /// <summary>
    /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
    /// </summary>
    /// <param name="x"></param>
    private MD2_Triangle(float x) {
      VertexIndex = null;
      TriangleIndex = null;
    }
  }

  //=================================================================
  /// <summary>
  /// Loads a Quake2 model.
  /// </summary>
  /// <remarks>
  ///   <para>Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// Credits go to David Henry for his tutorial about the md2 format: 
  /// http://tfc.duke.free.fr/us/tutorials/models/md2.htm
  /// </remarks>
  //=================================================================
  public class MD2Loader : IFileSystemContainer {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------				
    /// <summary>
    /// The contained <see cref="FileSystem"/>.
    /// </summary>
    public IFileSystem FileSystem { 
      get {
        return fileSystem;
      }
      set {
        fileSystem = value;
      }
    }
    IFileSystem fileSystem = RefFileSystem.Instance;

    /// <summary>
    /// Returns the singleton instance of the MD2Loader
    /// </summary>
    public static MD2Loader Instance {
      get {
        return instance;
      }
    }
    static MD2Loader instance = new MD2Loader();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    private MD2Loader() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Loads the model.
    /// </summary>
    /// <param name="fileName">FileName of model to load</param>
    /// <returns>The loaded model.</returns>
    public Model LoadModel(string fileName) {
      using (FileStream stream = new FileStream(fileName, FileMode.Open, FileAccess.Read)) {
        return LoadModel(stream);
      }
    }

    /// <summary>
    /// Loads the model.
    /// </summary>
    /// <param name="stream">Stream to load model from.</param>
    /// <returns>The loaded model.</returns>
    public Model LoadModel(Stream stream) {
      BlendMesh mesh;

      // get header and check if it is ok
      MD2_Header header = (MD2_Header) RawSerializer.Deserialize( stream , typeof(MD2_Header) );			
      if (header.Ident != 844121161 || header.Version != 8)
        return null;

      // Load skins
      MD2_Skin[] skinNames = (MD2_Skin[])RawSerializer.DeserializeArray(stream, typeof(MD2_Skin), header.NumSkins);    

      // Load texture coordinates
      MD2_TextureCoordinate[] textureCoordinates = (MD2_TextureCoordinate[])RawSerializer.DeserializeArray(stream, typeof(MD2_TextureCoordinate), header.NumTextureCoordinates);

      // Load triangless
      MD2_Triangle[] triangles = (MD2_Triangle[])RawSerializer.DeserializeArray(stream, typeof(MD2_Triangle), header.NumTris);
      IndexStream indexStream = new IndexStream16(triangles.Length);
      for (int i=0; i<triangles.Length; i++) {
       // indexStream[i] = triangles[i].VertexIndex[j;
      }

      mesh = new BlendMesh(header.NumFrames);

      // Load frames
      for (int i=0; i<header.NumFrames; i++) {
        MD2_Frame frame = (MD2_Frame)RawSerializer.Deserialize(stream, typeof(MD2_Frame));
        MD2_Vertex[] vertices = (MD2_Vertex[])RawSerializer.DeserializeArray(stream, typeof(MD2_Vertex), header.NumVertices);
        VertexUnit vu = new VertexUnit( VertexFormat.Position, vertices.Length );
        PositionStream ps = (PositionStream)vu[typeof(Purple.Graphics.VertexStreams.PositionStream)];
        mesh.Meshes[i] = new Mesh( new SubSet( vu, indexStream ) );
      }
      return new Model( mesh, null) ;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
