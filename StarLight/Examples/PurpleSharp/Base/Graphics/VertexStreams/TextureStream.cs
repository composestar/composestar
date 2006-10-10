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

namespace Purple.Graphics.VertexStreams
{
	//=================================================================
	/// <summary>
	/// Texture array
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public class TextureStream : GraphicsStream, IVertexStream {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
    static string name = "Texture";
    static VertexElement defaultElement = new VertexElement(0, 0, DeclarationType.Float2,
      DeclarationMethod.Default,
      DeclarationUsage.TextureCoordinate, 0);

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
    /// name of stream
    /// </summary>
    public override string Name { 
      get {
        return name;
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
      StreamFactory.Instance.Bind(name, typeof(TextureStream), defaultElement);
    }

		/// <summary>
		/// constructor
		/// </summary>
		/// <param name="vertexUnit"></param>
		public TextureStream(VertexUnit vertexUnit) : base(vertexUnit.Size) {
      this.vertexUnit = vertexUnit;
		}

		/// <summary>
		/// constructor
		/// </summary>
		/// <param name="size">number of elements</param>
		public TextureStream(int size) : base(size) {
		}

    /// <summary>
    /// Constructor
    /// Init has to be called!!!
    /// </summary>
    public TextureStream() : base() {
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
    /// returns the nth texture unit from a certain vertex unit
    /// </summary>
    /// <param name="vertexUnit">vertexUnit to get stream from</param>
    /// <param name="index">the number of the texture stream to retrieve</param>
    /// <returns>the nth texture stream from the vertex unit </returns>
    public static TextureStream From(VertexUnit vertexUnit, int index) {
      return (TextureStream)vertexUnit[typeof(TextureStream), index];
    }

    /// <summary>
    /// return the first texture stream from a certain vertex unit
    /// </summary>
    /// <param name="vertexUnit">the vertex unit to retrieve stream from</param>
    /// <returns>first texture stream from a certain vertex unit</returns>
    public static TextureStream From(VertexUnit vertexUnit) {
      return From(vertexUnit, 0);
    }

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public override IGraphicsStream Clone() {
      IGraphicsStream stream = new TextureStream(this.Size);
      stream.Copy(this);
      return stream;
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
