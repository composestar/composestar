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
using Purple.Graphics.Core;
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.Geometry.Importer {
	internal enum QuakeLumps {
		/// <summary>Stores player/object positions, etc...</summary>
		Entities = 0,
		/// <summary>Stores texture information</summary>
		Textures,
		/// <summary>Stores the splitting planes</summary>
		Planes,
		/// <summary>Stores the BSP nodes</summary>
		Nodes,
		/// <summary>Stores the leafs of the nodes</summary>
		Leafs,
		/// <summary>Stores the leaf's indices into the faces</summary>
		LeafFaces,
		/// <summary>Stores the leaf's indices into the brushes</summary>
		LeafBrushes,
		/// <summary> Stores the info of world models</summary>
		Models,
		/// <summary>Stores the brushes info (for collision)</summary>
		Brushes,
		/// <summary>Stores the brush surfaces info</summary>
		BrushSides,
		/// <summary>Stores the level vertices</summary>
		Vertices,
		/// <summary>Stores the model vertices offsets</summary>
		MeshVerts,
		/// <summary>Stores the shader files (blending, anims..)</summary>
		Shaders,
		/// <summary>Stores the faces for the level</summary>
		Faces,
		/// <summary>Stores the lightmaps for the level</summary>
		Lightmaps,
		/// <summary>Stores extra world lighting information</summary>
		LightVolumes,
		/// <summary>Stores PVS and cluster info (visibility)</summary>
		VisData,
		/// <summary>A constant to store the number of lumps</summary>
		LumpNumber
	}
	
	internal struct LumpLocation{
		/// <summary>offset to beginning of lump (in bytes)</summary>
		public uint Offset;
		/// <summary>length of lump (in bytes)</summary>
		public uint Length;

		/// <summary>
		/// mail goal is to remove warning CS0649: Field '...' is never assigned to, and will always have its default value 0
		/// </summary>		
		private LumpLocation(uint offset, uint length) {
			Offset = offset;
			Length = length;
		}
	}		

	internal struct QuakeHeader {
		/// <summary>This should always be 'IBSP'</summary>
		public uint ID;
		/// <summary>This should be 0x2e for Quake 3 files</summary>
		public uint Version;

		/// <summary>
		/// mail goal is to remove warning CS0649: Field '...' is never assigned to, and will always have its default value 0
		/// </summary>	
		private QuakeHeader(uint id, uint version) {
			ID = id;
			Version = version;
		}
	}

	internal struct QuakeVertex {
		/// <summary>(x, y, z) position. </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=3 )] 
		public float[] Position;
		/// <summary>(u, v) texture coordinate</summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=2 )] 
		public float[] TextureCoord;
		/// <summary>(u, v) lightmap coordinate</summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=2 )] 
		public float[] LightmapCoord;
		/// <summary>(x, y, z) normal vector</summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=3 )] 
		public float[] Normal;
		/// <summary> RGBA color for the vertex </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=4 )] 
		public byte[] Color;

		/// <summary>
		/// mail goal is to remove warning CS0649: Field '...' is never assigned to, and will always have its default value 0
		/// </summary>	
		internal QuakeVertex(float x) {
			Position = null;
			TextureCoord = null;
			LightmapCoord = null;
			Normal = null;
			Color = null;
		}
	};

	internal struct QuakeFace : IComparable {
		/// <summary>The index into the texture array</summary>
		public int TextureID;
		/// <summary>The index for the effects (or -1 = n/a) </summary>
		public int Effect;
		/// <summary>1=polygon, 2=patch, 3=mesh, 4=billboard </summary>
		public int Type;
		/// <summary>The index into this face's first vertex </summary>
		public int VertexIndex;
		/// <summary>The number of vertices for this face </summary>
		public int NumOfVerts;
		/// <summary>The index into the first meshvertex </summary>
		public int MeshVertIndex; 
		/// <summary>The number of mesh vertices </summary>
		public int NumMeshVerts; 
		/// <summary>The texture index for the lightmap </summary>
		public int LightmapID;
		/// <summary>The face's lightmap corner in the image </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=2 )] 
		public int[] MapCorner;
		/// <summary>The size of the lightmap section </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=2 )] 
		public int[] MapSize;
		/// <summary>The 3D origin of lightmap. </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=3 )] 
		public float[] MapPos;
		/// <summary>The 3D space for s and t unit vectors. </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=3 )] 
		public float[] VectorS;
		/// <summary>The 3D space for s and t unit vectors. </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=3 )] 
		public float[] VectorT;
		/// <summary>The face normal. </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=3 )] 
		public float[] Normal;
		/// <summary>The bezier patch dimensions. </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=2 )] 
		public int[] Size;

		/// <summary>
		/// mail goal is to remove warning CS0649: Field '...' is never assigned to, and will always have its default value 0
		/// </summary>	
		private QuakeFace(float x) {
			TextureID = 0;
		  Effect = 0;
			Type = 0;
			VertexIndex = 0;
			NumOfVerts = 0;
			MeshVertIndex = 0; 
			NumMeshVerts = 0; 
			LightmapID = 0;
			MapCorner = null;
			MapSize = null;
			MapPos = null;
			VectorS = null;
			VectorT = null;
			Normal = null;
			Size = null;
		}

		/// <summary>
		/// compares to faces
		/// </summary>
		/// <param name="obj">to compare with</param>
		/// <returns></returns>
		public int CompareTo(Object obj) {			
			QuakeFace other = (QuakeFace) obj;

			if (other.LightmapID == LightmapID) 
				return TextureID - other.TextureID;
			else 
				return LightmapID - other.LightmapID;
		}
	};

	internal struct QuakeLightMap {
		/// <summary>The RGB data in an 128x128 image </summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst= 128*128*3 )] 
		public byte[] image;

		/// <summary>
		/// mail goal is to remove warning CS0649: Field '...' is never assigned to, and will always have its default value 0
		/// </summary>	
		private QuakeLightMap(float x) {
			image = null;
		}
	};

	internal struct QuakeTexture {
		/// <summary>The name of the texture w/o the extension</summary>
		[ MarshalAs( UnmanagedType.ByValArray, SizeConst=64 )] 
		public byte[] strName;
		/// <summary>The surface flags (unknown)</summary>
		public int flags;
		/// <summary>The content flags (unknown)</summary>
		public int contents;

		/// <summary>
		/// mail goal is to remove warning CS0649: Field '...' is never assigned to, and will always have its default value 0
		/// </summary>	
		private QuakeTexture(float x) {
			strName = null;
			flags = 0;
			contents = 0;
		}
	};

	//=================================================================
	/// <summary>
	/// loads a quake3 Level into a mesh
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Update: 0.5</para>
	/// </remarks>
	//=================================================================
	public class Quake3LevelLoader : IMeshImporter {
		//---------------------------------------------------------------
    #region Variables and Properties
		//---------------------------------------------------------------		
		Device device;
		IList locations;
		Stream stream;

    /// <summary>
    /// The FileSystem used for the Quake3LevelLoader.
    /// </summary>
    public IFileSystem FileSystem {
      get {
        return fs;
      }
      set {
        fs = value;
      }
    }
    IFileSystem fs = RefFileSystem.Instance;

    /// <summary>
    /// get the mesh
    /// </summary>
    public Mesh Mesh { 
      get {
        return mesh;
      }
    }
    Mesh mesh = null;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// standard constructor
		/// </summary>
		public Quake3LevelLoader() {
			this.device = Device.Instance;
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		/// <summary>
		/// Helper function loading a lump from file into structs
		/// </summary>
		/// <param name="lump">Vertices, Faces, ...</param>
		/// <param name="structType">type of struct to load into</param>		
		/// <returns>list of instances of structType</returns>
		private IList LoadLump( QuakeLumps lump, Type structType) {
			LumpLocation location = (LumpLocation) locations[	(int)lump ];
			stream.Seek( location.Offset, SeekOrigin.Begin);
			int vertexNum = (int)(location.Length / Marshal.SizeOf(structType));
			return RawSerializer.DeserializeArray(stream, structType, vertexNum);
		}

    /// <summary>
    /// Imports a mesh.
    /// </summary>
    /// <param name="fileName">Name of file to import.</param>
    public void Import( string fileName ) {
      mesh = Load(fileName);
    }

    /// <summary>
    /// Imports a mesh.
    /// </summary>
    /// <param name="stream">Stream to import.</param>
    /// <returns>The imported mesh.</returns>
    public void Import( Stream stream ) {
      mesh = Load(stream);
    }

    /// <summary>
    /// loads a quake3 level from a stream
    /// </summary>
    /// <param name="fileName">file to load</param>
    /// <returns>level as a mesh</returns>
    public Mesh Load( string fileName) {
      Stream stream = fs.Open(fileName);
      Mesh ret = Load(stream);
      stream.Close();
      return ret;
    }

		/// <summary>
		/// loads a quake3 level from a stream
		/// </summary>
		/// <param name="stream">stream to load from</param>
		/// <returns>level as a mesh</returns>
		public Mesh Load( Stream stream ) {

			Mesh mesh = new Mesh();
			this.stream = stream;
							
			// get header and check if it is ok
			QuakeHeader header = (QuakeHeader) RawSerializer.Deserialize(stream , typeof(QuakeHeader) );			
			if (header.ID != 1347633737 || header.Version != 0x2e)
				return null;

			// get locations of lumps
			locations = RawSerializer.DeserializeArray(stream, typeof(LumpLocation), (int) QuakeLumps.LumpNumber);						
			
			// get lumps					
			IList quakeVertices =		LoadLump( QuakeLumps.Vertices, typeof(QuakeVertex));			
			IList quakeFaces =			LoadLump( QuakeLumps.Faces, typeof(QuakeFace));			
			IList quakeTextures =		LoadLump( QuakeLumps.Textures, typeof(QuakeTexture));			
			IList quakeLightMaps =	LoadLump( QuakeLumps.Lightmaps, typeof(QuakeLightMap));			
			      			

			// Load all texture images and put into array
			IList textures = LoadTextures(quakeTextures);
      // Load lightMaps, create texture and put into array
      IList lightMaps = LoadLightMaps(quakeLightMaps);
			
			// create list from vertices
			VertexUnit vertexUnit = new VertexUnit( VertexFormat.PositionTexture2, quakeVertices.Count );
			PositionStream pos = (PositionStream)vertexUnit[typeof(PositionStream)];
			TextureStream texStream = (TextureStream)vertexUnit[typeof(TextureStream)];
			TextureStream light = (TextureStream)vertexUnit[typeof(TextureStream),1];						
		
			int i=0;
			foreach( QuakeVertex v in quakeVertices) {				
				pos[i]			  = new Math.Vector3(v.Position[0], v.Position[2], -v.Position[1]);
				texStream[i]  = new Math.Vector2( v.TextureCoord[0], v.TextureCoord[1] );
				light[i]			= new Math.Vector2( v.LightmapCoord[0], v.LightmapCoord[1] );
				i++;
			}			

			// presort faces
			Array.Sort( ((Array)quakeFaces) );
			
			// create mesh
			int oldLightMap = ((QuakeFace)quakeFaces[0]).LightmapID;
			int oldTexture = ((QuakeFace)quakeFaces[0]).TextureID;
      ArrayList indices = new ArrayList();
			for (i=0; i<quakeFaces.Count; ++i) {
        QuakeFace qf = (QuakeFace)quakeFaces[i];
        if (qf.Type == 1) {
          if (qf.TextureID != oldTexture || qf.LightmapID != oldLightMap) {
            mesh.SubSets.Add( new SubSet(vertexUnit, IndexStream.Create( indices, vertexUnit.Size) ) );
            Textures texs = new Textures("color", (ITexture) textures[ oldTexture ]);
            if (oldLightMap == -1)
              texs["lightMap"] = null;
            else
              texs["lightMap"] = (ITexture) lightMaps[ oldLightMap];
            mesh.Textures.Add( texs );
            indices.Clear();
          }
          
          // add indices => convert from fan to list						
          for (int j=2; j<qf.NumOfVerts; j++) {	
            indices.Add(qf.VertexIndex);
            indices.Add(qf.VertexIndex + j - 1);
            indices.Add(qf.VertexIndex + j);
          }			

          oldTexture = qf.TextureID;
          oldLightMap = qf.LightmapID;
        }
			}			
			return mesh;
		}

    private IList LoadLightMaps(IList quakeLightMaps) {
      IList lightMaps = new ArrayList();
      foreach( QuakeLightMap lightMap in quakeLightMaps) {
        Stream lightMapStream = new MemoryStream( lightMap.image );
        ITexture2d lm = LoadRaw( lightMapStream, 128, 128, 1, Format.R8G8B8 );
        lightMaps.Add( lm );				
        lightMapStream.Close();				
      }			
      return lightMaps;
    }

    private IList LoadTextures(IList quakeTextures) {
      ArrayList textures = new ArrayList();
      TextureManager textureManager = TextureManager.Instance;
      IFileSystem fs = textureManager.FileSystem;

      string path = "";
      foreach( QuakeTexture tex in quakeTextures) {			
        path = StringHelper.Convert(tex.strName);
        string fileName = Purple.IO.Path.GetFileName(path);
        //fileName = fileName.Replace("textures/", "");
        string directory = Purple.IO.Path.GetFolder(path);
        if (fs.ExistsDirectory(directory)) {
          string[] matchingFiles = fs.GetFiles(directory, fileName+".*");
          if (matchingFiles.Length != 0) {
            string matchingFile = matchingFiles[0];
            Stream imageStream = fs.Open(matchingFile);
            ITexture2d lt = textureManager.Load( imageStream );
            textures.Add( lt );
            imageStream.Close();
          }
          else {
            Log.Spam("Couldn't load texture: " + path);
            textures.Add( null );
          }
        } else {
          textures.Add( null );
          Log.Spam("Couldn't load texture: " + path + " because directory doesn't exist: " + directory);
        }
      }
      return textures;
    }

    /// <summary>
    /// loads a texture from a stream containing raw bitmap data
    /// </summary>
    /// <param name="stream">stream to load from</param>
    /// <param name="width">width of texture</param>
    /// <param name="height">height of texture</param>
    /// <param name="mipLevels">number of MipMap level</param>
    /// <param name="format">format of texture</param>
    /// <returns>texture object</returns>
    private ITexture2d LoadRaw(Stream stream, int width, int height, int mipLevels, Purple.Graphics.Format format) {
      int countBytes = width*height*Purple.Graphics.GraphicsEngine.BitsPerPixel( format ) /8;

      byte[] data = new Byte[ countBytes ];
      stream.Read( data, 0, countBytes );

      System.Drawing.Bitmap bitmap;
      // pin array
      System.Runtime.InteropServices.GCHandle handle = System.Runtime.InteropServices.GCHandle.Alloc(data);
      System.IntPtr ptr = System.Runtime.InteropServices.Marshal.UnsafeAddrOfPinnedArrayElement(data, 0);
      bitmap = new System.Drawing.Bitmap(width, height, width*3, System.Drawing.Imaging.PixelFormat.Format24bppRgb, ptr);	
      
      ITexture2d tex = TextureManager.Instance.Create(width, height, 1, Format.A8R8G8B8, TextureUsage.Normal);
      tex.CopyBitmap(bitmap, new System.Drawing.Rectangle( 0,0, width, height), System.Drawing.Point.Empty);
      bitmap.Dispose();
      handle.Free();
      return tex;
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
