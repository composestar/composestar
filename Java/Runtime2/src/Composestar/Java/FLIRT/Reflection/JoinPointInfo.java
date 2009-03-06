/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Java.FLIRT.Reflection;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Env.RTFilterModule;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 * Information about the current join point. Exposes the internals and externals
 * of the current object.
 * 
 * @author Michiel Hendriks
 */
public class JoinPointInfo
{
	/**
	 * @return The current object
	 */
	public static final Object getInstance()
	{
		return getCache().getInstance();
	}

	/**
	 * @return The list of internals
	 */
	public static final Collection<Object> getInternals()
	{
		return getCache().getInternals();
	}

	/**
	 * Get an internal by its name. Names are not guaranteed to be unique. So
	 * multiple internals can be returned. You can use the name of the internal,
	 * or the fully qualified name of the internal.
	 * 
	 * @param name
	 * @return
	 */
	public static final Collection<Object> getInternal(String name)
	{
		return getCache().getInternal(name);
	}

	/**
	 * @return The list of externals
	 */
	public static final Collection<Object> getExternals()
	{
		return getCache().getExternals();
	}

	/**
	 * Get an external by its name. Names are not guaranteed to be unique. So
	 * multiple externals can be returned.
	 * 
	 * @param name
	 * @return
	 */
	public static final Collection<Object> getExternal(String name)
	{
		return getCache().getExternal(name);
	}

	/**
	 * Weak references to the cache info
	 */
	protected static final WeakHashMap<FilterExecutionContext, JoinPointInfoCache> caches =
			new WeakHashMap<FilterExecutionContext, JoinPointInfoCache>();

	protected static final JoinPointInfoCache EMPTY_CACHE = new JoinPointInfoCache(null);

	/**
	 * @return
	 */
	protected static final JoinPointInfoCache getCache()
	{
		if (!ReflectionHandler.hasMessage())
		{
			return EMPTY_CACHE;
		}
		FilterExecutionContext fec = ReflectionHandler.getCurrentContext();
		JoinPointInfoCache cache = caches.get(fec);
		if (cache == null)
		{
			cache = new JoinPointInfoCache(fec);
			caches.put(fec, cache);
		}
		return cache;
	}

	/**
	 * A cache of the internals data
	 * 
	 * @author Michiel Hendriks
	 */
	protected static class JoinPointInfoCache
	{
		protected static final String ALL = "\0";

		protected Object instance;

		protected Map<String, WeakHashMap<Object, Object>> internals;

		protected Map<String, WeakHashMap<Object, Object>> externals;

		public JoinPointInfoCache(FilterExecutionContext fec)
		{
			internals = new WeakHashMap<String, WeakHashMap<Object, Object>>();
			internals.put(ALL, new WeakHashMap<Object, Object>());
			externals = new WeakHashMap<String, WeakHashMap<Object, Object>>();
			externals.put(ALL, new WeakHashMap<Object, Object>());
			if (fec != null)
			{
				init(fec);
			}
		}

		/**
		 * @return the instance
		 */
		public Object getInstance()
		{
			return instance;
		}

		/**
		 * @return the internals
		 */
		public Collection<Object> getInternals()
		{
			return Collections.unmodifiableCollection(internals.get(ALL).keySet());
		}

		public Collection<Object> getInternal(String name)
		{
			Map<Object, Object> res = internals.get(name);
			if (res == null)
			{
				return Collections.emptyList();
			}
			return Collections.unmodifiableCollection(res.keySet());
		}

		/**
		 * @return the externals
		 */
		public Collection<Object> getExternals()
		{
			return Collections.unmodifiableCollection(externals.get(ALL).keySet());
		}

		public Collection<Object> getExternal(String name)
		{
			Map<Object, Object> res = externals.get(name);
			if (res == null)
			{
				return Collections.emptyList();
			}
			return Collections.unmodifiableCollection(res.keySet());
		}

		/**
		 * @param fec
		 */
		protected void init(FilterExecutionContext fec)
		{
			instance = fec.getObjectManager().getObject();
			for (RTFilterModule fm : fec.getObjectManager().getFilterModules())
			{
				String fmName = fm.getFullyQualifiedName();
				int token = fmName.lastIndexOf('`');
				if (token > -1)
				{
					fmName = fmName.substring(0, token);
				}
				for (FilterModuleVariable var : fm.getVariables())
				{
					if (var instanceof Internal)
					{
						RTCpsObject obj = fm.getMemberObject(var);
						if (obj == null)
						{
							continue;
						}
						register(internals, obj.getObject(), var.getName(), fmName);
					}
					else if (var instanceof External)
					{
						RTCpsObject obj = fm.getMemberObject(var);
						if (obj == null)
						{
							continue;
						}
						register(externals, obj.getObject(), var.getName(), fmName);
					}
				}
			}
		}

		/**
		 * @param externals2
		 * @param obj
		 * @param name
		 * @param fullyQualifiedName
		 */
		private void register(Map<String, WeakHashMap<Object, Object>> target, Object obj, String name, String fmName)
		{
			if (obj == null)
			{
				return;
			}
			register(target, obj, ALL);
			register(target, obj, name);
			register(target, obj, fmName + "." + name);
		}

		/**
		 * @param target
		 * @param obj
		 * @param name
		 */
		private void register(Map<String, WeakHashMap<Object, Object>> target, Object obj, String name)
		{
			WeakHashMap<Object, Object> map = target.get(name);
			if (map == null)
			{
				map = new WeakHashMap<Object, Object>();
				target.put(name, map);
			}
			map.put(obj, null);
		}
	}
}
