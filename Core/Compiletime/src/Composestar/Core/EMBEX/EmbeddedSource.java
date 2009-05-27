/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.EMBEX;

import java.io.Serializable;

import Composestar.Core.CpsRepository2.Meta.SourceInformation;

/**
 * Embedded source code as found in the Cps file
 * 
 * @author Michiel Hendriks
 */
public class EmbeddedSource implements Serializable
{
	private static final long serialVersionUID = -1707063912541624495L;

	/**
	 * The programming language this code is written in. Can be null.
	 */
	protected String language;

	/**
	 * The desired filename. Can be null.
	 */
	protected String filename;

	/**
	 * The source code
	 */
	protected String code = "";

	/**
	 * Information where this embedded source came from.
	 */
	protected SourceInformation sourceInformation;

	/**
	 * 
	 */
	public EmbeddedSource()
	{
		this(null, null, "");
	}

	/**
	 * @param sourceCode
	 */
	public EmbeddedSource(String sourceCode)
	{
		this(null, null, sourceCode);
	}

	/**
	 * @param sourceFilename
	 * @param sourceCode
	 */
	public EmbeddedSource(String sourceFilename, String sourceCode)
	{
		this(null, sourceFilename, sourceCode);
	}

	/**
	 * @param sourceLanguage
	 * @param sourceFilename
	 * @param sourceCode
	 */
	public EmbeddedSource(String sourceLanguage, String sourceFilename, String sourceCode)
	{
		if (sourceCode == null)
		{
			sourceCode = "";
		}
		language = sourceLanguage;
		filename = sourceFilename;
		code = sourceCode;
	}

	/**
	 * @return the language
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * @param value the language to set
	 */
	public void setLanguage(String value)
	{
		language = value;
	}

	/**
	 * @return the filename
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 * @param value the filename to set
	 */
	public void setFilename(String value)
	{
		filename = value;
	}

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param value the code to set
	 */
	public void setCode(String value)
	{
		if (value == null)
		{
			value = "";
		}
		code = value;
	}

	/**
	 * @param value
	 */
	public void setSourceInformation(SourceInformation value)
	{
		sourceInformation = value;
	}

	/**
	 * @return
	 */
	public SourceInformation getSourceInformation()
	{
		return sourceInformation;
	}
}
