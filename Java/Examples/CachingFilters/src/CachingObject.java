import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

/**
 * The cache handler. Keeps a cache for each object that passes. This
 * implementation has the following limitations:
 * <ul>
 * <li>The value of the first argument is used as the key for caching. Caching
 * methods with more than one argument is therefor not reliable.</li>
 * </ul>
 */
public class CachingObject {
	/**
	 * set to "true" to see what is going on inside
	 */
	private static boolean debug = false;

	/**
	 * Singleton cache instance
	 */
	private static CachingObject cacheInstance;

	/**
	 * Maps (method argument => return value) for each object
	 */
	private Map<Object, Map<CacheRecord, Object>> objectCache;

	/**
	 * @return The caching instance.
	 */
	public static CachingObject getInstance() {
		if (cacheInstance == null) {
			cacheInstance = new CachingObject();
		}
		return cacheInstance;
	}

	/**
	 * enable/disable debug output
	 * 
	 * @param doDebug
	 */
	public static void setDebug(boolean doDebug) {
		debug = doDebug;
	}

	private CachingObject() {
		// use a weak hashmap so that object references are not kept in memory
		// for ever
		objectCache = new WeakHashMap<Object, Map<CacheRecord, Object>>();
	}

	/**
	 * This is where the actual caching takes place. A "Meta-filter" can forward
	 * "Reified (*) messages" to this method. (*) To regard or treat (an
	 * abstraction) as if it had concrete or material existence.
	 * 
	 * @param message
	 */
	public void storeValue(ReifiedMessage message) {
		// Obtain message argument values. To keep things simple, this version
		// only supports methods that have 0 or 1 arguments (otherwise, you need
		// nested hashtables, etc.)

		CacheRecord rec = new CacheRecord(message.getSelector(), message
				.getArgs());

		if (debug) {
			debugPrint("Method " + message.getSelector()
					+ " called with argument value: "
					+ Arrays.toString(rec.args));
		}

		Map<CacheRecord, Object> cache = objectCache.get(message.getTarget());
		if (cache == null) {
			cache = new HashMap<CacheRecord, Object>();
			objectCache.put(message.getTarget(), cache);
		}

		// Did we already calculate the return value, given this argument value?
		if (cache.containsKey(rec)) { // Yes....
			Object result = cache.get(rec);
			if (debug) {
				debugPrint("Cached result for argument value: "
						+ Arrays.toString(rec.args) + ": " + result);
			}

			message.reply(result);
			return; // do not proceed! Skip normal execution.
		}

		message.proceed();

		Object res = message.getReturnValue();

		if (res != null) // don't crash if accidentally used on void methods
		// (which have no result obviously)
		{
			cache.put(rec, res);
			if (debug) {
				debugPrint("Calculated result for " + rec + ": " + res);
			}
		}
	}

	/**
	 * Clear the cache for the target object
	 * 
	 * @param message
	 */
	public void invalidate(ReifiedMessage message) {
		Map<CacheRecord, Object> cache = objectCache.get(message.getTarget());
		if (cache != null) {
			cache.clear();
		}
	}

	/**
	 * Print debug output
	 * 
	 * @param str
	 */
	public void debugPrint(String str) {
		if (debug) {
			System.err.println(str);
		}
	}

	/**
	 * A cache record
	 */
	public static class CacheRecord {
		public String method;
		public Object[] args;

		public CacheRecord(String m, Object[] a) {
			method = m;
			args = a;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(args);
			result = prime * result
					+ ((method == null) ? 0 : method.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final CacheRecord other = (CacheRecord) obj;
			if (!Arrays.equals(args, other.args))
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (!method.equals(other.method))
				return false;
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(method);
			sb.append('(');
			if (args != null) {
				sb.append(Arrays.toString(args));
			}
			sb.append(')');
			return sb.toString();
		}
	}
}