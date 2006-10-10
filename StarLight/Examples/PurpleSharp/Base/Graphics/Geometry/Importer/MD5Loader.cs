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
using Purple.Graphics.Core;
using Purple.Graphics.Geometry;
using Purple.Graphics.VertexStreams;
using Purple.Math;
using Purple.Collections;

namespace Purple.Graphics.Geometry.Importer
{
  //=================================================================
  /// <summary>
  /// loads a doom3 model (alpha)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
	public class MD5Loader
	{   
    //---------------------------------------------------------------
    #region Internal structures
    //---------------------------------------------------------------
    /// <summary>
    /// MD5 vertex structure
    /// </summary>
    struct MD5Vertex {
      /// <summary>texture coordinates</summary>
      public Vector2 UV;
      /// <summary>weight index</summary>
      public int WeightIndex;
      /// <summary>weight count</summary>
      public int WeightCount;

      /// <summary>
      /// constructor
      /// </summary>
      /// <param name="uv">texture coordinates</param>
      /// <param name="weightIndex">weight index</param>
      /// <param name="weightCount">weight count</param>
      public MD5Vertex(Vector2 uv, int weightIndex, int weightCount) {
        UV = uv;
        WeightIndex = weightIndex;
        WeightCount = weightCount;
      }
    }

    /// <summary>
    /// MD5 Weight structure
    /// </summary>
    struct MD5Weight {
      /// <summary>weight per axis</summary>
      public Vector3 Vector;
      /// <summary>bias factor</summary>
      public float BiasFactor;
      /// <summary>index of bone for this weight</summary>
      public int BoneIndex;

      /// <summary>
      /// constructor
      /// </summary>
      /// <param name="vec">weight per axis</param>
      /// <param name="bias">bias factor</param>
      /// <param name="boneIndex">index of bone for this weight</param>
      public MD5Weight( Vector3 vec, float bias, int boneIndex) {
        Vector = vec;
        BiasFactor = bias;
        BoneIndex = boneIndex;
      }
    }

    /// <summary>
    /// MD5 bone structure
    /// </summary>
    struct MD5Bone {
      /// <summary>name of bone</summary>
      public string Name;
      /// <summary>parent bone</summary>
      public string Parent;
      /// <summary>matrix</summary>
      public Matrix4 Matrix;

