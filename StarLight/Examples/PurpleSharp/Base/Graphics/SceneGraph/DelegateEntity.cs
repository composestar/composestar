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
  /// An entity, which delegates the work to another method.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  ///   <para>Last change: 0.3</para>
  /// </remarks>
  //=================================================================
  public class DelegateEntity : SceneEntity, ISceneEntity {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    /// <summary>
    /// The event get fired before the entity gets traversed.
    /// </summary>
    /// <remarks>
    /// This is the right time to save the part of the state that gets changed.
    /// </remarks>
    public event VoidEventHandler OnBefore;

    /// <summary>
    /// When entity gets traversed, the <c>OnTraverse</c> EventHandler gets fired.
    /// </summary>
    public event VoidEventHandler OnTraverse;

    /// <summary>
    /// This event gets fired after all children were traversed.
    /// </summary>
    /// <remarks>
    /// This is the right time to restore the part of the state that was changed.
    /// </remarks>
    public event VoidEventHandler OnAfter;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Crates an instance of a <see cref="DelegateEntity"/>.
    /// </summary>
    public DelegateEntity(VoidEventHandler eventHandler) {
      OnTraverse += eventHandler;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Before controllers can manipulate the entity and before <c>Traverse</c> is called.
    /// </summary>
    /// <remarks>
    /// This is the right time to save the part of the state that gets changed.
    /// </remarks>
    public void Before() {
      if (OnBefore != null)
        OnBefore();
    }
    /// <summary>
    /// Do preparations - Traverse is called, before entity might get culled!
    /// </summary>
    public void Traverse() {
      if (OnTraverse != null)
        OnTraverse();
    }

    /// <summary>
    /// This method is called when all children were traversed.
    /// </summary>
    /// <remarks>
    /// This is the right time to restore the part of the state that was changed.
    /// </remarks>
    public void After() {
      if (OnAfter != null)
        OnAfter();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}