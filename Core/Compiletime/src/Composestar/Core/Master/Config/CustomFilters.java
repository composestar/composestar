/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class CustomFilters implements Serializable
{
	private static final long serialVersionUID = -3968104066872916800L;

	private List<CustomFilter> filters;

	public CustomFilters()
	{
		filters = new ArrayList<CustomFilter>();
	}

	public void addCustomFilters(CustomFilter cf)
	{
		filters.add(cf);
	}

	public List<CustomFilter> getCustomFilters()
	{
		return filters;
	}
}
