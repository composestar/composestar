package Composestar.RuntimeCore.CODER.BreakPoint.Parsers.Reg;

import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.*;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Halter;
import java.io.StringReader;

/**
 * Summary description for BreakPointRegParser.
 */
public class BreakPointRegParser extends BreakPointParser 
{
	public BreakPointRegParser()
	{
	}

	public BreakPoint parse(String regularExpression, Halter halt) throws BreakPointParseException
	{
		if(regularExpression == null || "".equals(regularExpression.trim()))
		{
			return null;
		}

		try 
		{
			StringReader sr = new StringReader(regularExpression);
			RegLexer lexer = new RegLexer(sr);
			RegParser parser = new RegParser(lexer);
			RegularExpression expression = parser.expression();
            return regularExpressionToBreakPoint(expression);
		} 
		catch (Exception e) 
		{
			throw new BreakPointParseException(e);
		}
	}

	public BreakPoint regularExpressionToBreakPoint(RegularExpression expression)
	{
		return null;
	}
}
