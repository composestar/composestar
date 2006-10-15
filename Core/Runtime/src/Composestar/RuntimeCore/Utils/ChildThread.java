package Composestar.RuntimeCore.Utils;

/**
 * Summary description for Subthread.
 */
public interface ChildThread
{
	public ChildThread createNew();
	public void setRunnable(ChildRunnable runnable);
	public void start();
	public ChildThread getCurrentChildThread();

	public void suspendThread();
	public void resumeThread();
	public boolean isSuspended();

	public EntryPoint getEntryPoint();
}