package Composestar.Core.INCRE;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

public class DynamicNode extends Node
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1031299707125956098L;

	public DynamicNode(String ref)
	{
		super(ref);
	}

	/**
	 * @return the referenced dynamic object of input
	 * @param Object obj
	 */
	public Object visit(Object obj)
	{
		if (obj instanceof RepositoryEntity)
		{
			return ((RepositoryEntity) obj).getDynObject(reference);
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return an unique id for a referenced dynamic object
	 */
	public String getUniqueID(Object obj)
	{
		return obj.hashCode() + "." + this.reference;
	}
}
