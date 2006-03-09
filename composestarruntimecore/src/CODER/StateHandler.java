package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.Utils.*;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.CODER.*;
import java.util.*;
/**
 * Summary description for StateHandler.
 */
public class StateHandler {
	private final static String MODULENAME= "CODER(StateHandler)";

	private Halter halter;
	private Thread myThread; //Cant use currentTread because of debugger in different thread

	private Object currentFilter = null;
	private Object source = null;
	private Object target = null;
	private Object filters = null;
	private Object context = null;
	private BreakPoint breakpoint;

	public StateHandler(Thread thread, BreakPoint breakpoint, Halter halter)
	{
		this.breakpoint = breakpoint;
		this.halter = halter;
		this.myThread = thread;
	}

	public BreakPoint getBreakPoint()
	{
		return breakpoint;
	}

	public void setBreakPoint(BreakPoint breakpoint)
	{
		Assertion.pre(halter.isGlobalHalted(),MODULENAME,"Changing breakpoint while running");
		this.breakpoint = breakpoint;
	}
	
	public void event(int eventType,DebuggableFilter currentFilter,DebuggableMessageList beforeMessage ,DebuggableMessageList afterMessage, ArrayList filters, Dictionary context)
	{
		halter.halting();
		Debug.out(Debug.MODE_DEBUG,"FLIRT(RuntimeStateHandler)","Having event");
		this.source= source;
		this.target= target;
		this.filters = filters;
		this.context = context;
		breakpoint.event(eventType,this,currentFilter,beforeMessage,afterMessage,filters,context);
	}

	public boolean isTreadHalted()
	{
		return halter.isTreadHalted();
	}

	public void threadResume()
	{
		halter.threadResume();
	}

	public void threadSuspend()
	{
		halter.threadSuspend();
	}

	public String getStackTrace()
	{
		return halter.getStackTrace();
	}
}
