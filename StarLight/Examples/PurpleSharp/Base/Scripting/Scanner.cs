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

namespace Purple.Scripting
{
  //=================================================================
  /// <summary>
  /// an abstract interface for a script scanner
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
  public interface IScanner {
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
    /// returns the current line
    /// </summary>
    int Line { get; }

    /// <summary>
    /// returns the current column
    /// </summary>
    int Column { get; }

    /// <summary>
    /// the fileName of the script (if any)
    /// </summary>
    string FileName { get; }

    /// <summary>
    /// returns the current script
    /// </summary>
    string Script { get; }

    /// <summary>
    /// returns the current token
    /// </summary>
    string Token { get; }

    /// <summary>
    /// type of token
    /// </summary>
    TokenType TokenType { get; }

    /// <summary>
    /// moves on to the next token
    /// </summary>
    /// <returns>false if end of script or error</returns>
    bool Next();

    /// <summary>
    /// test if currentToken is equal to one of the passed symbols
    /// </summary>
    /// <param name="args">symbols to test for</param>
    /// <returns>true if current token is one of the passed symbols</returns>
    bool IsSymbol(params string[] args);

    /// <summary>
    /// tests if current token is equal to one of the passed idents
    /// </summary>
    /// <param name="args">idents to test for</param>
    /// <returns>true if current token is equal to one of the passed idents</returns>
    bool IsIdent(params string[] args);
  }

  //=================================================================
  /// <summary>
  /// token types of an IScanner
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  /// </remarks>
  //=================================================================
  public enum TokenType {
    /// <summary>
    /// end of script token
    /// </summary>
    EndOfScript,
    /// <summary>
    /// the token is some kind of identifier
    /// </summary>
    Ident,
    /// <summary>
    /// the token is a certain symbol
    /// </summary>
    Symbol,
    /// <summary>
    /// the token is a number
    /// </summary>
    Number,
    /// <summary>
    /// the token is a string e.g. "TestString"
    /// </summary>
    String,
  }

  //=================================================================
  /// <summary>
  /// an abstract class implementing the standard behaviour of a scanner
  /// a specialised scanner must override Next and may override NextCharacter
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  /// </remarks>
  //=================================================================
  public abstract class Scanner : IScanner {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    int index;
    /// <summary>
    /// the variable that contains the current character
    /// </summary>
    protected char ch = ' ';

    /// <summary>
    /// returns the current script
    /// </summary>
    public string Script {
      get {
        return script;
      }
    }
    string script = "";

    /// <summary>
    /// returns the current column
    /// </summary>
    public int Column {
      get {
        return column;
      }
    }
    int column;

    /// <summary>
    /// returns the current line
    /// </summary>
    public int Line {
      get {
        return line;
      }
    }
    int line;

    /// <summary>
    /// returns the current token
    /// </summary>
    public string Token { 
      get {
        return token;
      }
      set {
        token = value;
      }
    }
    private string token = "";

    /// <summary>
    /// type of token
    /// </summary>
    public TokenType TokenType {
      get {
        return tokenType;
      }
      set {
        tokenType = value;
      }
    }
    private TokenType tokenType;

    /// <summary>
    /// the fileName of the script (if any)
    /// </summary>
    public string FileName { 
      get {
        return fileName;
      }
    }
    private string fileName = "";
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new instance of the scanner
    /// </summary>
    public Scanner() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// returns true if the end of the script is reached
    /// </summary>
    /// <returns>ture if the end of the script is reached</returns>
    protected bool EndOfScript() {
      return index >= script.Length;
    }

    /// <summary>
    /// interprets the next character
    /// </summary>
    protected virtual void NextCharacter() {
      // test if end of script is reached
      if (EndOfScript()) {
        ch = Char.MinValue;
        return;
      }

      // read the next character and advance the index and current column
      ch = script[index];
      index++;
      column++;

      // test for end of line
      if (ch == '\r') { // \r or \r\n
        if (EndOfScript())
          return;
        if (script[index] == '\n')
          index++;
        column = 0;
        line++;
        ch = '\n';
      } else if (ch == '\n') { // \n or \n\r
        if (EndOfScript())
          return;
        if (script[index] == '\r')
          index++;
        column = 0;
        line++;
        ch = '\n';
      }
    }

    /// <summary>
    /// loads a new script
    /// </summary>
    /// <param name="script">script to load</param>
    public void Load(string script) {
      this.script = script;
      line = 1;
      column = 0;
      index = 0;
      ch = ' ';
      fileName = "script";
    }

    /// <summary>
    /// loads a new script from a stream
    /// </summary>
    /// <param name="stream">stream to load script from</param>
    public void LoadStream(Stream stream) {
      StreamReader reader = new StreamReader(stream);
      Load(reader.ReadToEnd());
      reader.Close();
    }

    /// <summary>
    /// loads a script from a file
    /// </summary>
    /// <param name="fileName">name of file to load script from</param>
    public void LoadFile(string fileName) {
      FileStream stream = new FileStream(fileName, FileMode.Open, FileAccess.Read);
      LoadStream(stream);
      stream.Close();
      this.fileName = fileName;
    }

    /// <summary>
    /// moves on to the next token
    /// </summary>
    /// <returns>false if end of script or error</returns>
    public abstract bool Next();
    #endregion

    #region Helper Methods
    /// <summary>
    /// tests if currentToken is equal to one of the passed symbols
    /// </summary>
    /// <param name="args">symbols to test for</param>
    /// <returns>true if current token is equal to one of the passed symbols</returns>
    public bool IsSymbol(params string[] args) {
      if (TokenType != TokenType.Symbol)
        return false;
      for (int i=0; i<args.Length; i++) {
        if (token == args[i])
          return true;
      }
      return false;
    }

    /// <summary>
    /// tests if current token is equal to one of the passed idents
    /// </summary>
    /// <param name="args">idents to test for</param>
    /// <returns>true if current token is equal to one of the passed idents</returns>
    public bool IsIdent(params string[] args) {
      if (TokenType != TokenType.Ident)
        return false;
      for (int i=0; i<args.Length; i++) {
        if (token == args[i])
          return true;
      }
      return false;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
