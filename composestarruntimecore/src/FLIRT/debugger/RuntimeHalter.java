package Composestar.RuntimeCore.FLIRT.Debugger;

import Composestar.RuntimeCore.CODER.*;
import Composestar.Utils.Debug;

import java.io.*;
import java.util.*;

/**
 * Summary description for Halter.
 */
public class RuntimeHalter implements Halter {
    //ALL
    private static boolean globalHalt = false;
	//Thread specific
	private boolean threadHalt = false;

	public RuntimeHalter()
	{
		stacktrace = produceStackTrace();
	}

	public void setThread(Thread thread)
	{
		threadHalt = false;
	}

    public void halting() 
	{
        while (globalHalt || threadHalt) {
            try {
				if(dumpStackSpace)
				{
					stacktrace = produceStackTrace();
					dumpStackSpace = false;
				}
               wait();
            } catch (Exception e) {
            }
        }
    }

	public synchronized void reanimate()
	{
		notify();
	}

	private boolean dumpStackSpace = false;

	private String stacktrace = null;

	public String getStackTrace()
	{
		dumpStackSpace = true;
		while(stacktrace == null)
		{
			try
			{
				Thread.sleep(0);
			}
			catch(Exception e)
			{
			}
		}
		return stacktrace;
	}

	private String produceStackTrace()
	{
		//Lets do something very dirty
		try
		{
			throw new Throwable(); //need the stack :P
		}
		catch(Throwable t)
		{
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			t = t.fillInStackTrace();
			t.printStackTrace(writer);
			writer.flush();
			String stack = sw.toString();
			return stack;
		}
	}

    public boolean isGlobalHalted() {
        return globalHalt;
    }

    public void globalResume() {
        Debug.out(Debug.MODE_DEBUG, "FLIRT(RuntimeHalter)", "Resuming the runtime");
        globalHalt = false;
		reanimate();
    }

    public void globalSuspend() {
        Debug.out(Debug.MODE_DEBUG, "FLIRT(RuntimeHalter)", "Suspending the runtime");
        globalHalt = true;
    }

    public boolean isTreadHalted() {
        return threadHalt;
    }

    public void threadResume() {
        Debug.out(Debug.MODE_DEBUG, "FLIRT(RuntimeHalter)", "Resuming Thread");
        threadHalt = false;
		reanimate();
    }

    public void threadSuspend() {
        Debug.out(Debug.MODE_DEBUG, "FLIRT(RuntimeHalter)", "Suspending Thread");
        threadHalt = true;
    }
}
