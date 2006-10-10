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

namespace Purple.Tools
{
  //=================================================================
  /// <summary>
  /// Class that contains some methods that simplify working with streams.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.72</para>
  /// </remarks>
  //=================================================================
	public class StreamHelper
	{
    /// <summary>
    /// Copies the content of one stream to another.
    /// </summary>
    /// <param name="from">The stream containing the source data.</param>
    /// <param name="to">The destination stream.</param>
    /// <param name="size">The size of the temporary buffer to use.</param>
    public static void Copy(Stream from, Stream to, int size) {
      byte[] buffer = new byte[size];
      int written = 0;
      while ( (written = from.Read(buffer, 0, buffer.Length)) > 0) 
        to.Write( buffer, 0, written );        
    }

    /// <summary>
    /// Copies the content of one stream to another (uses a 64kb temporary buffer).
    /// </summary>
    /// <param name="from">The stream containing the source data.</param>
    /// <param name="to">The destination stream.</param>
    public static void Copy(Stream from, Stream to) {
      Copy(from, to, 64*1024*1024);
    }
	}
}
