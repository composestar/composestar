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

namespace Purple.Actions
{

  //=================================================================
  /// <summary>
  /// Delegate that handles the event fired when an <see cref="IAction"/> is started or finished.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Last change: 0.4</para>
  /// </remarks>
  //=================================================================
  public delegate void ActionHandler(IAction action);

  //=================================================================
  /// <summary>
  /// Delegate that handles the event fired when an <see cref="IAction"/> is started or finished finished.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Last change: 0.4</para>
  /// </remarks>
  //=================================================================
  public delegate void SimpleActionHandler();

  //=================================================================
  /// <summary>
  /// Abstract interface for an action.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  ///   <para>Last change: 0.5</para>
  /// </remarks>
  //=================================================================
	public interface IAction
	{
    
    /// <summary>
    /// Event that is fired when <see cref="IAction"/> gets started.
    /// </summary>
    event ActionHandler Started;

    /// <summary>
    /// Event that is fired when <see cref="IAction"/> get started.
    /// </summary>
    event SimpleActionHandler SimpleStarted;

    /// <summary>
    /// Event that is fired when <see cref="IAction"/> has finished.
    /// </summary>
    event ActionHandler Finished;

    /// <summary>
    /// Event that is fired when <see cref="IAction"/> has finished.
    /// </summary>
    event SimpleActionHandler SimpleFinished;

    /// <summary>
    /// Returns the name of the <see cref="IAction"/> for debugging purposes.
    /// </summary>
    string Name { get; }

    /// <summary>
    /// Returns the total time of the <see cref="IAction"/>.
    /// </summary>
    float TotalTime { get; }

    /// <summary>
    /// Returns the current (passed) time.
    /// </summary>
    float Time { get; }

    /// <summary>
    /// Internal object of the <see cref="IAction"/>.
    /// </summary>
    /// <remarks>
    /// This object can be used to store a reference on the object that should be 
    /// manipulated by the current <see cref="IAction"/> or some internal state.
    /// </remarks>
    object Object { get; }

    /// <summary>
    /// Returns true if the <see cref="IAction"/> is running.
    /// </summary>
    bool IsRunning { get; }

    /// <summary>
    /// Returns true if the <see cref="IAction"/> is finished.
    /// </summary>
    bool IsFinished { get; }

    /// <summary>
    /// Updates the current <see cref="IAction"/>.
    /// </summary>
    /// <param name="deltaTime">Time since the last call of <c>Update</c>."/></param>
    void Update(float deltaTime);

    /// <summary>
    /// Finish the action.
    /// </summary>
    void Finish();

    /// <summary>
    /// Reset the action to its inital state.
    /// </summary>
    void Reset();
	}

  //=================================================================
  /// <summary>
  /// Abstract base class of an <see cref="IAction"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Last change: 0.6</para>
  /// </remarks>
  //=================================================================
  public abstract class ActionBase : IAction {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Event that is fired when <see cref="IAction"/> gets started.
    /// </summary>
    public event ActionHandler Started;

    /// <summary>
    /// Event that is fired when <see cref="IAction"/> get started.
    /// </summary>
    public event SimpleActionHandler SimpleStarted;

    /// <summary>
    /// Event that is fired when <see cref="IAction"/> has finished.
    /// </summary>
    public event ActionHandler Finished;

    /// <summary>
    /// Event that is fired when <see cref="IAction"/> has finished.
    /// </summary>
    public event SimpleActionHandler SimpleFinished;

    /// <summary>
    /// Returns the name of the <see cref="IAction"/> for debugging purposes.
    /// </summary>
    public string Name { 
      get {
        return name;
      }
    }
    string name = "";

    /// <summary>
    /// Returns the current (passed) time.
    /// </summary>
    public float Time { 
      get {
        return time;
      }
    }
    /// <summary>
    /// Set the current time -> change to property in .NET 2.0
    /// </summary>
    /// <param name="time">The new time.</param>
    protected void setTime(float time) {
      this.time = time;
    }
    private float time = 0.0f;

    /// <summary>
    /// Returns the total time of the <see cref="IAction"/>.
    /// </summary>
    public abstract float TotalTime { get; }

    /// <summary>
    /// Returns true if the <see cref="IAction"/> is running.
    /// </summary>
    public bool IsRunning { 
      get {
        return isRunning;
      }
    }
    /// <summary>
    /// Set the internal isRunning variable.
    /// </summary>
    /// <param name="isRunning">New value.</param>
    protected void setIsRunning(bool isRunning) {
      this.isRunning = isRunning;
    }
    private bool isRunning = false;

    /// <summary>
    /// Returns true if the <see cref="IAction"/> is finished.
    /// </summary>
    public bool IsFinished { 
      get {
        return isFinished;
      }
    }
    /// <summary>
    /// Sets the internal value of isFinished. 
    /// </summary>
    /// <param name="isFinished">The new value for isFinished.</param>
    protected void setIsFinished(bool isFinished) {
      this.isFinished = isFinished;
    }
    private bool isFinished = false;

    /// <summary>
    /// Internal object of the <see cref="IAction"/>.
    /// </summary>
    /// <remarks>
    /// This object can be used to store a reference on the object that should be 
    /// manipulated by the current <see cref="IAction"/> or some internal state.
    /// </remarks>
    public object Object {
      get {
        return internalObject;
      }
      set {
        internalObject = value;
      }
    }
    object internalObject = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new <see cref="ActionBase"/> with a given name.
    /// </summary>
    /// <param name="name">Name of <see cref="IAction"/>.</param>
    public ActionBase(string name) {
      this.name = name;
    }

    /// <summary>
    /// Creates a new <see cref="ActionBase"/> with a given name.
    /// </summary>
    /// <param name="name">Name of <see cref="IAction"/>.</param>
    /// <param name="internalObject">The object that is used by the action or some kind of internal state.</param>
    public ActionBase(string name, object internalObject) {
      this.name = name;
      this.internalObject = internalObject;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------	  
    /// <summary>
    /// Finish the action.
    /// </summary>
    public void Finish() {
      if (isRunning && !isFinished) {
        if (time < TotalTime)
          HandleUpdate(1.0f);
        OnFinish();
        isRunning = false;
        isFinished = true;
        if (Finished != null)
          Finished(this);
        if (SimpleFinished != null)
          SimpleFinished();
      }
    }

    /// <summary>
    /// The method that is called on every update.
    /// </summary>
    /// <param name="deltaTime">The time passed since the last update</param>
    protected abstract void HandleUpdate(float deltaTime);

    /// <summary>
    /// Updates the <see cref="IAction"/>.
    /// </summary>
    /// <param name="deltaTime">Time that has passed since the last update.</param>
    public virtual void Update(float deltaTime) {
      // if action is already finished => return
      if (isFinished)
        return;

      // if action is not already running => fire Started event
      if (!isRunning)
        DoStart();
      
      time += deltaTime;
      float t = System.Math.Min(1.0f, time/TotalTime);
      HandleUpdate( t );

      // action finished?
      if (time >= TotalTime)
        Finish();
    }

    /// <summary>
    /// The action finished.
    /// </summary>
    protected abstract void OnFinish();

    /// <summary>
    /// The action started.
    /// </summary>
    protected void DoStart() {
      Reset();
      isRunning = true;
      if (Started != null)
        Started(this);
      if (SimpleStarted != null)
        SimpleStarted();
    }

    /// <summary>
    /// Reset the action to its inital state.
    /// </summary>
    public virtual void Reset() {
      isRunning = false;
      isFinished = false;
      time = 0.0f;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
