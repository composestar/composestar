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
using Purple.Graphics.States;

namespace Purple.Graphics.Effect
{
  //=================================================================
  /// <summary>
  /// Class for an effect pass.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// <para>An <see cref="Pass"/> represents one render pass of a 
  /// certain <see cref="Technique"/>. While simple effects just need 
  /// one render pass, more complex effects (especially on older hardware) 
  /// need multiple render passes.</para>
  /// </remarks>
  //=================================================================
	public class Pass
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Name of the current pass.
    /// </summary>
    public string Name { 
      get {
        return name;
      }
    }
    string name = "";

    /// <summary>
    /// Annotations for current pass.
    /// </summary>
    public SortedList Annotations { 
      get {
        return annotations;
      }
    }
    SortedList annotations = new SortedList();

    /// <summary>
    /// The vertex shader that is used for the current pass.
    /// </summary>
    public IVertexShader VertexShader {
      get {
        return vertexShader;
      }
      set {
        vertexShader = value;
      }
    }
    IVertexShader vertexShader;

    /// <summary>
    /// The pixelShader to use for the current pass.
    /// </summary>
    public IPixelShader PixelShader {
      get {
        return pixelShader;
      }
      set {
        pixelShader = value;
      }
    }
    IPixelShader pixelShader;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new pass.
    /// </summary>
    /// <param name="name">Name of the pass.</param>
    public Pass(string name) {
      this.name = name;
    }

    /// <summary>
    /// Creates a new pass with a vertex and a pixel shader.
    /// </summary>
    /// <param name="name">Name of the pass.</param>
    /// <param name="vertexShader">VertexShader to use for pass.</param>
    /// <param name="pixelShader">PixelShader to use for pass.</param>
    public Pass(string name, IVertexShader vertexShader, IPixelShader pixelShader) {
      this.name = name;
      this.vertexShader = vertexShader;
      this.pixelShader = pixelShader;      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
