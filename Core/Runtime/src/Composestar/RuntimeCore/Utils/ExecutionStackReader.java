package Composestar.RuntimeCore.Utils;
/*
 * Summary description for ExecutionStackReader.
 */
public abstract class ExecutionStackReader
{
	protected static ExecutionStackReader instance = null;
	
	public static ExecutionStackReader getInstance()
	{
		if(instance == null)
		{
			Debug.out(Debug.MODE_ERROR,"ExecutionStackReader","ExecutionStackReader instance called without platform instanciation");
		}
		return instance;
	}
	
	public abstract EntryPoint parseStack(String stack);	
}