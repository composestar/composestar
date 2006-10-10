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

namespace Purple.Graphics.SceneGraph
{
  //=================================================================
  /// <summary>
  /// abstract interface for a scene node
  /// A scene node handles children, parents, traversal, bounding volumes
  /// and such stuff
  /// It contains an ISceneEntity object which defines the behaviour of the current node
  /// Every scene node hast just one parent, but it is possible that several 
  /// scene nodes share one ISceneEntity.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
	public interface ISceneNode
	{
    /// <summary>
    /// set parent scene node
    /// </summary>
    ISceneNode Parent {get; set;}

    /// <summary>
    /// Returns the <see cref="SceneManager"/> object.
    /// </summary>
    SceneManager Manager {get; set; }

    /// <summary>
    /// the core of the scene node
    /// the entity defines the behaviour of the scene node
    /// </summary>
    ISceneEntity Entity {get; set;}

    /// <summary>
    /// the bounding box for this subtree
    /// </summary>
    AABB BoundingBox { get; set; }
  
    /// <summary>
    /// test if node is currently attached to another node
    /// </summary>
    /// <returns></returns>
    bool IsAttached();

    /// <summary>
    /// get array of children
    /// </summary>
    SceneNodes Children { get; }

    /// <summary>
    /// Returns true if the <see cref="ISceneNode"/> contains children.
    /// </summary>
    bool HasChildren { get; }

    /// <summary>
    /// traverses the current node
    /// </summary>
    void Traverse();

    /// <summary>
    /// attaches a new child scene node
    /// the nodes parent property is set automatically
    /// </summary>
    /// <param name="node">node to add</param>
    /// <returns>the added sceneNode</returns>
    ISceneNode Attach(ISceneNode node);

    /// <summary>
    /// detaches an existing child node
    /// </summary>
    /// <param name="node">node to remove</param>
    void Detach(ISceneNode node);
	}
}
