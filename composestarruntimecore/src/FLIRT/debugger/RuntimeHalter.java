package Composestar.RuntimeCore.FLIRT.Debugger;

import Composestar.RuntimeCore.CODER.*;
import Composestar.Utils.Debug;

/**
 * Summary description for Halter.
 */
public class RuntimeHalter implements Halter {
    //ALL
    private static boolean globalHalt = false;
	//Thread specific
	private boolean threadHalt = false;

    public void halting() 
	{
        while (globalHalt || threadHalt) {
            try {
                Thread.yield();
            } catch (Exception e) {
            }
        }
    }

    public boolean isGlobalHalted() {
        return globalHalt;
    }

    public void globalResume() {
        Debug.out(Debug.MODE_DEBUG, "FLIRT(RuntimeHalter)", "Resuming the runtime");
        globalHalt = false;
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
    }

    public void threadSuspend() {
        Debug.out(Debug.MODE_DEBUG, "FLIRT(RuntimeHalter)", "Suspending Thread");
        threadHalt = true;
    }
}
