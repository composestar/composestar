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
using System.Xml;
using System.Collections;
using System.Collections.Specialized;

using Purple.IO;
using Purple.Graphics;
using Purple.Graphics.VertexStreams;
using Purple.Math;
using Purple.Profiling;

namespace Purple.Graphics.Geometry.Importer {   
  //=================================================================
  /// <summary>
  /// Imports a mesh from a binary file.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  ///   <para>Update: 0.5</para>
  /// The binary format is much smaller and can be loaded much faster than 
  /// the xml based format.
  /// </remarks>
  //=================================================================
  public class BinaryModelImporter : IModelImporter, ISkeletonImporter {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    byte[] matrixBytes = new byte[64];

    /// <summary>
    /// Returns imported <see cref="Model"/>.
    /// </summary>
    public Model Model {
      get {
        return model;
      }
    }
    Model model = null;

    /// <summary>
    /// Returns the imported <see cref="Skeleton"/>.
    /// </summary>
    public Skeleton Skeleton { 
      get {
        return skeleton;
      }
    }
    Skeleton skeleton = null;

    /// <summary>
    /// The <see cref="IFileSystem"/> that is used by the <see cref="BinaryAnimationImporter"/>.
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
    /// Flag that indicates if importer should create Software or Hardware streams 
    /// for skinning.
    /// </summary>
    public bool HardwareSkinning {
      get {
        return hardwareSkinning;
      }
      set {
        hardwareSkinning = value;
      }
    }
    bool hardwareSkinning = false;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="BinaryModelImporter"/>.
    /// </summary>
    public BinaryModelImporter() {
    }

    /// <summary>
    /// Imports an <see cref="Mesh"/>.
    /// </summary>
    /// <param name="fileName">File to load mesh from.</param>
    public BinaryModelImporter(string fileName) {
      Import(fileName);
    }

