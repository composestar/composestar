package Composestar.RuntimeCore.FLIRT.Reflection;
import java.lang.Thread;

public class JoinPointInfoProxy
{
	public static void updateJoinPoint(JoinPoint jp)
	{
		JoinPointInfo.updateJoinPoint(jp);
	}

	public static void updateJoinPoint(Thread thread, JoinPoint jp)
	{
		JoinPointInfo.updateJoinPoint(thread, jp);
	}
}
