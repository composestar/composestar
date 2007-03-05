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
using Microsoft.VisualStudio.TextManager.Interop;


namespace Composestar.StarLight.VisualStudio.LanguageServices
{
	public struct Declaration : IComparable
	{
		public const int SnippetGlyph = 205;
		public const int ClassGlyph = 0;
		public const int FunctionGlyph = 72;

		public Declaration(string description, string displayText, int glyph, string name)
		{
			this.Description = description;
			this.DisplayText = displayText;
			this.Glyph = glyph;
			this.Name = name;
		}

		public Declaration(VsExpansion expansion)
		{
			this.Glyph = CompletionGlyph.GetSnippetId();
			this.DisplayText = expansion.shortcut;
			this.Name = expansion.title;
			this.Description = expansion.description;
		}

		public string Description;
		public string DisplayText;
		public int Glyph;
		public string Name;

		public int CompareTo(object obj)
		{
			Declaration temp = (Declaration)obj;
			return this.Name.CompareTo(temp.Name);
		}
	}

	public enum Accessibility
	{
		Public = 0,
		Internal = 1,
		Friend = 2,
		Protected = 3,
		Private = 4,
		Shortcut = 5,
	}

	public enum Element
	{
		Class = 0,
		Constant = 1,
		Delegate = 2,
		Enum = 3,
		Enummember = 4,
		Event = 5,
		Exception = 6,
		Field = 7,
		Interface = 8,
		Macro = 9,
		Map = 10,
		Mapitem = 11,
		Method = 12,
		Overload = 13,
		Module = 14,
		Namespace = 15,
		Operator = 16,
		Property = 17,
		Struct = 18,
		Template = 19,
		Typedef = 20,
		Type = 21,
		Union = 22,
		Variable = 23,
		Valuetype = 24,
		Intrinsic = 25,

	}
	
	public class CompletionGlyph
	{

		public static int GetIndexFor(Accessibility accessibility, Element element)
		{
			return (int)accessibility + (int)element * 6;
		}

		public static int GetSnippetId()
		{
			return 205;
		}

		public static int GetKeywordId()
		{
			return (6 * 31) + 20;
		}
	}
}