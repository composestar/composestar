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

namespace Purple.Graphics.HLSL {
  //=================================================================
  /// <summary>
  /// abstract interface for a high level shader language compiler
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public interface ICompiler : IDisposable {
    /// <summary>
    /// loads a hlsl program from a stream
    /// </summary>
    /// <param name="stream">stream to load program from</param>
    /// <param name="entryPoint">entry point to the program in the hlsl source or null ("main")</param>
    /// <param name="profile">defines target format</param>
    /// <returns>loaded program or null in case of failure</returns>
    IProgram Load( Stream stream, string entryPoint, Profile profile );

    /// <summary>
    /// loads a hlsl programm from a file
    /// </summary>
    /// <param name="fileName">name of file</param>
    /// <param name="entryPoint">entry point to the program in the hlsl source or null ("main")</param>
    /// <param name="profile">defines target format</param>
    /// <returns>loaded program or null in case of failure</returns>
    IProgram Load( string fileName, string entryPoint, Profile profile );

    /// <summary>
    /// get error string if Compile... returned null
    /// </summary>
    string CompilationError { get; }
  }
}
