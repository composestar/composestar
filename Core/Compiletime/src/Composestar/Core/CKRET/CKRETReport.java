/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.io.Serializable;
import java.util.ArrayList;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FILTH.FilterModuleOrder;

/**
 * Semantic Reasoning Report generated by CKRET
 */
public class CKRETReport implements Serializable
{
	private FilterModuleOrder order;
	private FilterSetAnalysis analysis;
	private boolean selected;

	public CKRETReport(FilterModuleOrder order, FilterSetAnalysis analysis, boolean selected)
	{
		this.order = order;
		this.analysis = analysis;
		this.selected = selected;

		this.addToConcern();
	}

	/**
	 * Adds this report to the set of reports of a concern
	 * The set is the dynamic object called 'CKRETReports'
	 */
	public void addToConcern()
	{
		Concern c = this.analysis.getConcern();
		ArrayList reports = (ArrayList)c.getDynObject("CKRETReports");
		// if this is not available yet, create it
		if (reports == null) 
		{
			reports = new ArrayList(); 
			c.addDynObject("CKRETReports", reports);
		}

		reports.add(this);
	}

	public FilterModuleOrder getOrder()
	{
		return this.order;		
	}

	public boolean getSelected()
	{
		return this.selected;		
	}

	public FilterSetAnalysis getAnalysis()
	{
		return this.analysis;		
	}
}

