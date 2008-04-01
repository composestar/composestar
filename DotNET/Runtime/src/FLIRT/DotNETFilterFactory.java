package Composestar.RuntimeDotNET.FLIRT.Filtertypes;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.LegacyCustomFilterType;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.Utils.Debug;

import System.Reflection.Assembly;
import System.Reflection.AssemblyName;
import System.Type;

/**
 * .NET specific FilterFactory for the creation of custom filter types.
 * This concrete filter factory is implemented as a singleton.
 * 
 * @author Wim Minnen and Olaf Conradi
 */
public class DotNETFilterFactory extends FilterFactory
{
	public DotNETFilterFactory() 
	{
		setInstance(this);
	}

	/**
	 * Creates an instance of the requested custom filter, if found. 
	 * 
	 * Searches the referenced DLLs of the entry assembly (base program in
	 * default appdomain) for all classes which inherit from CustomFilterType.
	 * 
	 * @param filterType
	 * @return Instance of FilterTypeRuntime.
	 */
	protected FilterTypeRuntime getCustomFilterTypeFor(FilterType filterType)
	{
		String filterName = ((LegacyCustomFilterType) filterType).getName();
			
		AssemblyName[] ans = Assembly.GetEntryAssembly().GetReferencedAssemblies();
		
		// Search referenced assemblies for types which inherit from CustomFilter.
		for( int i = 0; i < ans.length; i++ ) 
		{
			// TODO should I put a try...catch block here?
			Assembly a = Assembly.Load( ans[i] );

			Type[] ts = a.GetTypes();
			for( int j = 0; j < ts.length; j++ ) 
			{
				String name = ts[j].get_Name();
				boolean isFilter = ts[j].IsSubclassOf( Class.ToType(CustomFilter.class) );
				if( isFilter )
				{
					if(Debug.SHOULD_DEBUG) Debug.out( Debug.MODE_INFORMATION, "FLIRT", "Found a custom filter in class '" +name+ "'." );

					try
					{
						CustomFilter cf = (CustomFilter) System.Activator.CreateInstance( ts[j] );
						// Check if the found custom filter is the one we are looking for.
						if( cf.getName().equalsIgnoreCase( filterName ) )
						{
							if(Debug.SHOULD_DEBUG) Debug.out( Debug.MODE_INFORMATION, "FLIRT", "Found custom filter '" +cf.getName()+ "' in class '" +name+ "'." );
							return cf;
						}
						else if(Debug.SHOULD_DEBUG) Debug.out( Debug.MODE_INFORMATION, "FLIRT", "Custom filter '" +cf.getName()+ "' in class '" +name+ "' is not the one we are looking for." );
					}
					catch (Exception e)
					{
						if(Debug.SHOULD_DEBUG) Debug.out( Debug.MODE_INFORMATION, "FLIRT", "Could not instantiate filter from class '" +name+ "'." );

						// TODO Should I re-throw this exception?
						// It is referenced, but it might not be the filter we're looking for.
						//throw e;
					}
				}
			}
		}

		// Until we define Debug.MODE_ERROR
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_ERROR, "FLIRT", "Could not load filter type '" +filterName+ "'." );
		// TODO Throw an exception. (It does get thrown in RepositoryLinker.java ,actually, because of null return).
		return null;
	}
}
