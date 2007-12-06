package Composestar.Core.INCRE;

import java.io.Serializable;

import Composestar.Core.Exception.ModuleException;

public class Dependency implements Serializable
{
	private static final long serialVersionUID = -6858098114389292052L;

	protected String name;

	protected Path mypath = new Path();

	public boolean store;

	public boolean lookup;

	/**
	 * @roseuid 42206A590280
	 * @param inName
	 */
	public Dependency(String inName)
	{
		name = inName;
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
		return name;
	}

	public Path getPath()
	{
		return mypath;
	}

	public void addNode(Node n)
	{
		mypath.addNode(n);
	}
}
