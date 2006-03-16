package Composestar.RuntimeCore.FLIRT.Debugger;

import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.Utils.*;

import java.io.*;
import java.util.*;

/**
 * Summary description for Halter.
 */
public class RuntimeHalter implements Halter {
    //ALL
    private static boolean globalHalt = false;

	ChildThread thread;

	public RuntimeHalter()
	{
	}

	public void setThread(ChildThread thread)
	{
		this.thread= thread;
	}

    public void halting() 
	{
        while (globalHalt) {
            try {
               wait();
            } catch (Exception e) {
            }
        }
    }

	public synchronized void reanimate()
	{
		this.notify();
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
        return thread.isSuspended();
    }

    public void threadResume() {
        Debug.out(Debug.MODE_DEBUG, "FLIRT(RuntimeHalter)", "Resuming Thread");
        thread.resumeThread();
		reanimate();
    }

    public void threadSuspend() {
        Debug.out(Debug.MODE_DEBUG, "FLIRT(RuntimeHalter)", "Suspending Thread");
        thread.suspendThread();
    }
}
