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

namespace Purple.Graphics.Geometry.Importer {
	//=================================================================
	/// <summary>
	/// Importer for Quake3 models.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para> 
	///   <para>Last Update: 0.7</para> 
	/// </remarks>
	//=================================================================
	public class MD3Loader : IFileSystemContainer {
    //---------------------------------------------------------------
    #region Internal structures
    //---------------------------------------------------------------				
    internal struct MD3_Header {		
      /// <summary>"IDP3"</summary>		
      public uint	 Id;
      /// <summary>15 for Q3</summary>
      public int  Version;
      /// <summary>MD3 name, usually its pathname in the PK3. null terminated C string. </summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 64 )]						
      public byte[] FileName;
      /// <summary> ??? </summary>
      public int Flags;
      /// <summary>number of bone frames</summary>
      public int NumFrames;
      /// <summary>number of tags</summary>
      public int  NumTags;
      /// <summary>number of meshes</summary>
      public int  NumMeshes;
      /// <summary>unused = 0</summary>
      public int  MaxSkins;
      /// <summary>length of header</summary>
      public int  HeaderSize;
      /// <summary>offset for tag block</summary>
      public int  TagStart;
      /// <summary>end offset for tag block</summary>
      public int  TagEnd;
      /// <summary>size of file</summary>
      public int  FileSize;
				
      /// <summary>
      /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
      /// </summary>
      /// <param name="x"></param>
      private MD3_Header(float x) {
        Id = 0;
        Version = Flags = NumFrames = NumTags = NumMeshes = MaxSkins = HeaderSize = 0;
        TagStart = TagEnd = FileSize = 0;
        FileName = null; 
      }
    }

    internal struct MD3_Frame {
      /// <summary>first corner of bounding box</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 3 )]
      public float[] MinBounds;
      /// <summary>second corner of bounding box</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 3 )]
      public float[] MaxBounds;
      /// <summary>local origin - usually (0,0,0)</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 3 )]
      public float[] Position;
      /// <summary>radius of bounding sphere</summary>
      public float   Radius;
      /// <summary>name of frame - null terminated C string</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 16 )]
      public byte[]	 Name;

      /// <summary>
      /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
      /// </summary>
      /// <param name="x"></param>
      private MD3_Frame(float x) {
        MinBounds = MaxBounds = Position = null;
        Radius = 0;
        Name = null;			
      }
    }

    internal struct MD3_Tag {
      /// <summary>Name of Tag</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 64)]
      public byte[] Name;
      /// <summary>Coordinates of Tag</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 3 )]
      public float[] Origin;
      /// <summary>Orientation of Tag</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 9 )]
      public float[] Rotation;
		

      /// <summary>
      /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
      /// </summary>
      /// <param name="x"></param>
      private MD3_Tag(float x) {
        Origin = null;
        Rotation = null;			
        Name = null;			
      }
    }

    internal struct MD3_MeshHeader {
      /// <summary>"IDP3"</summary>
      public uint		Id;
      /// <summary>Name of mesh object</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 64 )]
      public byte[] Name;
      /// <summary>???</summary>
      public uint   Flags;
      /// <summary>number of animation frames</summary>
      public int    NumFrames;
      /// <summary>number of Skin objects</summary>
      public int		NumSkins;
      /// <summary>number of vertices</summary>
      public int		NumVertices;
      /// <summary>number of triangles</summary>
      public int		NumTriangles;
      /// <summary>relative offset from surface start to the list of triangle objects</summary>
      public int    TriangleOffset;
      /// <summary>relative offset from surface start to the list of Skin objects</summary>
      public int		SkinOffset;
      /// <summary>relative offset from surface start to the list of texture coordinates</summary>
      public int		TexCoordOffset;
      /// <summary>relative offset from surface start to the list of vertex objects</summary>
      public int		VertexOffset;
      /// <summary>size of mesh</summary>
      public int		MeshSize;

