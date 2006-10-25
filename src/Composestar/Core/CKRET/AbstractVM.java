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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Utils.Debug;

/**
 * 
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
		
		//String msg = "";
		for( Iterator ri = getResources().iterator(); ri.hasNext(); )
		{
			Resource res = (Resource) ri.next();
			Debug.out(Debug.MODE_DEBUG,"CKRET","Resource-op["+res.getName()+"]: "+res.fancySequence());
			//System.err.println(res);
			//msg+=res.toString()+"\n";
		}
		//JOptionPane.showMessageDialog(null,msg);
		
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
						Conflict conflict = new Conflict();
						conflict.setResource(res.getName());
						conflict.setMsg(constraint.getMessage());
						conflict.setSequence(res.fancySequence());
						conflict.setExpr(constraint.getPattern());
						conflicts.add(conflict);
						//conflicts.add("Resource " + res.getName() + ": " + constraint.getMessage() + " (" + res.sequence() + ")" );
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
				//boolean proceeded = false;
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
			Debug.out(Debug.MODE_WARNING,"CKRET","Filterset doesn't dispatch!");
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
