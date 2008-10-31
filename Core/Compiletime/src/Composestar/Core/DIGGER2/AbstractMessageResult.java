/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import Composestar.Core.CpsRepository2.Concern;

/**
 * Base class for the message results
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractMessageResult
{
	/**
	 * The breadcrumb related to the result.
	 */
	protected Breadcrumb crumb;

	/**
	 * @return the associated breadcrumb
	 */
	public Breadcrumb getCrumb()
	{
		return crumb;
	}

	/**
	 * @return the concern of the crumb
	 */
	public Concern getConcern()
	{
		return crumb.getConcern();
	}

	/**
	 * @return true when it is a valid result, false if the result is erroneous
	 */
	public abstract boolean isValidResult();
}
