package Composestar.DotNET.PACMAN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.DotNET.LAMA.DotNETMethodInfo;

class ExpandedType
{
	private final String m_name;
	private final List m_methods;
	
	public ExpandedType(String name)
	{
		m_name = name;
		m_methods = new ArrayList();
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public Iterator methods()
	{
		return m_methods.iterator();
	}
	
	public void addMethod(DotNETMethodInfo mi)
	{
		m_methods.add(mi);
	}
}