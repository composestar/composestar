package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;

import Composestar.RuntimeCore.Utils.*;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.*;
import java.util.*;
/**
 * Summary description for StateHandler.
 */
public class StateHandler {
	private final static String MODULENAME= "CODER(StateHandler)";

	private Halter halter;
	private ChildThread thisThread;
	private BreakPoint breakpoint;

	public StateHandler(BreakPoint breakpoint, Halter halter)
	{
		this(ThreadPool.getCurrentChildTread(),breakpoint, halter);
	}

	public void cleanup()
	{
		thisThread = ThreadPool.getCurrentChildTread();
		halter.setThread(thisThread);
	}

	public StateHandler(ChildThread thread, BreakPoint breakpoint, Halter halter)
	{
		this.breakpoint = breakpoint;
		this.halter = halter;
		this.thisThread = thread;
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
	
	public void event(int eventType,FilterRuntime currentFilter,MessageList beforeMessage ,MessageList afterMessage, ArrayList filters, Dictionary context)
	{
		halter.halting();
		Debug.out(Debug.MODE_DEBUG,"FLIRT(RuntimeStateHandler)","Having event");
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

	public EntryPoint getEntryPoint()
	{
		return thisThread.getEntryPoint();
	}
}
