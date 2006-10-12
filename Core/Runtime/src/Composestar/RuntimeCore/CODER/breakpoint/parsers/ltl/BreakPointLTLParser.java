package Composestar.RuntimeCore.CODER.BreakPoint.Parsers.LTL;

import Composestar.RuntimeCore.CODER.Halter;
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

	public BreakPoint parse(String ltlExpression, Halter halt) throws BreakPointParseException
	{
		if(ltlExpression == null || "".equals(ltlExpression.trim()))
		{
			return null;
		}

		try 
		{
			StringReader sr = new StringReader(ltlExpression);
			LtlLexer lexer = new LtlLexer(sr);
			LtlParser parser = new LtlParser(lexer);
			parser.setHalter(halt);
			return parser.formula();
		} 
		catch (Exception e) 
		{
			throw new BreakPointParseException(e);
		}
	}
}
