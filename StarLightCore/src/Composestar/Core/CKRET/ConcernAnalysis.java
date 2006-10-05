/*
 * Created on Dec 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.util.HashMap;
import java.util.Map;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ConcernAnalysis {
	private Concern concern;
	
	private Map orders;

	
	public ConcernAnalysis(Concern concern)
	{
		this.concern = concern;
		this.orders =  new HashMap();
	}

	public Concern getConcern()
	{
		return this.concern;
	}
	
	
	protected boolean checkOrder(FilterModuleOrder order, boolean isSelected)
	{
		FilterSetAnalysis oa = new FilterSetAnalysis(this.concern, order);
		
		oa.analyze();

		this.orders.put( order, oa );
		
		switch(SECRET.MODE)
		{
			case 0:
				SECRET.getReporter().reportOrder(order, oa, isSelected, false);
				break;
			case 1:
				SECRET.getReporter().reportOrder(order, oa, isSelected, false);
				break;
				
			case 2:
				if( oa.numConflictingExecutions() == 0 )
					SECRET.getReporter().reportOrder(order, oa, isSelected, false);
				else
					SECRET.getReporter().reportOrder(order, oa, false, false);
				break;
		}
		
		return (oa.numConflictingExecutions() == 0);
	}
	
}
