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
using Purple.Tools;
using Purple.Scripting;
using Purple.Serialization;

namespace Purple.Scripting {
	//=================================================================
	/// <summary>
	/// object containing script data for easy xml integration
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>
	/// </remarks>
	//=================================================================
	public class Scriptlet : ICommand {
		//-------------------------------------------------------------------------
		#region Variables
		//-------------------------------------------------------------------------
		string source = "";		
		ArrayList references = new ArrayList();		
		ArrayList namespaces = new ArrayList();
		string language = "CSharp";
		string lastCompiledSource = null;
		IScript script = null;
		string templateSource = "";
		//-------------------------------------------------------------------------
		#endregion
		//-------------------------------------------------------------------------

		//-------------------------------------------------------------------------
		#region Properties
		//-------------------------------------------------------------------------
		/// <summary>
		/// language used for source
		/// </summary>
		[Serialize(true)]
		public string Language {
			get {
				return language;
			}
			set {
				language = value;
			}
		}

		/// <summary>
		/// references (e.g. DirectX.dll)
		/// </summary>	
		[Serialize()]	
		public ArrayList References {
			get {
				return references;
			}
			set {			
				references.Clear();
				foreach (string refer in value)
					references.Add(refer);			
			}
		}

		/// <summary>
		/// namespaces to use
		/// </summary>
		[Serialize()]
		public ArrayList Namespaces {
			get {
				return namespaces;
			}
			set {			
				namespaces.Clear();
				foreach (string ns in value)
					namespaces.Add(ns);			
			}
		}

		/// <summary>
		/// source which is used as template (predefined variables, ...)
		/// </summary>
		[Serialize()]	
		public string TemplateSource {
			get {
				return templateSource;
			}
			set {
				templateSource = value;
			}
		}

		/// <summary>
		/// source to execute
		/// </summary>
		[Serialize()]
		public string Source {
			get {
				return source;
			}
			set {
				source = value;
			}
		}
		//-------------------------------------------------------------------------
		#endregion
		//-------------------------------------------------------------------------

		//-------------------------------------------------------------------------
		#region Initialisation
		//-------------------------------------------------------------------------
		/// <summary>
		/// constructor
		/// </summary>
		public Scriptlet() {
		}
		//-------------------------------------------------------------------------
		#endregion
		//-------------------------------------------------------------------------

		//-------------------------------------------------------------------------
		#region Methods
		//-------------------------------------------------------------------------
		/// <summary>
		/// compile the source (property Source)
		/// </summary>
		public void Compile() {
			script = null;
			lastCompiledSource = source;
			IScriptingHost host = GetHost();
			host.Source = source;
			
			host.References.Clear();
			foreach(string refer in references)
				host.References.Add(refer);
			
			host.Namespaces.Clear();
			foreach(string ns in namespaces)
				host.Namespaces.Add(ns);

			script = host.Create();
		}

		/// <summary>
		/// executes the command/script
		/// </summary>
		public void Execute() {
			if (lastCompiledSource != source) 
        Compile();	
			if (script != null)
				script.Execute();
		}

		private IScriptingHost GetHost() {
			string typeName;
			switch (language) {
				case "CSharp":
					typeName = "Purple.Scripting.CSharp.ScriptingHost";
          break;							
				default:
					typeName = language;
					break;
			}
			return (IScriptingHost)TypeRegistry.Create(typeName);
		}
		//-------------------------------------------------------------------------
		#endregion
		//-------------------------------------------------------------------------
	}
}
