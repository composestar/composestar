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

namespace Purple.Tools {
  //=================================================================
  /// <summary>
  /// Purple Uniform Ressource Locator
  /// Helper class to work with Purple URLs like Root.Graphics.Resolution  
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public class PURL {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    string url;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// initializes Purple Uniform Resource Locater with a given url
    /// </summary>
    /// <param name="url"></param>
    public PURL(string url) {      
      this.url = url;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// if key consists of more than one keys
    /// e.g. Root.Graphics.Resolution
    /// </summary>    
    /// <returns>true if it is a complex PURL</returns>
    public bool IsComplex() {
      return(url.IndexOf('.') != -1);
    }

    /// <summary>
    /// returns first part of complex PURL
    /// </summary>    
    /// <returns>first part of complex key</returns>
    public string GetFirstPart() {
      if (!IsComplex())
        return url;
      int index = url.IndexOf('.');
      return url.Substring(0, index);
    }

    /// <summary>
    /// returns the PURL without the first part
    /// </summary>
    /// <returns>PURL without the first part</returns>
    public string GetWithoutFirstPart() {
      if (!IsComplex())
        return "";
      int index = url.IndexOf('.');
      return url.Substring(index+1);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
