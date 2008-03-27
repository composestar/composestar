package Composestar.RuntimeCore.FLIRT;

import java.util.Iterator;
import java.util.WeakHashMap;

public class GlobalObjectManager
{
	// <String,ObjectManager>
	protected static WeakHashMap objectmanagers = new WeakHashMap(100);

	// public static String getObjectKey(Object subject)
	// {
	// return subject.getClass().getName() + "@" +
	// System.identityHashCode(subject);
	// }

	/**
	 * @param key
	 * @return java.lang.Object
	 * @roseuid 41162D0102DC
	 */
	public static ObjectManager getObjectManagerFor(Object key)
	{
		// String hc = getObjectKey(key);
		// if (Debug.SHOULD_DEBUG)
		// {
		// Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Getting object with key
		// '" + hc + "'.");
		// }
		// if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","Currently
		// " + objectmanagers.size() + " objectmanager(s) allocated.");
		// if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","Getting
		// objectmanager with key " + key.GetHashCode() + " (" + key.getClass()
		// + " | " + key.getClass().get_Namespace().startsWith("java.") + ").");
		return (ObjectManager) objectmanagers.get(key);
	}

	public static void removeObjectManager(ObjectManager key)
	{
		objectmanagers.values().remove(key);
	}

	public static Iterator iterator()
	{
		return objectmanagers.values().iterator();
	}

	/**
	 * @param key
	 * @param obj
	 * @roseuid 41162D0102E1
	 */
	public static void setObjectManagerFor(Object key, ObjectManager obj)
	{
		if (key == null)
		{
			return;
		}
		// Do not store the object managers for framework classes
		if ((key.getClass() == null) || // Fixed crash: namespace can be null
				// (when class is in default namespace)!
				(!(key.getClass().getName().startsWith("java.") || key.getClass().getName().startsWith("System."))))
		{
			// String hc = getObjectKey(key);
			// if (Debug.SHOULD_DEBUG)
			// {
			// Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Storing '" + obj + "'
			// with key '" + hc + "'.");
			// }
			// if(Debug.SHOULD_DEBUG)
			// Debug.out(Debug.MODE_DEBUG,"FLIRT","Storing objectmanager for
			// object '" + ((ObjectManager)obj).theObject + "' with key '" +
			// key.GetHashCode() + " (" + key + ")'.");
			objectmanagers.put(key, obj);
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
		objectmanagers = new WeakHashMap(100);
	}
}
