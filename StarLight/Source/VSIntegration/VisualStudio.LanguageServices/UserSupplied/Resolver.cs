#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

#region Using directives
using Composestar.StarLight.VisualStudio.Babel;
using Composestar.StarLight.VisualStudio.LanguageServices;
using Composestar.StarLight.VisualStudio.LanguageServices.Prolog;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.TextManager.Interop;
using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;
using ErrorHandler = Microsoft.VisualStudio.ErrorHandler;
using VSConstants = Microsoft.VisualStudio.VSConstants;
#endregion

namespace Composestar.StarLight.VisualStudio.Babel
{
	/// <summary>
	/// Resolver
	/// </summary>
	public class Resolver : Babel.IASTResolver
	{

		/// <summary>
		/// _prolog functions
		/// </summary>
		private static PrologFunctions _prologFunctions = PrologFunctions.LoadFunctions(); 


		#region IASTResolver Members

		/// <summary>
		/// Types
		/// </summary>
		private List<Type> types = new List<Type>();

		/// <summary>
		/// Create resolver
		/// </summary>
		public Resolver()
		{
			if (types.Count == 0)
			{
				Type t = typeof(System.DateTime);
				Type[] tt = t.Assembly.GetTypes();
				foreach (Type t2 in tt)
				{
					if (t2.IsPublic)
						types.Add(t2);
				}
			
			}

			 /// <summary>
			 /// Expansions list
			 /// </summary>
			 //if (this.expansionsList == null)
			 //{
			 //    GetSnippets();
			 //}
		}

		private List<VsExpansion> expansionsList;

		/// <summary>
		/// Gets the snippets.
		/// </summary>
		private void GetSnippets()
		{

			if (null == this.expansionsList)
			{
				this.expansionsList = new List<VsExpansion>();
			}
			else
			{
				this.expansionsList.Clear();
			}
			IVsTextManager2 textManager = Package.GetGlobalService(typeof(SVsTextManager)) as IVsTextManager2;
			if (textManager == null)
			{
				return;
			}
			SnippetsEnumerator enumerator = new SnippetsEnumerator(textManager, new Guid(ComposeStarConstants.languageServiceGuidString));
			foreach (VsExpansion expansion in enumerator)
			{
				if (!string.IsNullOrEmpty(expansion.shortcut))
				{
					this.expansionsList.Add(expansion);
				}
			}
		}

		/// <summary>
		/// Disable the "DoNotPassTypesByReference" warning.
		/// </summary>
		/// <param name="declarations">Declarations</param>
		[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1045")]
		public void AddSnippets(ref List<Babel.Declaration> declarations)
		{
			if (null == this.expansionsList)
			{
				return;
			}
			foreach (VsExpansion expansionInfo in this.expansionsList)
			{
				declarations.Add(new Declaration(expansionInfo));
			}
		}

		/// <summary>
		/// Finds the completions.
		/// </summary>
		/// <param name="result">The result.</param>
		/// <param name="line">The line.</param>
		/// <param name="col">The col.</param>
		/// <returns></returns>
		public IList<Babel.Declaration> FindCompletions(object result, int line, int col)
		{
			if (result != null)
				System.Diagnostics.Trace.WriteLine(result.ToString());

			// Add prologfunctions
			List<Babel.Declaration> members = new List<Babel.Declaration>();

			members.AddRange(_prologFunctions.RetrieveCompletions() );
		
			// Add Snippets
			//AddSnippets(ref members);

			// Add types
			foreach (Type t in types)
			{
				if (t.IsClass || t.IsInterface)
				{
					Declaration decl = new Declaration();

					decl.Description = t.FullName;
					decl.DisplayText = t.Name;

					if (t.IsInterface)
						decl.Glyph = CompletionGlyph.GetIndexFor(Accessibility.Public, Element.Interface);
					if (t.IsClass )
						decl.Glyph = CompletionGlyph.GetIndexFor(Accessibility.Public, Element.Class);
				
					decl.Name = t.FullName;

					members.Add(decl);
				}
			}
			
			members.Sort();

			return members;
		}

		/// <summary>
		/// Finds the members.
		/// </summary>
		/// <param name="result">The result.</param>
		/// <param name="line">The line.</param>
		/// <param name="col">The col.</param>
		/// <returns></returns>
		public IList<Babel.Declaration> FindMembers(object result, int line, int col)
		{
			if (result != null)
				System.Diagnostics.Trace.WriteLine(result.ToString());
			else
				result = "";

			List<Babel.Declaration> members = new List<Babel.Declaration>();
					

			string sel = ((string)result);

			if (sel.Length == 0)
			{
				List<string> added = new List<string>();
 
				// Only display root elements
				foreach (Type s in types)
				{
					string root = s.FullName.Split('.')[0];
					if (!added.Contains(root))
					{
						added.Add(root);
						Declaration decl = new Declaration();

						decl.Description = String.Format("{0} namespace", root);
						decl.DisplayText = root;
						decl.Glyph = CompletionGlyph.GetIndexFor(Accessibility.Public, Element.Namespace);
						decl.Name = root;

						members.Add(decl); 
					}
				}
			
			}
					
			return members;
		}

		/// <summary>
		/// Finds the quick info.
		/// </summary>
		/// <param name="result">The result.</param>
		/// <param name="line">The line.</param>
		/// <param name="col">The col.</param>
		/// <returns></returns>
		public string FindQuickInfo(object result, int line, int col)
		{
			if (result != null)
				System.Diagnostics.Trace.WriteLine(result.ToString());

			return "unknown";
		}

		/// <summary>
		/// Finds the methods.
		/// </summary>
		/// <param name="result">The result.</param>
		/// <param name="line">The line.</param>
		/// <param name="col">The col.</param>
		/// <param name="name">The name.</param>
		/// <returns></returns>
		public IList<Babel.Method> FindMethods(object result, int line, int col, string name)
		{
			if (result != null)
				System.Diagnostics.Trace.WriteLine(name);
			else
				result = "";

			List<Babel.Method> members = new List<Babel.Method>();

			if (types.Count == 0)
			{
				Type t = typeof(System.DateTime);
				Type[] tt = t.Assembly.GetTypes();
				foreach (Type t2 in tt)
				{
					if (t2.IsPublic)
						types.Add(t2);
				}
				types.Sort();
			}
			
			// Display prolog functions
			members.AddRange(_prologFunctions.RetrieveFunctions(name));

			Type tm = Type.GetType(name, false);
			if (tm != null)
			{
				MethodInfo[] methods = tm.GetMethods();

				foreach (MethodInfo method in methods)
				{
					Method m = new Method();
					m.Name = method.Name;
					m.Parameters = new List<Parameter>();
					foreach (ParameterInfo param in method.GetParameters())
					{
						Parameter item = new Parameter();
						item.Description = param.ParameterType.FullName;
						item.Display = param.Name;
						item.Name = param.Name;
						m.Parameters.Add(item);
					}
					m.Type = method.ReturnType.FullName;
					members.Add(m);
				}
			}
			

			return members;
		}

		/// <summary>
		/// Determines whether [is prolog function] [the specified text].
		/// </summary>
		/// <param name="text">The text.</param>
		/// <returns>
		/// 	<c>true</c> if [is prolog function] [the specified text]; otherwise, <c>false</c>.
		/// </returns>
		public static bool IsPrologFunction(string text)
		{
			if (string.IsNullOrEmpty(text))
				return false;

			return _prologFunctions.ContainsFunction(text);
		}

		#endregion
	}
}
