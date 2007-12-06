package Composestar.Core.INCRE;

public class INCRETimer
{
	public static final int TYPE_ALL = 0;

	public static final int TYPE_NORMAL = 1;

	public static final int TYPE_INCREMENTAL = 2;

	public static final int TYPE_OVERHEAD = 3;

	private String module;

	private String description;

	private int type;

	private long startTime;

	private long elapsedTime;

	public INCRETimer(String inModule, String inDescription, int inType)
	{
		module = inModule;
		description = inDescription;
		type = inType;
	}

	public void start()
	{
		startTime = System.nanoTime();
	}

	public void stop()
	{
		long stopTime = System.nanoTime();
		elapsedTime = stopTime - startTime;
	}

	/**
	 * @return elapsed time in miliseconds
	 */
	public long getElapsed()
	{
		return elapsedTime / 1000000;
	}

	public String getModule()
	{
		return module;
	}

	public String getDescription()
	{
		return description;
	}

	public int getType()
	{
		return type;
	}

	public String strType()
	{
		switch (type)
		{
			case 0:
				return "all";
			case 1:
				return "normal";
			case 2:
				return "incremental";
			default:
				return "overhead";
		}
	}
}
