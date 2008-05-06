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

import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;

import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.Exception.CpsSemanticException;
import Composestar.Core.FILTH.SyntacticOrderingConstraint;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;

/**
 * Base class for the Cps tree walker.
 * 
 * @author Michiel Hendriks
 */
public class CpsTreeWalkerBase extends TreeParser
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(COPPER.MODULE_NAME);

	/**
	 * Used for error reporting
	 */
	protected String sourceFile;

	/**
	 * Number of reported errors. Must be 0 or else COPPER will raise an
	 * exception.
	 */
	protected int errorCnt;

	/**
	 * The map containing filter module ordering constraints.
	 */
	protected Map<String, SyntacticOrderingConstraint> orderingconstraints;

	/**
	 * Contains the filter type mapping which is used to retrieve the filtertype
	 */
	protected FilterTypeMapping filterTypes;

	/**
	 * Will be set when legacyFilterTypes are used. Will be used to construct
	 * legacy custom filter types on the fly.
	 */
	protected DefaultFilterFactory filterFactory;

	public CpsTreeWalkerBase(TreeNodeStream input)
	{
		super(input);
	}

	/**
	 * Set the source file, used for error reporting.
	 * 
	 * @param srcfl
	 */
	public void setSourceFile(String srcfl)
	{
		sourceFile = srcfl;
	}

	/**
	 * Sets the map that will received the filter module ordering constraints
	 * 
	 * @param oc
	 */
	public void setOrderingConstraints(Map<String, SyntacticOrderingConstraint> oc)
	{
		orderingconstraints = oc;
	}

	/**
	 * Set the filter type map
	 * 
	 * @param ftm
	 */
	public void setFilterTypeMapping(FilterTypeMapping ftm)
	{
		filterTypes = ftm;
	}

	/**
	 * Set the filter factory to use to create custom legacy filters
	 * 
	 * @param lft
	 */
	public void setFilterFactory(DefaultFilterFactory lft)
	{
		filterFactory = lft;
	}

	/**
	 * @return the number of parse errors
	 */
	public int getErrorCnt()
	{
		return errorCnt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.antlr.runtime.BaseRecognizer#displayRecognitionError(java.lang.String[],
	 *      org.antlr.runtime.RecognitionException)
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
	 * 
	 * @see org.antlr.runtime.BaseRecognizer#emitErrorMessage(java.lang.String)
	 */
	@Override
	public void emitErrorMessage(String msg)
	{
		++errorCnt;
		logger.error(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.antlr.runtime.tree.TreeParser#getErrorMessage(org.antlr.runtime.RecognitionException,
	 *      java.lang.String[])
	 */
	@Override
	public String getErrorMessage(RecognitionException e, String[] tokenNames)
	{
		String res = super.getErrorMessage(e, tokenNames);
		if (e instanceof CpsSemanticException)
		{
			res = e.toString();
		}
		return res;
	}

	/**
	 * Sets the location information on the repository entity according to the
	 * token
	 */
	protected void setLocInfo(RepositoryEntity re, Token t)
	{
		re.setDescriptionFileName(sourceFile);
		if (t != null)
		{
			re.setDescriptionLineNumber(t.getLine());
			re.setDescriptionLinePosition(t.getCharPositionInLine());
		}
	}

	/**
	 * Sets the location information on the repository entity according to the
	 * token
	 */
	protected void setLocInfo(RepositoryEntity re, Tree t)
	{
		re.setDescriptionFileName(sourceFile);
		if (t != null)
		{
			re.setDescriptionLineNumber(t.getLine());
			re.setDescriptionLinePosition(t.getCharPositionInLine());
		}
	}
}
