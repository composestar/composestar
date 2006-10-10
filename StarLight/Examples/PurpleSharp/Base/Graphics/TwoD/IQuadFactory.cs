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

using Purple.Graphics.Effect;

namespace Purple.Graphics.TwoD
{
  //=================================================================
  /// <summary>
  /// abstract interface for a quad factory
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
	public interface IQuadFactory
	{
    /// <summary>
    /// Returns the effect used by the factory.
    /// </summary>
    IEffect Effect {get;}

    /// <summary>
    /// the vertex format used for quads of this group
    /// </summary>
    VertexFormat Format {get;}

    /// <summary>
    /// returns the vertexUnit
    /// </summary>
    VertexUnit VertexUnit { get; }
    
    /// <summary>
    /// number of quads that can be hold by factory
    /// </summary>
    int Capacity {get;}

    /// <summary>
    /// The number of vertices that are used.
    /// </summary>
    int Filled { get; set; }

    /// <summary>
    /// resize factory => changes capacity
    /// </summary>
    /// <param name="quadNum"></param>
    void Resize(int quadNum);

    /// <summary>
    /// creates a new quad
    /// </summary>
    /// <returns>a newly created quad</returns>
    IQuad CreateQuad();

    /// <summary>
    /// Fills the vertexUnit with a quad.
    /// </summary>
    /// <param name="quad">The quad to fill vertexUnit with.</param>
    void FillVertexUnit(IQuad quad);

    /// <summary>
    /// Uploads the changes.
    /// </summary>
    void Upload();
	}
}
