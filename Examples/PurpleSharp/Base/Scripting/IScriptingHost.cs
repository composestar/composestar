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
using System.IO;

namespace Purple.Scripting
{
  //=================================================================
  /// <summary>
  /// abstract interface for creating a faked C# scripting host
  /// it's just faked because C# code is never interpreted but gets compiled
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
	public interface IScriptingHost
	{	
    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------   
    /// <summary>
    /// scriptingEngine to use to "interpret" code
    /// </summary>
    IScriptingEngine ScriptingEngine { get; set; }

    /// <summary>
    /// to add assembly references like: System.DLL, ...
    /// </summary>
    IList References { get; set;}

    /// <summary>
    /// to add namespaces like: System.IO, Purple.Graphics....
    /// </summary>
    IList Namespaces { get; set;}

    /// <summary>
    /// source which is used as template (predefined variables, ...)
    /// </summary>
    string TemplateSource {get; set; }

    /// <summary>
    /// actual source to execute
    /// </summary>
    string Source {get; set; }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------   
    /// <summary>
    /// creates a script
    /// </summary>
    /// <exception cref="Purple.Scripting.ScriptingException">if IScript object can't be created</exception>
    IScript Create();

		/// <summary>
		/// loads a scriptlet
		/// </summary>
		/// <param name="scriptlet">scriptlet to take data from</param>
		/// <returns>true if succeeded</returns>
		bool LoadScriptlet(Scriptlet scriptlet);

		/// <summary>
		/// creates an scriptlet from the current state of the scripting host
		/// </summary>
		/// <returns>scriptlet</returns>
		Scriptlet CreateScriptlet();

    /// <summary>
    /// loads script from a PurpleSharp script file
    /// </summary>
    /// <param name="stream">to load script from</param>
    /// <returns>true if loading was successful</returns>
    bool Load(Stream stream);  
  
    /// <summary>
    /// saves data into a PurpleSharp script file
    /// </summary>
    /// <param name="stream">to save data to</param>    
    void Save(Stream stream);

    /// <summary>
    /// executing script
    /// </summary>
    /// <exception cref="Purple.Scripting.ScriptingException">if source can't be executed</exception>
    void Execute();

    /// <summary>
    /// returns the source combined with the template and references
    /// </summary>
    /// <returns>combined source</returns>
    string GetCombinedSource();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
