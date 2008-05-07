/*
 * Created on 31-mei-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

/**
 * Exception thrown when the pattern could not be parsed
 * 
 * @author Arjan de Roo
 */
public class PatternParseException extends Exception
{
	private static final long serialVersionUID = -1422741759836668151L;

	public PatternParseException(String message)
	{
		super(message);
	}
}
