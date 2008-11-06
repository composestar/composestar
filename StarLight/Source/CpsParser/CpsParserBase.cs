#region License
/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */
#endregion

using System;
using System.Collections.Generic;
using System.Text;
using Antlr.Runtime;
using Antlr.Runtime.Tree;

namespace Composestar.StarLight.CpsParser
{
    [CLSCompliant(false)]
    public class CpsParserBase : Parser
    {
        public CpsParserBase(ITokenStream input)
            : base(input)
        { }

        protected void warning(String msg, IToken t)
        { }

        public String extractEmbeddedCode(ITreeAdaptor adapter)
        {
            IToken start = (IToken)input.LT(-1);
            MatchAny(input);
            IToken stop = null;

            while (input.LA(3) != Token.EOF)
            {
                stop = (IToken)input.LT(1);
                MatchAny(input);
            }
            stop = (IToken)input.LT(1); // this should be a '}' will be matched by
            // the parser

            String result = input.ToString(start.TokenIndex + 1, stop.TokenIndex - 1);
            return result;
        }

        protected String inputToString(IToken start, IToken end)
        {
            return input.ToString(start, end);
        }

        protected Object adaptorCreate(ITreeAdaptor ta, int token, String text)
        {
            return ta.Create(token, text);
        }

        protected Object adaptorCreate(ITreeAdaptor ta, int token, String text, IToken baset)
        {
            return ta.Create(token, baset, text);
        }
    }
}
