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
using Purple.Graphics.Core;

namespace Purple.Graphics
{
  //=================================================================
  /// <summary>
  /// A collection for storing <see cref="DisplayMode"/> objects.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
	public class DisplayModes : Purple.Collections.CollectionBase
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ArrayList displayModes = new ArrayList();

    /// <summary>
    /// Returns the <see cref="DisplayMode"/> for a certain index.
    /// </summary>
    public DisplayMode this[int index] {
      get {
        return (DisplayMode)displayModes[index];
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of <see cref="DisplayModes"/>.
    /// </summary>
		public DisplayModes()
		{
      this.collection = displayModes;
		}

    private DisplayModes(ArrayList list) {
      displayModes = list;
      this.collection = list;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Returns a suiting <see cref="DisplayMode"/> or null if not supported 
    /// by device.
    /// </summary>
    /// <param name="width">Width in pixels of the desired mode.</param>
    /// <param name="height">Height in pixels of the desired mode.</param>
    /// <param name="bpp">Color depth in bits per pixel of the desired mode.</param>
    /// <param name="refreshRate">The desired refresh rate. If no such refresh rate was found but a 
    /// displayMode exists, another displayMode is returned. A refreshRate of 0 means the refresh rate 
    /// doesn't matter.</param>
    /// <returns>A suiting <see cref="DisplayMode"/> or null if not supported 
    /// by device.</returns>
    public DisplayMode Find(int width, int height, int bpp, int refreshRate) {
      int index = FindIndex(width, height, bpp, refreshRate);
      if (index == -1)
        return DisplayMode.None;
      return (DisplayMode)displayModes[index];
    }

    private int FindIndex(int width, int height, int bpp, int refreshRate) {
      int refreshRateIndependent = -1;
      for (int i=0; i<collection.Count; i++) {
        DisplayMode dm = (DisplayMode)displayModes[i];
        if (dm.BitsPerPixel == bpp && dm.Width == width && dm.Height == height) {
          if (refreshRate == 0 || refreshRate == dm.RefreshRate)
            return i;
          else
            refreshRateIndependent = i;
        }
      }
      return refreshRateIndependent;
    }

    /// <summary>
    /// Adds a new <see cref="DisplayMode"/> to the list.
    /// </summary>
    /// <param name="displayMode">Mode to add.</param>
    public void Add(DisplayMode displayMode) {
      displayModes.Add(displayMode);
    }

    /// <summary>
    /// Just keeps the displaymodes with the highest frameRates.
    /// </summary>
    public DisplayModes HighestRefreshRates() {
      DisplayModes newModes = new DisplayModes();
      foreach (DisplayMode mode in displayModes) {
        int index = newModes.FindIndex( mode.Width, mode.Height, mode.BitsPerPixel, 0);
        if (index == -1)
          newModes.Add(mode);
        else if (mode.RefreshRate > newModes[index].RefreshRate)
          newModes.displayModes[index] = mode;
      }
      return newModes;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
