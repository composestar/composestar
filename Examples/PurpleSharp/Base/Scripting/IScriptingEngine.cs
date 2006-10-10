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
using System.Collections;

namespace Purple.Scripting {
  //=================================================================
  /// <summary>
  /// abstract interface for a certain scriptEngine (VbScript, JScript, ..)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public interface IScriptingEngine {
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// loads a script from a file and creates a script object
    /// </summary>
    /// <param name="className">name of object e.g. Purple.Scripts.TestScript</param>
    /// <param name="fileName">filepath for script</param>
    /// <returns>script object</returns>   
    /// <exception cref="CompilerErrorException">thrown if script contains compiler errors</exception>
    /// <exception cref="ScriptingException">if assembly couldn't be created</exception>
    /// <exception cref="Purple.Exceptions.StreamException">if script file couldn't be loaded</exception>
    IScript Load(string className, string fileName);

    /// <summary>
    /// creates a new script from a string containing the source
    /// </summary>
    /// <param name="className">name of object e.g. Purple.Scripts.TestScript</param>
    /// <param name="source">string containing source</param>
    /// <returns>script object</returns>   
    /// <exception cref="CompilerErrorException">thrown if script contains compiler errors</exception>
    /// <exception cref="ScriptingException">if assembly couldn't be created</exception>
    IScript Create(string className, string source);

    /// <summary>
    /// creates a new script from a string containing the source
    /// </summary>
    /// <param name="className">name of object e.g. Purple.Scripts.TestScript</param>
    /// <param name="source">string containing source</param>
    /// <param name="compiledFileName">fileName of compiled script</param>
    /// <returns>script object</returns>   
    /// <exception cref="CompilerErrorException">thrown if script contains compiler errors</exception>
    /// <exception cref="ScriptingException">if assembly couldn't be created</exception>
    IScript Create(string className, string source, string compiledFileName);

		/// <summary>
		/// list of references of type string (e.g. "DirectX.dll")
		/// </summary>
		IList References { get; }    
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
