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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method;
import Composestar.Utils.CPSIterator;

/**
 * reference to a method
 */
public class MethodReference extends FilterModuleElementReference
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1502343124123806850L;

	private Method ref;

	public Vector parameterTypeList;

	/**
	 * @roseuid 401FAA6602BB
	 */
	public MethodReference()
	{
		super();
		parameterTypeList = new Vector();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
	 * @roseuid 401FAA6602C5
	 */
	public Method getRef()
	{
		return ref;
	}

	/**
	 * @param refValue
	 * @roseuid 40503C7102B9
	 */
	public void setRef(Method refValue)
	{
		this.ref = refValue;
	}

	/**
	 * @param parameter
	 * @return boolean
	 * @roseuid 40ADE3150276
	 */
	public boolean addParameter(ConcernReference parameter)
	{
		parameterTypeList.addElement(parameter);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 * @roseuid 40ADE32D016C
	 */
	public ConcernReference removeParameter(int index)
	{
		Object o = parameterTypeList.elementAt(index);
		parameterTypeList.removeElementAt(index);
		return (ConcernReference) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 * @roseuid 40ADE346038F
	 */
	public ConcernReference getParameter(int index)
	{
		return (ConcernReference) parameterTypeList.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @roseuid 40ADE35901C9
	 */
	public Iterator getParameterIterator()
	{
		return new CPSIterator(parameterTypeList);
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
	// nothing yet
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	public void writeObject(ObjectOutputStream out) throws IOException
	{
	// nothing yet
	}
}
