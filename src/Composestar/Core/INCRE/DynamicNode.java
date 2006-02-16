package Composestar.Core.INCRE;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

public class DynamicNode extends Node
{
	public DynamicNode(String objectref)
	{
		super(objectref);
	}

	public Object visit(Object obj)
	{
		// return the dynamic object
		if(obj instanceof RepositoryEntity)
		{
			String objtype = ((RepositoryEntity)obj).getDynObject(objectref).getClass().getName();
			return ((RepositoryEntity)obj).getDynObject(objectref);
		}
		else return null;
	}
}
