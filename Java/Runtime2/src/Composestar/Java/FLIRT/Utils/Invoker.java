/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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
package Composestar.Java.FLIRT.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Composestar.Java.FLIRT.FLIRTConstants;

/**
 * Utility class to invoke methods on objects
 * 
 * @author Michiel Hendriks
 */
public class Invoker
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME + ".Invoker");

	// TODO what is this for?
	public static List<Object> getAttributesFor(Object target, String selector)
	{
		List<Object> attributes = new ArrayList<Object>();
		return attributes;
	}

	public static Class<?> getType(Object object)
	{
		if (object == null)
		{
			return Object.class;
		}
		return object.getClass();
	}

	public static Class<?> getType(String type)
	{
		Class<?> realType = null;
		try
		{
			realType = Class.forName(type);
		}
		catch (ClassNotFoundException e)
		{
			logger.log(Level.SEVERE, "Class not found:" + type, e);
		}
		return realType;
	}

	public static Object invoke(String target, String selector, Object[] args)
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
			logger.log(Level.SEVERE, "Cannot acces target: " + target, e);
		}
		catch (NoSuchMethodException e)
		{
			logger.log(Level.SEVERE, "No such static method: " + selector + " - " + e.getMessage(), e);
		}
		catch (InvocationTargetException e)
		{
			logger.log(Level.SEVERE, "Error while delegating to target: " + target + "selector: " + selector
					+ " args: " + Arrays.toString(args), e);
		}
		return null;
	}

	public static Object invoke(Object target, String selector, Object[] args)
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
			logger.log(Level.SEVERE, "Cannot acces target:" + target, e);
		}
		catch (NoSuchMethodException e)
		{
			logger.log(Level.SEVERE, "No such method:" + selector + " - " + e.getMessage(), e);
		}
		catch (InvocationTargetException e)
		{
			logger.log(Level.SEVERE, "Error while delegating to target: " + target + " selector: " + selector
					+ " args: " + args, e);
		}
		return null;
	}

	private static Class<?>[] convertToJavaArgs(Object[] args)
	{
		Class<?>[] result = new Class[args.length];
		for (int i = 0; i < args.length; i++)
		{
			result[i] = getType(args[i]);
		}
		return result;
	}

	private static Method getMethod(Class<?> type, String name)
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

	public static boolean objectHasMethod(Object inner, String m_selector)
	{
		Class<?> type = getType(inner);
		return getMethod(type, m_selector) != null;
	}

	public static Object requestInstance(String target, Object[] args)
	{
		Class<?> type = getType(target);
		try
		{
			Constructor<?> constructor = type.getConstructor(convertToJavaArgs(args));
			return constructor.newInstance(args);
		}
		catch (IllegalAccessException e)
		{
			logger.log(Level.SEVERE, "Cannot acces target:" + target, e);
		}
		catch (NoSuchMethodException e)
		{
			logger.log(Level.SEVERE, "No such method:" + target, e);
		}
		catch (InvocationTargetException e)
		{
			logger.log(Level.SEVERE, "Error while delegating to target:" + target, e);
		}
		catch (InstantiationException e)
		{
			logger.log(Level.SEVERE, "Cannot instantiate:" + target, e);
		}
		return null;
	}
}
