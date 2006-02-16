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
    */
	public Dependency(String name) 
	{
		this.name = name;
	}
   
   /**
    * @return Object
    * @roseuid 420A220F0128
    */
	public Object getDepObject(Object obj) throws ModuleException
	{
		return null;
	}

	public String getName()
	{
		return name;
	}

	public void addNode(Node n)
	{
		//Debug.out(Debug.MODE_DEBUG, "INCRE","added node "+n.getObjectRef() +" to "+name);
		mypath.addNode(n);
	}
}
