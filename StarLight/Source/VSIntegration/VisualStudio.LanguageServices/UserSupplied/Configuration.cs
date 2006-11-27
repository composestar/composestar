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
using Microsoft.VisualStudio.Package;
using Composestar.StarLight.VisualStudio.Babel.Parser;
using Microsoft.VisualStudio.TextManager.Interop;

namespace Composestar.StarLight.VisualStudio.Babel
{
	public static partial class Configuration
	{
		public const string Name = "ComposeStarConcern";
		public const string Extension = ".cps";

		private static CommentInfo myCInfo;

		/// <summary>
		/// Gets my C info.
		/// </summary>
		/// <value>My C info.</value>
		public static CommentInfo MyCInfo
		{
			get
			{
				return myCInfo;
			}
		}

		/// <summary>
		/// Initializes the <see cref="T:Configuration"/> class.
		/// </summary>
		static Configuration()
		{
			myCInfo.BlockEnd = "*/";
			myCInfo.BlockStart = "/*";
			myCInfo.LineStart = "//";
			myCInfo.UseLineComments = true;

			// default colors - currently, these need to be declared
			CreateColor("Keyword", COLORINDEX.CI_BLUE, COLORINDEX.CI_USERTEXT_BK);
			CreateColor("Comment", COLORINDEX.CI_DARKGREEN, COLORINDEX.CI_USERTEXT_BK);
			CreateColor("Identifier", COLORINDEX.CI_SYSPLAINTEXT_FG, COLORINDEX.CI_USERTEXT_BK);
			CreateColor("String", COLORINDEX.CI_RED, COLORINDEX.CI_USERTEXT_BK);
			CreateColor("Number", COLORINDEX.CI_SYSPLAINTEXT_FG, COLORINDEX.CI_USERTEXT_BK);
			CreateColor("Text", COLORINDEX.CI_SYSPLAINTEXT_FG, COLORINDEX.CI_USERTEXT_BK);
			CreateColor("PrologFunction", COLORINDEX.CI_AQUAMARINE, COLORINDEX.CI_USERTEXT_BK);

			TokenColor error = CreateColor("Error", COLORINDEX.CI_RED, COLORINDEX.CI_USERTEXT_BK, false, true);

			//
			// map tokens to color classes
			//
			ColorToken((int)Tokens.KWCONCERN, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWFILTERMODULE, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWSUPERIMPOSITION, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWIMPLEMENTATION, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWINTERNALS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWEXTERNALS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWCONDITIONS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWINPUTFILTERS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWOUTPUTFILTERS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWANNOTATIONS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWAS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWBY, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWCONSTRAINTS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWFILTERMODULES, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.FALSE, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.TRUE, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWINNER, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWPRE, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWPREHARD, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWPRESOFT, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWIN, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWSELECTORS, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.None);
			ColorToken((int)Tokens.KWPROLOGFUN, TokenType.Keyword, TokenColor.Keyword, TokenTriggers.MethodTip & TokenTriggers.MemberSelect);

			ColorToken((int)Tokens.NUMBER, TokenType.Literal, TokenColor.Number, TokenTriggers.None);
			ColorToken((int)Tokens.CONSTSTRING, TokenType.Literal, TokenColor.String, TokenTriggers.None);

			ColorToken((int)Tokens.IDENTIFIER, TokenType.Identifier, TokenColor.Identifier, TokenTriggers.Parameter);

			ColorToken((int)Tokens.DOT, TokenType.Literal, TokenColor.Text, TokenTriggers.MemberSelect);
			ColorToken((int)Tokens.COLON, TokenType.Literal, TokenColor.Text, TokenTriggers.MemberSelect);
			ColorToken((int)Tokens.COMMA, TokenType.Literal, TokenColor.Text, TokenTriggers.ParameterNext);

			ColorToken((int)'(', TokenType.Delimiter, TokenColor.Text, TokenTriggers.MatchBraces & TokenTriggers.ParameterStart);
			ColorToken((int)')', TokenType.Delimiter, TokenColor.Text, TokenTriggers.MatchBraces & TokenTriggers.ParameterEnd);
			ColorToken((int)'{', TokenType.Delimiter, TokenColor.Text, TokenTriggers.MatchBraces);
			ColorToken((int)'}', TokenType.Delimiter, TokenColor.Text, TokenTriggers.MatchBraces);
			ColorToken((int)'<', TokenType.Delimiter, TokenColor.Text, TokenTriggers.MatchBraces);
			ColorToken((int)'>', TokenType.Delimiter, TokenColor.Text, TokenTriggers.MatchBraces);
			ColorToken((int)Tokens.BAR, TokenType.Delimiter, TokenColor.Text, TokenTriggers.MemberSelect);

			// Extra token values internal to the scanner
			ColorToken((int)Tokens.LEX_ERROR, TokenType.Text, error, TokenTriggers.None);
			ColorToken((int)Tokens.LEX_COMMENT, TokenType.Text, TokenColor.Comment, TokenTriggers.None);

		}
	}
}