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
using System.Runtime.InteropServices;

namespace Purple.Tools
{
  //=================================================================
  /// <summary>
  /// Dll helper stuff.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
	public class Dll
	{
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Tests if a native dll with a certain name exists.
    /// </summary>
    /// <param name="name">Name of dll to test for.</param>
    /// <returns>True if the dll exits.</returns>
    public static bool Exists(string name) { 
      IntPtr ptr = LoadLibrary(name);
      if (ptr != IntPtr.Zero) {
        FreeLibrary(ptr);
        return true;
      }
      return false;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Kernel32
    //---------------------------------------------------------------
    /// <summary>
    /// Loads a native dll.
    /// </summary>
    /// <param name="fileName">Dll to load.</param>
    /// <returns>Returns the handle to the dll or IntPtr.Zero.</returns>
    [DllImport("Kernel32.dll")]
    private extern static IntPtr LoadLibrary(string fileName);

    /// <summary>
    /// Unloads the native dll.
    /// </summary>
    /// <param name="library">The dll to unload.</param>
    /// <returns>True if unloading succeeded.</returns>
    [DllImport("Kernel32.dll")]
    private extern static bool FreeLibrary(IntPtr library);
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
