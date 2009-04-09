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

import java.io.File;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;

import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Meta.FileInformation;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintFactory;
import Composestar.Core.EMBEX.EmbeddedSources;
import Composestar.Core.Exception.CpsSemanticException;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.LogMessage;

/**
 * Base class for the Cps tree walker for the CpsRepository2
 * 
 * @author Michiel Hendriks
 */
public abstract class CpsTreeWalkerBase extends TreeParser
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.COPPER);

	/**
	 * The repository to add the elements to
	 */
	protected Repository repository;

	/**
	 * The reference manager
	 */
	protected ReferenceManager references;

	/**
	 * Used for error reporting
	 */
	protected FileInformation fileInformation;

	/**
	 * Number of reported errors. Must be 0 or else COPPER will raise an
	 * exception.
	 */
	protected int errorCnt;

	/**
	 * Contains the filter type mapping which is used to retrieve the filtertype
	 */
	protected FilterTypeMapping filterTypes;

	/**
	 * ...
	 */
	protected FilterTypeFactory filterTypeFactory;

	/**
	 * 
	 */
	protected EmbeddedSources embeddedSourceManager;

	/**
	 * 
	 */
	protected ConstraintFactory constraintFactory;

	public CpsTreeWalkerBase(TreeNodeStream input)
	{
		super(input);
	}

	/**
	 * Set the source file, used for error reporting.
	 * 
	 * @param srcfl
	 */
	public void setSourceFile(File srcfl)
	{
		fileInformation = new FileInformation(srcfl);
	}

	/**
	 * @param repositoryEntities
	 */
	public void setRepository(Repository val)
	{
		repository = val;
	}

	/**
	 * @param refman
	 */
	public void setReferenceManager(ReferenceManager refman)
	{
		references = refman;
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
	public void setFilterFactory(FilterTypeFactory lft)
	{
		filterTypeFactory = lft;
	}

	/**
	 * @param srcMan
	 */
	public void setEmbeddedSourceManager(EmbeddedSources srcMan)
	{
		embeddedSourceManager = srcMan;
	}

	/**
	 * @param constraintFactory the constraintFactory to set
	 */
	public void setConstraintFactory(ConstraintFactory factory)
	{
		constraintFactory = factory;
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
		logger.error(new LogMessage(msg, fileInformation.getLocation().toString(), e.line, e.charPositionInLine));
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

	/*
	 * (non-Javadoc)
	 * @seeorg.antlr.runtime.tree.TreeParser#getErrorMessage(org.antlr.runtime.
	 * RecognitionException, java.lang.String[])
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
		SourceInformation srcInfo = new SourceInformation(fileInformation);
		re.setSourceInformation(srcInfo);
		if (t != null)
		{
			srcInfo.setLine(t.getLine());
			srcInfo.setLinePos(t.getCharPositionInLine());
		}
	}

	/**
	 * Sets the location information on the repository entity according to the
	 * token
	 */
	protected void setLocInfo(RepositoryEntity re, Tree t)
	{
		SourceInformation srcInfo = new SourceInformation(fileInformation);
		re.setSourceInformation(srcInfo);
		if (t != null)
		{
			srcInfo.setLine(t.getLine());
			srcInfo.setLinePos(t.getCharPositionInLine());
		}
	}

	/**
	 * Converts the input string to the unescaped counter part.
	 * 
	 * @param value
	 * @return
	 */
	public static final String unescapeLiteral(String value)
	{
		StringBuffer sb = new StringBuffer(value.length());
		boolean escaped = false;
		for (int i = 0; i < value.length(); ++i)
		{
			char c = value.charAt(i);
			if (escaped)
			{
				escaped = false;
				switch (c)
				{
					case 'r':
						sb.append('\r');
						break;
					case 'n':
						sb.append('\n');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case '\\':
						sb.append('\\');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '"':
						sb.append('"');
						break;
					case 'b':
						sb.append('\b');
						break;
					/*
					 * case 'u': // ... break;
					 */
					default:
						sb.append(c);
				}
			}
			else if (c == '\\')
			{
				escaped = true;
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
