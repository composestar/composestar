package Composestar.RuntimeCore.FLIRT.Reflection;

import Composestar.RuntimeCore.Utils.ChildThread;

public class JoinPointInfoProxy
{
	public static void updateJoinPoint(JoinPoint jp)
	{ 
		JoinPointInfo.updateJoinPoint(jp);
	}
	
	public static void updateJoinPoint(ChildThread thread, JoinPoint jp)
	{ 
		JoinPointInfo.updateJoinPoint(thread, jp);
	}
}