    /// <summary>
    /// Imports an <see cref="Mesh"/>.
    /// </summary>
    /// <param name="stream">Stream to import mesh from.</param>
    public BinaryModelImporter(Stream stream) {
      Import(stream);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Import a mesh from a file.
    /// </summary>
    /// <param name="fileName">Name of file.</param>
    public void Import(string fileName) {
      using(Stream stream = fileSystem.Open(fileName)) {
        Import(stream);
      }
    }

    /// <summary>
    /// Import a mesh from a stream.
    /// </summary>
    /// <param name="stream">Stream containing mesh data.</param>
    public void Import(Stream stream) {

      Profiler.Instance.Begin("Import binary mesh");
      // Header
      BinaryReader reader = new BinaryReader( stream );
      if (ReadString(reader) != "mesh" || ReadString(reader) != "v0.3")
        throw new NotSupportedException("Can't load mesh, file not supported!");
      
      // Joints
      int jointNum = ReadInt(reader);
      Joint[] joints = new Joint[jointNum];
      Matrix4[] jointArray = new Matrix4[jointNum];
      Hashtable jointTable = new Hashtable(joints.Length);

      for (int i=0; i<joints.Length; i++) {
        string name = ReadString(reader);
        string parent = ReadString(reader);

        reader.Read(matrixBytes, 0, matrixBytes.Length);
        Matrix4 m = Matrix4.From( matrixBytes );
        
        Joint parentJoint = null;
        if (parent != null && jointTable.Contains(parent))
          parentJoint = (Joint)jointTable[parent];                                                                   
        joints[i] = new Joint(name, i, parentJoint);
        jointArray[i] = m;
        jointTable[name] = joints[i];
      }
      skeleton = new Skeleton(jointArray, joints);

      // SubSet
      int subSetNum = ReadInt(reader);
      for (int i=0; i<subSetNum; i++) {
        ArrayList streams = new ArrayList(10);
        // Header
        if (ReadString(reader) != "subset")
          throw new NotSupportedException("Error on loading subSet!");
        string name = ReadString(reader);
        string parentJoint = ReadString(reader);

        int attributeCount = ReadInt(reader);
        StringDictionary attributes = new StringDictionary();
        for (int t=0; t<attributeCount; t++)
          attributes.Add( ReadString(reader), ReadString(reader) );

        // IndexStream
        // Todo Replace ushort.MaxValue with size of vertex unit
        IndexStream indexStream = IndexStream.Create(ReadInt(reader), ushort.MaxValue);
        byte[] indexBuffer = new byte[indexStream.Size*4];
        reader.Read(indexBuffer, 0, indexStream.Size*4);
        for (int t=0; t<indexStream.Size; t++)
          indexStream[t] = BitConverter.ToInt32(indexBuffer, t*4);

        int vertexSize = ReadInt(reader);
        PositionStream posStream = new PositionStream(vertexSize);
        streams.Add(posStream);
        byte[] vertexBuffer = new byte[vertexSize * 12];
        reader.Read(vertexBuffer, 0, vertexSize*12);
        for (int t=0; t<vertexSize; t++)
          posStream[t] = Vector3.From(vertexBuffer, 12*t);

        NormalStream normalStream = new NormalStream(vertexSize);
        streams.Add(normalStream);
        reader.Read(vertexBuffer, 0, vertexSize*12);
        for (int t=0; t<vertexSize; t++)
          normalStream[t] = Vector3.From(vertexBuffer, t*12);

        ColorStream colorStream = new ColorStream(vertexSize);
        streams.Add(colorStream);
        reader.Read(vertexBuffer, 0, vertexSize*12);
        for (int t=0; t<vertexSize; t++) {
          int r = Math.Basic.Clamp((int) (System.BitConverter.ToSingle(vertexBuffer, t*12)*255 + 0.5f), 0, 255);
          int g = Math.Basic.Clamp((int) (System.BitConverter.ToSingle(vertexBuffer, 4 + t*12)*255 + 0.5f), 0, 255);
          int b = Math.Basic.Clamp((int) (System.BitConverter.ToSingle(vertexBuffer, 8 + t*12)*255 + 0.5f), 0, 255);
          colorStream[t] = System.Drawing.Color.FromArgb(r, g, b).ToArgb();	
        }

        TextureStream[] textureStreams = new TextureStream[ReadInt(reader)];
        for (int t=0; t<textureStreams.Length; t++) {
          TextureStream texStream = new TextureStream(vertexSize);
          streams.Add(texStream);
          reader.Read(vertexBuffer, 0, vertexSize*8);
          for (int j=0; j<vertexSize; j++)
            texStream[j] = Vector2.From(vertexBuffer, j*8);
          textureStreams[t] = texStream;
        }      
      
        IBoneIndicesStream boneStream = null;
        IBoneWeightsStream weightsStream = null;
        int weightNum = ReadInt(reader);
        if (weightNum != 0) {
          if (HardwareSkinning) {
            boneStream = new BoneIndicesStream(vertexSize);
            weightsStream = new BoneWeightsStream(vertexSize);
          } else {
            boneStream = new SoftwareBoneIndicesStream(vertexSize);
            weightsStream = new SoftwareBoneWeightsStream(vertexSize);
          }
          streams.Add(boneStream);
          streams.Add(weightsStream);
          ArrayList[] indicesList = new ArrayList[vertexSize];
          ArrayList[] weightsList = new ArrayList[vertexSize];
          for (int t=0; t<vertexSize; t++) {
            indicesList[t] = new ArrayList(8);
            weightsList[t] = new ArrayList(8);
          }


          byte[] weightBuffer = new byte[weightNum*12];
          reader.Read(weightBuffer, 0, weightNum*12);
          for (int t=0; t<weightNum; t++) {
            int vertexIndex = BitConverter.ToInt32(weightBuffer, t*12);
            int jointIndex = BitConverter.ToInt32(weightBuffer, 4 + t*12);
            float weight = BitConverter.ToSingle(weightBuffer, 8 + t*12);
            indicesList[vertexIndex].Add((byte)jointIndex);
            weightsList[vertexIndex].Add(weight);
          }

          for (int t=0; t<vertexSize; t++) {
            boneStream.SetIndices(t, (byte[])indicesList[t].ToArray(typeof(byte)));
            weightsStream.SetWeights(t, (float[])weightsList[t].ToArray(typeof(float)));
          }
        }

        VertexUnit vertexUnit = new VertexUnit(streams);
        Mesh mesh = new Mesh( new SubSet(vertexUnit, indexStream) );
        if (model == null) {
          if (skeleton.Joints.Length != 0)
            model = new Model( new SkinnedMesh(mesh, skeleton), skeleton);
          else
            model = new Model( mesh, skeleton);
        } else {
          Joint attachTo = skeleton.RootJoint;
          if (parentJoint != "")
            attachTo = (jointTable[parentJoint] as Joint);
          model.AttachModel( new Model( mesh, skeleton), attachTo );
        }
      }
      reader.Close();

      Profiler.Instance.End("Import binary mesh");
    }

    string ReadString(BinaryReader reader) {
      int length = ReadInt(reader);
      return new string(reader.ReadChars(length));
    }

    int ReadInt(BinaryReader reader) {
      return reader.ReadInt32();
    }

    float ReadFloat(BinaryReader reader) {
      return reader.ReadSingle();
    }

    Vector3 ReadVector3(BinaryReader reader) {
      reader.Read(matrixBytes, 0, 12);
      return Vector3.From(matrixBytes );
    }

    Vector2 ReadVector2(BinaryReader reader) {
      reader.Read(matrixBytes, 0, 8);
      return Vector2.From(matrixBytes);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