      /// <summary>
      /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
      /// </summary>
      /// <param name="x"></param>
      private MD3_MeshHeader(float x) {
        Id = Flags = 0;
        Name = null;
        NumFrames = NumSkins = NumVertices = NumTriangles = TriangleOffset = SkinOffset = 0;
        TexCoordOffset = VertexOffset = MeshSize = 0;
      }
    }
	
    internal struct MD3_Skin {
      /// <summary>pathname of Skin in PK3 file</summary>
      [ MarshalAs( UnmanagedType.ByValArray, SizeConst = 64 )]
      public byte[] Name;
      /// <summary>Skin index number</summary>
      public int		SkinIndex;

      /// <summary>
      /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
      /// </summary>
      /// <param name="x"></param>
      private MD3_Skin(float x) {
        Name = null;
        SkinIndex = 0;
      }
    }

    internal struct MD3_Triangle {
      /// <summary>index of first vertex</summary>
      public int A;
      /// <summary>index of second vertex</summary>
      public int B;
      /// <summary>index of third vertex</summary>
      public int C;

      /// <summary>
      /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
      /// </summary>
      /// <param name="x"></param>
      private MD3_Triangle(float x) {
        A = B = C = 0;
      }
    }


    internal struct MD3_TexCoord {
      /// <summary>first texture coordinate</summary>
      public float U;
      /// <summary>second texture coordinate</summary>
      public float V;

      /// <summary>
      /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
      /// </summary>
      /// <param name="x"></param>
      private MD3_TexCoord(float x) {
        U = V = 0;
      }
    }

    internal struct MD3_Vertex {
      /// <summary>x coordinate => scale by 1/64 to get correct value </summary>
      public short X;
      /// <summary>y coordinate => scale by 1/64 to get correct value </summary>
      public short Y;
      /// <summary>z coordinate => scale by 1/64 to get correct value </summary>
      public short Z;
      /// <summary>Encoded normal vector: http://www.icculus.org/homepages/phaethon/q3/formats/md3format.html#MD3</summary>
      public short Normal;

      /// <summary>
      /// get rid of warning CS0649: Field '...' is never assigned to, and will always have its default value 0
      /// </summary>
      /// <param name="x"></param>
      private MD3_Vertex(float x) {
        X = Y = Z = Normal = 0;
      }
    }	

    internal class MD3Part {
      public BlendMesh Mesh;
      public SortedList Links;

      public MD3Part(BlendMesh mesh, SortedList links) {
        this.Mesh = mesh;
        this.Links = links;
      }
    }

    //=================================================================
    /// <summary>
    /// Links for connecting several meshes
    /// </summary>
    /// <remarks>
    ///   <para>Author: Markus Wöß</para>
    ///   <para>Since: 0.1</para>  
    /// </remarks>
    //=================================================================
    internal struct MD3Animation {
      /// <summary>name of animation</summary>
      public string Name;
      /// <summary>number of first frame</summary>
      public int StartFrame;
      /// <summary>number of total frames</summary>
      public int Count;
      /// <summary>???</summary>
      public int LoopingFrames;
      /// <summary>Frames Per Second</summary>
      public int FramesPerSecond;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------				

		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------				
    string modelPath;
    string path;
    SortedList textures;
    SortedList tags;

