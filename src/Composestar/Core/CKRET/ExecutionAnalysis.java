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

import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;

/**
 * 
 */
public class ExecutionAnalysis implements Serializable{

    private List filters;
	private int execution;
	
	List actions = new ArrayList();
	
	public ExecutionAnalysis(Concern concern, List filters, int execution)
	{
        //Concern concern1 = concern;
        this.filters = filters;
		this.execution = execution;
	}
	
	public boolean process()
	{
		boolean endofset = false;

		for( int i = 0; i < filters.size(); i++ )
		{
			if( !endofset )
			{
				FilterAction fa;
				if( ((execution >> i) & 1) == 1)
					fa = Repository.getAction((Filter) filters.get(i), true);
				else
					fa = Repository.getAction((Filter) filters.get(i), false);
			
				if( fa.isEndOfSet() )
					endofset = true;
				actions.add(fa);
			}
		}
		return endofset;
	}
	

	public List analyze()
	{
		AbstractVM avm = new AbstractVM();
		return avm.analyze(filters, actions);
	}
	
	public List getActions()
	{
		return this.actions;
	}
	
	public boolean equals(Object o)
	{
		if( o instanceof ExecutionAnalysis )
		{
			List actions = ((ExecutionAnalysis) o).getActions();
			if( actions.size() != this.actions.size() )
				return false;
			for( int i = 0; i < actions.size(); i++ )
				if( ! actions.get(i).equals(this.actions.get(i)))
					return false;
		}
		else
		{
			return false;
		}
		return true;
	}
}
