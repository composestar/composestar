// .NET specific
package Composestar.RuntimeCore.FLIRT;

import Composestar.RuntimeCore.FLIRT.Interpreter.FilterModuleRuntime;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Summary description for CastingFacility.
 */
public class CastingFacility
{
	private CastingFacility()
	{

	}

	public synchronized static Object handleCast(Object from, String to) 
	{
		Object result = null;

		if (from == null) return null; // because there is nothing to reason about.

		if (to.indexOf(']') >= 0)
		{
			to = to.substring(to.indexOf(']')+1);
		}
		
		ObjectManager om = (ObjectManager)GlobalObjectManager.getObjectManagerFor(from);
		
		if (om != null) 
		{
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Found an object manager for '" + from.getClass().getName() + "'.");

			java.util.ArrayList filterModules = om.getFilterModules();
			for (int i = 0; i < filterModules.size(); i++) 
			{
			
				FilterModuleRuntime fmr = (FilterModuleRuntime)filterModules.get(i);

				java.util.Hashtable internals = (java.util.Hashtable)fmr.getInternals();
				java.util.Enumeration internalObjects = internals.elements();

				while( internalObjects.hasMoreElements())
				{
					Object internal = internalObjects.nextElement();
					if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Checking internal: "+internal.getClass().getName());

					if ( internal.getClass().getName().equals(to) )
					{
						// This should be the match and return the correct internal object
						result = internal;
						if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Found internal object of type to return for casting: "+result);
						break;
					}
				}

				if (result != null) 
				{
					break;
				}
			}
		}
		else 
		{
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","No object manager found for '" + from + "'.");
		}

		if (result == null)
		{
			// Search for a possible cast in the other direction, from internal object to concern object...
			Enumeration enumManagers = GlobalObjectManager.getEnumerator();
			while (enumManagers.hasMoreElements())
			{
				om = (ObjectManager)enumManagers.nextElement();
				Iterator i = om.getFilterModules().iterator();
				while (i.hasNext())
				{
					FilterModuleRuntime fmr = (FilterModuleRuntime)i.next();
					if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Inspecting FilterModuleRuntime: " + fmr);

					java.util.Hashtable internals = (java.util.Hashtable)fmr.getInternals();
					java.util.Enumeration internalObjects = internals.elements();

					while(internalObjects.hasMoreElements())
					{
						Object internal = internalObjects.nextElement();
					
						if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Inspecting internal '" + internal + "' of '" + fmr  + "'.");
						
						if ( internal.hashCode() == from.hashCode() )
						{
							// This should be the match and return the correct parent concern object
							result = fmr.getObjectManager().theObject;
							if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Found managed object for given internal to return for casting: "+result.getClass().getName() + ", key " + result.GetHashCode());
							break;
						}
					}

					if (result != null) 
					{
						break;
					}
				}
				
				if (result != null) 
				{
					break;
				}
			}

		}

		if (result == null) 
		{
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","No internal object found for casting: "+from.getClass().getName() + " -> " + to);

			result = from;
		}
		
		return result;
	}

	public synchronized static Object handleInheritedCall(Object target) 
	{
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Object under investigation: " + target + " (type: " + target.getClass().getName() + ").");

		Object args[] = {};
		if (target.getClass().getName().Equals("VenusFlyTrap.VenusFlyTrap"))
		{
			// target matches type of intercepted classes
			MessageHandlingFacility.handleVoidMethodCall("VenusFlyTrap.LivingBeing", target, "buildBodyParts", args);
		}
		else 
		{
			MessageHandlingFacility.handleVoidMethodCall("VenusFlyTrap.LivingBeing", target, "buildBodyParts", args);
		}

		return target;
	}
}
