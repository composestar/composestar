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
using System.Drawing;

using Purple.Math;
using Purple.Graphics.Core;
using Purple.Graphics.TwoD;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// The abstract interface for gauge.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para> 
  /// </remarks>
  //=================================================================
  public interface IGuiGauge : IGuiProgressBar {

    /// <summary>
    /// Event that is fired when gauge expires.
    /// </summary>
    event VoidEventHandler GaugeExpired;

    /// <summary>
    /// Returns true if the queue is currently running.
    /// </summary>
    bool IsRunning { get; }

    /// <summary>
    /// Stops the gauge.
    /// </summary>
    void Stop();

    /// <summary>
    /// Starts the gauge.
    /// </summary>
    void Start();

    /// <summary>
    /// Flag that defines if <see cref="IGuiProgressBar"/> increments or decrements.
    /// </summary>
    bool Increment { get; set; }
  }

  //=================================================================
  /// <summary>
  /// Enumeration of all possible button states.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para> 
  /// </remarks>
  //=================================================================
	public class GuiGauge : GuiProgressBar, IGuiGauge
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns true if the queue is currently running.
    /// </summary>
    public bool IsRunning {
      get {
        return isRunning;
      }
    }
    bool isRunning = false;

    /// <summary>
    /// Event that is fired when gauge expires.
    /// </summary>
    public event VoidEventHandler GaugeExpired;

    /// <summary>
    /// Flag that defines if <see cref="GuiGauge"/> increments or decrements.
    /// </summary>
    public bool Increment { 
      get {
        return increment;
      }
      set {
        increment = value;
      }
    }
    bool increment = true;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="GuiGauge"/>.
    /// </summary>
    /// <param name="texture">The texture to use for the gauge.</param>
		public GuiGauge(ITexture2d texture) : base(texture)
		{
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Stops the gauge.
    /// </summary>
    public void Stop() {
      isRunning = false;      
    }

    /// <summary>
    /// Starts the gauge.
    /// </summary>
    public void Start() {
      isRunning = true;
    }
    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public override void OnRender(float deltaTime) {
      if (increment)
        Current += deltaTime;
      else
        Current -= deltaTime;

      base.OnRender(deltaTime);
      if (Current >= Total && isRunning) {
        isRunning = false;
        if (GaugeExpired != null)
          GaugeExpired();
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
