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

package Composestar.Core.COPPER3;

import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;

/**
 * Parent for the CpsParser
 * 
 * @author Michiel Hendriks
 */
public abstract class CpsParserBase extends Parser
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.COPPER);

	/**
	 * Used for error reporting
	 */
	protected String sourceFile;

	/**
	 * Number of reported errors. Must be 0 or else COPPER will raise an
	 * exception.
	 */
	protected int errorCnt;

	public CpsParserBase(TokenStream input)
	{
		super(input);
	}

	/**
	 * @return the number of errors
	 */
	public int getErrorCnt()
	{
		return errorCnt;
	}

	/**
	 * Set the source file name. Used for error reporting
	 * 
	 * @param srcfl
	 */
	public void setSourceFile(String srcfl)
	{
		sourceFile = srcfl;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.antlr.runtime.BaseRecognizer#displayRecognitionError(java.lang.String
	 * [], org.antlr.runtime.RecognitionException)
	 */
	@Override
	public void displayRecognitionError(String[] tokenNames, RecognitionException e)
	{
		++errorCnt;
		// String hdr = getErrorHeader(e);
		String msg = getErrorMessage(e, tokenNames);
		logger.error(new LogMessage(msg, sourceFile, e.line, e.charPositionInLine));
	}

	/*
	 * (non-Javadoc)
	 * @see org.antlr.runtime.BaseRecognizer#emitErrorMessage(java.lang.String)
	 */
	@Override
	public void emitErrorMessage(String msg)
	{
		++errorCnt;
		logger.error(msg);
	}

	/**
	 * This will consume all tokens up to: } } EOF. It returns the content up to
	 * the end token as a string.
	 */
	protected String extractEmbeddedCode(TreeAdaptor adaptor)
	{

		Token start = input.LT(-1);
		matchAny(input);
		Token stop = null;

		while (input.LA(3) != Token.EOF)
		{
			matchAny(input);
		}
		stop = input.LT(1); // this should be a '}' will be matched by
		// the parser

		String result = input.toString(start.getTokenIndex() + 1, stop.getTokenIndex() - 1);
		return result;
	}

	/**
	 * Simple construction to avoid a target specific input.ToString vs
	 * input.toString
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	protected String inputToString(Token start, Token end)
	{
		return input.toString(start, end);
	}

	/**
	 * Another hack to create a target neutral grammar
	 * 
	 * @param ta
	 * @param token
	 * @param text
	 * @return
	 */
	protected Object adaptorCreate(TreeAdaptor ta, int token, String text)
	{
		return ta.create(token, text);
	}

	/**
	 * Another hack to create a target neutral grammar
	 * 
	 * @param ta
	 * @param token
	 * @param text
	 * @param base
	 * @return
	 */
	protected Object adaptorCreate(TreeAdaptor ta, int token, String text, Token base)
	{
		return ta.create(token, base, text);
	}

	/**
	 * Issue a warning
	 * 
	 * @param msg
	 * @param t
	 * @return
	 */
	protected void warning(String msg, Token t)
	{
		logger.warn(new LogMessage(msg, sourceFile, t.getLine(), t.getCharPositionInLine()));
	}
}
