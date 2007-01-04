package Composestar.RuntimeJava.Utils;

import java.util.ArrayList;
import Composestar.RuntimeCore.Utils.*;

/**
 * Summary description for JavaThreadPool.
 * Java doesn't require manual thread pooling
 */
public class JavaThreadPool extends ThreadPool
{
	public JavaThreadPool()
	{
		instance = this;
	}


	protected void add(ChildThread returned)
	{
	}

	protected ChildThread get()
	{
		return new JavaChildThread();
	}

}
