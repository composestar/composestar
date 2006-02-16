package Composestar.RuntimeCore.CODER;

/**
 * Summary description for Halter.
 */
public interface Halter {
    public void halting();

    public boolean isGlobalHalted();

    public void globalResume();

    public void globalSuspend();

    public boolean isTreadHalted();

    public void threadResume();

    public void threadSuspend();
}
