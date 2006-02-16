/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Resource.java,v 1.1 2006/02/13 11:16:53 pascal Exp $
 */
package Composestar.Core.ACTING;

import java.util.List;

/**
 * @author Staijen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Resource {
	
	List operations;
	String name;
	
	public Resource(String name) {
		this.name = name;
		operations = new java.util.ArrayList();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void addOperation(String op) {
		operations.add(op);
	}
}
