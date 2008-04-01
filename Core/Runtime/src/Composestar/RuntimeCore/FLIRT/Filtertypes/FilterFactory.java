package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.FilterTypeNames;

/**
 * FilterFactory is implemented as an abstract factory. It used to be a static
 * factory, but the addition of custom filter types required it to be refactored
 * as an abstract class. This allows a concrete FilterFactory to rely on .NET
 * specific library calls. Each concrete FilterFactory should be implemented as
 * a singleton.
 */
public abstract class FilterFactory
{
	private static FilterFactory instance;

	public static FilterFactory getInstance()
	{
		return instance;
	}

	protected static void setInstance(FilterFactory ffin)
	{
		instance = ffin;
	}

	protected FilterFactory()
	{}

	/**
	 * Returns a filter object of the requested type.
	 * 
	 * @param filterType
	 * @return Instance of FilterTypeRuntime.
	 */
	public FilterTypeRuntime getFilterTypeFor(FilterType filterType)
	{
		if (filterType.getType().equals(FilterTypeNames.DISPATCH))
		{
			return (new Dispatch());
		}
		else if (filterType.getType().equals(FilterTypeNames.WAIT))
		{
			return (new Wait());
		}
		else if (filterType.getType().equals(FilterTypeNames.ERROR))
		{
			return (new ErrorFilter());
		}
		else if (filterType.getType().equals(FilterTypeNames.META))
		{
			return (new Meta());
		}
		else if (filterType.getType().equals(FilterTypeNames.SEND))
		{
			return (new Send());
		}
		else if (filterType.getType().equals(FilterTypeNames.PREPEND))
		{
			return (new Prepend());
		}
		else if (filterType.getType().equals(FilterTypeNames.APPEND))
		{
			return (new Append());
		}
		else if (filterType.getType().equals(FilterTypeNames.CUSTOM))
		{
			return getCustomFilterTypeFor(filterType);
		}
		else
		{
			return null;
		}
	}

	/**
	 * This abstract method should create a custom filter object. Provide this
	 * environment specific implementation in a concrete FilterFactory.
	 * 
	 * @param filterType
	 * @return Instance of FilterTypeRuntime.
	 */
	protected abstract FilterTypeRuntime getCustomFilterTypeFor(FilterType filterType);
}
