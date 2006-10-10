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

namespace Purple.Graphics {

	//=================================================================
	/// <summary>
	/// VertexElement - Defines input vertex data to the pipeline. 
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public struct VertexElement {
    static VertexElement none = new VertexElement(-1, -1, DeclarationType.Unused, 
      DeclarationMethod.Default, DeclarationUsage.Normal, 0);

		/// <summary>Stream number</summary>
		public short Stream;
		/// <summary>Offset (if any) from the beginning of the stream to the start of the data</summary>
		public short Offset;
		/// <summary>One of several predefined types that define the data size</summary>
		public DeclarationType DeclarationType;
		/// <summary>Tessellator processing method. This method determines how the tessellator interprets/operates on the vertex data</summary>
		public DeclarationMethod DeclarationMethod;
		/// <summary>Defines the intended use of the data</summary>
		public DeclarationUsage DeclarationUsage;
		/// <summary>Modifies the usage data to allow the user to specify multiple usage types</summary>
		public byte UsageIndex;

		/// <summary>
		/// VertexElement - Defines input vertex data to the pipeline
		/// </summary>
		/// <param name="stream">Stream number</param>
		/// <param name="offset">Offset (if any) from the beginning of the stream to the start of the data</param>
		/// <param name="declarationType">One of several predefined types that define the data size</param>
		/// <param name="declarationMethod">Tessellator processing method. This method determines how the tessellator interprets/operates on the vertex data</param>
		/// <param name="declarationUsage">Defines the intended use of the data</param>
		/// <param name="usageIndex">Modifies the usage data to allow the user to specify multiple usage types</param>
		public VertexElement(short stream, short offset, DeclarationType declarationType, DeclarationMethod declarationMethod,
			DeclarationUsage declarationUsage, byte usageIndex) {
			Stream = stream; 
			Offset = offset;
			DeclarationType = declarationType;
			DeclarationMethod = declarationMethod;
			DeclarationUsage = declarationUsage;
			UsageIndex = usageIndex;
		}

    /// <summary>
    /// Returns a null <see cref="VertexElement"/>.
    /// </summary>
    public static VertexElement None {
      get {
        return none;
      }
    }
	}
}
