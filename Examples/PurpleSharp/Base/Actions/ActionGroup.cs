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

namespace Purple.Actions
{
  //=================================================================
  /// <summary>
  /// An ActionGroup is formed by a group of actions executed in parallel
  /// order.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  ///   <para>Last change: 0.5</para>
  /// </remarks>
  //=================================================================
	public class ActionGroup : ActionBase
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ArrayList list = new ArrayList();

    /// <summary>
    /// Returns the total time of the <see cref="IAction"/>.
    /// </summary>
    public override float TotalTime {
      get {
        // iterate over all actions and return maximum time
        float curTime = 0;
        for (int i=0; i<list.Count; i++) {
          if ((list[i] as IAction).TotalTime > curTime)
            curTime = (list[i] as IAction).TotalTime;
        }
        return curTime;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
		/// <summary>
		/// Creates a new instance of an <see cref="ActionGroup"/>.
		/// </summary>
		/// <param name="name">Name of the <see cref="IAction"/>.</param>
    public ActionGroup(string name) : base(name)
		{
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
    /// Updates the <see cref="IAction"/>.
    /// </summary>
    /// <param name="deltaTime">Time that has passed since the last update.</param>
    public override void Update(float deltaTime) {
      // if action is already finished => return
      if (IsFinished)
        return;

      // if action is not already running => fire Started event
      if (!IsRunning)
        DoStart();

      setTime(Time + deltaTime);
      ArrayList cloned = (ArrayList)list.Clone();
      for (int i=0; i<cloned.Count; i++) {
        IAction action = (cloned[i] as IAction);
        action.Update(deltaTime);
      }
      if (Time >= TotalTime)
        Finish();
    }

    /// <summary>
    /// Reset the action to its inital state.
    /// </summary>
    public override void Reset() {
      base.Reset();
      for (int i=0; i<list.Count; i++) {
        IAction action = (list[i] as IAction);
        action.Reset();
      }
    }

    /// <summary>
    /// The method that is called on every update.
    /// </summary>
    /// <param name="t">The time passed since the last update</param>
    protected override void HandleUpdate(float t) {
    }

    /// <summary>
    /// Finish the action.
    /// </summary>
    protected override void OnFinish() {
      for (int i=0; i<list.Count; i++)
        (list[i] as IAction).Finish();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
