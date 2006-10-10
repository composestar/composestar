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
using System.CodeDom.Compiler;

namespace Purple.Scripting {
  //=================================================================
  /// <summary>
  /// exception which is thrown when compilation errors occured
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public class CompilerErrorException : Exception {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private CompilerErrorCollection errors;
    private string className;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// get the errors in form of a collection - this way detailed information can
    /// be read out about the occured errors
    /// </summary>
    public CompilerErrorCollection CompilerErrors {
      get {
        return errors;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// scripting exception taking className and collection of CompilerErrors as parameters
    /// </summary>
    /// <param name="className">name of compiled class</param>
    /// <param name="errors">collection of CompilerErrors</param>
    public CompilerErrorException(string className, CompilerErrorCollection errors) {
      this.className = className;
      this.errors = errors;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// get CompilerErrors in form of a string (similar to C#.NET way)
    /// </summary>
    /// <returns>string describing errors</returns>
    public override string ToString() {
      string errorString = "";
      foreach (CompilerError error in errors) {
        errorString += className + "(" + error.Line + "," + error.Column + 
          ") - " + error.ErrorNumber + ": " + error.ErrorText + "\n";
      }
      return(errorString);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
