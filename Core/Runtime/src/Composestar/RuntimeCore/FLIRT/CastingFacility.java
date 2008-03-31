package Composestar.RuntimeCore.FLIRT;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import Composestar.RuntimeCore.FLIRT.Interpreter.FilterModuleRuntime;
import Composestar.RuntimeCore.Utils.Debug;

/**
 * 
 */
public class CastingFacility
{
	// for a quick lookup for a cast to outer
	private static WeakHashMap internalMapping = new WeakHashMap();

	private CastingFacility()
	{}

	public static void registerInternal(Object internal, WeakReference outer)
	{
		internalMapping.put(internal, outer);
	}

	public static Object getOuterForInternal(Object internal)
	{
		WeakReference ref = (WeakReference) internalMapping.get(internal);
		if (ref != null)
		{
			Object res = ref.get();
			if (res == null)
			{
				internalMapping.remove(internal);
			}
			return res;
		}
		return null;
	}

	public synchronized static Object handleCast(Object from, String to)
	{
		// no need to convert null
		if (from == null)
		{
			return null;
		}

		// a null typename is wrong though
		if (to == null)
		{
			throw new IllegalArgumentException("to cannot be null");
		}

		// remove assembly name
		int bracket = to.indexOf(']');
		if (bracket != -1)
		{
			to = to.substring(bracket + 1);
		}

		// cast to type of itself is always safe, really it is
		if (from.getClass().getName().equals(to))
		{
			return from;
		}

		ObjectManager om = (ObjectManager) GlobalObjectManager.getObjectManagerFor(from);
		if (om == null)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "No object manager found for '" + from + "'.");
			}
		}
		else
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Found an object manager for '" + from.getClass().getName()
						+ "'.");
			}

			java.util.ArrayList filterModules = om.getFilterModules();
			for (int i = 0; i < filterModules.size(); i++)
			{
				FilterModuleRuntime fmr = (FilterModuleRuntime) filterModules.get(i);

				java.util.Hashtable internals = (java.util.Hashtable) fmr.getInternals();
				java.util.Enumeration internalObjects = internals.elements();

				while (internalObjects.hasMoreElements())
				{
					Object internal = internalObjects.nextElement();
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Checking internal: "
								+ internal.getClass().getName());
					}

					if (internal.getClass().getName().equals(to))
					{
						// This should be the match and return the correct
						// internal object
						if (Debug.SHOULD_DEBUG)
						{
							Debug.out(Debug.MODE_INFORMATION, "FLIRT",
									"Found internal object of type to return for casting: " + internal);
						}
						return internal;
					}
				}
			}
		}

		// Search for a possible cast in the other direction, from internal
		// object to concern object...
		Object res = getOuterForInternal(from);
		if (res != null)
		{
			// TODO has to check if the "to" is of a compatible class
			// if (res.getClass().getName().equals(to))
			{
				return res;
			}
		}
		// String idhash = from.getClass() + "@" +
		// System.identityHashCode(from);
		// Iterator enumManagers = GlobalObjectManager.iterator();
		// while (enumManagers.hasNext())
		// {
		// om = (ObjectManager) enumManagers.next();
		// Iterator i = om.getFilterModules().iterator();
		// while (i.hasNext())
		// {
		// FilterModuleRuntime fmr = (FilterModuleRuntime) i.next();
		// if (Debug.SHOULD_DEBUG)
		// {
		// Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Inspecting
		// FilterModuleRuntime: " + fmr);
		// }
		//
		// java.util.Hashtable internals = (java.util.Hashtable)
		// fmr.getInternals();
		// java.util.Enumeration internalObjects = internals.elements();
		//
		// while (internalObjects.hasMoreElements())
		// {
		// Object internal = internalObjects.nextElement();
		//
		// if (Debug.SHOULD_DEBUG)
		// {
		// Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Inspecting internal '" +
		// internal + "' of '" + fmr
		// + "'.");
		// }
		//
		// if (idhash.equals(internal.getClass() + "@" +
		// System.identityHashCode(internal)))
		// {
		// // This should be the match and return the correct
		// // parent concern object
		// Object obj = fmr.getObjectManager().getObject();
		//
		// // mh: also check if the types match, otherwise it's not
		// // the designed cast
		// // (e.g. for the multiple inheritance hack)
		// // this change (prefixed with //!) isnt safe because it
		// // doesn't check subclasses
		// // and break a couple of classes
		//
		// // !if (obj.getClass().getName().equals(to))
		// // !{
		// if (Debug.SHOULD_DEBUG)
		// {
		// Debug.out(Debug.MODE_INFORMATION, "FLIRT",
		// "Found managed object for given internal to return for casting: "
		// + obj.getClass().getName() + ", key " + obj.hashCode());
		// }
		// return obj;
		// // !}
		// }
		// }
		// }
		// }

		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "No internal object found for casting: "
					+ from.getClass().getName() + " -> " + to);
		}
		return from;
	}

	public synchronized static Object handleInheritedCall(Object target)
	{
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Object under investigation: " + target + " (type: "
					+ target.getClass().getName() + ").");
		}

		// TODO: wtf is this all about? The code isn't even relevant for the
		// noted example
		/*
		 * Object args[] = {}; if
		 * (target.getClass().getName().equals("VenusFlyTrap.VenusFlyTrap")) { //
		 * target matches type of intercepted classes
		 * MessageHandlingFacility.handleVoidMethodCall("VenusFlyTrap.LivingBeing",
		 * target, "buildBodyParts", args); } else {
		 * MessageHandlingFacility.handleVoidMethodCall("VenusFlyTrap.LivingBeing",
		 * target, "buildBodyParts", args); }
		 */

		return target;
	}
}
