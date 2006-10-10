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
using System.IO;
using System.Collections;

namespace Purple.Scripting {
  //=================================================================
  /// <summary>
  /// An abstract interface for script parsers.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
  public interface IParser {
    /// <summary>
    /// loads a certain script
    /// </summary>
    /// <param name="script">script to load</param>
    void Load(string script);

    /// <summary>
    /// loads a new script from a stream
    /// </summary>
    /// <param name="stream">stream to load script from</param>
    void LoadStream(Stream stream);

    /// <summary>
    /// loads a script from a file
    /// </summary>
    /// <param name="fileName">name of file to load script from</param>
    void LoadFile(string fileName);

    /// <summary>
    /// execute script
    /// </summary>
    /// <returns>the variablest collection</returns>
    /// <exception cref="Purple.Scripting.ParserException">in case of a syntactical error a ParserException is thrown</exception>
    /// <exception cref="Purple.Scripting.ScannerException">in case of a textual error a ScannerException is thrown</exception>
    /// <exception cref="Purple.Scripting.SemanticException">in case of a semantical error a SemanticException is thrown</exception>
    Variables Execute();

    /// <summary>
    /// Executes script, without reseting the semantics.
    /// </summary>
    /// <param name="script">Script to execute.</param>
    /// <returns>The collection of variables.</returns>
    Variables Execute(string script);
  }

  //=================================================================
  /// <summary>
  /// an abstract parser class, that implements the common functionality for script parsers
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  /// </remarks>
  //=================================================================
  public abstract class Parser : IParser {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// the culture info object used for parsing numbers, ...
    /// </summary>
    protected System.Globalization.CultureInfo cultureInfo = new System.Globalization.CultureInfo("en-US");

    /// <summary>
    /// the scanner object, which is used internally by the parser for tokenization of the script
    /// </summary>
    protected internal abstract IScanner Scanner { get; set; }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new instance of a parser object
    /// </summary>
    public Parser() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// loads a certain script
    /// </summary>
    /// <param name="script">script to load</param>
    public void Load(string script) {
      Scanner.Load(script);
    }

    /// <summary>
    /// loads a new script from a stream
    /// </summary>
    /// <param name="stream">stream to load script from</param>
    public void LoadStream(Stream stream) {
      Scanner.LoadStream(stream);
    }

    /// <summary>
    /// loads a script from a file
    /// </summary>
    /// <param name="fileName">name of file to load script from</param>
    public void LoadFile(string fileName) {
      Scanner.LoadFile(fileName);
    }

    /// <summary>
    /// execute script
    /// </summary>
    /// <returns>the variables collection</returns>
    /// <exception cref="Purple.Scripting.ParserException">in case of a syntactical error a ParserException is thrown</exception>
    /// <exception cref="Purple.Scripting.ScannerException">in case of a textual error a ScannerException is thrown</exception>
    /// <exception cref="Purple.Scripting.SemanticException">in case of a semantical error a SemanticException is thrown</exception>
    public abstract Variables Execute();

    /// <summary>
    /// Executes script, without reseting the semantics.
    /// </summary>
    /// <param name="script">Script to execute.</param>
    /// <returns>The collection of variables.</returns>
    public abstract Variables Execute(string script);
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}