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
  /// abstract interface for a scene entity
  /// There are several implementations for entities and it defines the
  /// actual behaviour of the scene node. Currently there is just one entity
  /// per node - this may change.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
  public interface ISceneEntity {
    /// <summary>
    /// Returns the collection of assigned controllers.
    /// </summary>
    EntityControllers Controllers { get; }

    /// <summary>
    /// The <see cref="ISceneNode"/> the entity is attached to.
    /// </summary>
    ISceneNode Node { get; set; }

    /// <summary>
    /// Returns the <see cref="SceneManager"/> for the current entity.
    /// </summary>
    SceneManager Manager { get; }

    /// <summary>
    /// attach an entity Controller
    /// </summary>
    /// <param name="controller">to add</param>
    void Attach(IEntityController controller);

    /// <summary>
    /// detach an entity Controller
    /// </summary>
    /// <param name="controller">to remove</param>
    void Detach(IEntityController controller);

    /// <summary>
    /// update all Controllers
    /// </summary>
    void UpdateControllers();

    /// <summary>
    /// Before controllers can manipulate the entity and before <c>Traverse</c> is called.
    /// </summary>
    /// <remarks>
    /// This is the right time to save the part of the state that gets changed.
    /// </remarks>
    void Before();

    /// <summary>
    /// do preparations - Traverse is called, before entity might get culled!
    /// </summary>
    void Traverse();

    /// <summary>
    /// This method is called when all children were traversed.
    /// </summary>
    /// <remarks>
    /// This is the right time to restore the part of the state that was changed.
    /// </remarks>
    void After();

  }
}
