/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Implementation;

import java.util.Vector;

/**
 * @modelguid {ED820E47-7CAC-4364-A69D-8081EE7C219F}
 */
public class Source extends Implementation
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3940707016510175491L;

	/**
	 * @modelguid {B0CD8BE6-72C8-4ABB-BFB8-4A2F704062CD}
	 */
	public String language;

	/**
	 * @modelguid {AAFEDDE6-9929-41C5-BF1E-FBF39245155D}
	 */
	private transient String source;

	public String sourceFile;

	public String className;

	/**
	 * @modelguid {B9B6C216-CB8F-4E10-8850-EE7863636FC1}
	 * @roseuid 401FAA680259
	 */
	public Source()
	{
		super();
	}

	/**
	 * @return java.lang.String
	 * @modelguid {8653394A-B974-4F90-BAAE-0F3E0F39727F}
	 * @roseuid 401FAA680264
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * @param languageValue
	 * @modelguid {F00D3749-0F6E-4C96-A7CA-BCEFF1BB8255}
	 * @roseuid 401FAA680265
	 */
	public void setLanguage(String languageValue)
	{
		this.language = languageValue;
	}

	/**
	 * @return java.lang.String
	 * @modelguid {C8582A6C-306E-411C-96F8-2162F190DE1F}
	 * @roseuid 401FAA68026F
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * @param sourceValue
	 * @modelguid {EB15BBC3-CD49-428A-B365-9087E17EF651}
	 * @roseuid 401FAA680278
	 */
	public void setSource(String sourceValue)
	{
		this.source = sourceValue;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 40ADE10401DF
	 */
	public String getSourceFile()
	{
		return sourceFile;
	}

	/**
	 * @param sourceFileValue
	 * @roseuid 40ADE10401E9
	 */
	public void setSourceFile(String sourceFileValue)
	{
		this.sourceFile = sourceFileValue;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(Vector qualifiedClass)
	{
		if (!qualifiedClass.isEmpty())
		{
			className = (String) qualifiedClass.elementAt(0);
		}
		for (int i = 1; i < qualifiedClass.size(); i++)
		{
			className += '.' + (String) qualifiedClass.elementAt(i);
		}
	}
}
