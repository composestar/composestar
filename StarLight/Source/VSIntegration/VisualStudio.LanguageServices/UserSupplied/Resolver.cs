/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using Composestar.StarLight.VisualStudio.LanguageServices.Prolog;    

namespace Composestar.StarLight.VisualStudio.Babel
{
	public class Resolver : Babel.IASTResolver
	{

		private static PrologFunctions _prologFunctions = PrologFunctions.LoadFunctions(); 


		#region IASTResolver Members

		private List<String> types = new List<String>();

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

			return new List<Babel.Declaration>();
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

			if (types.Count == 0)
			{
				Type t = typeof(System.DateTime);
				Type[] tt = t.Assembly.GetTypes();
				foreach (Type t2 in tt)
				{
					if (t2.IsPublic)
						types.Add(t2.FullName);
				}
				types.Sort(); 
			}

			string sel = ((string)result);

			if (sel.Length == 0)
			{
				List<string> added = new List<string>();
 
				// Only display root elements
				foreach (string s in types)
				{
					string root = s.Split('.')[0];
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
						types.Add(t2.FullName);
				}
				types.Sort();
			}

			if (name.Length == 0)
			{

			}
			else if (name.Equals("prologfunction"))
			{
				// Display prolog functions
				members.AddRange(_prologFunctions.GetFunctions);
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
