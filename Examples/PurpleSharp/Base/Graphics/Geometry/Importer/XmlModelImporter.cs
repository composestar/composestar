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

namespace Purple.Graphics.Geometry.Importer {   
  //=================================================================
  /// <summary>
  ///importing a mesh from a xml file
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  ///   <para>Update: 0.7</para>
  /// The xml format is much bigger and is loaded much slower than 
  /// the binary based format. However it is easier to read for humans.
  /// </remarks>
  //=================================================================
  public class XmlModelImporter : IModelImporter, ISkeletonImporter {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    System.Globalization.CultureInfo culture = new System.Globalization.CultureInfo("en-US");

    /// <summary>
    /// returns imported model
    /// </summary>
    public Model Model {
      get {
        return model;
      }
    }
    Model model = null;

    /// <summary>
    /// get the skeleton
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
    /// constructor
    /// </summary>
    public XmlModelImporter() {
    }

    /// <summary>
    /// imports a mesh
    /// </summary>
    /// <param name="fileName">file to take mesh from</param>
    public XmlModelImporter(string fileName) {
      Import(fileName);
    }

    /// <summary>
    /// imports a mesh from a stream
    /// </summary>
    /// <param name="stream">stream to import mesh from</param>
    public XmlModelImporter(Stream stream) {
      Import(stream);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// import a mesh from a file
    /// </summary>
    /// <param name="fileName">name of file</param>
    public void Import(string fileName) {
      using(Stream stream = fileSystem.Open(fileName)) {
        Import(stream);
      }
    }

    /// <summary>
    /// import a mesh from a stream
    /// </summary>
    /// <param name="stream">stream containing mesh data</param>
    public void Import(Stream stream) {
      model = null;
      skeleton = null;
      IndexStream indexStream = null;
      IVertexStream currentStream = null;
      ArrayList streams = new ArrayList();
      StringDictionary attributes = new StringDictionary();
      XmlTextReader reader = new XmlTextReader(stream);
      Matrix4[] jointArray = null;
      Joint[] joints = null;
      Hashtable jointTable = null;
      int index = 0;
      int currentJoint = 0;
      int vertexCount = 0;
      int binding = -1;
      ArrayList[] indicesList = null;
      ArrayList[] weightsList = null;

      while(reader.Read()) {
        if (reader.NodeType == XmlNodeType.Element) {
          switch (reader.Name) {

              // <mesh>
            case "mesh":
              if (model != null)
                throw new GraphicsException("Only one mesh allowed in mesh stream!");
              model = new Model();              						
              break;

              // <subset>
            case "subset":
              string parentJoint = reader.GetAttribute("parentJoint");
              if (parentJoint != null && parentJoint != "")
                binding = (jointTable[parentJoint] as Joint).Index;
              else
                binding = -1;
              break;

              // <attributes>
            case "attributes":
              break;

            case "attribute":
              // todo !!!
              attributes.Add(reader.GetAttribute("name"), reader.GetAttribute("value"));
              break;

              //<indexStream>
            case "indexStream": {
              index = 0;
              int size = int.Parse(reader.GetAttribute("size"), culture);
              indexStream = new IndexStream16(size);						
            }
              break;

              //<triangle>
            case "triangle": {
              int a = int.Parse(reader.GetAttribute("a"), culture);
              int b = int.Parse(reader.GetAttribute("b"), culture);
              int c = int.Parse(reader.GetAttribute("c"), culture);
              indexStream[index++] = a;
              indexStream[index++] = b;
              indexStream[index++] = c;
            }
              break;

              //<positionStream>
            case "positionStream": {
              index = 0;
              vertexCount = int.Parse(reader.GetAttribute("size"), culture);
              currentStream = new PositionStream(vertexCount);
              streams.Add(currentStream);              
            }
              break;

              //<vector3>
            case "vector3": {
              float x = float.Parse( reader.GetAttribute("x"), culture);
              float y = float.Parse( reader.GetAttribute("y"), culture);
              float z = float.Parse( reader.GetAttribute("z"), culture);
              (currentStream as PositionStream)[index++] = new Vector3(x,y,z);							
            }
              break;

              //<normalStream>
            case "normalStream": {
              index = 0;
              int size = int.Parse(reader.GetAttribute("size"), culture);
              currentStream = new NormalStream(size);
              streams.Add(currentStream);              
            }
              break;

              //<colorStream>
            case "colorStream": {
              index = 0;
              int size = int.Parse(reader.GetAttribute("size"), culture);
              currentStream = new ColorStream(size);
              streams.Add(currentStream);              
            }
              break;

              //<color>
            case "color": {
              int r = (int) ((float.Parse( reader.GetAttribute("r"), culture))*255.0f + 0.5f);
              int g = (int) ((float.Parse( reader.GetAttribute("g"), culture))*255.0f + 0.5f);
              int b = (int) ((float.Parse( reader.GetAttribute("b"), culture))*255.0f + 0.5f);							
              (currentStream as ColorStream)[index ++] = System.Drawing.Color.FromArgb(r, g, b).ToArgb();							
            }
              break;

              //<textureStream>
            case "textureStream": {
              index = 0;
              int size = int.Parse(reader.GetAttribute("size"), culture);
              currentStream = new TextureStream(size);
              streams.Add(currentStream);              
            }
              break;

              //<vector2>
            case "vector2": {
              float x = float.Parse( reader.GetAttribute("x"), culture);
              float y = float.Parse( reader.GetAttribute("y"), culture);
              (currentStream as TextureStream)[index++] = new Vector2(x,y);							
            }
              break;

            case "joints": {
              int size = int.Parse( reader.GetAttribute("size"), culture);
              jointArray = new Matrix4[size];
              joints = new Joint[size];
              jointTable = new Hashtable();
              currentJoint = 0;
            }
              break;

            case "joint": {
              string jointName = reader.GetAttribute("name");
              string parentName = reader.GetAttribute("parent");
              Matrix4 m = new Matrix4(  float.Parse( reader.GetAttribute("a1"), culture),
                float.Parse( reader.GetAttribute("a2"), culture),
                float.Parse( reader.GetAttribute("a3"), culture),
                float.Parse( reader.GetAttribute("a4"), culture),
                float.Parse( reader.GetAttribute("b1"), culture),
                float.Parse( reader.GetAttribute("b2"), culture),
                float.Parse( reader.GetAttribute("b3"), culture),
                float.Parse( reader.GetAttribute("b4"), culture),
                float.Parse( reader.GetAttribute("c1"), culture),
                float.Parse( reader.GetAttribute("c2"), culture),
                float.Parse( reader.GetAttribute("c3"), culture),
                float.Parse( reader.GetAttribute("c4"), culture),
                float.Parse( reader.GetAttribute("d1"), culture),
                float.Parse( reader.GetAttribute("d2"), culture),
                float.Parse( reader.GetAttribute("d3"), culture),
                float.Parse( reader.GetAttribute("d4"), culture) );
              jointArray[currentJoint] = m; //new Joint(jointName, m);
              Joint parent = null;
              if (parentName != null && jointTable.Contains(parentName))
                parent = (Joint)jointTable[parentName];
              joints[currentJoint] = new Joint(jointName, currentJoint, parent);
              jointTable[jointName] = joints[currentJoint];
              currentJoint++;
            }
              break;

            case "weights": {
              index = 0;
              //vertexCount = int.Parse(reader.GetAttribute("size"), culture);
              indicesList = new ArrayList[vertexCount];
              weightsList = new ArrayList[vertexCount];
              for (int i=0; i<vertexCount; i++) {
                indicesList[i] = new ArrayList(8);
                weightsList[i] = new ArrayList(8);
              }
            }
              break;

            case "weight": {
              int vertexIndex = int.Parse( reader.GetAttribute("vertexIndex"));
              byte jointIndex = byte.Parse( reader.GetAttribute("jointIndex"));
              float value = float.Parse( reader.GetAttribute("weight"), culture);
              indicesList[vertexIndex].Add(jointIndex);
              weightsList[vertexIndex].Add(value);            
             }
             break;

          }
        }

        if (reader.NodeType == XmlNodeType.EndElement) {
          if (reader.Name.Equals("weights")) {
            IBoneIndicesStream bis = null;
            IBoneWeightsStream bws = null;
            if (HardwareSkinning) {
              bis = new BoneIndicesStream(vertexCount);
              bws = new BoneWeightsStream(vertexCount);
            } else {
              bis = new SoftwareBoneIndicesStream(vertexCount);
              bws = new SoftwareBoneWeightsStream(vertexCount);
            }
            for (int i=0; i<vertexCount; i++) {
              bis.SetIndices(i, (byte[])indicesList[i].ToArray(typeof(byte)));
              bws.SetWeights(i, (float[])weightsList[i].ToArray(typeof(float)));
            }
            streams.Add(bis);              
            streams.Add(bws);
          } else if (reader.Name.Equals("subset")) {
            VertexUnit vertexUnit = new VertexUnit(streams);
            if (binding == -1) {
              model.Mesh = new Mesh( new SubSet(vertexUnit, indexStream)); 
            } else {
              model.AttachModel( new Model( new Mesh( new SubSet( vertexUnit, indexStream)), null), binding);
            }
            streams.Clear();
          }
        }
      };			

      reader.Close();
      if (jointArray != null && joints != null)
        skeleton = new Skeleton(jointArray, joints);
      model.Skeleton = skeleton;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
