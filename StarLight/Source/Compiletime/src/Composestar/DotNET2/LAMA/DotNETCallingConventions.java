package Composestar.DotNET2.LAMA;

import java.io.Serializable;

/**
 * This class represents the CallingConventions Enum in the .NET framework.
 */
public class DotNETCallingConventions implements Serializable
{
	private static final long serialVersionUID = -2916103173240390588L;

	public static final int Standard = 1;

	public static final int VarArgs = 2;

	public static final int Any = 3;

	public static final int HasThis = 32;

	public static final int ExplicitThis = 64;
}
