/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2005-2008 University of Twente.
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
package Composestar.RuntimeJava.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.Invoker;

public class JavaInvoker extends Invoker
{
	public JavaInvoker()
	{
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.RuntimeCore.Utils.Invoker#getAttributesFor(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public List getAttributesFor(Object target, String selector)
	{
		List attributes = new ArrayList();
		return attributes;
	}

	public Class<?> getType(Object object)
	{
		if (object == null)
		{
			return Object.class;
		}
		return object.getClass();
	}

	public Class<?> getType(String type)
	{
		Class<?> realType = null;
		try
		{
			realType = Class.forName(type);
		}
		catch (ClassNotFoundException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Class not found:" + type);
		}
		return realType;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.RuntimeCore.Utils.Invoker#invoke(java.lang.String,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object invoke(String target, String selector, Object[] args)
	{
		Class<?> type = getType(target);
		Class<?>[] newArgs = convertToJavaArgs(args);

		try
		{
			MethodFinder m = new MethodFinder(type);
			Method method = m.findMethod(selector, newArgs);
			method.setAccessible(true);
			Object result = method.invoke(target, args);
			return result;
		}
		catch (IllegalAccessException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Cannot acces target: " + target);
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		catch (NoSuchMethodException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "No such static method: " + selector + " - " + e.getMessage());
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		catch (InvocationTargetException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Error while delegating to target: " + target + "selector: " + selector
					+ " args: " + Arrays.toString(args));
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.RuntimeCore.Utils.Invoker#invoke(java.lang.Object,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object target, String selector, Object[] args)
	{
		Class<?> type = getType(target);
		Class<?>[] newArgs = convertToJavaArgs(args);
		try
		{
			MethodFinder m = new MethodFinder(type);
			Method method = m.findMethod(selector, newArgs);
			method.setAccessible(true);
			Object result = method.invoke(target, args);
			return result;
		}
		catch (IllegalAccessException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Cannot acces target:" + target);
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		catch (NoSuchMethodException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "No such method:" + selector + " - " + e.getMessage());
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		catch (InvocationTargetException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Error while delegating to target: " + target + " selector: "
					+ selector + " args: " + args);
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		return null;
	}

	private Class<?>[] convertToJavaArgs(Object[] args)
	{
		Class<?>[] result = new Class[args.length];
		for (int i = 0; i < args.length; i++)
		{
			result[i] = getType(args[i]);
		}
		return result;
	}

	private Method getMethod(Class<?> type, String name)
	{
		Method[] meths = type.getMethods();
		for (Method meth : meths)
		{
			if (meth.getName().equalsIgnoreCase(name))
			{
				return meth;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.RuntimeCore.Utils.Invoker#objectHasMethod(java.lang.Object,
	 * java.lang.String, java.util.Dictionary)
	 */
	@Override
	public boolean objectHasMethod(Object inner, String m_selector, Dictionary context)
	{
		Class<?> type = getType(inner);
		return getMethod(type, m_selector) != null;
	}

	@Override
	public Object requestInstance(String target, Object[] args)
	{
		Class<?> type = getType(target);
		try
		{
			Constructor<?> constructor = type.getConstructor(convertToJavaArgs(args));
			return constructor.newInstance(args);
		}
		catch (IllegalAccessException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Cannot acces target:" + target);
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		catch (NoSuchMethodException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "No such method:" + target);
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		catch (InvocationTargetException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Error while delegating to target:" + target);
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		catch (InstantiationException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Cannot instantiate:" + target);
			Debug.out(Debug.MODE_ERROR, "Util", e.getMessage());
		}
		return null;
	}
}
