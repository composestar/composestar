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

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// standard implementation for some scene entity methods
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last change: 0.3</para>
  /// </remarks>
  //=================================================================
  public abstract class SceneEntity {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the collection of assigned controllers.
    /// </summary>
    public EntityControllers Controllers {
      get {
        return controllers;
      }
    }
    EntityControllers controllers = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor for a scene entity
    /// </summary>
    public SceneEntity() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region ISceneEntity stuff
    //---------------------------------------------------------------
    /// <summary>
    /// attach an entity Controller
    /// </summary>
    /// <param name="controller">to add</param>
    public void Attach(IEntityController controller) {
      if (controller.Entity != null)
        throw new Exception("Controller is already connected to a scene entity!");
      controller.Entity = (ISceneEntity)this;
      if (controllers == null)
        controllers = new EntityControllers();
      controllers.Add(controller);
    }

    /// <summary>
    /// detach an entity Controller
    /// </summary>
    /// <param name="controller">to remove</param>
    public void Detach(IEntityController controller) {
      controllers.Remove(controller);
      controller.Entity = null;
    }

    /// <summary>
    /// The <see cref="ISceneNode"/> the entity is attached to.
    /// </summary>
    public ISceneNode Node { 
      get {
        return node;
      }
      set {
        node = value;
      }
    }
    ISceneNode node;

    /// <summary>
    /// Returns the <see cref="SceneManager"/> for the current entity.
    /// </summary>
    public SceneManager Manager {
      get {
        if (node == null)
          return SceneManager.Instance;
        return node.Manager;
      }
    }

    /// <summary>
    /// update all Controllers
    /// </summary>
    public void UpdateControllers() {
      if (Controllers == null)
        return;
      for (int i=0; i<Controllers.Count; i++)
        Controllers[i].Do();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
