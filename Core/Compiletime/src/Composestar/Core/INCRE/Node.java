package Composestar.Core.INCRE;

import java.io.Serializable;

import Composestar.Core.Exception.ModuleException;

public abstract class Node implements Serializable
{
	// Serves as a reference to a field
	// dynamic object, method or configuration
	protected String reference = "";

	public Node(String ref)
	{
		reference = ref;
	}

	public String getReference()
	{
		return reference;
	}

	// Gathers the referenced object (reference) from the input object (obj)
	abstract Object visit(Object obj) throws ModuleException;

	abstract String getUniqueID(Object obj);
}
