package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: FilterFactory.java,v 1.1 2006/02/16 23:03:56 pascal_durr Exp $
 * 
**/

public class FilterFactory
{
	public static Filter getFilter(String name)
	{
		if (name.equals("Error")) return new ErrorFilter();
		else if (name.equals( "EOF")) return new EndOfFilter();
		else if (name.equals( "Meta")) return new MetaFilter();
		else if (name.equals( "Custom")) return new MetaFilter();
		else if (name.equals( "Dispatch")) return new DispatchFilter();
		else if (name.equals( "Prepend")) return new MetaFilter();
		else if (name.equals( "Append")) return new MetaFilter();

		return null;
	}
}
