/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

import java.util.List;
import java.util.Vector;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * identifiers that are references; in principle, these will be resolved by
 * searching the repository, but it can also be a pseudo variable (server,
 * sender, inner, message, ...); in this case, it may, or may not be possible to
 * resolve the name (i.e. to let it refer to a concern that defines its
 * signature)
 */
public abstract class Reference extends RepositoryEntity
{
	/**
	 * package name
	 */
	public Vector pack;

	/**
	 * name of an identifier, will be resolved later on
	 */
	public String name;

	/**
	 * whether the reference is resolved or not
	 */
	public boolean resolved;

	/**
	 * whether the reference is resolved or not
	 * 
	 * @roseuid 401FAA6701B8
	 */
	public Reference()
	{
		super();
		resolved = false;
		this.pack = new Vector();
	}

	public Reference(List pkg)
	{
		this();
		if (pkg.size() > 0)
		{
			pack.addAll(pkg.subList(0, pkg.size() - 1));
			name = (String) pkg.get(pkg.size() - 1);
		}
	}

	/**
	 * @return java.lang.String
	 * @roseuid 401FAA6701C2
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param nameValue
	 * @roseuid 401FAA6701CC
	 */
	public void setName(String nameValue)
	{
		this.name = nameValue;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 401FAA6701CE
	 */
	public Vector getPackage()
	{
		return pack;
	}

	/**
	 * @param type
	 * @roseuid 401FAA6701D6
	 * @deprecated Should not be used; package should be a Vector
	 */
	public void setPackage(String type)
	{
		Vector temp;
		temp = new Vector();
		temp.addElement(type);
		this.pack = temp;
	}

	/**
	 * @param packValue
	 * @roseuid 40ADE4B50151
	 */
	public void setPackage(Vector packValue)
	{
		if (packValue == null)
		{
			packValue = new Vector();
		}

		pack.clear();
		pack.addAll(packValue);
	}

	/**
	 * @return boolean
	 * @roseuid 40ADE4550302
	 */
	public boolean getResolved()
	{
		return resolved;
	}

	/**
	 * @param resolvedValue
	 * @roseuid 40ADE46301B8
	 */
	public void setResolved(boolean resolvedValue)
	{
		this.resolved = resolvedValue;
	}

	public String getQualifiedName()
	{
		StringBuffer qname = new StringBuffer();
		for (int i = 0; i < pack.size(); i++)
		{
			qname.append(pack.elementAt(i));
			qname.append(".");
		}
		qname.append(name);
		return qname.toString();
	}

}
