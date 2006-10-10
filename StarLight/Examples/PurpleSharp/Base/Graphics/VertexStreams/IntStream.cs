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

namespace Purple.Graphics.VertexStreams {
  //=================================================================
  /// <summary>
  /// a simple integer stream (4 bytes)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public abstract class IntStream : GraphicsStream, IVertexStream {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    static string name = "IntStream";
    /// <summary>
    /// Default element for an <see cref="IntStream"/>.
    /// </summary>
    protected static VertexElement defaultElement = new VertexElement(0, 0, DeclarationType.UByte4,
      DeclarationMethod.Default,
      DeclarationUsage.None, 0);

    private VertexElement vertexElement = defaultElement;
    VertexUnit vertexUnit = null;

    /// <summary>
    /// Sets the usage of the stream.
    /// </summary>
    protected DeclarationUsage Usage {
      set {
        vertexElement.DeclarationUsage = value;
      }
    }

    /// <summary>
    /// The int data.
    /// </summary>
    [System.CLSCompliant(false)]
    protected uint[] IntData {
      get {
        return data;
      }
    }
    uint[] data = null;
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
      StreamFactory.Instance.Bind(name, typeof(IntStream), defaultElement);
    }

    /// <summary>
    /// a simple compressed int stream
    /// </summary>
    public IntStream(VertexUnit vertexUnit) : base(vertexUnit.Size) {
      this.vertexUnit = vertexUnit;
    }

    /// <summary>
    /// constructor
    /// </summary>
    /// <param name="size">number of elements</param>
    public IntStream(int size) : base(size) {
    }

    /// <summary>
    /// Constructor
    /// Init has to be called!!!
    /// </summary>
    public IntStream() : base(){
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// data access over indexer
    /// </summary>
    [System.CLSCompliant(false)]
    public uint this[int index] {
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
        return typeof(uint);
      }
    }

		
    /// <summary>
    /// returns the size in bytes of one array element
    /// </summary>
    public override short ElementSize { 
      get {
        return 4;
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
      set {
        vertexUnit = value;
      }
      get {
        return vertexUnit;
      }
    }

    /// <summary>
    /// initialises the array with a given size
    /// </summary>
    /// <param name="size">size of array</param>
    public override void Init(int size) {
      data = new uint[size];
      used = size;
    }

    /// <summary>
    /// sets the value of the array
    /// </summary>
    /// <param name="data">array to set</param>
    protected override void SetData(Array data) {
      this.data = (uint[])data;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}