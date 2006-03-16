package Composestar.C.wrapper.utils;

public class Profiler {

	private static long start = 0;
	private static long split = 0;
	public static boolean high_per = true;
	
	public static void start()
	{
		start = System.currentTimeMillis();
	}
	
	public static void end()
	{
		int time = 0;
		if(high_per)
			time =(int)((System.currentTimeMillis() - start));
		else
			time =(int)((System.currentTimeMillis() - start)/1000);
		System.out.println("WeaveC done in "+time+" msecs...");
	}
	
	public static void printStartStatus(String msg)
	{
		split = System.currentTimeMillis();
		System.out.println(msg+"...");
	}
	
	public static void printEndStatus(String msg)
	{
		int time = 0;
		if(split != 0)
		{
			if(high_per)
				time =(int)((System.currentTimeMillis() - split));
			else
				time =(int)((System.currentTimeMillis() - split)/1000);
		}	
		else if(start !=0)
		{
			if(high_per)
				time =(int)((System.currentTimeMillis() - start));
			else
				time =(int)((System.currentTimeMillis() - start)/1000);
		}
		System.out.println(msg+" in "+time+" msecs...");
	}
}
