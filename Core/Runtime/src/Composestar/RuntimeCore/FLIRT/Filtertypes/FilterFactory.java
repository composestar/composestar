package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.RuntimeCore.FLIRT.Filtertypes.FilterTypeRuntime;

/**
 * FilterFactory is implemented as an abstract factory.
 * 
 * It used to be a static factory, but the addition of custom filter types
 * required it to be refactored as an abstract class. This allows a concrete
 * FilterFactory to rely on .NET specific library calls.
 * 
 * Each concrete FilterFactory should be implemented as a singleton.
 */
public abstract class FilterFactory
{
	protected static FilterFactory instance = null;

	public static FilterFactory getInstance()
	{
		return instance;
	}

	protected FilterFactory() {}

	/**
	 * Returns a filter object of the requested type. 
	 * 
	 * @param filterType
	 * @return Instance of FilterTypeRuntime.
	 */
	public FilterTypeRuntime getFilterTypeFor(FilterType filterType)
	{
		if(filterType.getClass().equals(FilterType.DISPATCH))
			return(new Dispatch());
		else if(filterType.getClass().equals(FilterType.WAIT))
			return(new Wait());
		else if(filterType.getClass().equals(FilterType.ERROR))
			return(new ErrorFilter());
		else if(filterType.getClass().equals(FilterType.META))
			return(new Meta());
		else if(filterType.getClass().equals(FilterType.SEND))
			return(new Send());
		else if(filterType.getClass().equals(FilterType.PREPEND))
			return(new Prepend());
		else if(filterType.getClass().equals(FilterType.APPEND))
			return(new Append());
		else if(filterType.getClass().equals(FilterType.CUSTOM))
			return getCustomFilterTypeFor(filterType);
		else 
			return null;
	}

	/**
	 * This abstract method should create a custom filter object.
	 * Provide this environment specific implementation in a concrete FilterFactory.
	 * 
	 * @param filterType
	 * @return Instance of FilterTypeRuntime.
	 */
	protected abstract FilterTypeRuntime getCustomFilterTypeFor(FilterType filterType);
}
