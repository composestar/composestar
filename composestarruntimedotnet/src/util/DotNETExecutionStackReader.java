package Composestar.RuntimeDotNET.Utils;

import System.Threading.Thread;
import System.Diagnostics.*;
import Composestar.RuntimeCore.Utils.*;
import java.util.*;

/**
 * Summary description for DotNETExecutionStackReader.
 */
public class DotNETExecutionStackReader extends ExecutionStackReader
{
	public DotNETExecutionStackReader()
	{
		instance = this;
	}

	public EntryPoint getComposestarEntryPoint(ChildThread thread)
	{
		System.Threading.Thread parent = ((DotNETChildThread) thread).getParentThread();
		parent.Suspend();
		StackTrace trace = new StackTrace(parent,true);
		parent.Resume();
		String stacktrace = trace.ToString();
		return parseStackTrace(stacktrace);
	}

	private EntryPoint parseStackTrace(String stackTrace)
	{
		StringTokenizer tokenizer = new StringTokenizer(stackTrace);
		String temp = "";
		while(tokenizer.hasMoreTokens())
		{
			temp = tokenizer.nextToken();
			while(!"at".equalsIgnoreCase(temp))
			{
				if(!tokenizer.hasMoreTokens()) break;
				temp = tokenizer.nextToken();
			}
			if(!tokenizer.hasMoreTokens()) continue;
			temp = tokenizer.nextToken();
			if(temp.indexOf("System.") == -1 && temp.indexOf("com.ms.vjsharp") == -1 && temp.indexOf("Composestar.") == -1)
			{
				if(temp.indexOf("..ctor") < 0)
				{
					while(!"at".equalsIgnoreCase(temp))
					{
						if(!tokenizer.hasMoreTokens()) break;
						temp = tokenizer.nextToken();
					}
					if(!tokenizer.hasMoreTokens()) continue;
					temp = tokenizer.nextToken();
				}
				return new EntryPoint(temp  + tokenizer.nextToken(),0);
			}
		}
		return new EntryPoint("Main",0);
	}
}
