package Composestar.RuntimeCore.FLIRT.Reflection;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

public class JoinPoint
{
	private Object joinPointInstance; // the 'current' object

	private HashMap attributemap = new HashMap();

	private Dictionary internals = new Hashtable();

	private Dictionary externals = new Hashtable();

	private ArrayList attributelist = new ArrayList();

	public JoinPoint()
	{}

	public JoinPoint(Object instance)
	{
		this.joinPointInstance = instance;
	}

	public JoinPoint(Object instance, Dictionary internals, Dictionary externals, ArrayList atributesList)
	{
		this(instance);
		this.internals = internals;
		this.externals = externals;
		this.attributelist = atributesList;
	}

	public Object getInstance()
	{
		return joinPointInstance;
	}

	public void setAttributeList(ArrayList atts)
	{
		this.attributelist = atts;
	}

	/**
	 * Returns a list of attribute objects associated with this join point.
	 * 
	 * @return ArrayList The list of attributes.
	 */
	public ArrayList getAttributeList()
	{
		return this.attributelist;
	}

	public void setAttributeMap(HashMap attrmap)
	{
		this.attributemap = attrmap;
	}

	public HashMap getAttributeMap()
	{
		return this.attributemap;
	}

	public Object getAttribute(String name)
	{
		for (int i = 0; i < attributelist.size(); i++)
		{
			Object obj = attributelist.get(i);
			if (obj.toString().equals(name))
			{
				return obj;
			}
		}
		return null;
		// return this.attributemap.get(name);
	}

	/**
	 * Returns the name of the current class.
	 * 
	 * @return String The name of the current class.
	 */
	public String getClassName()
	{
		return this.joinPointInstance.toString();
	}

	public Enumeration getInternals()
	{
		return this.internals.elements();
	}

	public Object getInternal(String name)
	{
		return this.internals.get(name);
	}

	public Enumeration getExternals()
	{
		return this.externals.elements();
	}

	public Object getExternal(String name)
	{
		return this.externals.get(name);
	}

	public void setExternals(Dictionary dic)
	{
		this.externals = dic;
	}

	public void setInternals(Dictionary dic)
	{
		this.internals = dic;
	}
}
