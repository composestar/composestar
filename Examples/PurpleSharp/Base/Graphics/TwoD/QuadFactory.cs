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

using Purple.Graphics.VertexStreams;
using Purple.Graphics.Core;
using Purple.Graphics.Effect;

namespace Purple.Graphics.TwoD
{
  //=================================================================
  /// <summary>
  /// A quad factory.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
	public class QuadFactory : IQuadFactory, IMeshQuadFactory
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    static QuadFactory instance = null;

    /// <summary>
    /// Returns the effect used by the factory.
    /// </summary>
    public IEffect Effect { 
      get {
        return fx;
      }
    }
    IEffect fx;

    /// <summary>
    /// The vertex format used for quads of this group.
    /// </summary>
    public VertexFormat Format {
      get {
        return format;
      }
    }
    VertexFormat format;

    /// <summary>
    /// Returns the vertexUnit.
    /// </summary>
    public VertexUnit VertexUnit {
      get {
        return vertexUnit;
      }
    }
    VertexUnit vertexUnit = null;
    
    /// <summary>
    /// Number of vertices that can be hold by the factory.
    /// </summary>
    public int Capacity {
      get {
        return vertexUnit.Size;
      }
    }

    /// <summary>
    /// The number of vertices that are used.
    /// </summary>
    public int Filled {
      get {
        return filled;
      }
      set {
        filled = 0;
      }
    }
    int filled = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
		/// <summary>
		/// Creates a new QuadFactory.
		/// </summary>
		/// <param name="vertexNum">Number of vertices for this group.</param>
    private QuadFactory(int vertexNum)
		{
      this.format = new VertexFormat( 
        new Type[]{ typeof(PositionStream2), typeof(ColorStream), typeof(TextureStream) } );
      Resize(vertexNum);

      LoadShader();
		}

    /// <summary>
    /// Returns the default instance of the QuadFactory.
    /// </summary>
    public static QuadFactory Instance {
      get {
        if (instance == null)
          instance = new QuadFactory(256);
        return instance;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private void LoadShader() {
      // load shaders from the resource files
      using (System.IO.Stream fxStream = Purple.IO.ResourceFileSystem.Instance.Open("Purple/Graphics/TwoD/2d.fx")) {
        fx = EffectCompiler.Instance.Compile(fxStream);
      }
    }
    
    /// <summary>
    /// Resize buffers.
    /// </summary>
    /// <param name="vertexNum">The number of vertices that can be hold by the factory.</param>
    public void Resize(int vertexNum) {
      if (vertexUnit == null)
        vertexUnit = new VertexUnit(format, vertexNum);
      else
        vertexUnit.Resize(vertexNum);
    }

    /// <summary>
    /// Creates a new quad.
    /// </summary>
    /// <returns>The created quad.</returns>
    public IQuad CreateQuad() {
      return new Quad(this);
    }

    /// <summary>
    /// Creates a IMeshQuad.
    /// </summary>
    /// <param name="columns">The number of vertices in every row.</param>
    /// <param name="rows">The number of vertices in every column.</param>
    /// <returns>The creates meshQuad.</returns>
    public IMeshQuad CreateMeshQuad(int columns, int rows) {
      return new MeshQuad(columns, rows, this);
    }

    /// <summary>
    /// Fills the vertexUnit with a quad.
    /// </summary>
    /// <param name="quad">The quad to fill vertexUnit with.</param>
    public void FillVertexUnit(IQuad quad) {
      if (Filled + quad.VertexCount > Capacity)
        Resize( System.Math.Max(Capacity*2, Filled + quad.VertexCount) );
      quad.FillVertexUnit(VertexUnit, Filled);
      filled += quad.VertexCount;
    }

    /// <summary>
    /// Uploads the changes.
    /// </summary>
    public void Upload() {
      vertexUnit.Upload(0, filled);
      filled = 0;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
