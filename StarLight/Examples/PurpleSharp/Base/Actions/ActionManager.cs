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
  //
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
  //*****************************************************************************
using System;
using System.Collections;

namespace Purple.Actions
{
  //=================================================================
  /// <summary>
  /// Class that handles a certain group of <see cref="IAction"/> objects.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para>
  ///   <para>Last change: 0.6</para>
  /// </remarks>
  //=================================================================
	public class ActionManager
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ArrayList list = new ArrayList();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance <see cref="ActionManager"/>.
    /// </summary>
    public ActionManager() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds an <see cref="IAction"/> to the <see cref="ActionGroup"/>.
    /// </summary>
    /// <param name="action">Action to add.</param>
    public void Add(IAction action) {
      list.Add(action);
    }

    /// <summary>
    /// Removes an <see cref="IAction"/> from the manager without firing the finish event.
    /// </summary>
    /// <param name="action">The action to remove.</param>
    public void Remove(IAction action) {
      list.Remove(action);
    }
 
    /// <summary>
    /// Removes all pending actions from the action manager.
    /// </summary>
    public void Clear() {
      list.Clear();
    }

    /// <summary>
    /// Updates the <see cref="IAction"/>.
    /// </summary>
    /// <param name="deltaTime">Time that has passed since the last update.</param>
    public void Update(float deltaTime) {
      ArrayList cloned = (ArrayList)list.Clone();
      for (int i=0; i<cloned.Count; i++) {
        IAction action = (cloned[i] as IAction);
        action.Update(deltaTime);
        if (action.IsFinished)
          list.Remove(action);
      }
    }

    /// <summary>
    /// Finish the action.
    /// </summary>
    public void Finish() {
      for (int i=0; i<list.Count; i++)
        (list[i] as IAction).Finish();
    }

    /// <summary>
    /// Returns true if the actionManager contains an action with a given name.
    /// </summary>
    /// <param name="name">Name of the action to test for.</param>
    /// <returns>True if the actionManager contains an action with the given name.</returns>
    public bool Contains(string name) {
      for (int i=0; i<list.Count; i++)
        if ((list[i] as IAction).Name == name)
          return true;
      return false;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
