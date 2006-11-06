package Composestar.Core.INCRE;

import Composestar.Core.Exception.ModuleException;

import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodNode extends Node
{
	private ArrayList parameters;

	public MethodNode(String ref)
	{
		super(ref);
		parameters = new ArrayList();
	}

	/**
	 * Calls referenced method with obj as input
	 * 
	 * @return object returned by the called method
	 * @param Object obj
	 */
	public Object visit(Object obj) throws ModuleException
	{
		try
		{
			Method mymethod;

			if (reference.indexOf('.') > 0)
			{
				// reference => FULLNAME_OF_CLASS.NAME_OF_METHOD
				String fullclassname = reference.substring(0, reference.lastIndexOf('.'));
				String methodname = reference.substring(reference.lastIndexOf('.') + 1);
				Class myclass = Class.forName(fullclassname);
				Class[] paramclasses = { obj.getClass() };
				mymethod = myclass.getMethod(methodname, paramclasses);
				Object[] paramobjects = { obj };
				return mymethod.invoke(myclass.newInstance(), paramobjects);
			}
			else
			{
				// reference => NAME_OF_METHOD
				mymethod = obj.getClass().getMethod(reference, getParameterTypes());
				return mymethod.invoke(obj, parameters.toArray());
			}
		}
		catch (InvocationTargetException ex)
		{
			String error = ex.getCause().getMessage();
			if (error == null)
			{
				error = ex.getCause().toString();
			}

			throw new ModuleException(error, "INCRE");
		}
		catch (Exception excep)
		{
			throw new ModuleException("Cannot visit method node " + reference + ' ' + excep.toString(), "INCRE");
		}
	}

	public void setParameters(ArrayList params)
	{
		this.parameters = params;
	}

	public ArrayList getParameters()
	{
		return this.parameters;
	}

	public Class[] getParameterTypes()
	{

		try
		{
			if (!this.parameters.isEmpty())
			{
				Class[] classes = new Class[this.parameters.size()];
				for (int i = 0; i < this.parameters.size(); i++)
				{
					Object obj = parameters.get(i);
					classes[i] = obj.getClass();
				}
				return classes;
			}
		}
		catch (NullPointerException npex)
		{
			npex.printStackTrace();
		}

		return null;
	}

	/**
	 * @return an unique id for a referenced method
	 */
	public String getUniqueID(Object obj)
	{
		String uniqueID = obj.hashCode() + "." + this.reference;
		if (!parameters.isEmpty())
		{
			Iterator params = parameters.iterator();
			while (params.hasNext())
			{
				uniqueID += (String) params.next();
			}
		}
		return uniqueID;
	}
}
