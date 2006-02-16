package Composestar.RuntimeCore.FLIRT.Message;

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
