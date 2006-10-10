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

namespace Purple.Graphics.Effect
{
  //=================================================================
  /// <summary>
  /// Technique class.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// <para>A technique implements a certain effect for one certain 
  /// target hardware. This allows to put more features into the same 
  /// effect for newer hardware, while it also works for older hardware 
  /// (maybe not that nice, and with more required passes).</para>
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
	public class Technique
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Name of technique.
    /// </summary>
    public string Name { 
      get {
        return name;
      }
    }
    string name = "";

    /// <summary>
    /// List of annotations.
    /// </summary>
    public SortedList Annotations { 
      get {
        return annotations;
      }
    }
    SortedList annotations = new SortedList();

    /// <summary>
    /// List of passes.
    /// </summary>
    public Passes Passes { 
      get {
        return passes;
      }
    }
    Passes passes = new Passes();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new technique.
    /// </summary>
    /// <param name="name">Name of the technique.</param>
    public Technique(string name) {
      this.name = name;
    }

    /// <summary>
    /// Creates a new technique.
    /// </summary>
    /// <param name="name">Name of the technique.</param>
    /// <param name="pass">The pass to add.</param>
    public Technique(string name, Pass pass) {
      this.name = name;
      this.passes.Add( pass );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
