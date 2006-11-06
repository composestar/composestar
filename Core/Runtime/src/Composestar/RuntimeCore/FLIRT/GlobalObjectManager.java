package Composestar.RuntimeCore.FLIRT;

import Composestar.RuntimeCore.Utils.Debug;

import java.util.Enumeration;
import java.util.Hashtable;

public class GlobalObjectManager
{
	protected static Hashtable objectmanagers = new Hashtable(100);

	/**
	 * @param key
	 * @return java.lang.Object
	 * @roseuid 41162D0102DC
	 */
	public static Object getObjectManagerFor(Object key)
	{
		if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Getting object with key '" + key.hashCode()
				+ "'.");
		// if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","Currently
		// " + objectmanagers.size() + " objectmanager(s) allocated.");
		// if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","Getting
		// objectmanager with key " + key.GetHashCode() + " (" + key.getClass()
		// + " | " + key.getClass().get_Namespace().startsWith("java.") + ").");
		return objectmanagers.get(new Integer(key.hashCode()));
	}

	public static Enumeration getEnumerator()
	{
		return objectmanagers.elements();
	}

	/**
	 * @param key
	 * @param obj
	 * @roseuid 41162D0102E1
	 */
	public static void setObjectManagerFor(Object key, Object obj)
	{
		// Do not store the object managers for framework classes
		if ((key.getClass() == null) || // Fixed crash: namespace can be null
										// (when class is in default namespace)!
				(!(key.getClass().getName().startsWith("java.") || key.getClass().getName().startsWith("System."))))
		{
			if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Storing '" + obj + "' with key '"
					+ key.hashCode() + "'.");
			// if(Debug.SHOULD_DEBUG)
			// Debug.out(Debug.MODE_DEBUG,"FLIRT","Storing objectmanager for
			// object '" + ((ObjectManager)obj).theObject + "' with key '" +
			// key.GetHashCode() + " (" + key + ")'.");
			objectmanagers.put(new Integer(key.hashCode()), obj);
			// if(Debug.SHOULD_DEBUG)
			// Debug.out(Debug.MODE_DEBUG,"FLIRT","Currently " +
			// objectmanagers.size() + " objectmanager(s) have been
			// allocated.");
		}
	}

	/**
	 * @roseuid 41162D0102E4
	 */
	public static void reset()
	{
		objectmanagers = new Hashtable();
	}
}
