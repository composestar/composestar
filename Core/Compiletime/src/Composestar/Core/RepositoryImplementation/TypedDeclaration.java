/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.RepositoryImplementation;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;

/**
 * This defines a declaration of an entity, together with its type (e.g.
 * variable, parameters, etc.), such as "newThing : ItsType"; It combines a
 * DeclaredRepositoryEntity (through inheritance), with a reference to a type
 * (i.e. a reference to a Concern: hence ConcernReference)
 */
public class TypedDeclaration extends DeclaredRepositoryEntity
{

	private static final long serialVersionUID = -1939895305879861259L;

	public ConcernReference type;

	public Reference theReference;

	public TypedDeclaration()
	{
		super();
	}

	/**
	 * @param type
	 */
	public void setType(ConcernReference type)
	{
		this.type = type;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
	 */
	public ConcernReference getType()
	{
		return type;
	}

	public String asSourceCode()
	{
		return getName() + ": " + type.getQualifiedName();
	}
}
