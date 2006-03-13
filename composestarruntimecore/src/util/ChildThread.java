package Composestar.RuntimeCore.Utils;

import java.util.*;
/**
 * Summary description for Subthread.
 */
public interface ChildThread
{
	public ChildThread createNew();
	public void setRunnable(ChildRunnable runnable);
	public void start();
	public Thread getThread();

}