      /// <summary>
      /// constructor
      /// </summary>
      /// <param name="name">name of bone</param>
      /// <param name="matrix">matrix</param>
      /// <param name="parent">parent bone</param>
      public MD5Bone( string name, Matrix4 matrix, string parent) {
        Name = name;
        Matrix = matrix;
        Parent = parent;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    string[] tokens;
    int currentToken;
    MD5Bone[] bones;
    MD5Weight[] weights;
    MD5Vertex[] vertices;
    IndexStream indexStream;
    Mesh mesh;
    System.Globalization.CultureInfo culture = new System.Globalization.CultureInfo("en-US");
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor
    /// </summary>
		public MD5Loader()
		{
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// loads a model from a file
    /// </summary>
    /// <param name="fileName">of model file</param>
    /// <returns>model</returns>
    public Model LoadModel(string fileName) {
      using (FileStream stream = new FileStream(fileName, FileMode.Open)) {
        return LoadModel(stream);;
      }
    }

    /// <summary>
    /// loads a model from a stream
    /// </summary>
    /// <param name="stream">stream to load</param>
    /// <returns>md5 model</returns>
    public Model LoadModel(Stream stream) {
      StreamReader reader = new StreamReader(stream);
      string text = reader.ReadToEnd();
      tokens = text.Split(' ', '"', '\n', '\r', '\t');
      CleanTokens();      
      currentToken = 0;

      // read header
      Advance("MD5Version");
      if (NextToken != "6")
        throw new GraphicsException("MD5 Header not correct!");

      // read bones
      if (!ReadBones())
        return null;

      // read meshes
      if (!ReadMeshes())
        return null;

      return new Model(mesh, null);
    }

    bool ReadMeshes() {
      Advance("nummeshes");
      int meshNum = int.Parse(NextToken);
      mesh = new Mesh();

      for (int i=0; i<1 /*meshNum*/; i++) {
        Advance("mesh");

        // sanity check
        int num = int.Parse(NextToken);
        System.Diagnostics.Debug.Assert( num == i, "Invalid mesh num!");

        // read mesh data - shader, verts
        Advance("shader");
        string shaderPath = NextToken;
        FileInfo info = new FileInfo(shaderPath);
        string localPath = shaderPath.Insert( shaderPath.Length - info.Extension.Length,"_local");
        string diffusePath = shaderPath.Insert( shaderPath.Length - info.Extension.Length,"_d");
        Textures textures = new Textures();
        ITexture texture = null;
        try {
          texture = TextureManager.Instance.Load(shaderPath);
        } catch {
          texture = TextureManager.Instance.Load(diffusePath);
        }
        ITexture normalMap = TextureManager.Instance.Load(localPath);
        textures["color"] = texture;
        textures["normal"] = normalMap;

        if (!ReadVertices())
          return false;

        if (!ReadTriangles())
          return false;

        if (!ReadWeights())
          return false;

        // let's test it
        VertexUnit vertexUnit = new VertexUnit( VertexFormat.PositionTexture2Tangent, vertices.Length);
        PositionStream positionStream = (PositionStream) vertexUnit[0];
        TextureStream textureStream = (TextureStream) vertexUnit[1];
        TextureStream normalStream = (TextureStream) vertexUnit[2];
        for(int j=0; j<vertices.Length; j++) {
          Vector3 pos = Vector3.Zero;
          MD5Vertex vertex = vertices[j];
          for (int k=0; k<vertex.WeightCount; k++) {
            MD5Weight weight = weights[vertex.WeightIndex + k];
            MD5Bone bone = bones[weight.BoneIndex];
            
            pos += weight.Vector * bone.Matrix * weight.BiasFactor;      
          }
          positionStream[j] = pos;
          textureStream[j] = vertex.UV;
          normalStream[j] = vertex.UV;
        }

        // add tangent space stuff
        IGraphicsStream[] streams = Util.CalcTangentSpaceStreams(positionStream, textureStream, indexStream);
        
        Array.Copy( streams[0].Data, vertexUnit[3].Data, vertices.Length);
        Array.Copy( streams[1].Data, vertexUnit[4].Data, vertices.Length);
        Array.Copy( streams[2].Data, vertexUnit[5].Data, vertices.Length);
        //mesh.SubSets.Add( new SubSet(vertexUnit, indexStream, material));
      }

      return true;
    }

    bool ReadWeights() {
      // weights
      Advance("numweights");
      int weightNum = int.Parse(NextToken);
      weights = new MD5Weight[weightNum];
      for (int j=0; j<weightNum; j++) {
        Advance("weight");
        //sanity check
        int currentWeightNum = int.Parse(NextToken);
        System.Diagnostics.Debug.Assert( j== currentWeightNum, "Invalid weight num!");

        MD5Weight weight = new MD5Weight();     
        weight.BoneIndex = int.Parse(NextToken);
        weight.BiasFactor = NextFloat;
        weight.Vector = new Vector3(NextFloat, NextFloat, NextFloat);
        weights[j] = weight;
      }
      return true;
    }

    bool ReadTriangles() {
      // triangles
      Advance("numtris");
      int triNum = int.Parse(NextToken);
      indexStream = new IndexStream16(triNum*3);
      for (int j=0; j<triNum; j++) {
        Advance("tri");
        // sanity check
        int currentTriNum = int.Parse(NextToken);
        System.Diagnostics.Debug.Assert( j == currentTriNum, "Invalid tri num!");
        indexStream[j*3] = int.Parse(NextToken);
        indexStream[j*3 + 1] = int.Parse(NextToken);
        indexStream[j*3 + 2] = int.Parse(NextToken);
      }
      return true;
    }

    bool ReadVertices() {
      // vertices 
      Advance("numverts");
      int vertNum = int.Parse(NextToken);
      vertices = new MD5Vertex[vertNum];
      for (int j=0; j<vertNum; j++) {
        Advance("vert");
        // sanity check
        int currentVertNum = int.Parse(NextToken);
        System.Diagnostics.Debug.Assert( j == currentVertNum, "Invalid vert num!");

        MD5Vertex vertex;
        vertex.UV = new Vector2( NextFloat, NextFloat);
        vertex.WeightIndex = int.Parse(NextToken);
        vertex.WeightCount = int.Parse(NextToken);

        vertices[j] = vertex;
      }
      return true;
    }
    
    bool ReadBones() {
      Advance("numbones");
      int boneNum = int.Parse(NextToken);
      bones = new MD5Bone[boneNum];
      
      for (int i=0; i<boneNum; i++) {
        Advance("bone");

        // sanity check
        int num = int.Parse(NextToken);
        System.Diagnostics.Debug.Assert( num == i, "Invalid bone num!");

        // read bone data - name, bindpos, bindmat and [parent]
        Advance("name");
        string name = NextToken;
        Advance("bindpos");
        Math.Vector3 position = new Math.Vector3(
          NextFloat,NextFloat, NextFloat);
        Advance("bindmat");
        Math.Matrix4 md5matrix = new Math.Matrix4(
          NextFloat, NextFloat, NextFloat, 0,
          NextFloat, NextFloat, NextFloat, 0,
          NextFloat, NextFloat, NextFloat, 0,
        position.X, position.Y, position.Z, 0);
        Math.Matrix4 matrix = Math.Matrix4.Zero;
        matrix.Column1 = md5matrix.Column1;
        matrix.Column2 = md5matrix.Column3;
        matrix.Column3 = md5matrix.Column2;
        matrix.Column4 = md5matrix.Column4;
        Advance("parent", "bone");
        string parentName = null;
        if (CurrentToken == "parent")
          parentName = NextToken;    
   
        bones[i] = new MD5Bone(name, matrix, parentName);
      }

      return true;
    }

    /// <summary>
    /// advances to a given token
    /// </summary>
    /// <param name="token">token to advance to</param>
    /// <returns>true if token</returns>
    bool Advance(string token) {
      do {
        if (CurrentToken == token)
          return true;
      } while ( Advance());
      return false;
    }

    /// <summary>
    /// advance to next token
    /// </summary>
    /// <returns>true if there is another token; false if end is reached</returns>
    bool Advance() {
      currentToken++;
      return currentToken < tokens.Length;
    }

    /// <summary>
    /// advance to the next token which is equal to one of the given tokens
    /// </summary>
    /// <param name="tokens">token list</param>
    /// <returns>true if there is another token; false if end is reached</returns>
    bool Advance(params string[] tokens) {
      do {
        foreach(string token in tokens) {
          if (token == CurrentToken)
            return true;
        }
      }while( Advance() );
      return false;
    }

    string CurrentToken {
      get {
        return tokens[currentToken];
      }
    }

    string NextToken {
      get {
        if (Advance())
          return CurrentToken;
        return null;
      }
    }

    float NextFloat {
      get {
        return float.Parse(NextToken, culture);
      }
    }

    void CleanTokens() {
      ArrayList list = new ArrayList();
      foreach(string token in tokens) {
        if (token != "")
          list.Add(token);
      }
      tokens = (string[])list.ToArray(typeof(string));
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

	}
}
