package Composestar.RuntimeCore.CODER.VisualDebugger.Parsers;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.BreakPoint;
/**
 * Summary description for BreakPointParser.
 */
public abstract class BreakPointParser
{
	public abstract BreakPoint parse(String formula, Halter halt) throws BreakPointParseException;
}
