import java.util.HashMap;
import java.util.Map;

import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

public class CachingObject {
	boolean debug = false; // set to "true" to see what is going on inside

	// Maps (method argument => return value)
	Map<Object, Map<Object, Object>> objectCache;

	// Singleton cache instance
	private static CachingObject cacheInstance;

	public CachingObject() {
		objectCache = new HashMap<Object, Map<Object, Object>>();
	}

	// This is where the actual caching takes place. A "Meta-filter" can
	// forward "Reified (*) messages" to this method.
	// (*) To regard or treat (an abstraction) as if it had concrete or material
	// existence.

	public void storeValue(ReifiedMessage message) {
		// Obtain message argument values. To keep things simple, this version
		// only supports methods that have 0 or 1 arguments (otherwise, you need
		// nested hashtables, etc.)
		Object arg;
		if (message.getArgs().length > 0)
			arg = message.getArg(0);
		else
			arg = "(no argument)";

		debugPrint("Method " + message.getSelector()
				+ " called with argument value: " + arg);
		
		Map<Object, Object> cache = objectCache.get(message.getTarget());
		if (cache == null)
		{
			cache = new HashMap<Object, Object>();
			objectCache.put(message.getTarget(), cache);
		}

		// Did we already calculate the return value, given this argument value?
		if (cache.containsKey(arg)) { // Yes....
			debugPrint("Cached result for argument value: " + arg + ": "
					+ cache.get(arg));

			message.reply(cache.get(arg));
			return; // do not proceed! Skip normal execution.
		}

		message.proceed();

		Object res = message.getReturnValue();

		if (res != null) // don't crash if accidentally used on void methods
		// (which have no result obviously)
		{
			cache.put(arg, res);
			debugPrint("Calculated result for " + arg + ": " + res);
		}
	}
	
	public void invalidate(ReifiedMessage message) {
		Map<Object, Object> cache = objectCache.get(message.getTarget());
		if (cache != null)
		{
			cache.clear();
		}
	}

	public void debugPrint(String str) {
		if (debug)
			System.err.println(str);
	}

	public static CachingObject getInstance() {
		if (cacheInstance == null)
			cacheInstance = new CachingObject();
		return cacheInstance;
	}
}