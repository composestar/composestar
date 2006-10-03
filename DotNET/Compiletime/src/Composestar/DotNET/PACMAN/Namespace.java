package Composestar.DotNET.PACMAN;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class Namespace
{
	private final String m_name;
	private final Set m_types;
	
	public Namespace(String name)
	{
		this.m_name = name;
		m_types = new HashSet();
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public Iterator types()
	{
		return m_types.iterator();
	}
	
	public void addType(ExpandedType et)
	{
		m_types.add(et);
	}

	public int hashCode()
	{
		return m_name.hashCode();
	}
	
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		
		if (! (o instanceof Namespace))
			return false;
		
		Namespace other = (Namespace)o;
		return m_name.equals(other.m_name);
	}
}