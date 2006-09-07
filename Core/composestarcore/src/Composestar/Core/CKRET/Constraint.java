/*
 * Created on Mar 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.util.regex.Pattern;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Constraint {

	public static boolean CONFLICT = true;
	public static boolean REQUIREMENT = false;
	
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
