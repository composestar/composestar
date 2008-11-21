package Composestar.Java.FLIRT;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Java.FLIRT.Env.ObjectManager;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Env.RTFilterModule;

/**
 * Manages the "casting" hack to get the outer for an internal or vice versa.
 * 
 * @author Michiel Hendriks
 */
public class CastingFacility
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME + ".CastingFacility");

	/**
	 * for a quick lookup for a cast to outer
	 */
	private static WeakHashMap<Object, WeakReference<Object>> internalMapping =
			new WeakHashMap<Object, WeakReference<Object>>();

	private CastingFacility()
	{}

	/**
	 * Register the relation between and internal and it's "outer" object
	 * 
	 * @param internal
	 * @param outer
	 */
	public static void registerInternal(Object internal, Object outer)
	{
		internalMapping.put(internal, new WeakReference<Object>(outer));
	}

	/**
	 * Return the outer for an internal
	 * 
	 * @param internal
	 * @return
	 */
	public static Object getOuterForInternal(Object internal)
	{
		WeakReference<Object> ref = internalMapping.get(internal);
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

	/**
	 * Up or down case an object
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
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

		// cast to type of itself is always safe, really it is
		if (from.getClass().getName().equals(to))
		{
			return from;
		}

		ObjectManager om = ObjectManagerHandler.getObjectManager(from, null);
		if (om == null)
		{
			logger.info(String.format("No object manager for an %s instance", from.getClass().getName()));
		}
		else
		{
			// look for a cast from outer -> inner
			for (RTFilterModule fm : om.getFilterModules())
			{
				for (FilterModuleVariable var : fm.getVariables())
				{
					if (!(var instanceof Internal))
					{
						continue;
					}
					RTCpsObject obj = fm.getMemberObject(var);
					if (obj == null || obj.getObject() == null)
					{
						continue;
					}
					if (obj.getObject().getClass().getName().equals(to))
					{
						// TODO doesn't check for "compatible" classes
						return obj.getObject();
					}
				}
			}
		}

		// Search for a possible cast in the other direction: inner -> outer
		Object res = getOuterForInternal(from);
		if (res != null)
		{
			// TODO has to check if the "to" is of a compatible class
			// if (res.getClass().getName().equals(to))
			{
				return res;
			}
		}
		return from;
	}
}
