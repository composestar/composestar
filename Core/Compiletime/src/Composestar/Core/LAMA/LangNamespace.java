/*
 * LangNamespace.java - Created on 5-aug-2004 by havingaw
 */

package Composestar.Core.LAMA;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * Namespace unit type
 * 
 * @author havingaw
 */
public class LangNamespace extends ProgramElement
{
	private static final long serialVersionUID = -9184363245232975615L;

	private HashSet childClasses;

	private HashSet childInterfaces;

	private LangNamespace parentNamespace;

	private HashSet childNamespaces;

	private String name;

	/**
	 * @param id
	 */
	public LangNamespace()
	{
		this(null);
	}

	public LangNamespace(String inName)
	{
		this.name = inName;
		this.childClasses = new HashSet();
		this.childInterfaces = new HashSet();
		this.parentNamespace = null;
		this.childNamespaces = new HashSet();
	}

	/**
	 * @return Hashset containing LangType children of this namespace
	 */
	public HashSet getChildClasses()
	{
		return this.childClasses;
	}

	public LangNamespace getParentNamespace()
	{
		return this.parentNamespace;
	}

	/**
	 * @param inParentNamespace The parentNamespace to set.
	 */
	public void setParentNamespace(LangNamespace inParentNamespace)
	{
		this.parentNamespace = inParentNamespace;
	}

	/**
	 * @param inChildClasses The childClasses to set.
	 */
	public void setChildClasses(HashSet inChildClasses)
	{
		this.childClasses = inChildClasses;
	}

	/**
	 * @return Returns the childInterfaces.
	 */
	public HashSet getChildInterfaces()
	{
		return childInterfaces;
	}

	/**
	 * @param inChildInterfaces The childInterfaces to set.
	 */
	public void setChildInterfaces(HashSet inChildInterfaces)
	{
		this.childInterfaces = inChildInterfaces;
	}

	public void addChildClass(ProgramElement childClass)
	{
		childClasses.add(childClass);
	}

	public void addChildInterface(ProgramElement childInterface)
	{
		childInterfaces.add(childInterface);
	}

	/**
	 * @return Returns the childNamespaces.
	 */
	public Set getChildNamespaces()
	{
		return childNamespaces;
	}

	/**
	 * @param childNamespaces The childNamespaces to set.
	 */
	public void setChildNamespaces(HashSet childNamespaces)
	{
		this.childNamespaces = childNamespaces;
	}

	public void addChildNamespace(LangNamespace childNamespace)
	{
		childNamespaces.add(childNamespace);
	}

	public String toString()
	{
		return "LangNamespace(name=" + getUnitName() + ')';
	}

	/**
	 * @param name The name to set.
	 */
	public void setUnitName(String name)
	{
		this.name = name;
	}

	/** Stuff for LOLA* */

	/**
	 * @return Returns the unit name.
	 */
	public String getUnitName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */

	public UnitResult getUnitRelation(String argumentName)
	{
		if (argumentName.equals("ChildNamespaces"))
		{
			return new UnitResult(childNamespaces);
		}
		else if (argumentName.equals("ParentNamespace"))
		{
			return new UnitResult(parentNamespace);
		}
		else if (argumentName.equals("ChildClasses"))
		{
			return new UnitResult(childClasses);
		}
		else if (argumentName.equals("ChildInterfaces"))
		{
			return new UnitResult(childInterfaces);
		}
		else
		{
			System.err.println("LangNamespace: unknown unit relation: " + argumentName);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	public String getUnitType()
	{
		return "Namespace";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#hasAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
	{
		// Namespaces don't have any attributes because they're not 'real' units
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	public Collection getUnitAttributes()
	{
		return new HashSet();
	}
}
