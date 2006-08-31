/*
 * Created on Dec 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FilterSetAnalysis implements Serializable {

	private Concern concern;
	private FilterModuleOrder order;
	
	private List filters;
	private List executions;
	
	private Map conflictingExecutions;
	

	public FilterSetAnalysis(Concern concern, FilterModuleOrder order)
	{
		this.concern = concern;
		this.order = order;
		//this.messages = new HashMap();
		this.executions = new ArrayList();
		this.conflictingExecutions = new HashMap();
	}

	public List getFilters()
	{
		return this.filters;
	}
	
	public void analyze()
	{
		this.filters = getFilterList(this.order.orderAsList());
		int numFilters = filters.size();
		
		int numPaths = (int) Math.pow(2,numFilters);
		for( int i = 0; i < numPaths; i++ )
		{
			ExecutionAnalysis execution = new ExecutionAnalysis(concern, filters, i);
			if( execution.process() )
			{
				if( !this.executions.contains(execution) )
					this.executions.add(execution);
			}
			else
			{
				//skipping execution for it is not feasible
				//System.err.println("Execution " + i + " has no end of filterset");
			}
		}
		for( Iterator it = this.executions.iterator(); it.hasNext(); )
		{
			ExecutionAnalysis execution = (ExecutionAnalysis) it.next();
			List conflicts = execution.analyze();
			if( conflicts.size() > 0 )
				this.conflictingExecutions.put(execution, conflicts);
		}
	}
	
	public int numConflictingExecutions()
	{
		return this.conflictingExecutions.size();
	}

	public Map executionConflicts()
	{
		return this.conflictingExecutions;
	}

	protected static List getFilterList(List filterModules)
	{
		List list = new ArrayList();
	
		Iterator itr = filterModules.iterator();
		while (itr.hasNext())
		{
			String name = (String) itr.next();
			//if( !(name.equals("CpsDefaultInnerDispatchConcern.CpsDefaultInnerDispatchFilterModule")))
			{
				FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID(name);
				Iterator ifItr = fm.getInputFilterIterator();
		
				while (ifItr.hasNext())
				{
					Filter f = (Filter) ifItr.next();
					list.add(f);
				}
			}
		}
		return list;
	}
	
	public Concern getConcern()
	{
		return this.concern;
	}
}
