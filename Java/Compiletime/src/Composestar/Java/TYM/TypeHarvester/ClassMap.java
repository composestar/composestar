package Composestar.Java.TYM.TypeHarvester;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class containing the harvested classes in a <code>HashMap</code>.
 */
public class ClassMap
{
	private Map<String,Class> harvestedClasses = new HashMap<String,Class>();

	private static ClassMap Instance;

	/**
	 * Default constructor.
	 */
	public ClassMap()
	{

	}

	/**
	 * Adds a <code>class</code> instance to the map.
	 * 
	 * @param c - the class.
	 */
	public void addClass(Class c)
	{
		String name = c.getName();
		if (!harvestedClasses.containsKey(name))
		{
			harvestedClasses.put(name, c);
		}
		else
		{
			// duplicate
		}

	}

	/**
	 * Retrieves a <code>Class</code> from the map.
	 * 
	 * @param name - the fully qualified name.
	 */
	public Class getClass(String name)
	{
		Class c = (Class) harvestedClasses.get(name);
		return c;
	}

	/**
	 * Creates a <code>ClassMap</code> instance.
	 */
	public static ClassMap instance()
	{
		if (Instance == null)
		{
			Instance = new ClassMap();
		}
		return Instance;
	}

	/**
	 * Returns the map.
	 */
	public Map<String,Class> map()
	{
		return harvestedClasses;
	}
}
