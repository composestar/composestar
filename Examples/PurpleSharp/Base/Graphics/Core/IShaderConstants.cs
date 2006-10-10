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

namespace Purple.Graphics.Core {
	//=================================================================
	/// <summary>
	/// abstract interface for Shader constant registers
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para> 
	///   <para>Last Update: 0.7</para> 
	/// </remarks>
	//=================================================================
	public interface IShaderConstants {
		/// <summary>
		/// Sets an array of floats.
		/// </summary>
		/// <param name="startRegister">The register to start with.</param>
		/// <param name="floats">The array of floats.</param>
		void Set(int startRegister, float[] floats);

    /// <summary>
    /// Sets an array of ints.
    /// </summary>
    /// <param name="startRegister">The register to start with.</param>
    /// <param name="ints">The array of ints.</param>
    void Set(int startRegister, int[] ints);

    /// <summary>
    /// Sets an array of bools.
    /// </summary>
    /// <param name="startRegister">The register to start with.</param>
    /// <param name="bools">The array of bools.</param>
    void Set(int startRegister, bool[] bools);
  }

	//=================================================================
	/// <summary>
	/// abstract interface for vertexShader constant registers
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public interface IVertexShaderConstants : IShaderConstants {
	}

	//=================================================================
	/// <summary>
	/// abstract interface for vertexShader constant registers
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public interface IPixelShaderConstants : IShaderConstants {
	}
}
