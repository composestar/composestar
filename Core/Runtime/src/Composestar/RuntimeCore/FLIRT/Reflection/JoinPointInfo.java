package Composestar.RuntimeCore.FLIRT.Reflection;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.WeakHashMap;

public class JoinPointInfo
{
	private static WeakHashMap joinpointByThread = new WeakHashMap();

	public static JoinPoint getJoinPointInfo()
	{
		return (JoinPoint) joinpointByThread.get(Thread.currentThread());
	}

	protected static void updateJoinPoint(JoinPoint jp)
	{
		joinpointByThread.put(Thread.currentThread(), jp);
	}

	protected static void updateJoinPoint(Thread thread, JoinPoint jp)
	{
		joinpointByThread.put(thread, jp);
	}

	/**
	 * Returns the attributes of the current join point.
	 * 
	 * @return java.lang.Object[]
	 */
	public static ArrayList getAttributes()
	{
		return JoinPointInfo.getJoinPointInfo().getAttributeList();
	}

	public static Object getAttribute(String name)
	{
		return JoinPointInfo.getJoinPointInfo().getAttribute(name);
	}

	public static Object getAttributeMap()
	{
		return JoinPointInfo.getJoinPointInfo().getAttributeMap();
	}

	public static Enumeration getInternals()
	{
		return JoinPointInfo.getJoinPointInfo().getInternals();
	}

	public static Object getInternal(String name)
	{
		return JoinPointInfo.getJoinPointInfo().getInternal(name);
	}

	public static Enumeration getExternals()
	{
		return JoinPointInfo.getJoinPointInfo().getExternals();
	}

	public static Object getExternal(String name)
	{
		return JoinPointInfo.getJoinPointInfo().getExternal(name);
	}

	public static Object getInstance()
	{
		return JoinPointInfo.getJoinPointInfo().getInstance();
	}

}
