/*
 * Created on Mar 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.FILTH;

/**
 * @author Isti
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class FilterModuleOrder extends Composestar.Core.RepositoryImplementation.ContextRepositoryEntity
{
	public Vector _order;
	
	public FilterModuleOrder()
	{
		_order = new Vector();

	}
	public FilterModuleOrder(Concern parent){
		super(parent);
	}
	
	public FilterModuleOrder(LinkedList order)
	{
		_order = new Vector();
		int size = order.size();
		for( int i = 0; i < size; i++  ) 
		{
			FilterModuleReference fmr = (FilterModuleReference) order.get(i);
			_order.addElement(fmr.getRef().getQualifiedName());
			//_order.addElement(order.get(i));
		}
	}

	public void addFilterModule(FilterModuleReference fmr)
	{
		_order.addElement(fmr.getQualifiedName());
	}


	public List orderAsList(){
		return _order;
	}
	
	public boolean equals(Object other)
	{
        return other instanceof FilterModuleOrder && this.orderAsList().equals(((FilterModuleOrder) other).orderAsList());
	}
	
	public String toString()
	{
		String retval = "";
		List modules = orderAsList();
		for( int i = 0; i < modules.size()-1; i++ )
		{
			retval += (String) modules.get(i);
			if( i < modules.size()-2 )
				retval += " + ";
		}
		return retval;
	}
}
