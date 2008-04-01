package Composestar.RuntimeJava.FLIRT;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.LegacyCustomFilterType;
import Composestar.RuntimeCore.FLIRT.Filtertypes.FilterFactory;
import Composestar.RuntimeCore.FLIRT.Filtertypes.FilterTypeRuntime;
import Composestar.RuntimeCore.Utils.Debug;

public class JavaFilterFactory extends FilterFactory
{
	public JavaFilterFactory()
	{
		setInstance(this);
	}

	@Override
	protected FilterTypeRuntime getCustomFilterTypeFor(FilterType filterType)
	{
		String filtername = ((LegacyCustomFilterType) filterType).getClassName();
		try
		{
			Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(filtername);
			if (FilterTypeRuntime.class.isAssignableFrom(cls))
			{
				FilterTypeRuntime ftr = (FilterTypeRuntime) cls.newInstance();
				return ftr;
			}
		}
		catch (ClassNotFoundException e)
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", e.getMessage());
			return null;
		}
		catch (InstantiationException e)
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", e.getMessage());
			return null;
		}
		catch (IllegalAccessException e)
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", e.getMessage());
			return null;
		}
		Debug.out(Debug.MODE_ERROR, "FLIRT", "Filter type " + ((LegacyCustomFilterType) filterType).getName()
				+ " not found");
		return null;
	}
}
