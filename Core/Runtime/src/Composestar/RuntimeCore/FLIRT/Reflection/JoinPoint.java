package Composestar.RuntimeCore.FLIRT.Reflection;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class JoinPoint
{
	private WeakReference joinPointInstance; // the 'current' object

	private HashMap attributemap = new HashMap();

	private Dictionary internals = new Hashtable();

	private Dictionary externals = new Hashtable();

	private List attributelist = new ArrayList();

	public JoinPoint()
	{}

	public JoinPoint(Object instance)
	{
		this.joinPointInstance = new WeakReference(instance);
	}

	public JoinPoint(Object instance, Dictionary internals, Dictionary externals, List atributesList)
	{
		this(instance);
		this.internals = internals;
		this.externals = externals;
		this.attributelist = atributesList;
	}

	public Object getInstance()
	{
		return joinPointInstance.get();
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
	public List getAttributeList()
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
		if (joinPointInstance.get() != null)
		{
			return this.joinPointInstance.get().getClass().getName();
		}
		return null;
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
