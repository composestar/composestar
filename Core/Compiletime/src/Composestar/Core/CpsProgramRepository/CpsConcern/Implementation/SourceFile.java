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

/**
 * @modelguid {1878A9B5-951D-401C-8D9D-5FCAAF385A0F}
 */
public class SourceFile extends Implementation
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1064681681786007517L;

	/**
	 * @modelguid {7E5D9CF8-CFC1-4215-BB26-EE6E2CA1307C}
	 */
	public String language;

	/**
	 * @modelguid {FA9431E2-571B-4845-BAA3-B49B7C236213}
	 */
	public String sourceFile;

	/**
	 * @modelguid {D9A20B21-D063-4332-ABFC-E299B38612C6}
	 * @roseuid 401FAA6802D3
	 */
	public SourceFile()
	{
		super();
	}

	/**
	 * @return java.lang.String
	 * @modelguid {C74D2944-108D-42F1-A947-8E651071E6C4}
	 * @roseuid 401FAA6802DC
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * @param languageValue
	 * @modelguid {3885C704-E309-48FB-B90B-125A273C770A}
	 * @roseuid 401FAA6802DD
	 */
	public void setLanguage(String languageValue)
	{
		this.language = languageValue;
	}

	/**
	 * @return java.lang.String
	 * @modelguid {7C748E43-83F4-457E-ACFD-09753ACA01D9}
	 * @roseuid 401FAA6802E7
	 */
	public String getSourceFile()
	{
		return sourceFile;
	}

	/**
	 * @param sourceFileValue
	 * @modelguid {257D3B1B-71F3-4C3E-AC4D-825803A54E79}
	 * @roseuid 401FAA6802F0
	 */
	public void setSourceFile(String sourceFileValue)
	{
		this.sourceFile = sourceFileValue;
	}
}
