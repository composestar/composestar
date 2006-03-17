package Composestar.RuntimeDotNET.Utils;

import System.Threading.Thread;
import System.Diagnostics.*;
import Composestar.RuntimeCore.Utils.*;
import Composestar.RuntimeDotNET.Interface.*;
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

	public EntryPoint parseStack(String stackTrace)
	{
		StringTokenizer tokenizer = new StringTokenizer(stackTrace);
		String temp = "";
		while(tokenizer.hasMoreTokens())
		{
			temp = tokenizer.nextToken();
			System.out.println(temp);
			while(!"at".equalsIgnoreCase(temp))
			{
				if(!tokenizer.hasMoreTokens()) break;
				temp = tokenizer.nextToken();
				System.out.println(temp);
			}
			if(!tokenizer.hasMoreTokens()) continue;
			temp = tokenizer.nextToken();
			if(temp.indexOf("System.") == -1 && temp.indexOf("com.ms.vjsharp") == -1 && temp.indexOf("Composestar.") == -1)
			{
				if(temp.indexOf("..ctor") != -1 || temp.indexOf(".<clinit>") != -1)
				{
					while(!"at".equalsIgnoreCase(temp))
					{
						if(!tokenizer.hasMoreTokens()) break;
						temp = tokenizer.nextToken();
					}
					if(!tokenizer.hasMoreTokens()) continue;
					temp = tokenizer.nextToken();
				}
				if(temp.endsWith(":line"))
				{
					return new EntryPoint(temp,Integer.parseInt(tokenizer.nextToken()));
				}
				return new EntryPoint(temp);
			}
		}
		return new EntryPoint(stackTrace,0);
	}
}
