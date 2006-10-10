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
  /// implementation for a scene node
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
	public class SceneNode : ISceneNode
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    SceneNodes nodes = null;
    ISceneNode parent;
    ISceneEntity entity;
    AABB boundingBox;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// create an instance of a scene node
    /// </summary>
    /// <param name="entity">entity to use for this scene node</param>
    public SceneNode(ISceneEntity entity) {
      this.entity = entity;
      entity.Node = this;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region ISceneNode stuff
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the <see cref="SceneManager"/> object.
    /// </summary>
    public SceneManager Manager {
      get {
        return sceneManager;
      }
      set {
        sceneManager = value;
      }
    }
    SceneManager sceneManager;

    /// <summary>
    /// the bounding box for this subtree
    /// </summary>
    public AABB BoundingBox { 
      get {
        return boundingBox;
      }
      set {
        boundingBox = value;
      }
    }

    /// <summary>
    /// the core of the scene node
    /// the entity defines the behaviour of the scene node
    /// </summary>
    public ISceneEntity Entity {
      get {
        return entity;
      }
      set {
        entity = value;
      }
    }

    /// <summary>
    /// Returns true if the <see cref="ISceneNode"/> contains children.
    /// </summary>
    public bool HasChildren { 
      get {
        return nodes != null && nodes.Count != 0;
      }
    }

    /// <summary>
    /// set parent scene node
    /// </summary>
    public ISceneNode Parent {
      get {
        return parent;
      }
      set {
        parent = value;
      }
    }

    /// <summary>
    /// get array of children
    /// may return null if no scene node doesn't has children
    /// </summary>
    public SceneNodes Children { 
      get {
        return nodes;
      }
    }

    /// <summary>
    /// traverses current node
    /// </summary>
    public void Traverse() {
      entity.Before();
      entity.UpdateControllers();
      entity.Traverse();
      // Todo: Culling
      TraverseChildren();
      entity.After();
    }

    /// <summary>
    /// calls draw for all children
    /// </summary>
    public virtual void TraverseChildren() {
      if (nodes == null)
        return;
      for (int i=0; i<nodes.Count; i++)
        nodes[i].Traverse();
    }

    /// <summary>
    /// adds a new child scene node
    /// the nodes parent property is set automatically
    /// </summary>
    /// <param name="node">node to add</param>
    /// <returns>the added node</returns>
    public ISceneNode Attach(ISceneNode node) {
      if (node.IsAttached())
        throw new GraphicsException("Node is already attached to a root node! Detach first!");
      if (nodes == null)
        nodes = new SceneNodes();
      nodes.Add(node);
      node.Parent = this;
      node.Manager = Manager;
      return node;
    }

    /// <summary>
    /// detaches an existing child node
    /// </summary>
    /// <param name="node">not to remove</param>
    public void Detach(ISceneNode node) {
      nodes.Remove(node);
      node.Parent = null;
    }

    /// <summary>
    /// test if node is currently attached to another node
    /// </summary>
    /// <returns></returns>
    public bool IsAttached() {
      return parent != null;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
