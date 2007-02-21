package Composestar.RuntimeCore.CODER.BreakPoint.Parsers.Ltl;

import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.*;
import Composestar.RuntimeCore.CODER.BreakPoint.*;

import java.io.StringReader;
/**
 * Summary description for BreakPointRegParser.
 * Custom Made Parser because ANTLR doesn't swallow the formal LTL grammar
 */
public class BreakPointLTLParser extends BreakPointParser 
{
	public BreakPointLTLParser()
	{
	}

	public BreakPoint parse(String ltlExpression) throws BreakPointParseException
	{
		if(ltlExpression == null || "".equals(ltlExpression.trim()))
		{
			return null;
		}

		try 
		{
			StringReader sr = new StringReader(ltlExpression);
			/*LtlLexer lexer = new LtlLexer(sr);
			LtlParser parser = new LtlParser(lexer);
			return parser.formula();*/ return new AlwaysBreakPoint();
		} 
		catch (Exception e) 
		{
			throw new BreakPointParseException(e);
		}
	}
}
