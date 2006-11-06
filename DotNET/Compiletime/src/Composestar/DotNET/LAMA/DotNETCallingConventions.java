package Composestar.DotNET.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * This class represents the CallingConventions Enum in the .NET framework.
 */
public class DotNETCallingConventions implements SerializableRepositoryEntity
{
	public static final int Standard = 1;

	public static final int VarArgs = 2;

	public static final int Any = 3;

	public static final int HasThis = 32;

	public static final int ExplicitThis = 64;
}
