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
using System.Collections;
using Purple.Graphics.Core;
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics {
	//=================================================================
	/// <summary>
	/// This class defines the layout (the streams) of a <see cref="VertexUnit"/>.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para> 
	///   <para>Reworked: 0.3</para> 
	/// </remarks>
	//=================================================================
  [System.ComponentModel.TypeConverter(typeof(System.ComponentModel.ExpandableObjectConverter))]
	public class VertexFormat : IComparable{
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
    Type[] types = null;
		byte[] usageIndices = null;
    private IVertexDeclaration vertexDeclaration = null;
    private Semantic[] semantics = null;

    static VertexFormat position = null;
		static VertexFormat positionColor = null;
    static VertexFormat positionColorTexture = null;
		static VertexFormat positionTexture = null;
		static VertexFormat positionNormalTexture = null;
		static VertexFormat positionTexture2 = null;
    static VertexFormat positionNormalColorTexture = null;
    static VertexFormat positionTexture2Tangent = null;
    static VertexFormat positionCompressedNormalTexture = null;
    static VertexFormat positionCompressedNormalColorTexture = null;

    static VertexFormat boneIndices = null;
    static VertexFormat boneWeights = null;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
		/// <summary>
		/// Returns the <see cref="Type"/>s of the current <see cref="VertexFormat"/>.
		/// </summary>
		public Type[] Types {
			get {
				return types;
			}
		}

		/// <summary>
		/// Returns the number of streams of the current <see cref="VertexFormat"/>.
		/// </summary>
		public int Size {
			get {
				return types.Length;
			}
		}

    /// <summary>
    /// Returns the <see cref="IVertexDeclaration"/> of the current format.
    /// </summary>
    public IVertexDeclaration VertexDeclaration {
      get {
        return vertexDeclaration;
      }
    }

    /// <summary>
    /// Returns the <see cref="Semantic"/>s for the current <see cref="VertexFormat"/>.
    /// </summary>
    public Semantic[] Semantics {
      get {
        return semantics;
      }
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// Creates a new instance of a <see cref="VertexFormat"/>.
		/// </summary>
		/// <param name="types">The types of the <see cref="IVertexStream"/>s.</param>
		public VertexFormat(Type[] types) {
      // Todo: Stable Sort (DeclarationUsage, UsageIndex)
      // Todo: offline streams
			Init(types);
		}

		/// <summary>
		/// Creates a new instance of a <see cref="VertexFormat"/>.
		/// </summary>
		/// <remarks>
		/// It is assumed, that the streams are sorted!
		/// </remarks>
		/// <param name="streams">List of IVertexStreams to take format from.</param>
		public VertexFormat(IList streams) {
			Type[] types = new Type[streams.Count];
			for (int i=0; i<streams.Count; i++)
				types[i] = streams[i].GetType();
			Init(types);
		}

		private void Init(Type[] types) {

			this.types = types;

      usageIndices = new byte[types.Length];		
			int index=0;
      VertexElement[] vertexElements = new VertexElement[types.Length];
      semantics = new Semantic[types.Length];

      SortedList usages = new SortedList( );
			foreach(Type type in types) {
        VertexElement element =  StreamFactory.Instance.VertexElement(type);
        element.Stream = (short)index;
        element.Offset = 0;
        if (!usages.Contains(element.DeclarationUsage))
          usages[element.DeclarationUsage] = (byte)0;
        element.UsageIndex = usageIndices[index] = (byte)usages[element.DeclarationUsage];
        usages[element.DeclarationUsage] = (byte)((byte)usages[element.DeclarationUsage] + 1);
				vertexElements[index] = element;
        semantics[index] = new Semantic(element.DeclarationUsage, element.UsageIndex);
        index++;
			}
      vertexDeclaration = Device.Instance.CreateVertexDeclaration(vertexElements);
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		/// <summary>
		/// Returns true if a stream of a certain type is contained by this 
		/// <see cref="VertexFormat"/>.
		/// </summary>
		/// <param name="type">Type of the <see cref="IVertexStream"/>.</param>
    /// <returns>True if a stream of a certain type is contained by this 
    /// <see cref="VertexFormat"/>.</returns>
		public bool Contains(Type type) {
      for (int i=0; i<types.Length; i++) 
        if (types[i] == type)
          return true;
      return false;
		}

		/// <summary>
		/// Returns true if a stream of a certain type is contained by this <see cref="VertexFormat"/>.
		/// </summary>
		/// <param name="type">Type of the <see cref="IVertexStream"/>.</param>
		/// <param name="usageIndex">The index of the stream.</param>
		/// <returns>True if a stream of a certain type is contained by this <see cref="VertexFormat"/>.</returns>
		public bool Contains(Type type, int usageIndex) {
			return Count(type)>usageIndex;
		}

    /// <summary>
    /// Returns true if a stream with the given semantic is contained by this <see cref="VertexFormat"/>.
    /// </summary>
    /// <param name="sem"><see cref="Semantic"/> to test for.</param>
    /// <returns>True if a stream with the given semantic is contained by this <see cref="VertexFormat"/>.</returns>
    public bool Contains(Semantic sem) {
      return Count(sem.Usage)>sem.UsageIndex;
    }

		/// <summary>
		/// Returns the number of streams of a certain type contained by this <see cref="VertexFormat"/>.
		/// </summary>
		/// <param name="type">Type of the <see cref="IVertexStream"/>.</param>
		/// <returns>the number of streams of a certain type contained by this <see cref="VertexFormat"/>.</returns>
		public int Count(Type type) {
      int counter = 0;
			for (int i=0; i<types.Length; i++)
        if (types[i] == type)
          counter++;
      return counter;
    }

    /// <summary>
    /// Returns the number of streams of a certain <see cref="DeclarationUsage"/>.
    /// </summary>
    /// <param name="usage">The <see cref="DeclarationUsage"/> to get the number of streams for.</param>
    /// <returns>The number of streams of a certain <see cref="DeclarationUsage"/>.</returns>
    public int Count(DeclarationUsage usage) {
      int counter = 0;
      for (int i=0; i<types.Length; i++)
        if (StreamFactory.Instance.VertexElement(types[i]).DeclarationUsage == usage)
          counter++;
      return counter;
    }

		/// <summary>
		/// Returns the index of the <see cref="IVertexStream"/> in the <see cref="VertexFormat"/>.
		/// </summary>
		/// <param name="type">Type of stream.</param>
		/// <param name="usageIndex">The nth stream with this type.</param>
		/// <returns>The index of the <see cref="IVertexStream"/> in the <see cref="VertexFormat"/>.</returns>
		/// <exception cref="IndexOutOfRangeException">If type or index is invalid.</exception>
		public int GetIndex(Type type, int usageIndex) {
      int counter = 0;
      for (int i=0; i<types.Length; i++) {
        if (type.IsAssignableFrom(types[i])) {
          if (counter == usageIndex)
            return i;
          counter++;
        }
      }
      throw new System.IndexOutOfRangeException("The number of streams of " + type + " are less then the requested number!");
		}

    /// <summary>
    /// Returns the index of the <see cref="IVertexStream"/> for a given <see cref="Semantic"/> description.
    /// </summary>
    /// <param name="sem">The <see cref="Semantic"/> to get stream for.</param>
    /// <returns>The index of the <see cref="IVertexStream"/> for a given <see cref="Semantic"/> description.</returns>
    public int GetIndex(Semantic sem) {
      int counter = 0;
      for (int i=0; i<types.Length; i++) {
        if (StreamFactory.Instance.VertexElement(types[i]).DeclarationUsage == sem.Usage) {
          if (counter == sem.UsageIndex)
            return i;
          counter++;
        }
      }
      throw new System.IndexOutOfRangeException("The number of streams of " + sem.ToString() + " are less then the requested number!");
    }

		/// <summary>
		/// Returns the UsageIndex of a stream with a certain index.
		/// </summary>
		/// <param name="index">Index of stream.</param>
		/// <returns>The UsageIndex of a stream with a certain index.</returns>
		public byte GetUsageIndex(int index) {
			return (byte)usageIndices[index];
		}
		
		/// <summary>
		/// Returns the type of a stream with a certain index.
		/// </summary>
		/// <param name="index">Index of stream.</param>
		/// <returns>The type of a stream.</returns>
		public Type GetType(int index) {
			return types[index];
		}

		/// <summary>
		/// Compares two <see cref="VertexFormat"/>s.
		/// </summary>
		/// <param name="format">Format to compare current object with.</param>
		/// <returns>0 if equal ...</returns>
		public int CompareTo(object format) {
      // TODO sorting!
			for (int i=0; i<Types.Length; i++) {
				int result = Types[i].GUID.CompareTo( (format as VertexFormat).Types[i].GUID);
				if (result != 0)
					return result;
			}
			return 0;
		}

		/// <summary>
		/// New hashcode function for <see cref="VertexFormat"/>.
		/// </summary>
		/// <returns>The hashcode of the current object.</returns>
		public override int GetHashCode() {
			int hashCode = 0;
			for (int i=0; i<Types.Length; i++)
				hashCode ^= Types[i].GetHashCode();
			return hashCode;
		}

		/// <summary>
		/// New equals function.
		/// </summary>
		/// <param name="obj"><see cref="VertexFormat"/> to compare current object with.</param>
		/// <returns>True if equals.</returns>
		public override bool Equals(object obj) {
      // TODO sorting!
			if (obj == null)
				return false;
			VertexFormat format = (VertexFormat) obj;
			if (this.Size != format.Size)
				return false;
			for (int i=0; i<Types.Length; i++)
        if (Types[i] != format.Types[i])
					return false;
			return true;
		}

    /// <summary>
    /// Converts this <see cref="VertexFormat"/> to suit the passed <see cref="Semantic"/>s.
    /// </summary>
    /// <param name="semantics">The semantics to convert to.</param>
    /// <returns>The converted <see cref="VertexFormat"/>.</returns>
    public VertexFormat Clone(Semantic[] semantics) {
      Type[] newTypes = new Type[semantics.Length];
      for (int i=0; i<semantics.Length; i++) {
        if (this.Contains(semantics[i]))
          newTypes[i] = this.GetType( this.GetIndex(semantics[i]));
        else
          throw new NotSupportedException("Not supported so far!");
      }
      return new VertexFormat(newTypes);
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Predefined Formats
		//---------------------------------------------------------------
    /// <summary>
    /// VertexUnit containing only a position stream.
    /// </summary>
    public static VertexFormat Position {
      get {
        if (position == null) {
          Type[] types = new Type[] {typeof(PositionStream) };
          position = new VertexFormat(types);
        }
        return position;
      }
    }

    /// <summary>
    /// VertexUnit contains a position and color stream.
    /// </summary>
    public static VertexFormat PositionColor {
      get {
        if (positionColor == null) {				
          Type[] types = new Type[] {	typeof(PositionStream), 
                                      typeof(ColorStream)	};
          positionColor = new VertexFormat( types );
        }
        return positionColor;
      }
    }

    /// <summary>
    /// VertexUnit contains a position, color and texture stream.
    /// </summary>
    public static VertexFormat PositionColorTexture {
      get {
        if (positionColorTexture == null) {				
          Type[] types = new Type[] {	typeof(PositionStream), 
                                      typeof(ColorStream),
                                      typeof(TextureStream) };
          positionColorTexture = new VertexFormat( types );
        }
        return positionColorTexture;
      }
    }

    /// <summary>
    /// VertexUnit contains a position and Texture stream.
    /// </summary>
    public static VertexFormat PositionTexture {
      get {
        if (positionTexture == null) {
          Type[] types = new Type[] {	typeof(PositionStream), 
                                      typeof(TextureStream)	};
          positionTexture = new VertexFormat( types );
        }
        return positionTexture;
      }
    }

    /// <summary>
    /// VertexUnit contains position, normal and a texture stream.
    /// </summary>
    public static VertexFormat PositionNormalTexture {
      get {
        if (positionNormalTexture == null) {
          Type[] types = new Type[] { typeof(PositionStream),
                                      typeof(NormalStream),
                                      typeof(TextureStream) };
          positionNormalTexture = new VertexFormat( types );
        }
        return positionNormalTexture;
      }
    }

    /// <summary>
    /// VertexUnit contains position, normal and a texture stream.
    /// </summary>
    public static VertexFormat PositionCompressedNormalTexture {
      get {
        if (positionCompressedNormalTexture == null) {
          Type[] types = new Type[] { typeof(PositionStream),
                                      typeof(CompressedNormalStream),
                                      typeof(TextureStream) };
          positionCompressedNormalTexture = new VertexFormat( types );
        }
        return positionCompressedNormalTexture;
      }
    }

    /// <summary>
    /// VertexUnit contains position, normal and a texture stream.
    /// </summary>
    public static VertexFormat PositionCompressedNormalColorTexture {
      get {
        if (positionCompressedNormalColorTexture == null) {
          Type[] types = new Type[] { typeof(PositionStream),
                                      typeof(CompressedNormalStream),
                                      typeof(ColorStream),
                                      typeof(TextureStream) };
          positionCompressedNormalColorTexture = new VertexFormat( types );
        }
        return positionCompressedNormalColorTexture;
      }
    }

    /// <summary>
    /// VertexUnit contains position and two texture streams.
    /// </summary>
    public static VertexFormat PositionTexture2 {
      get {
        if (positionTexture2 == null) {
          Type[] types = new Type[] { typeof(PositionStream),
                                      typeof(TextureStream),
                                      typeof(TextureStream) };
          positionTexture2 = new VertexFormat( types );
        }
        return positionTexture2;
      }
    }

    /// <summary>
    /// VertexUnit contains position, normal, color and a texture streams.
    /// </summary>
    public static VertexFormat PositionNormalColorTexture {
      get {
        if ( positionNormalColorTexture == null) {
          Type[] types = new Type[] { typeof(PositionStream),
                                      typeof(NormalStream),
                                      typeof(ColorStream),
                                      typeof(TextureStream) };
          positionNormalColorTexture = new VertexFormat( types );
        }
        return positionNormalColorTexture;
      }
    }

    /// <summary>
    /// VertexUnit contains position, texture, normal texture, tangent, binormal, normal streams.
    /// </summary>
    public static VertexFormat PositionTexture2Tangent {
      get {
        if ( positionTexture2Tangent == null) {
          Type[] types = new Type[] { typeof(PositionStream),
                                      typeof(TextureStream),
                                      typeof(TextureStream),
                                      typeof(TangentStream),
                                      typeof(BinormalStream),
                                      typeof(NormalStream) };
          positionTexture2Tangent = new VertexFormat( types );
        }
        return positionTexture2Tangent;
      }
    }

    /// <summary>
    /// VertexUnit contains boneIndices.
    /// </summary>
    public static VertexFormat BoneIndices {
      get {
        if ( boneIndices == null) {
          Type[] types = new Type[] { typeof(IBoneIndicesStream)};
          boneIndices = new VertexFormat( types );
        }
        return boneIndices;
      }
    }

    /// <summary>
    /// VertexUnit contains boneWeights.
    /// </summary>
    public static VertexFormat BoneWeights {
      get {
        if ( boneWeights == null) {
          Type[] types = new Type[] { typeof(IBoneWeightsStream)};
          boneWeights = new VertexFormat( types );
        }
        return boneWeights;
      }
    }

    /// <summary>
    /// Adds two <see cref="VertexFormat"/>s together.
    /// </summary>
    /// <param name="fmt1">First format.</param>
    /// <param name="fmt2">Second format.</param>
    /// <returns>The combined <see cref="VertexFormat"/>.</returns>
    public static VertexFormat operator+(VertexFormat fmt1, VertexFormat fmt2) {
      Type[] types = new Type[fmt1.Types.Length + fmt2.Types.Length];
      fmt1.Types.CopyTo(types, 0);
      Array.Copy(fmt2.Types, 0, types, fmt1.Types.Length, fmt2.Types.Length);
      return new VertexFormat( types );
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
