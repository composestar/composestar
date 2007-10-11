/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Implementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SuperImposition;
import Composestar.Utils.CPSIterator;
import Composestar.Utils.StringUtils;

/**
 * we can reason about the implementation here.
 */
public class CpsConcern extends Concern
{
	private static final long serialVersionUID = 7396365167133765237L;

	public Implementation implementation;

	public SuperImposition superImposition;

	public Vector filterModules; // instances

	public Vector filterModulesAST;

	public Vector formalParameters;

	/**
	 * The namespace/package
	 */
	public Vector namespace;

	/**
	 * @modelguid {E0CC63BC-2E96-432B-8132-8C0ADC2BDBA7}
	 * @roseuid 401FAA57006A
	 */
	public CpsConcern()
	{
		super();
		filterModules = new Vector();
		filterModulesAST = new Vector();
		formalParameters = new Vector();
		namespace = new Vector();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Implementati
	 *         on
	 * @modelguid {46DF0151-F0BF-48FD-B241-0B3724EC2E1E}
	 * @roseuid 401FAA57006B
	 */
	public Implementation getImplementation()
	{
		return implementation;
	}

	/**
	 * @param implementationValue
	 * @modelguid {EFD2C194-0B71-4FA3-A44B-C02CFEFDA704}
	 * @roseuid 401FAA57007E
	 */
	public void setImplementation(Implementation implementationValue)
	{
		this.implementation = implementationValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SuperImposi
	 *         tion
	 * @modelguid {4F521D9A-70A9-4E51-8E13-F90016B44BB1}
	 * @roseuid 401FAA570089
	 */
	public SuperImposition getSuperImposition()
	{
		return superImposition;
	}

	/**
	 * @param superImpositionValue
	 * @modelguid {6ABA6FD1-A98D-4CCE-B045-DEFD54C2FD94}
	 * @roseuid 401FAA57009C
	 */
	public void setSuperImposition(SuperImposition superImpositionValue)
	{
		this.superImposition = superImpositionValue;
	}

	/**
	 * filterModules
	 * 
	 * @param filtermodule
	 * @return boolean
	 * @modelguid {842C6E19-1DEB-4D7B-8241-628B5BB34E72}
	 * @roseuid 401FAA5700B1
	 */
	public boolean addFilterModule(FilterModule filtermodule)
	{
		filterModules.addElement(filtermodule);
		return true;
	}

	public boolean addFilterModuleAST(FilterModuleAST filtermodule)
	{
		filterModulesAST.addElement(filtermodule);
		return true;
	}

	public FilterModuleAST getFilterModuleAST(String name)
	{
		Iterator it = filterModulesAST.iterator();
		while (it.hasNext())
		{
			FilterModuleAST fmast = (FilterModuleAST) it.next();
			if (fmast != null && fmast.getName().equals(name))
			{
				return fmast;
			}
		}
		return null;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule
	 * @modelguid {D7CB52CB-7A13-45A2-9CF7-A6625311D7E0}
	 * @roseuid 401FAA5700CF
	 */
	public FilterModule removeFilterModule(int index)
	{
		Object o = filterModules.elementAt(index);
		filterModules.removeElementAt(index);
		return (FilterModule) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule
	 * @modelguid {CD93B39D-EC05-4806-9043-633852DDE5BA}
	 * @roseuid 401FAA5700E4
	 */
	public FilterModule getFilterModule(int index)
	{
		return (FilterModule) filterModules.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {25161219-B395-4D4F-9705-E297083CD069}
	 * @roseuid 401FAA5700F7
	 */
	public Iterator getFilterModuleIterator()
	{
		return new CPSIterator(filterModules);
	}

	public Iterator getFilterModuleASTIterator()
	{
		return new CPSIterator(filterModulesAST);
	}

	/**
	 * @return java.util.Iterator
	 * @roseuid 404F1D710057
	 */
	public Iterator getParameterIterator()
	{
		return new CPSIterator(formalParameters);
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 * @roseuid 404F1D820084
	 */
	public LabeledConcernReference removeParameter(int index)
	{
		Object o = formalParameters.elementAt(index);
		formalParameters.removeElementAt(index);
		return (LabeledConcernReference) o;
	}

	/**
	 * @param parameter
	 * @return boolean
	 * @roseuid 404F1DA002F1
	 */
	public boolean addParameter(LabeledConcernReference parameter)
	{
		formalParameters.addElement(parameter);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 * @roseuid 404F1DE50151
	 */
	public LabeledConcernReference getParameter(int index)
	{
		return (LabeledConcernReference) formalParameters.elementAt(index);
	}

	public void setNamespace(String inName)
	{
		Vector vect = new Vector();
		String[] str = StringUtils.split(inName, '.');
		for (int i = 0; i < str.length; i++)
		{
			vect.add(str[i]);
		}
		setNamespace(vect);
	}

	public void setNamespace(List inName)
	{
		namespace.clear();
		namespace.addAll(inName);
		updateRepositoryReference();
	}

	public Vector getNamespace()
	{
		return namespace;
	}

	public String getQualifiedName()
	{
		if (namespace == null)
		{
			return super.getQualifiedName();
		}
		else
		{
			StringBuffer ns = new StringBuffer();
			for (int i = 0; i < namespace.size(); i++)
			{
				if (ns.length() > 0)
				{
					ns.append(".");
				}
				ns.append(namespace.get(i));
			}
			if (ns.length() > 0)
			{
				ns.append(".");
			}
			ns.append(getName());
			return ns.toString();
		}
	}

	public Object clone() throws CloneNotSupportedException
	{
		CpsConcern newObject;
		try
		{
			newObject = (CpsConcern) super.clone();

			// At this point, the newObject shares all data with the object
			// running clone. If you want newObject to have its own
			// copy of data, you must clone this data yourself.

		}
		catch (CloneNotSupportedException e)
		{
			// this should never happen
			throw new InternalError(e.toString());
		}

		return newObject;
	}

	public String toString()
	{
		return "CpsConcern[" + getQualifiedName() + "]";
	}
}
