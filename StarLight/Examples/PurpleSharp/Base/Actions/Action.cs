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

namespace Purple.Actions {
  //=================================================================
  /// <summary>
  /// Delegate that handles the event fired when an <see cref="IAction"/> gets updated.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para>
  ///   <para>Update: 0.5</para>
  /// </remarks>
  //=================================================================
  public delegate void ActionUpdateHandler(IAction action, float t);

  //=================================================================
  /// <summary>
  /// Delegate that handles the event fired when an <see cref="IAction"/> gets updated.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Last change: 0.4</para>
  /// </remarks>
  //=================================================================
  public delegate void SimpleActionUpdateHandler(float t);

  //=================================================================
  /// <summary>
  /// A simple generic action that uses a delegate for performing the 
  /// actual action.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Last change: 0.4</para>
  /// </remarks>
  //=================================================================
	public class Action : ActionBase
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Event that is fired when <see cref="Action"/> should be updated.
    /// </summary>
    public event SimpleActionUpdateHandler OnSimpleUpdate;

    /// <summary>
    /// Event that is fired when <see cref="IAction"/> should be updated.
    /// </summary>
    public event ActionUpdateHandler OnUpdate;

    /// <summary>
    /// Returns the total time of the <see cref="IAction"/>.
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
    /// Creates a new generic <see cref="IAction"/>.
    /// </summary>
    /// <param name="name">Name of the action.</param>
    /// <param name="totalTime">The total time of the <see cref="IAction"/>.</param>
		public Action(string name, float totalTime) : base(name)
		{
      this.totalTime = totalTime;
		}

    /// <summary>
    /// Creates a new generic <see cref="IAction"/>.
    /// </summary>
    /// <param name="name">Name of the action.</param>
    /// <param name="totalTime">The total time of the <see cref="IAction"/>.</param>
    /// <param name="internalObject">The object that is used by the action or some kind of internal state.</param>
    public Action(string name, float totalTime, object internalObject) : base(name, internalObject) {
      this.totalTime = totalTime;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// The method that is called on every update.
    /// </summary>
    /// <param name="t">The time in the range of [0..1].</param>
    protected override void HandleUpdate(float t) {
      if (OnUpdate != null)
        OnUpdate(this, t);
      if (OnSimpleUpdate != null)
        OnSimpleUpdate(t);
    }

    /// <summary>
    /// Finishe the current action.
    /// </summary>
    /// <remarks>
    /// The actions time is moved to one. Then it is updated and the the FinishedEvent 
    /// is fired.
    /// </remarks>
    protected override void OnFinish() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
