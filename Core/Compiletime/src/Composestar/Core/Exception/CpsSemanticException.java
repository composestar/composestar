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

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Logging.LocationProvider;

/**
 * An exception thrown by the CpsTreeWalker (COPPER) when there's a semantic
 * error in the AST. This is a RuntimeException because ANTLRv3.01 does not
 * support the "throws" construction preventing use to throw our preferred
 * exception type. COPPER will catch this exception and rethrow it as a
 * ModuleException.
 * 
 * @author Michiel Hendriks
 */
public class CpsSemanticException extends RuntimeException implements LocationProvider
{
	private static final long serialVersionUID = 1678078507939703989L;

	protected String filename;

	protected int line;

	protected int charOnLine;

	/**
	 * @param message
	 */
	public CpsSemanticException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public CpsSemanticException(String message, Throwable throwable)
	{
		super(message, throwable);
	}

	/**
	 * @param message
	 * @param re
	 */
	public CpsSemanticException(String message, RepositoryEntity re)
	{
		this(message, re, null);
	}

	/**
	 * @param message
	 * @param re
	 * @param throwable
	 */
	public CpsSemanticException(String message, RepositoryEntity re, Throwable throwable)
	{
		super(message, throwable);
		filename = re.getDescriptionFileName();
		line = re.getDescriptionLineNumber();
		charOnLine = re.getDescriptionLinePosition();
	}

	public CpsSemanticException(String message, String filename, Token token)
	{
		this(message, filename, token, null);
	}

	public CpsSemanticException(String message, String infn, Token token, Throwable throwable)
	{
		super(message, throwable);
		filename = infn;
		line = token.getLine();
		charOnLine = token.getCharPositionInLine();
	}

	public CpsSemanticException(String message, String filename, Tree tree)
	{
		this(message, filename, tree, null);
	}

	public CpsSemanticException(String message, String infn, Tree tree, Throwable throwable)
	{
		super(message, throwable);
		filename = infn;
		line = tree.getLine();
		charOnLine = tree.getCharPositionInLine();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (filename != null)
		{
			sb.append(filename);
			sb.append(" #");
			sb.append(line);
			sb.append(",");
			sb.append(charOnLine);
			sb.append(": ");
		}
		sb.append(getMessage());
		return sb.toString();
	}

	public String getFilename()
	{
		return filename;
	}

	public int getLineNumber()
	{
		return line;
	}

	public int getLinePosition()
	{
		return charOnLine;
	}
}
