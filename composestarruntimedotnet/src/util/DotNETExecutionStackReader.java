package Composestar.RuntimeDotNET.Utils;

import System.Diagnostics.*;
import Composestar.RuntimeCore.Utils.*;

/**
 * Summary description for DotNETExecutionStackReader.
 */
public class DotNETExecutionStackReader extends ExecutionStackReader
{
	public DotNETExecutionStackReader()
	{
		instance = this;
	}

	public String getTopParrentStack(ChildThread thread)
	{
		return new StackTrace(((DotNETChildThread) thread).getThisThread(),true).toString();
	}
	public EntryPoint getComposestarEntryPoint()
	{
		return null;
	}
}
