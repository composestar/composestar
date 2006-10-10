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

using Purple.Collections;

namespace Purple.Actions
{
  //=================================================================
  /// <summary>
  /// A simple enumeration for aligning a simple action.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>
  /// </remarks>
  //=================================================================
  public enum ActionAlignment {
    /// <summary>
    /// Start
    /// </summary>
    Start,
    /// <summary>
    /// End
    /// </summary>
    End,
    /// <summary>
    /// Centered
    /// </summary>
    Centered
  }

  //=================================================================
  /// <summary>
  /// Class that schedules the execution of certain actions.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>
  /// </remarks>
  //=================================================================
	public class ActionScheduler : ActionBase
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    SortedMultiList list = new SortedMultiList();

    /// <summary>
    /// Returns the total time of the action scheduler.
    /// </summary>
    public override float TotalTime {
      get {
        return totalTime;
      }
    }
    float totalTime = 0.0f;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an <see cref="ActionScheduler"/>.
    /// </summary>
    /// <param name="name">Name of the action.</param>
    public ActionScheduler(string name) : base(name) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
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

      foreach (DictionaryEntry entry in list) {
        float startTime = (float) entry.Key;
        IAction action = (IAction) entry.Value;
        if (startTime <= Time)
          action.Update(deltaTime);
        else
          break;
      }

      if (Time >= TotalTime)
        Finish();
    }

    /// <summary>
    /// Add an <see cref="IAction"/> to the <see cref="ActionScheduler"/>.
    /// </summary>
    /// <param name="action">Action to add.</param>
    /// <param name="time">The time for starting the action.</param>
    public void Add(IAction action, float time) {
      Add(action, time, ActionAlignment.Start);
    }

    /// <summary>
    /// Add an <see cref="IAction"/> to the <see cref="ActionScheduler"/>.
    /// </summary>
    /// <param name="action">The action to add.</param>
    /// <param name="time">The time for starting the action.</param>
    /// <param name="alignment">The alignment of the action.</param>
    public void Add(IAction action, float time, ActionAlignment alignment) {
      float startTime = time;
      switch(alignment) {
        case ActionAlignment.Centered:
          startTime = time - action.TotalTime * 0.5f;
          break;
        case ActionAlignment.End:
          startTime = time - action.TotalTime;
          break;
      }
      list.Add(time, action);
      totalTime = System.Math.Max(totalTime, startTime + action.TotalTime);
    }

    /// <summary>
    /// Finish the action and all its children.
    /// </summary>
    protected override void OnFinish() {
      foreach(DictionaryEntry entry in list)
        (entry.Value as IAction).Finish();
    }

    /// <summary>
    /// Reset the action to its inital state.
    /// </summary>
    public override void Reset() {
      base.Reset();
      foreach(DictionaryEntry entry in list)
        (entry.Value as IAction).Reset();
    }

    /// <summary>
    /// The method that is called on every update.
    /// </summary>
    /// <param name="deltaTime">The time passed since the last update</param>
    protected override void HandleUpdate(float deltaTime) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
