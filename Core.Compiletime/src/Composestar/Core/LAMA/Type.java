package Composestar.Core.LAMA;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Type extends ProgramElement
{

	public ArrayList m_methods;

	public ArrayList m_fields;

	public String m_name;

	public String m_fullName;

	public boolean m_isNestedPrivate;

	public boolean m_isNestedPublic;

	public Type()
	{
		UnitRegister.instance().registerLanguageUnit(this);
		m_methods = new ArrayList();
		m_fields = new ArrayList();
	}

	/**
	 * @param field
	 */
	public void addField(FieldInfo field)
	{
		m_fields.add(field);
		field.setParent(this);
	}

	/**
	 * @param method
	 */
	public void addMethod(MethodInfo method)
	{
		m_methods.add(method);
		method.setParent(this);
	}

	/**
	 * @return java.util.List
	 */
	public List getFields()
	{
		return m_fields;
	}

	/**
	 * @return java.lang.String
	 */
	public String fullName()
	{
		return m_fullName;
	}

	/**
	 * @param name
	 */
	public void setFullName(String name)
	{
		m_fullName = name;
	}

	/**
	 * @param name
	 * @param types
	 * @return Composestar.dotnet.LAMA.DotNETMethodInfo
	 */
	public MethodInfo getMethod(String name, String[] types)
	{
		for (Iterator it = m_methods.iterator(); it.hasNext();)
		{
			MethodInfo mi = (MethodInfo) it.next();
			if (mi.getName().equals(name) && mi.hasParameters(types))
			{
				return mi;
			}
		}
		return null;
	}

	/**
	 * @return java.util.List
	 */
	public List getMethods()
	{
		return m_methods;
	}

	/**
	 * @return boolean
	 */
	public boolean isNestedPrivate()
	{
		return m_isNestedPrivate;
	}

	/**
	 * @return boolean
	 */
	public boolean isNestedPublic()
	{
		return m_isNestedPublic;
	}

	/**
	 * @return java.lang.String
	 */
	public String name()
	{
		return m_name;
	}

	/**
	 * @param name
	 * @roseuid 4029F83F0366
	 */
	public void setName(String name)
	{
		m_name = name;
	}

	/** Stuff for annotations * */

	public ArrayList annotationInstances = new ArrayList();

	public void addAnnotationInstance(Annotation annotation)
	{
		this.annotationInstances.add(annotation);
	}

	public void removeAnnotationInstance(Annotation annotation)
	{
		this.annotationInstances.remove(annotation);
	}

	public List getAnnotationInstances()
	{
		return this.annotationInstances;
	}

	// stuff for LOLA

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
	{
		return fullName();
	}

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#hasUnitAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}
}
