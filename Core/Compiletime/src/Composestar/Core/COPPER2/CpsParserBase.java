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
import org.antlr.runtime.TokenStream;

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
	 * Stream location in the CPS file where the embedded source starts
	 */
	protected int embeddedSourceLoc = -1;

	/**
	 * Used for error reporting
	 */
	protected String sourceFile;

	public CpsParserBase(TokenStream input)
	{
		super(input);
	}

	/**
	 * Get the byte offset in the source file where the embedded source starts.
	 * When there is no embedded source this value will be 0.
	 */
	public int getEmbeddedSourceLoc()
	{
		return embeddedSourceLoc;
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
}
