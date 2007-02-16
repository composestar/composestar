package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class AlwaysBreakBreakPoint implements BreakPoint {

	public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList MessageList, JoinPoint point)
	{
		return true;
	}
}
