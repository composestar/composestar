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

namespace Purple.Tools {
  //=================================================================
  /// <summary>
  /// Some helper methods for easier string handling  
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public class StringHelper {
    /// <summary>
    /// removes one line beginning with a certain string from message
    /// </summary>
    /// <param name="message">to return from</param>
    /// <param name="line">string with which line to be removed begins</param>
    /// <returns></returns>
    static public string RemoveLine(string message, string line) {
      int index = message.IndexOf(line);
      if (index == -1)
        return message;
      int index2 = message.IndexOf(Environment.NewLine, index);      
      return message.Remove(index, index2-index + Environment.NewLine.Length);      
    }
    
    /// <summary>
    /// return \n by Environment.NewLine
    /// because \n is shown as box in texBoxes!!!
    /// </summary>
    /// <param name="message">input text</param>
    /// <returns>cleaned text</returns>
    static public string GuiNewLines(string message) {
      return message.Replace("\n", Environment.NewLine);
    }    

		/// <summary>
		/// converts a c-style string to a C# string
		/// </summary>
		/// <param name="cString">array of bytes - null terminated</param>
		/// <returns>C# string</returns>
		static public string Convert(byte[] cString) {
			int len = 0;
			while (cString[len] != 0 && len < cString.Length)
				len++;

			return System.Text.Encoding.Default.GetString(cString, 0, len);
		}

    /// <summary>
    /// creates a stream object from a string
    /// </summary>
    /// <param name="str"></param>
    /// <returns></returns>
    static public Stream ToStream(string str) {
      // create new memorystream and write string into it
      StreamWriter writer = new StreamWriter(new MemoryStream());
      writer.Write(str);
      writer.Flush();
      // reset stream position to the beginning and return stream
      writer.BaseStream.Seek(0, SeekOrigin.Begin);
      return writer.BaseStream;
    }
  }
}
