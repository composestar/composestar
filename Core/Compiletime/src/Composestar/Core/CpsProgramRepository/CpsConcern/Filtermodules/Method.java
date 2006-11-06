/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernReference;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Utils.CPSIterator;

/**
 * @modelguid {A51307B3-6469-4665-9303-4955BF349CE9}
 */
public class Method extends DeclaredRepositoryEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -986496657552076685L;

	/**
	 * @modelguid {E45C8888-FD59-4E57-9DDC-7588582E1AA5}
	 */
	public ConcernReference returnType;

	public Vector formalParameters;

	public Object PlatformRepresentation;

	/**
	 * @modelguid {01338DEC-2CD8-43AF-B575-167DBF710759}
	 * @roseuid 401FAA660103
	 */
	public Method()
	{
		super();
		formalParameters = new Vector();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 * @modelguid {BCBFC186-11CF-4EB1-B3C6-C407AC5F5B78}
	 * @roseuid 401FAA660117
	 */
	public ConcernReference getReturnType()
	{
		return returnType;
	}

	/**
	 * @param returnTypeValue
	 * @modelguid {5CE6E73F-C85F-4992-B5D5-91FEC8812EDF}
	 * @roseuid 401FAA660120
	 */
	public void setReturnType(ConcernReference returnTypeValue)
	{
		this.returnType = returnTypeValue;
	}

	/**
	 * parameters
	 * 
	 * @param parameter
	 * @return boolean
	 * @modelguid {14E4E07A-814B-4B74-8B6D-9E34A7F949DC}
	 * @roseuid 401FAA660122
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
	 * @modelguid {22B8EE88-1A44-4F60-B63B-7E39EC81E709}
	 * @roseuid 401FAA660135
	 */
	public LabeledConcernReference removeParameter(int index)
	{
		Object o = formalParameters.elementAt(index);
		formalParameters.elementAt(index);
		return (LabeledConcernReference) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
	 *         ference
	 * @modelguid {2608FBDD-2229-4855-874E-8883B7AE60A9}
	 * @roseuid 401FAA66013F
	 */
	public LabeledConcernReference getParameter(int index)
	{
		return (LabeledConcernReference) formalParameters.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {FA05353A-049C-4662-891F-B86081DA356D}
	 * @roseuid 401FAA660153
	 */
	public Iterator getParameterIterator()
	{
		return new CPSIterator(formalParameters);
	}

	/**
	 * @return java.lang.Object
	 * @roseuid 4050497E0175
	 */
	public Object getSignatureImplementation()
	{
		return this.getDynObject("SignatureImplementation");
	}

	public void setSignatureImplementation(Object signatureImplementation)
	{
		this.addDynObject("SignatureImplementation", signatureImplementation);
	}
}