    /// <summary>
    /// The filesystem to use.
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
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// Standard constructor.
		/// </summary>
		public MD3Loader() {
		}		
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
    /// <summary>
    /// Loads a quake md3 model.
    /// </summary>
    /// <param name="skinName">Name of skin.</param>
    /// <param name="path">FilePath - without extension and _lower, _upper ...</param>
    /// <returns></returns>
    public Model LoadModel(string path, string skinName) {
      textures = new SortedList();
      tags = new SortedList();

      modelPath = Purple.IO.Path.GetFolder(path);
      if (!path.EndsWith("/") && !path.EndsWith("\\"))
        path += '_';
      this.path = path;
      if (skinName.Length != 0)
        skinName = "_" + skinName;

      MD3Part lower = LoadPart("lower", skinName);      
      MD3Part upper = LoadPart("upper", skinName);
      MD3Part head = LoadPart("head", skinName);

      using (Stream animations = fileSystem.Open(path + "animation.cfg")) {
        AnimationPlayer[] players = LoadAnimations(animations);
        lower.Mesh.Player = players[0];
        upper.Mesh.Player = players[1];
      }

      // create the skeleton
      Joint jointTorso = new Joint( "tag_torso", 0, null );
      Joint jointHead = new Joint( "tag_head", 1, jointTorso );
      Joint jointWeapon = new Joint( "tag_weapon", 2, jointTorso );

      Skeleton skeleton = new Skeleton( 
        new Matrix4[] {Matrix4.Identity, Matrix4.Identity, Matrix4.Identity}, 
        new Joint[] { jointTorso, jointHead, jointWeapon }
        );

      ArrayList torsoLinks = lower.Links["tag_torso"] as ArrayList;
      ArrayList headLinks = upper.Links["tag_head"] as ArrayList;
      ArrayList weaponLinks = upper.Links["tag_weapon"] as ArrayList; 

      SkeletonFrame[] framesLower = new SkeletonFrame[torsoLinks.Count];
      SkeletonFrame[] framesUpper = new SkeletonFrame[headLinks.Count];

      for (int i=0; i<framesLower.Length; i++)
        framesLower[i] = new SkeletonFrame( new Matrix4[] {(Matrix4)torsoLinks[i]});        

      for (int i=0; i<framesUpper.Length; i++)
        framesUpper[i] = new SkeletonFrame( new Matrix4[] {(Matrix4)headLinks[i], (Matrix4)weaponLinks[i]});        

      Channel[] channels = new Channel[] { 
         new Channel(framesLower, new int[] {0}, 15, lower.Mesh.Player), 
         new Channel(framesUpper, new int[] {1, 2}, 15, upper.Mesh.Player) 
      };
      skeleton.Channels = channels;

      // create the Model      
      Model lowerModel = new Model( lower.Mesh, skeleton );
      Model upperModel = new Model( upper.Mesh, null );
      Model headModel = new Model( head.Mesh, null);
      Model model = new Model( lower.Mesh, skeleton);
      model.AttachModel( new Model( upper.Mesh, null), 0);
      model.AttachModel( new Model( head.Mesh, null), 1);
      return model;
    }

    private MD3Part LoadPart(string partName, string skinName) {
      LoadSkin( partName, skinName );
      return LoadMD3( partName );
    }
		

		private AnimationPlayer[] LoadAnimations(string path) {
      using (Stream stream = fileSystem.Open(path)) {
        return LoadAnimations( stream );
      }
		}

		private MD3Animation ExtractAnimation(String line) {			
			String[] values = line.Split(' ', '\t', '/');
			String[] strings = new String[5];
			int index = 0;

			foreach(String s in values) {
				if (s.Length!=0 && index < 5)
					strings[ index++ ] = s;          
			}

			MD3Animation ani = new MD3Animation();
			ani.StartFrame =			int.Parse(strings[0]);
			ani.Count =						int.Parse(strings[1]);
			ani.LoopingFrames   =	int.Parse(strings[2]);
			ani.FramesPerSecond =	int.Parse(strings[3]);
			ani.Name =						strings[4];
			return ani;
		}

