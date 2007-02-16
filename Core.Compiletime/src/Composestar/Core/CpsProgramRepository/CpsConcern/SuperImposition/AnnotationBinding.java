/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id:
 * 
 * Created on Feb 23, 2005 by whavinga
 */

package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition;

import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;

public class AnnotationBinding extends Binding
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3277653416800288682L;

	public Vector annotationList; // Vector of ConcernReference

	public AnnotationBinding()
	{
		super();
		annotationList = new Vector();
	}

	public boolean addAnnotation(ConcernReference annot)
	{
		annotationList.addElement(annot);
		return true;
	}

	/**
	 * @return Vector (.NET doesn't allow Collection) of ConcernReferences that
	 *         (when resolved) should point to annotation classes
	 */
	public Vector getAnnotations()
	{
		return annotationList;
	}
}