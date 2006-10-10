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
using System.Reflection;
using System.Collections;

using Purple.Graphics.VertexStreams;

namespace Purple.Graphics
{
  //=================================================================
  /// <summary>
  /// The <see cref="StreamFactory"/> can be used to create <see cref="IGraphicsStream"/> 
  /// objects via type or name.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public class StreamFactory
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    static StreamFactory instance;
    SortedList streamNames = new SortedList();
    SortedList vertexElements = new SortedList( new Purple.Tools.TypeComparer() );
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    private StreamFactory() {
    }

    /// <summary>
    /// Returns the singleton instance of a <see cref="StreamFactory"/>.
    /// </summary>
    public static StreamFactory Instance {
      get {
        if (instance == null) {
          instance = new StreamFactory();
          instance.Bind();
        }
        return instance;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private void Bind() {     
      BinormalStream.Bind();
      BoneIndicesStream.Bind();
      BoneWeightsStream.Bind();
      SoftwareBoneIndicesStream.Bind();
      SoftwareBoneWeightsStream.Bind();
      ColorStream.Bind();
      CompressedNormalStream.Bind();
      FloatStream.Bind();
      IndexStream16.Bind();
      IndexStream32.Bind();
      IntStream.Bind();
      NormalStream.Bind();
      PositionStream.Bind();
      PositionStream2.Bind();
      PositionStream4.Bind();
      TangentStream.Bind();
      TextureStream.Bind();
    }
    
    /// <summary>
    /// Binds a certain name of a <see cref="IGraphicsStream"/> to its type.
    /// </summary>
    /// <param name="name">Name of <see cref="IGraphicsStream"/>.</param>
    /// <param name="type">The type of the <see cref="IGraphicsStream"/>.</param>
    /// <param name="element">The default <see cref="VertexElement"/> of the stream.</param>
    public void Bind(string name, Type type, VertexElement element) {
      streamNames.Add(name, type);
      vertexElements.Add(type, element);
    }

    /// <summary>
    /// Converts the <see cref="IGraphicsStream"/> name to the type.
    /// </summary>
    /// <param name="name">Name of <see cref="IGraphicsStream"/>.</param>
    /// <returns>Type of <see cref="IGraphicsStream"/>.</returns>
    public Type ToType(string name) {
      if (!streamNames.Contains(name))
        return null;
      return (Type)streamNames[name];
    }

    /// <summary>
    /// Returns the <see cref="VertexElement"/> of an <see cref="IGraphicsStream"/> 
    /// specified by the type.
    /// </summary>
    /// <param name="type">Type of <see cref="IGraphicsStream"/>.</param>
    /// <returns>The <see cref="VertexElement"/> of an <see cref="IGraphicsStream"/> 
    /// specified by the type.</returns>
    public VertexElement VertexElement(Type type) {
      if (!vertexElements.Contains(type))
        throw new ArgumentException("type");
      return (VertexElement)vertexElements[type];
    }

    /// <summary>
    /// Creates a new instance of a certain <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <param name="name">The name of the <see cref="IGraphicsStream"/>.</param>
    /// <param name="size">The initial size of the <see cref="IGraphicsStream"/>.</param>
    /// <returns>A new instance of a certain <see cref="IGraphicsStream"/>.</returns>
    public IGraphicsStream Create(string name, int size) {
      return Create( ToType(name), size);
    }

    /// <summary>
    /// Creates a new instance of a certain <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <param name="type">The type of the <see cref="IGraphicsStream"/>.</param>
    /// <param name="size">The initial size of the <see cref="IGraphicsStream"/>.</param>
    /// <returns>A new instance of a certain <see cref="IGraphicsStream"/>.</returns>
    public IGraphicsStream Create(Type type, int size) {
      return (IGraphicsStream)Activator.CreateInstance(type, new object[]{size});
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