		/// <summary>
		/// Loads the the info for animations from a file.
		/// </summary>
		/// <param name="stream">Stream to load animations from.</param>
		/// <returns>Returns two AnimationPlayer objects.</returns>
		private AnimationPlayer[] LoadAnimations(Stream stream) {
			StreamReader reader = new StreamReader(stream);
			
			IList lowerAnimations = new ArrayList();
			IList upperAnimations = new ArrayList();

			int legOffset=0;
			int torsoOffset = 0;

      // Fill animations
			while (reader.Peek() != -1) {
				string line = reader.ReadLine();
				if (line.Length != 0 && char.IsDigit(line[0])) {
					MD3Animation ani = ExtractAnimation(line);					 					
					if (ani.Name.StartsWith("BOTH")) {
						lowerAnimations.Add( ani );
						upperAnimations.Add( ani );
					} else if (ani.Name.StartsWith("TORSO")) {
						if (torsoOffset == 0) 
							torsoOffset = ani.StartFrame;
						upperAnimations.Add( ani );
					}	else if (ani.Name.StartsWith("LEGS")) {
						if (legOffset == 0) 
							legOffset = ani.StartFrame;						
						ani.StartFrame = ani.StartFrame - legOffset + torsoOffset;		
						lowerAnimations.Add( ani );
					}
					else
						System.Diagnostics.Debug.Write("MD3 Config file - invalid name: " + ani.Name);
				}				
			}			
			reader.Close();

      // create AnimationPlayers
      AnimationClip[] lowerClips = new AnimationClip[ lowerAnimations.Count ];
      AnimationClip[] upperClips = new AnimationClip[ upperAnimations.Count ];

      for (int i=0; i< lowerClips.Length; i++) {
        MD3Animation ani = (MD3Animation)lowerAnimations[i];
        lowerClips[i] = new AnimationClip( ani.Name, ani.StartFrame, ani.StartFrame + ani.Count-1, 
          (float)ani.Count/ani.FramesPerSecond, ani.LoopingFrames != 0);
      }
      
      for (int i=0; i< upperClips.Length; i++) {
        MD3Animation ani = (MD3Animation)upperAnimations[i];
        upperClips[i] = new AnimationClip( ani.Name, ani.StartFrame, ani.StartFrame + ani.Count-1, 
          (float)ani.Count/ani.FramesPerSecond, ani.LoopingFrames != 0);
      }
      return new AnimationPlayer[] {new AnimationPlayer( lowerClips ), new AnimationPlayer( upperClips )};
		}

    private SortedList GetLinks(MD3_Tag[] tags) {
      SortedList list = new SortedList();
      for (int i=0; i<tags.Length; i++) {
        MD3_Tag tag = (MD3_Tag)tags[i];
        string name = StringHelper.Convert(tag.Name);
			
        Matrix4 m = Matrix4.Identity;						
        m.D1 = tag.Origin[0];
        m.D2 = tag.Origin[2];
        m.D3 = -tag.Origin[1];		
        m.A1 = tag.Rotation[0];
        m.B1 = tag.Rotation[6];
        m.C1 = -tag.Rotation[3];						
        m.A2 = tag.Rotation[2];												
        m.B2 = tag.Rotation[8];												
        m.C2 = -tag.Rotation[5];
        m.A3 = -tag.Rotation[1];
        m.B3 = -tag.Rotation[7];
        m.C3 = tag.Rotation[4];					
				
				if (!list.Contains(name))
			    list[name] = new ArrayList();
        (list[name] as ArrayList).Add( m );
      }
      return list;
    }
				
		private bool LoadSkin( string part, string skinName ) {
      using (Stream stream = fileSystem.Open( path + part + skinName + ".skin")) {
        StreamReader sr = new StreamReader( stream );			

        while (sr.Peek() != -1) {
          string line = sr.ReadLine();

          // test if line contains a path
          if (line.IndexOf("/") != -1) {
            int indexComma = line.IndexOf(",");
            string name = line.Substring(0, indexComma);
            string fileName = Purple.IO.Path.GetFileName(line.Substring(indexComma +1));
            ITexture2d tex = TextureManager.Instance.Load(modelPath + "/" + fileName);	
            textures.Add(name, tex);
          }
        }
        sr.Close();
        return true;
      }
		}

