package Composestar.C.LAMA;

import java.util.Collection;

import Composestar.Core.RepositoryImplementation.DataMap;

public class CTypeMap
{
	private DataMap Types;

	private static CTypeMap Instance;

	/**
	 * @roseuid 402CA342033C
	 */
	public CTypeMap()
	{
		Types = DataMap.newDataMapInstance();
	}

	/**
	 * Add a new type to the map
	 * 
	 * @param name Unique identifier of the type to add.
	 * @param type
	 * @roseuid 402CA2FF0154
	 */
	public void addType(String name, CType type)
	{
		Types.put(name, type);
	}

	/**
	 * Retrieve a type from the map
	 * 
	 * @param name
	 * @return The requested Type or null if it wasn't found.
	 * @roseuid 402CA30E002A
	 */
	public CType getType(String name)
	{
		return (CType) Types.get(name);
	}

	/**
	 * Retrieve the TypeMap instance
	 * 
	 * @return Composestar.Core.LAMA.TypeMap
	 * @roseuid 402CA35A010F
	 */
	public static CTypeMap instance()
	{
		if (Instance == null)
		{
			Instance = new CTypeMap();
		}
		return Instance;
	}

	/**
	 * @return java.util.Collection
	 * @roseuid 405AD19F032D
	 */
	public Collection values()
	{
		return Types.values();
	}

	/**
	 * @return java.util.HashMap
	 * @roseuid 405AD1CB0396
	 */
	// public java.util.HashMap map() {
	// return Types.getHashMap();
	// }
}
