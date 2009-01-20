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

package Composestar.Core.Exception;

import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * An exception thrown by the CpsTreeWalker (COPPER) when there's a semantic
 * error in the AST. When you throw an instance of this exception always make
 * sure you surround it with a try..catch in the same code scope:
 * 
 * <pre>
 * try 
 * {
 *  ...
 *  ...
 * 	throw new CpsSemanticException(...);
 * }
 * catch (RecognitionException re) {
 * 	reportError(re);
 * 	recover(input,re);
 * }
 * </pre>
 * 
 * This way there will be proper error recovery and multiple semnatic errors can
 * be reported in a single parse iteration.
 * 
 * @author Michiel Hendriks
 */
public class CpsSemanticException extends RecognitionException
{
	private static final long serialVersionUID = 1678078507939703989L;

	protected String message;

	/**
	 * @param message
	 */
	public CpsSemanticException(String inMessage, IntStream input)
	{
		super(input);
		message = inMessage;
	}

	public CpsSemanticException(String inMessage, IntStream inInput, Tree tree)
	{
		super();
		message = inMessage;
		input = inInput;
		node = tree;
		line = tree.getLine();
		charPositionInLine = tree.getCharPositionInLine();
		if (node instanceof CommonTree)
		{
			token = ((CommonTree) node).token;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString()
	{
		return message;
	}
}
