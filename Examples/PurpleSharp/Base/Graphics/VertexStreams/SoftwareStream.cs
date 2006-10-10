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
  /// An abstract class for a software stream, that can't be uploaded to 
  /// the graphics hardware.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public abstract class SoftwareStream : GraphicsStream, IVertexStream
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The default element.
    /// </summary>
    protected static VertexElement defaultElement = 
      new VertexElement(0, 0, DeclarationType.Software,
      DeclarationMethod.Default,
      DeclarationUsage.None, 0);

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
    VertexElement vertexElement = defaultElement;

    /// <summary>
    /// Returns true if the current stream is a <see cref="SoftwareStream"/>.
    /// </summary>
    public override bool IsSoftware { 
      get {
        return true;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Constructor for a <see cref="SoftwareStream"/>.
    /// </summary>
    /// <param name="size">Number of elements.</param>
    public SoftwareStream(int size) : base(size) {
    }

    /// <summary>
    /// Constructor for a <see cref="SoftwareStream"/>.
    /// </summary>
    /// <remarks>
    /// Init has to be called!!!
    /// </remarks>
    public SoftwareStream() : base(){
  }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the used BufferManager.
    /// </summary>
    /// <remarks>
    /// Not used for <see cref="SoftwareStream"/>s.
    /// </remarks>
    public override BufferManager BufferManager { 
      get {
        return null;
      }
    }

    /// <summary>
    /// Type of vertices.
    /// </summary>
    /// Not used for <see cref="SoftwareStream"/>s.
    public override Type Type {
      get {
        return typeof(object);
      }
    }

    /// <summary>
    /// Returns the size in bytes of one array element.
    /// </summary>
    /// Not used for <see cref="SoftwareStream"/>s.
    public override short ElementSize { 
      get {
        return 0;
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
    VertexUnit vertexUnit = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
