package Composestar.Core.INCRE;

import Composestar.Core.Exception.ModuleException;

public class ObjectDependency extends Dependency
{
	private static final long serialVersionUID = 8367947175676592879L;

	public ObjectDependency(String name)
	{
		super(name);
	}

	public Object getDepObject(Object obj) throws ModuleException
	{
		// follow the path and return final object
		return mypath.follow(obj);
	}
}
