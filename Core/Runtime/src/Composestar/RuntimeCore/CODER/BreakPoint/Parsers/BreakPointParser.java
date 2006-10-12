package Composestar.RuntimeCore.CODER.BreakPoint.Parsers;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
/**
 * Summary description for BreakPointParser.
 */
public abstract class BreakPointParser
{
	public abstract BreakPoint parse(String formula, Halter halt) throws BreakPointParseException;
}
