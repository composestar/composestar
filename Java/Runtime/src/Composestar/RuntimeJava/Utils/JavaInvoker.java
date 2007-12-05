package Composestar.RuntimeJava.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;

import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.Invoker;

public class JavaInvoker extends Invoker
{

	public JavaInvoker()
	{
		instance = this;
	}

	public ArrayList getAttributesFor(Object target, String selector)
	{
		ArrayList attributes = new ArrayList();
		return attributes;
	}

	public Class getType(Object object)
	{
		if (object == null)
		{
			return Object.class;
		}
		return object.getClass();
	}

	public Class getType(String type)
	{
		Class realType = null;
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

	public Object invoke(String target, String selector, Object[] args)
	{
		Class type = getType(target);
		Class[] newArgs = convertToJavaArgs(args);

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

	public Object invoke(Object target, String selector, Object[] args)
	{

		Class type = getType(target);
		Class[] newArgs = convertToJavaArgs(args);
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

	private Class[] convertToJavaArgs(Object[] args)
	{
		Class[] result = new Class[args.length];
		for (int i = 0; i < args.length; i++)
		{
			result[i] = getType(args[i]);
		}
		return result;
	}

	private Method getMethod(Class type, String name)
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

	public boolean objectHasMethod(Object inner, String m_selector, Dictionary context)
	{
		Class type = getType(inner);
		return getMethod(type, m_selector) != null;
	}

	public Object requestInstance(String target, Object[] args)
	{
		Class type = getType(target);
		try
		{
			Constructor constructor = type.getConstructor(convertToJavaArgs(args));
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
