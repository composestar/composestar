using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;

namespace CachingFilters
{
    public class Cache
    {
        protected static Cache _instance;

        protected static Object nullObj = new Object();

        public static Cache instance(Object subject)
        {
            if (_instance == null)
            {
                _instance = new Cache();
            }
            return _instance;
        }

        protected IDictionary<MethodBase, IDictionary<Object, Object>> internalCache;

        protected Cache()
        {
            internalCache = new Dictionary<MethodBase, IDictionary<Object, Object>>();
        }

        public void setCache(MethodBase selector, Object args, Object value)
        {
            if (args == null) args = nullObj;
            IDictionary<Object, Object> lc;
            if (!internalCache.TryGetValue(selector, out lc))
            {
                lc = new Dictionary<Object, Object>();
                internalCache.Add(selector, lc);
            }
            if (!lc.ContainsKey(args))
            {
                lc.Add(args, value);
            }
            //Console.Error.WriteLine("Setting cache for {0}({1})", selector, args);
        }

        public bool getCache(MethodBase selector, Object args, out Object value)
        {
            if (args == null) args = nullObj;
            if (internalCache.ContainsKey(selector))
            {
                IDictionary<Object, Object> lc;
                if (internalCache.TryGetValue(selector, out lc))
                {
                    Object retval;
                    if (lc.TryGetValue(args, out retval))
                    {
                        //Console.Error.WriteLine("Returning cache for {0}({1})", selector, args);
                        value = retval;
                        return true;
                    }
                }
            }
            value = null;
            return false;
        }

        public void clearCache()
        {
            internalCache.Clear();
        }
    }
}
