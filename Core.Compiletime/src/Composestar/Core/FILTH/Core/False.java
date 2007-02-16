/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH.Core;

/**
 * @author nagyist
 */
public class False implements Parameter
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see Parameter#evaluate()
	 */
	public Boolean evaluate()
	{

		return Boolean.FALSE;
	}

}
