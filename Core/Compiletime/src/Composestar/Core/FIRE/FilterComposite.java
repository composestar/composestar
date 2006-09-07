package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: FilterComposite.java,v 1.2 2006/03/06 09:07:40 reddog33hummer Exp $
 * 
**/

public abstract class FilterComposite extends FilterComponent
{
	protected FilterComponent fc1 = null;
	protected FilterComponent fc2 = null;
	
	public void addChild1(FilterComponent fc) { fc1 = fc; };
	public void addChild2(FilterComponent fc) { fc2 = fc; };

	protected void toTreeString(StringBuffer strPart)
	{
		strPart.append('(');
        strPart.append(toString()).append(' ');

		if (fc1 != null) 
		{
			fc1.toTreeString(strPart);
			strPart.append(' ');
		}

		if (fc2 != null) 
		{
			fc2.toTreeString(strPart);
			strPart.append(' ');
		}

		strPart.append(')');

	}
}

