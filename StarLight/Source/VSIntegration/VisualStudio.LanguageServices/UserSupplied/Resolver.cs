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
using ErrorHandler = Microsoft.VisualStudio.ErrorHandler;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.TextManager.Interop;
using VSConstants = Microsoft.VisualStudio.VSConstants;
using Composestar.StarLight.VisualStudio.Babel;
using Composestar.StarLight.VisualStudio.LanguageServices;

namespace Composestar.StarLight.VisualStudio.Babel
{
	public class Resolver : Babel.IASTResolver
	{

		private static PrologFunctions _prologFunctions = PrologFunctions.LoadFunctions(); 


		#region IASTResolver Members

		private List<Type> types = new List<Type>();

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

		// Disable the "DoNotPassTypesByReference" warning.
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
