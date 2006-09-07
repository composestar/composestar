/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: UnknownResourceException.java,v 1.1 2006/02/13 11:16:53 pascal Exp $
 */
package Composestar.Core.ACTING;

/**
 * @author Staijen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UnknownResourceException extends Exception {
	public UnknownResourceException(String name) {
		super("Unknown Resource: " + name);
	}
}