		private MD3Part LoadMD3( string part ) {
      using (Stream stream = fileSystem.Open(path + part + ".md3")) {

        // get header and check if it is ok
        MD3_Header header = (MD3_Header) RawSerializer.Deserialize( stream , typeof(MD3_Header) );			
        if (header.Id != 860898377 || header.Version != 15)
          return null;

        // load bone frames			
        MD3_Frame[] frames = (MD3_Frame[])RawSerializer.DeserializeArray( stream, typeof(MD3_Frame), header.NumFrames );

        // load tags
        SortedList links = GetLinks( (MD3_Tag[])RawSerializer.DeserializeArray( stream, typeof(MD3_Tag), header.NumTags*header.NumFrames ) );		

        long meshOffset = stream.Position;

        // one mesh for every frame
        BlendMesh mesh = new BlendMesh(header.NumFrames);

        // load meshes
        for (int iMesh=0; iMesh<header.NumMeshes; iMesh++) {				

          stream.Position = meshOffset;
          MD3_MeshHeader meshHeader = (MD3_MeshHeader) RawSerializer.Deserialize( stream, typeof(MD3_MeshHeader) );
        		
          MD3_Skin[] skins = (MD3_Skin[])RawSerializer.DeserializeArray( stream, typeof(MD3_Skin), meshHeader.NumSkins);
				
          stream.Position = meshOffset + meshHeader.TriangleOffset;
          MD3_Triangle[] triangles = (MD3_Triangle[])RawSerializer.DeserializeArray( stream, typeof(MD3_Triangle), meshHeader.NumTriangles );
	
          stream.Position = meshOffset + meshHeader.TexCoordOffset;
          MD3_TexCoord[] texCoords	= (MD3_TexCoord[])RawSerializer.DeserializeArray( stream, typeof(MD3_TexCoord), meshHeader.NumVertices);

          stream.Position = meshOffset + meshHeader.VertexOffset;
          MD3_Vertex[] vertices = (MD3_Vertex[])RawSerializer.DeserializeArray( stream, typeof(MD3_Vertex), meshHeader.NumFrames * meshHeader.NumVertices);

          float scale = 64.0f;						
          string name = StringHelper.Convert( meshHeader.Name );	
          ITexture tx = (ITexture)textures[name];

          Triangle[] tris = new Triangle[triangles.Length];
          for (int i = 0; i<triangles.Length; i++) {
            tris[i].A = (triangles[i]).A;
            tris[i].B = (triangles[i]).B;
            tris[i].C = (triangles[i]).C;
          }
          IndexStream indexStream = IndexStream16.FromTriangles(tris);				
				
          int vertCount = meshHeader.NumVertices; // *meshHeader.NumFrames;								
				
          for (int iFrame = 0; iFrame<meshHeader.NumFrames; iFrame++) {
            VertexUnit vertexUnit = new VertexUnit(VertexFormat.PositionNormalTexture, vertCount);
            PositionStream pos    = (PositionStream)vertexUnit[typeof(PositionStream)];
            NormalStream	 normal = (NormalStream)vertexUnit[typeof(NormalStream)];
            TextureStream  tex    = (TextureStream)vertexUnit[typeof(TextureStream)];

            for (int i = 0; i<vertCount; i++) {
              int vertIndex = iFrame*meshHeader.NumVertices + i;
              pos[i] = new Vector3( vertices[vertIndex].X / scale,
                vertices[vertIndex].Z / scale,
                -vertices[vertIndex].Y / scale );

              int texIndex = i % meshHeader.NumVertices;
              tex[i] = new Vector2( texCoords[texIndex].U,
                texCoords[texIndex].V );

              //Normal vector
              int compressedNormal = ((MD3_Vertex)vertices[vertIndex]).Normal;
              float lng = (compressedNormal & 0xFF) * Math.Basic.PI / 128;
              float lat = ((compressedNormal >> 8) & 0xFF) * Math.Basic.PI / 128;						

              normal[i] = new Vector3( Math.Trigonometry.Cos ( lat ) * Math.Trigonometry.Sin ( lng ),
                Math.Trigonometry.Cos(lng), 
                -Math.Trigonometry.Sin ( lat ) * Math.Trigonometry.Sin ( lng ) );
            }
            if (mesh.Meshes[iFrame] == null)
              mesh.Meshes[iFrame] = new Mesh();
            mesh.Meshes[iFrame].SubSets.Add( new SubSet(vertexUnit, indexStream) );
            mesh.Meshes[iFrame].Textures.Add( new Textures("color", tx) );
          }				

          // Increase the offset into the file
          meshOffset += meshHeader.MeshSize;
        }

        return new MD3Part( mesh, links );
      }
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
