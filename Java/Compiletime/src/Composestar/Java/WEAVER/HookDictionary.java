package Composestar.Java.WEAVER;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Storage place for hooks. A hook is a location in the program where a call to
 * the interpreter should be inserted by the weaver.
 * <p>
 * The following interception points are stored in the dictionary:
 * <p>
 * 1. Method calls
 * <p>
 * 2. Instantation expressions
 * <p>
 * 3. Cast expressions (not supported yet!)
 */
public class HookDictionary
{

	private List afterInstantationInterceptions = new ArrayList();

	private List castInterceptions = new ArrayList();

	private Hashtable methodInterceptions = new Hashtable();

	private static HookDictionary Instance = null;

	public HookDictionary()
	{

	}

	/**
	 * Singleton
	 * 
	 * @return A HookDictionary instance.
	 */
	public static HookDictionary instance()
	{
		if (Instance == null)
		{
			Instance = new HookDictionary();
		}
		return Instance;
	}

	/**
	 * Adds an instantation interception.
	 * 
	 * @param aClass the fully gualified name of the class that is being
	 *            instantiated.
	 */
	public void addAfterInstantationInterception(String aClass)
	{
		if (!afterInstantationInterceptions.contains(aClass))
		{
			afterInstantationInterceptions.add(aClass);
		}
	}

	/**
	 * Adds a cast interception
	 * 
	 * @param aClass the fully qualified name of the resulting class of the
	 *            cast.
	 */
	public void addCastInterception(String aClass)
	{
		if (!castInterceptions.contains(aClass))
		{
			castInterceptions.add(aClass);
		}
	}

	/**
	 * Adds <b>all</b> method calls of a target class.
	 * 
	 * @param className the fully qualified name of the target class.
	 */
	public void addMethodInterception(String className)
	{
		if (methodInterceptions.get(className) == null)
		{
			methodInterceptions.put(className, new Hashtable());
		}
	}

	/**
	 * Adds a method call of a target class.
	 * 
	 * @param className the fully qualified name of the target class.
	 * @param methodName the method name.
	 * @param params the method parameters.
	 */
	public void addMethodInterception(String className, String methodName, List params)
	{
		Hashtable methods;
		List parameters;
		if (methodInterceptions.get(className) != null)
		{
			methods = (Hashtable) methodInterceptions.get(className);
			if (methods.get(methodName) != null)
			{
				methods.put(methodName, params);
				methodInterceptions.put(className, methods);
			}
			else
			{
				parameters = (List) methods.get(methodName);
				if (!compareParameters(parameters, params))
				{
					methods.put(methodName, params);
					methodInterceptions.put(className, methods);
				}
			}
		}
		else
		{ // create a new entry
			methods = new Hashtable();
			methods.put(methodName, params);
			methodInterceptions.put(className, methods);
		}
	}

	/**
	 * Returns true if an instantiation of the specified class should be
	 * intercepted.
	 */
	public boolean isAfterInstantationInterception(String aClass)
	{
		if (afterInstantationInterceptions.indexOf(aClass) != -1)
		{
			return true;
		}
		return false;
	}

	/**
	 * Returns true if a cast to the specified class should be intercepted.
	 */
	public boolean isCastInterception(String aClass)
	{
		if (castInterceptions.indexOf(aClass) != -1)
		{
			return true;
		}
		return false;
	}

	/**
	 * Returns true if a method call to the specified class should be
	 * intercepted.
	 */
	public boolean isMethodInterception(String className)
	{
		if (methodInterceptions.containsKey(className))
		{
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the specified method call to the specified class should
	 * be intercepted.
	 * 
	 * @param className the fully qualified name of the target class.
	 * @param methodName the name of the method.
	 * @param params the method parameters.
	 */
	public boolean isMethodInterception(String className, String methodName, List params)
	{
		if (methodInterceptions.containsKey(className))
		{
			Hashtable methods = (Hashtable) methodInterceptions.get(className);
			if (methods.containsKey(methodName))
			{
				List parameters = (List) methods.get(methodName);
				if (compareParameters(parameters, params))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Helper method. Compares two list of parameters. Returns true if the lists
	 * are the same.
	 * 
	 * @param params1 - list of parameters
	 * @param params2 - a second list of parameters
	 */
	public boolean compareParameters(List params1, List params2)
	{
		// check the size
		if (params1.size() != params2.size())
		{
			return false;
		}
		String param1;
		String param2; // check the parameters
		Iterator paramsIt1 = params1.iterator();
		Iterator paramsIt2 = params2.iterator();
		while (paramsIt1.hasNext())
		{
			param1 = (String) paramsIt1.next();
			param2 = (String) paramsIt2.next();
			if (!param1.equalsIgnoreCase(param2))
			{
				return false;
			}
		}
		return true;
	}
}
