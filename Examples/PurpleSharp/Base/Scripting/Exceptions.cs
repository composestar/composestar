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

namespace Purple.Scripting
{
  //=================================================================
  /// <summary>
  /// an abstract base class for Scripting based exception
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  /// </remarks>
  //=================================================================
  public class ScriptingException : Exception {
    /// <summary>
    /// name of the file, where the error occured
    /// </summary>
    public string FileName {
      get {
        return fileName;
      }
    }
    string fileName;

    /// <summary>
    /// number of the line, where the error occured
    /// </summary>
    public int Line {
      get {
        return line;
      }
    }
    int line;

    /// <summary>
    /// number of the column, where the error occured
    /// </summary>
    public int Column {
      get {
        return column;
      }
    }
    int column;

    /// <summary>
    /// error description
    /// </summary>
    public string Description {
      get {
        return description;
      }
    }
    string description;

    /// <summary>
    /// creates a new exception object
    /// </summary>
    /// <param name="fileName">name of the file, where the error occured</param>
    /// <param name="line">number of the line, where the error occured</param>
    /// <param name="column">number of the column, where the error occured</param>
    /// <param name="description">error description</param>
    public ScriptingException(string fileName, int line, int column, string description) :
      base( fileName + "(" + line + "," + column + "): " + description) {
      this.fileName = fileName;
      this.line = line;
      this.column = column;
      this.description = description;
    }
  }

  //=================================================================
  /// <summary>
  /// a exception which is thrown in case of symbolical errors
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
  public class ScannerException : ScriptingException {
    /// <summary>
    /// creates a new exception object
    /// </summary>
    /// <param name="fileName">name of the file, where the error occured</param>
    /// <param name="line">number of the line, where the error occured</param>
    /// <param name="column">number of the column, where the error occured</param>
    /// <param name="description">error description</param>
    public ScannerException(string fileName, int line, int column, string description) :
      base( fileName, line, column, description) {
    }
  }

  //=================================================================
  /// <summary>
  /// a exception which is thrown in case of syntax errors
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
  public class ParserException : ScriptingException {
    /// <summary>
    /// creates a new exception object
    /// </summary>
    /// <param name="fileName">name of the file, where the error occured</param>
    /// <param name="line">number of the line, where the error occured</param>
    /// <param name="column">number of the column, where the error occured</param>
    /// <param name="description">error description</param>
    public ParserException(string fileName, int line, int column, string description) :
      base( fileName, line, column, description) {
    }
  }

  //=================================================================
  /// <summary>
  /// a exception which is thrown in case of semantic errors
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
  public class SemanticException : ScriptingException {
    /// <summary>
    /// creates a new exception object
    /// </summary>
    /// <param name="fileName">name of the file, where the error occured</param>
    /// <param name="line">number of the line, where the error occured</param>
    /// <param name="column">number of the column, where the error occured</param>
    /// <param name="description">error description</param>
    public SemanticException(string fileName, int line, int column, string description) :
      base( fileName, line, column, description) {
    }
  }
}
