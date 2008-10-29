package Composestar.Core.CORE2;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * A conflict found by CORE
 * 
 * @author Arjan
 */
public class CoreConflict
{
	/*
	 * Types of conflicts
	 */

	/**
	 * Indicates the type of conflict
	 */
	private ConflictType type;

	/**
	 * The location of the conflict
	 */
	private RepositoryEntity location;

	/**
	 * The cause of the conflict
	 */
	private CoreConflict cause;

	public CoreConflict(ConflictType ctype, RepositoryEntity clocation)
	{
		type = ctype;
		location = clocation;
	}

	public CoreConflict(ConflictType ctype, RepositoryEntity clocation, CoreConflict ccause)
	{
		this(ctype, clocation);

		cause = ccause;
	}

	/**
	 * @return the cause
	 */
	public CoreConflict getCause()
	{
		return cause;
	}

	/**
	 * @return the location
	 */
	public RepositoryEntity getLocation()
	{
		return location;
	}

	/**
	 * @return the type
	 */
	public ConflictType getType()
	{
		return type;
	}

	public String getDescription()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(type.toString());
		if (location instanceof QualifiedRepositoryEntity)
		{
			sb.append(": ");
			sb.append(((QualifiedRepositoryEntity) location).getFullyQualifiedName());
		}
		return sb.toString();
	}

}
