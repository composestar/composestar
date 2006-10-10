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

namespace Purple.Scripting.Resource
{
  //=================================================================
  /// <summary>
  /// a simple scanner for tokenizing resource scripts
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
  public class ResourceScanner : Scanner {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new instance of the scanner
    /// </summary>
    public ResourceScanner() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// moves on to the next Token
    /// </summary>
    public override bool Next() {

      // ignore spaces and tabs
      while (ch == ' ' || ch == '\t')
        NextCharacter();

      // read ident: [letter] {[letter, digit, _]}
      if (Char.IsLetter(ch)) {
        Token = "";
        while (Char.IsLetterOrDigit(ch) || ch == '_') {
          Token += ch;
          NextCharacter();
        }
        TokenType = TokenType.Ident;
      }
        // read number [digit] {[digit]}[.{digit}]
      else if (Char.IsDigit(ch)) {
        Token = "";
        while (Char.IsDigit(ch) || ch == '.') {
          Token += ch;
          NextCharacter();
        }
        if (ch == 'f' || ch == 'x' || ch == 'y') {
          Token += ch;
          NextCharacter();
        }
        TokenType = TokenType.Number;
      }
        // read slash or comment
      else if (ch == '/') {
        NextCharacter();
        if (ch == '/') {
          while (ch != '\n' && !EndOfScript())
            NextCharacter();
          if (ch != '\n') {
            TokenType = TokenType.EndOfScript;
            Token = "";
            return false;
          }
          NextCharacter();
          Next();
        } else if (ch == '*') {
          NextCharacter();
          do {
          while (ch != '*' && !EndOfScript())
            NextCharacter();
          if (ch != '*')
              Fail("End of script reached before end of comment!");
          NextCharacter();
          } while (ch != '/' && !EndOfScript());
          if (ch != '/')
            Fail("End of script reached before end of comment!");
          NextCharacter();
          Next();
        } else {
          Token = "/";
          TokenType = TokenType.Symbol;
        }
      }
        // read string
      else if (ch == '"') {
        NextCharacter();
        Token = "";
        while (ch != '"' && !EndOfScript()) {
          Token += ch;
          NextCharacter();
        }
        if (ch != '"')
          Fail( "End of script reached before end of string!");
        TokenType = TokenType.String;
        NextCharacter();
      }
        // read other symbols
      else {
        TokenType = TokenType.Symbol;
        switch (ch) {
          case '#': Token = "#"; NextCharacter(); break;
          case '\\': Token = "\\"; NextCharacter(); break;
          case '{': Token = "{"; NextCharacter(); break;
          case '}': Token = "}"; NextCharacter(); break;
          case '[': Token = "["; NextCharacter(); break;
          case ']': Token = "]"; NextCharacter(); break;
          case '(': Token = "("; NextCharacter(); break;
          case ')': Token = ")"; NextCharacter(); break;
          case '<': Token = "<"; NextCharacter(); 
            if (ch == '=') { Token = "<="; NextCharacter(); } break;
          case '>': Token = ">"; NextCharacter(); 
            if (ch == '=') { Token = ">="; NextCharacter(); } break;
          case '.': Token = "."; NextCharacter(); break;
          case ',': Token = ","; NextCharacter(); break;
          case ':': Token = ":"; NextCharacter(); break;
          case ';': Token = ";"; NextCharacter(); break;
          case '|': Token = "|"; NextCharacter(); 
            if (ch == '|') { Token = "||"; NextCharacter(); } break;
          case '&': Token = "&"; NextCharacter(); 
            if (ch == '&') { Token = "&&"; NextCharacter(); } break;
          case '=': Token = "="; NextCharacter(); 
            if (ch == '=') { Token = "=="; NextCharacter(); } break;
          case '!': Token = "!"; NextCharacter();
            if (ch == '=') { Token = "!="; NextCharacter(); } break;
          case '+': Token = "+"; NextCharacter();
            if (ch == '+') { Token = "++"; NextCharacter(); } break;
          case '-': Token = "-"; NextCharacter();
            if (ch == '-') { Token = "--"; NextCharacter(); } break;
          case '*': Token = "*"; NextCharacter(); break;
          case '\n': NextCharacter(); Next(); break;
          case Char.MinValue: Token = ""; TokenType = TokenType.EndOfScript;  break;
          default: 
            Fail( "Unknown character: " + ch);
            break;
        }
      }
      return TokenType != TokenType.EndOfScript;
    }

    private void Fail(string error) {
      throw new ScannerException( FileName, Line, Column, error);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
