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
	/// Binormal Vector stream
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public class BinormalStream : PositionStream {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
    private static string name = "Binormal";
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
    /// <summary>
    /// name of stream
    /// </summary>
    public override string Name { 
      get {
        return name;
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
    static public new void Bind() {
      VertexElement element = defaultElement;
      element.DeclarationUsage = DeclarationUsage.Binormal;
      StreamFactory.Instance.Bind(name, typeof(BinormalStream), element);
    }

		/// <summary>
		/// constructor
		/// </summary>
		/// <param name="vertexUnit"></param>
		public BinormalStream(VertexUnit vertexUnit) : base(vertexUnit) {
      Usage = DeclarationUsage.Binormal;
		}

		/// <summary>
		/// constructor
		/// </summary>
		/// <param name="capacity">number of elements</param>
		public BinormalStream(int capacity) : base(capacity) {
      Usage = DeclarationUsage.Binormal;
		}

    /// <summary>
    /// Constructor
    /// Init has to be called!!!
    /// </summary>
    public BinormalStream() : base() {
      Usage = DeclarationUsage.Binormal;
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public override IGraphicsStream Clone() {
      IGraphicsStream stream = new BinormalStream(this.Size);
      stream.Copy(this);
      return stream;
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
