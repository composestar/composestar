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

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * This is a wrapper for the implementation of a filter; here, specific
 * properties of a filter type (such as required by FIRE and SECRET) can be
 * added. The implementations of the predefined filters will be
 * PrimitiveConcerns; this may also be user-defined filter types that are
 * implemented as a ComposeStarConcern. In the future, add specific attributes
 * here that apply to all filter types, create subclasses on demand. we might or
 * might not be able to reason about the implementation here (depending on
 * whether it is a CpsConcern or PrimitiveConcern).
 */
public class FilterType extends Concern
{
	private static final long serialVersionUID = 7876700154644254076L;

	public String type;

	public static final String WAIT = "Wait";

	public static final String DISPATCH = "Dispatch";

	public static final String ERROR = "Error";

	public static final String META = "Meta";

	public static final String SUBSTITUTION = "Substitution";

	public static final String CUSTOM = "Custom";

	public static final String SEND = "Send";

	public static final String PREPEND = "Prepend";

	public static final String APPEND = "Append";

	/**
	 * @return java.lang.String
	 * @roseuid 401FAA650206
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param typeValue
	 * @roseuid 401FAA65020F
	 */
	public void setType(String typeValue)
	{
		this.type = typeValue;
	}
}
