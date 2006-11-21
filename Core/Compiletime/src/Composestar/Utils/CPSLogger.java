package Composestar.Utils;

public class CPSLogger
{
	private final String name;
	
	protected CPSLogger(String name)
	{
		this.name = name;
	}
	
	public void stackTrace(Throwable t)
	{
		Debug.out(Debug.MODE_DEBUG, name, Debug.stackTrace(t));
	}
	
	public void error(String msg)
	{
		Debug.out(Debug.MODE_ERROR, name, msg);
	}

	public void warn(String msg)
	{
		Debug.out(Debug.MODE_WARNING, name, msg);
	}

	public void info(String msg)
	{
		Debug.out(Debug.MODE_INFORMATION, name, msg);
	}

	public void debug(String msg)
	{
		Debug.out(Debug.MODE_DEBUG, name, msg);
	}

	public static CPSLogger getCPSLogger(String name)
	{
		return new CPSLogger(name);
	}
}
