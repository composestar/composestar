package Composestar.RuntimeCore.Utils;

import java.util.ArrayList;
import java.util.Dictionary;

public abstract class Invoker
{
	protected static Invoker instance = null;

	public static Invoker getInstance()
	{
		if (instance == null)
		{
			Debug.out(Debug.MODE_ERROR, "Invoker", "Invoker instance called without platform instanciation");
		}
		return instance;
	}

	public Object invoke(Object target, String selector, Object[] args)
	{
		return null;
	}

	public Object invoke(String target, String selector, Object[] args)
	{
		return null;
	}

	public Object requestInstance(String target, Object[] args)
	{
		return null;
	}

	public Class getClass(String type)
	{
		return null;
	}

	public ArrayList getAttributesFor(Object target, String selector)
	{
		return null;
	}

	public abstract boolean objectHasMethod(Object inner, String m_selector, Dictionary context);
}