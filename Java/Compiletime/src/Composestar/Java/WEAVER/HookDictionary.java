package Composestar.Java.WEAVER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private List<String> afterInstantationInterceptions = new ArrayList<String>();

	private List<String> castInterceptions = new ArrayList<String>();

	private Map<String, Object> incomingMethodInterceptions = new HashMap<String, Object>();

	private Map<String, Object> outgoingMethodInterceptions = new HashMap<String, Object>();

	public HookDictionary()
	{}

	/**
	 * Adds an instantation interception to Dictionary.
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
	 * Adds a cast interception to Dictionary.
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
	 * Adds <b>all</b> incoming method calls to a target class to Dictionary.
	 * 
	 * @param className the fully qualified name of the target class.
	 */
	public void addIncomingMethodInterception(String className)
	{
		if (incomingMethodInterceptions.get(className) == null)
		{
			incomingMethodInterceptions.put(className, new Object());
		}
	}

	/**
	 * Adds <b>all</b> outgoing method calls from a target class to Dictionary.
	 * 
	 * @param className the fully qualified name of the target class.
	 */
	public void addOutgoingMethodInterception(String className)
	{
		if (outgoingMethodInterceptions.get(className) == null)
		{
			outgoingMethodInterceptions.put(className, new Object());
		}
	}

	/**
	 * Adds a method call of a target class.
	 * 
	 * @param className the fully qualified name of the target class.
	 * @param methodName the method name.
	 * @param params the method parameters.
	 */
	/*
	 * public void addMethodInterception(String className, String methodName,
	 * List params) { Hashtable methods; List parameters; if
	 * (methodInterceptions.get(className) != null) { methods = (Hashtable)
	 * methodInterceptions.get(className); if (methods.get(methodName) != null)
	 * { methods.put(methodName, params); methodInterceptions.put(className,
	 * methods); } else { parameters = (List) methods.get(methodName); if
	 * (!compareParameters(parameters, params)) { methods.put(methodName,
	 * params); methodInterceptions.put(className, methods); } } } else { //
	 * create a new entry methods = new Hashtable(); methods.put(methodName,
	 * params); methodInterceptions.put(className, methods); } }
	 */

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
	 * Returns true if a method call to the specified target or from the
	 * specified caller should be intercepted.
	 */
	public boolean isMethodInterception(String target, String caller)
	{
		if (incomingMethodInterceptions.containsKey(target))
		{
			return true;
		}
		if (outgoingMethodInterceptions.containsKey(caller))
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
	/*
	 * public boolean isMethodInterception(String className, String methodName,
	 * List params) { if (methodInterceptions.containsKey(className)) {
	 * Hashtable methods = (Hashtable) methodInterceptions.get(className); if
	 * (methods.containsKey(methodName)) { List parameters = (List)
	 * methods.get(methodName); if (compareParameters(parameters, params)) {
	 * return true; } } } return false; }
	 */

	/**
	 * Helper method. Compares two list of parameters. Returns true if the lists
	 * are the same.
	 * 
	 * @param params1 - list of parameters
	 * @param params2 - a second list of parameters
	 */
	/*
	 * public boolean compareParameters(List params1, List params2) { // check
	 * the size if (params1.size() != params2.size()) { return false; } String
	 * param1; String param2; // check the parameters Iterator paramsIt1 =
	 * params1.iterator(); Iterator paramsIt2 = params2.iterator(); while
	 * (paramsIt1.hasNext()) { param1 = (String) paramsIt1.next(); param2 =
	 * (String) paramsIt2.next(); if (!param1.equalsIgnoreCase(param2)) { return
	 * false; } } return true; }
	 */
}
