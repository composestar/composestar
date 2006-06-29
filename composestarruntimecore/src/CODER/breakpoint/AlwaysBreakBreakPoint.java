package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class AlwaysBreakBreakPoint implements BreakPoint {

	public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filtersModules, Dictionary context)
	{
		return true;
	}
}
