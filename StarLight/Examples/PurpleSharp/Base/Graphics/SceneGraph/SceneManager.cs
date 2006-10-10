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

using Purple.Graphics.Geometry;

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// Manager class for scene graphs.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class SceneManager {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private RenderList renderList = new RenderList();
    private SceneNodes stack = new SceneNodes();
    private static SceneManager instance;
    private SceneState currentState = SceneState.Default;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Provides access to the current <see cref="SceneState"/>.
    /// </summary>
    public SceneState CurrentState {
      get {
        return currentState;
      }
      set {
        currentState = value;
      }
    }

    /// <summary>
    /// Provides access to the current <see cref="ISceneNode"/>.
    /// </summary>
    public ISceneNode Current {
      get {
        if (stack.Count == 0)
          return null;
        return stack.Last;
      }
    }

    /// <summary>
    /// Provides access to the current <see cref="ISceneEntity"/>.
    /// </summary>
    public ISceneEntity CurrentEntity {
      get {
        return Current.Entity;
      }
      set {
        Current.Entity = value;
      }
    }

    /// <summary>
    /// Provides access to the current root node.
    /// </summary>
    /// <remarks>
    /// If the root <see cref="ISceneNode"/> is set, the current node is set to the root.
    /// </remarks>
    public ISceneNode Root {
      get {
        return stack.First;
      }
      set {
        stack.Clear();
        stack.Add(value);
      }
    }

    /// <summary>
    /// Returns the default scene manager.
    /// </summary>
    public static SceneManager Instance {
      get {
        if (instance == null)
          instance = new SceneManager();
        return instance;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new <see cref="SceneManager"/> instance.
    /// </summary>
    public SceneManager() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Attach a scene node to the current node.
    /// </summary>
    /// <param name="node">Node to attach.</param>
    public void Attach(ISceneNode node) {
      if (Current != null)
        Current.Attach( node );
      node.Manager = this;
      stack.Add(node);
    }

    /// <summary>
    /// Creates a standard scene node and attachs an entity.
    /// </summary>
    /// <param name="entity">Entity to attach.</param>
    public void AttachNode(ISceneEntity entity) {
      Attach(new SceneNode( entity ) );
    }

    /// <summary>
    /// Moves back to the last node.
    /// </summary>
    public void Back() {
      stack.Pop();
    }

    /// <summary>
    /// Moves back a certain number of nodes.
    /// </summary>
    /// <param name="number">Number of nodes to move back.</param>
    public void Back(int number) {
      stack.Pop(number);
    }

    /// <summary>
    /// Clears the whole <see cref="SceneGraph"/>.
    /// </summary>
    public void Clear() {
      stack.Clear();
    }

    /// <summary>
    /// Renders the whole scene graph.
    /// </summary>
    public void Render() {
      if (Root != null)
        Render(Root);
    }

    /// <summary>
    /// Renders a certain subgraph.
    /// </summary>
    /// <remarks>
    /// <param name="rootNode">The node, that is used as root <see cref="ISceneNode"/> 
    /// of the subgraph.</param>
    /// </remarks>
    public void Render(ISceneNode rootNode) {
      // init stuff
      renderList.Clear();
      currentState.World = Math.Matrix4.Identity;

      // traverse tree, do culling and register nodes for drawing
      rootNode.Traverse();

      // sort nodes to minimize state changes

      // draw the stuff
      //Device device = Device.Instance;
      //device.Clear(System.Drawing.Color.Black);
      //device.BeginScene();		

      for (int i=0; i<renderList.Count; i++)
        renderList[i].Apply();

      //device.EndScene();      	
      //device.Present();	
    }

    /// <summary>
    /// Registers a <see cref="SubSet"/> for rendering.
    /// </summary>
    /// <param name="subSet"><see cref="SubSet"/> to add for rendering</param>
    public void RegisterForRendering(SubSet subSet) {
      renderList.Add( subSet, CurrentState );
    }

    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
