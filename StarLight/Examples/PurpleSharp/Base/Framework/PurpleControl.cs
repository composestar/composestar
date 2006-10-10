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
//   Markus W��
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
using System.Windows.Forms;

namespace Purple.Framework {
  //=================================================================
  /// <summary>
  /// control in which the game will be visualized
  /// usually this control will fill up the whole window
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus W��</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public class PurpleControl : Control {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// control which shows the game screen
    /// </summary>
    public PurpleControl() {	
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Internal Methods
    //---------------------------------------------------------------
    /// <summary>
    /// 
    /// </summary>
    protected override void OnPaint(PaintEventArgs e) {        
      //base.OnPaint(e);
    }

    private void InitializeComponent() {
      this.BackColor = Color.Black;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
