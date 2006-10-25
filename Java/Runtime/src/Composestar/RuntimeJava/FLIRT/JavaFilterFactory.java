package Composestar.RuntimeJava.FLIRT;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;

public class JavaFilterFactory extends FilterFactory
{

	public JavaFilterFactory()
	{
		instance = this;
	}

	protected FilterTypeRuntime getCustomFilterTypeFor(FilterType filterType)
	{
		return null;
	}

}
