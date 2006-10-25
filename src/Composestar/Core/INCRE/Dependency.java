package Composestar.Core.INCRE;

import Composestar.Core.Exception.ModuleException;

public class Dependency 
{
	protected String name;
	protected Path mypath = new Path();
	public boolean store = false;
	public boolean lookup = false;
	   
	/**
	 * @roseuid 42206A590280
     * @param name
	 */
	public Dependency(String name) 
	{
		this.name = name;
	}
	   
	/**
	  * @return Object
	  * @roseuid 420A220F0128
     * @param obj
	  */
	public Object getDepObject(Object obj) throws ModuleException
	{
		return null;
	}

	public String getName()
	{
		return this.name;
	}

	public Path getPath()
	{
		return this.mypath;
	}
		
	public void addNode(Node n)
	{
		this.mypath.addNode(n);
	}
}