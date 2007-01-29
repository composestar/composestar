/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.C.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;

import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * CFile is the class of the abstract meta model, from the core.
 */
public class CFile extends Type
{
	/**
	 * File in C and as a hack this is also used as a annotation However not now
	 * at the moment
	 */
	private static final long serialVersionUID = 5652622506200113401L;

	public int HashCode;

	private CDirectory parent;

	// private boolean isAnnotation;
	public String Name;

	public String FullName;

	public CFile()
	{
		super();
	}

	public int getHashCode()
	{
		return HashCode;
	}

	public void setHashCode(int code)
	{
		HashCode = code;
	}

	public void addVariable(CVariable variable)
	{
		addField(variable);
		variable.setParent(this);
	}

	public void setName(String Name)
	{
		this.Name = Name;
		FullName = Name;
	}

	public String getFullName()
	{
		return FullName;
	}

	public String fullname()
	{
		return FullName;
	}

	public void setDirectory(CDirectory dir)
	{
		parent = dir;
	}

	public CDirectory getDirectory()
	{
		return parent;
	}

	/***************************************************************************
	 * public CFunction getMethod(String name) { CFunction method = null; for(
	 * ListIterator iter = Functions.listIterator(); iter.hasNext(); ) { method =
	 * (CFunction)iter.next(); // if same name && param length if(
	 * method.name().equals( name ) ) { return method; } } return null; }
	 **************************************************************************/

	public CMethodInfo getMethodInfo(String name)
	{
		CMethodInfo method = null;
		for (ListIterator iter = m_methods.listIterator(); iter.hasNext();)
		{
			method = (CMethodInfo) iter.next();
			// if same name && param length
			if (method.getName().equals(name))
			{
				return method;
			}
		}
		return null;
	}

	/** **** Implementation of Language Unit interface ********* */

	private HashSet toHashSet(Collection c)
	{
		HashSet result = new HashSet();
		Iterator iter = c.iterator();
		for (Object aC : c)
		{
			result.add(aC);
		}
		return result;
	}

	private HashSet filterDeclaredHere(Collection in)
	{
		HashSet out = new HashSet();
		Iterator iter = in.iterator();
		for (Object obj : in)
		{
			out.add(obj);
			// System.out.println(((ProgramElement)obj).getUnitName());
		}
		return out;
	}

	public UnitResult getUnitRelation(String argumentName)
	{
		/**
		 * Real C model if (argumentName.equals("ChildMethods")) return new
		 * UnitResult(filterDeclaredHere(Methods)); if
		 * (argumentName.equals("ChildFunctions")) return new
		 * UnitResult(filterDeclaredHere(Functions)); else if
		 * (argumentName.equals("ChildVariables")) return new
		 * UnitResult(filterDeclaredHere(Fields)); else if
		 * (argumentName.equals("ParentDirectory")) return new
		 * UnitResult(parent);
		 */
		if (getUnitType().equals("Class"))
		{
			if (argumentName.equals("ChildMethods"))
			{
				return new UnitResult(filterDeclaredHere(m_methods));
			}
			else if (argumentName.equals("ChildFields"))
			{
				return new UnitResult(filterDeclaredHere(m_fields));
			}
		}
		else if (getUnitType().equals("Annotation"))
		{
			HashSet resMethods = new HashSet();
			Iterator i = getAnnotationInstances().iterator();
			for (Object o : getAnnotationInstances())
			{
				ProgramElement unit = ((CAnnotation) o).getTarget();
				if (unit instanceof CMethodInfo)
				{
					resMethods.add(unit);
				}
			}
			if (argumentName.equals("AttachedMethods"))
			{
				return new UnitResult(resMethods);
				// System.out.println("Annotation found it works quit good in
				// CFILE");
			}
		}
		return null; // Should never happen!
	}

	public String getUnitName()
	{
		return getFullName();
	}

	public boolean hasUnitAttribute(String attribute)
	{
		return false;
	}

	public String getUnitType()
	{
		// if (isAnnotation())
		// return "Annotation";
		// return "File";
		return "Class";
	}

	public Collection getUnitAttributes()
	{
		return null;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		HashCode = in.readInt();
		m_methods = (ArrayList) in.readObject();
		m_fields = (ArrayList) in.readObject();
		parent = (CDirectory) in.readObject();
		// isAnnotation = in.readBoolean();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeObject(m_fields);
		out.writeObject(m_methods);
		out.writeObject(parent);
		// out.writeBoolean(isAnnotation);
	}
}
