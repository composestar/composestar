package Composestar.Core.FILTH2;

import java.util.Arrays;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * A single constraint definition
 * 
 * @author Michiel Hendriks
 */
public class ConstraintDefinition extends RepositoryEntity
{
	private static final long serialVersionUID = 8720984472602046059L;

	/**
	 * The constraint type, like pre or include
	 */
	protected String type;

	/**
	 * The arguments for the constraint
	 */
	protected String[] args;

	public ConstraintDefinition(String constraintType, String... arguments)
	{
		type = constraintType;
		args = arguments.clone();
	}

	/**
	 * @return the constraint type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @return the constraint arguments
	 */
	public String[] getArguments()
	{
		return args.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result + (type == null ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final ConstraintDefinition other = (ConstraintDefinition) obj;
		if (!Arrays.equals(args, other.args))
		{
			return false;
		}
		if (type == null)
		{
			if (other.type != null)
			{
				return false;
			}
		}
		else if (!type.equals(other.type))
		{
			return false;
		}
		return true;
	}

}
