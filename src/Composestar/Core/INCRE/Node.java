package Composestar.Core.INCRE;

import java.io.Serializable;

import Composestar.Core.Exception.ModuleException;

public class Node implements Serializable
{
	protected String objectref = "";

	public Node(String objectref)
	{
		this.objectref = objectref;
	}

	public String getObjectRef()
	{
		return objectref;
	}

	public Object visit(Object obj) throws ModuleException
	{
		return null;
	}
	
	public String getUniqueID(Object obj){
		return "";
	}
}

	
