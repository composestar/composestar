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
using Composestar.StarLight.VisualStudio.Babel.Parser;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;
using System;
using System.Collections.Generic;
#endregion

namespace Composestar.StarLight.VisualStudio.Babel
{
	/// <summary>
	/// Configuration
	/// </summary>
	public static partial class Configuration
	{
		public const string Name = "ComposeStarConcern";
		public const string Extension = ".cps";

		/// <summary>
		/// My c info
		/// </summary>
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
