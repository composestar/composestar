/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2005-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.util.regex.Pattern;

/**
 * 
 */
public class Constraint {

	public static final boolean CONFLICT = true;
	public static final boolean REQUIREMENT = false;
	
	private boolean type;
	
	private String resource;
	private Pattern pattern;
	private String patternString;
	private String message;
	
	public Constraint(String resource, String patternString, String message, boolean type)
	{
		this.resource = resource;
		this.patternString = patternString;
		this.pattern = Pattern.compile(patternString);
		this.message = message;
		this.type = type;
	}

	public String getPattern()
	{
		return patternString;
	}
	
	public String getResource()
	{
		return this.resource;
	}

	public String getMessage()
	{
		return this.message;
	}

	public boolean conflicts(String sequence)
	{
		//return this.pattern.matcher(sequence).matches();
		//System.err.println("Matching " + sequence + " against " + patternString );
		if( !type )
			System.out.println("Assertion: matching " + sequence + " with " + patternString );
		return (type? pattern.matcher(sequence).find(): !pattern.matcher(sequence).find());
	}
}
