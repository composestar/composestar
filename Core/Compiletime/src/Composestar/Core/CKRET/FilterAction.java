/*
 * Created on Dec 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FilterAction implements Serializable{
	
	private String name;
	boolean eos = false;
	private List operations;
	
	private static int counter;
	
	private int uniqid;
	
	public FilterAction(String name)
	{
		this.name = name;
		this.operations = new ArrayList();
	}
	
	public void init(Filter filter)
	{
	}
	
	public boolean isEndOfSet()
	{
		return this.eos;
	}
	
	public void setEndOfSet()
	{
		this.eos = true;
	}
	
	public void addOperation(Operation op)
	{
		//System.err.println("Action " + this.name + ": added operation " + op.getResource() + "." + op.getName());
		this.operations.add(op);
	}
	
	
	private List getMatchingOperations(Filter filter)
	{
		FilterInfo fi = new FilterInfo(filter);
		return fi.getReadOperations();	
	}
	
	public List getOperations(Filter filter)
	{
		List operations = new ArrayList(getMatchingOperations(filter));
		/*
		CkretRepository repository = CkretRepository.instance();
		List operations = repository.getAllResourcesForAction(this.name);
		
		ArrayList v =  new ArrayList();
		v.add(new Operation("w","target"));
				
		return v;
		*/
		operations.addAll(this.operations);
		return operations;
	}

	public String toString()
	{
		return this.name;
	}

	public boolean equals(Object o)
	{
        return this.toString() == null && o.toString() == null || (o.toString().equals(this.toString()));
		}

}
