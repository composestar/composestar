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

package Composestar.Core.COPPER2;

import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;

/**
 * Parent for the CpsParser
 * 
 * @author Michiel Hendriks
 */
public abstract class CpsParserBase extends Parser
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(COPPER.MODULE_NAME);

	/**
	 * Used for error reporting
	 */
	protected String sourceFile;

	public CpsParserBase(TokenStream input)
	{
		super(input);
	}

	public void setSourceFile(String srcfl)
	{
		sourceFile = srcfl;
	}

	public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	{
		String hdr = getErrorHeader(e);
		String msg = getErrorMessage(e, tokenNames);
		logger.error(new LogMessage(hdr + " " + msg, sourceFile, e.line, e.charPositionInLine));
	}

	public void emitErrorMessage(String msg)
	{
		logger.error(msg);
	}

	/**
	 * This will consume all tokens up to: } } EOF. It returns the content up to
	 * the end token as a string.
	 */
	protected String extractEmbeddedCode(TreeAdaptor adaptor, int tokenType)
	{
		Token start = (Token) input.LT(-1);
		matchAny(input);
		Token stop = null;

		while (input.LA(3) != Token.EOF)
		{
			stop = (Token) input.LT(1);
			matchAny(input);
		}
		stop = (Token) input.LT(1); // this should be a '}' will be matched by
		// the parser

		String result = input.toString(start.getTokenIndex() + 1, stop.getTokenIndex() - 1);
		return result;
	}
}
