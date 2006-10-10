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
using Purple.Math;

namespace Purple.Graphics.VertexStreams {
  //=================================================================
  /// <summary>
  /// Position Stream containing 2d vectors
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class PositionStream2 : GraphicsStream, IVertexStream{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    static string name = "Position2";
    /// <summary>
    /// The default VertexElement.
    /// </summary>
    static protected VertexElement defaultElement = new VertexElement(0, 0, DeclarationType.Float2,
      DeclarationMethod.Default,
      DeclarationUsage.Position, 0);
    Vector2[] data = null;
    VertexElement vertexElement = defaultElement;
    VertexUnit vertexUnit = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Sets the declarationUsage for the PositionStream;
    /// </summary>
    protected DeclarationUsage DeclarationUsage {
      set {
        vertexElement.DeclarationUsage = value;
      }
    }

    /// <summary>
    /// data access over indexer
    /// </summary>
    public Vector2 this[int index] {
      set {
        data[index] = value;
      }
      get {
        return data[index];
      }
    }

    /// <summary>
    /// access to array data
    /// </summary>
    public override Array Data { 
      get {
        return data;
      }
    }

    /// <summary>
    /// name of stream
    /// </summary>
    public override string Name { 
      get {
        return name;
      }
    }

    /// <summary>
    /// type of vertices
    /// </summary>
    public override Type Type {
      get {
        return typeof(Vector2);
      }
    }

    /// <summary>
    /// returns the size in bytes of one array element
    /// </summary>
    public override short ElementSize { 
      get {
        return 8;
      }
    }

    /// <summary>
    /// gets the description of the vertex element
    /// </summary>
    public VertexElement VertexElement { 
      get {
        return vertexElement;
      }
      set {
        vertexElement = value;
      }
    }

    /// <summary>
    /// returns vertexUnit of VertexStream
    /// </summary>
    public VertexUnit VertexUnit { 
      get {
        return vertexUnit;
      }
      set {
        vertexUnit = value;
      }
    }

    /// <summary>
    /// returns the used BufferManager
    /// </summary>
    public override BufferManager BufferManager { 
      get {
        return VertexBufferManager.Instance;
      }
    }

    /// <summary>
    /// Returns true if the current stream is a <see cref="SoftwareStream"/>.
    /// </summary>
    public override bool IsSoftware { 
      get {
        return false;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Binds this type to the <see cref="StreamFactory"/>.
    /// </summary>
    static public void Bind() {
      StreamFactory.Instance.Bind(name, typeof(PositionStream2), defaultElement);
    }

    /// <summary>
    /// constructor
    /// </summary>
    /// <param name="vertexUnit"></param>
    public PositionStream2(VertexUnit vertexUnit) : base(vertexUnit.Size) {
      this.vertexUnit = vertexUnit;
    }

    /// <summary>
    /// constructor
    /// </summary>
    /// <param name="size">number of elements</param>
    public PositionStream2(int size) : base(size) {
    }

    /// <summary>
    /// Constructor
    /// Init has to be called!!!
    /// </summary>
    public PositionStream2() : base(){
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// initialises the array with a given size
    /// </summary>
    /// <param name="size">size of array</param>
    public override void Init(int size) {
      data = new Vector2[size];
      used = size;
    }

    /// <summary>
    /// sets the value of the array
    /// </summary>
    /// <param name="data">array to set</param>
    protected override void SetData(Array data) {
      this.data = (Vector2[])data;
    }

    /// <summary>
    /// retrieve n'th PositionStream2 from vertex Unit
    /// </summary>
    /// <param name="unit">vertexUnit to retrieve stream from</param>
    /// <param name="index">number of stream of type(PositionStream2)</param>
    /// <returns>the n'th PositionStream2 of the vertex unit</returns>
    public static PositionStream2 From(VertexUnit unit, int index) {
      return (PositionStream2)unit[typeof(PositionStream2), index];
    }

    /// <summary>
    /// retrieves the first PositionStream2 of a certain vertex unit
    /// </summary>
    /// <param name="unit">vertex unit to retrive stream from</param>
    /// <returns>the first position stream of the vertex unit</returns>
    public static PositionStream2 From(VertexUnit unit) {
      return From(unit, 0);
    }

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public override IGraphicsStream Clone() {
      IGraphicsStream stream = new PositionStream2(this.Size);
      stream.Copy(this);
      return stream;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
