package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.RuntimeCore.CODER.Model.*;

/**
 * Summary description for CustomFilter.
 */
public abstract class CustomFilter extends FilterTypeRuntime  implements DebuggableCustomFilterType
{

	public CustomFilter()
	{
	}

	public abstract String getName();

}
