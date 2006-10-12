/*
 * Created on Dec 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExecutionAnalysis implements Serializable{

    private List filters;
	private int execution;
	
	List actions = new ArrayList();
	
	public ExecutionAnalysis(Concern concern, List filters, int execution)
	{
        Concern concern1 = concern;
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
