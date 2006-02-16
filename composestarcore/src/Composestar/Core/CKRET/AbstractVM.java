/*
 * Created on Dec 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AbstractVM {

	private Map resources;

	public AbstractVM()
	{
	}
	
	public List analyze(List filters, List actions)
	{
		List conflicts = new ArrayList();
		resources = new HashMap();		
		//System.err.println("AVM: collecting operations...");
		List operations = this.getOperationsByActions(filters, actions);
		//System.err.println("AVM: processing operations...");
		for( Iterator oi = operations.iterator(); oi.hasNext(); )
		{
			Operation op = (Operation) oi.next();
			Resource res = getResource(op.getResource());
			res.add(op.getName());
		}
		//System.err.println("AVM: printing resources...");
		/*
		for( Iterator ri = getResources().iterator(); ri.hasNext(); )
		{
			Resource res = (Resource) ri.next();
			System.err.println(res);
		}
		*/
		//System.err.println("AVM: checking constraints...");
		for( Iterator ci = Repository.instance().getConstraints().iterator(); ci.hasNext(); )
		{
			Constraint constraint = (Constraint) ci.next();
			
			for( Iterator ri = resources.values().iterator(); ri.hasNext(); )
			{
				Resource res = (Resource) ri.next();
				if( constraint.getResource().equals("*") || constraint.getResource().equals(res.getName()))
				{
					if( constraint.conflicts(res.sequence()))
					{
						conflicts.add("Resource " + res.getName() + ": " + constraint.getMessage() + " (" + res.sequence() + ")" );
					}
				}
			}
		}
		//System.err.println("AVM: done...");
		return conflicts;
	}

	private Collection getResources()
	{
		return resources.values();
	}
	
	private Resource getResource(String name)
	{
		Resource resource = (Resource) resources.get(name);
		if( resource == null )
		{
			resource = new Resource(name);
			resources.put(name, resource);
		}
		return resource;
	}
	
	private List getOperationsByActions(List filters, List actions)
	{
		LinkedList header = new LinkedList();
		LinkedList footer = new LinkedList();
		
		Operation returnOp = null;
		
		for( int i = 0; i < actions.size(); i++ )
		{
			FilterAction action = (FilterAction) actions.get(i);
			List operations = action.getOperations((Filter) filters.get(i));

			for( Iterator operationIterator = operations.iterator(); operationIterator.hasNext(); )
			{
				boolean forked = false;
				boolean proceeded = false;
				Operation operation = (Operation) operationIterator.next();
				if( operation.isFork() )
				{
					while( operationIterator.hasNext() )
					{
						Operation op = (Operation) operationIterator.next();
						footer.addLast(op);
					}
				}
				else if( operation.isProceed())
				{
					int pos = 0;
					while( operationIterator.hasNext() )
					{
						
						Operation op = (Operation) operationIterator.next();
						footer.add(pos, op);
						pos++;
					}
				}
				else if( operation.isReturn() )
				{
					// only the first return op is used
					if( returnOp == null )
					{
						returnOp = operation;
						header.add(operation);
					}
				}
				else if( forked )
				{
					footer.addLast(operation);
				}
				else
				{
					header.addLast(operation);
				}
			}
		}
		
		if( returnOp == null )
		{
			Debug.out(Debug.MODE_WARNING,"SECRET","Filterset doesn't dispatch!");
		}
		else
		{
			int pos = header.indexOf(returnOp);
			header = new LinkedList(header.subList(0,pos));
			header.addAll(footer);
			//header.remove(returnOp);
		}
		
		return header;
	}

}
