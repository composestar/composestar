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

namespace Purple.Graphics.VertexStreams
{
  //=================================================================
  /// <summary>
  /// a compressed normal stream
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
	public class CompressedNormalStream : IntStream
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    static string name = "CompressedNormalStream";
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Binds this type to the <see cref="StreamFactory"/>.
    /// </summary>
    static public new void Bind() {
      VertexElement element = defaultElement;
      element.DeclarationUsage = DeclarationUsage.Normal;
      StreamFactory.Instance.Bind(name, typeof(CompressedNormalStream), element);
    }

    /// <summary>
    /// a simple compressed normal stream
    /// </summary>
		public CompressedNormalStream(VertexUnit vertexUnit) : base(vertexUnit)
		{
      Usage = DeclarationUsage.Normal;
		}

    /// <summary>
    /// constructor
    /// </summary>
    /// <param name="size">number of elements</param>
    public CompressedNormalStream(int size) : base(size) {
      Usage = DeclarationUsage.Normal;
    }

    /// <summary>
    /// Constructor
    /// Init has to be called!!!
    /// </summary>
    public CompressedNormalStream() : base(){
      Usage = DeclarationUsage.Normal;
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
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// name of stream
    /// </summary>
    public override string Name { 
      get {
        return name;
      }
    }
	
    /// <summary>
    /// create a compresses normal stream from an uncompressed stream
    /// </summary>
    /// <param name="stream">stream to compress</param>
    public static void From(NormalStream stream) {
      CompressedNormalStream compressed = new CompressedNormalStream(stream.Size);
      compressed.CompressAndFill(stream);
    }

    /// <summary>
    /// compresses the normal stream and fills the current stream
    /// both streams have to be the same size
    /// </summary>
    /// <param name="stream"></param>
    public void CompressAndFill(NormalStream stream) {
      System.Diagnostics.Debug.Assert(stream.Size == this.Size);
      const float fracScale = 127.5f;
      for(int i=0; i<stream.Size; i++) {
        Math.Vector3 vec = stream[i];
        IntData[i] = (((uint)(vec.X*fracScale + fracScale)*1) +
                  ((uint)(vec.Y*fracScale + fracScale)*256) +
                  ((uint)(vec.Z*fracScale + fracScale)*65536));

          //16777216
      }
    }

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public override IGraphicsStream Clone() {
      IGraphicsStream stream = new CompressedNormalStream(this.Size);
      stream.Copy(this);
      return stream;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
