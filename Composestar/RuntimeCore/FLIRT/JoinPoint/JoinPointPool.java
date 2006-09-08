package Composestar.RuntimeCore.FLIRT.JoinPoint;

import Composestar.RuntimeCore.FLIRT.Message.*;

public class JoinPointPool {
	
	public static JoinPoint getJoinPoint(Message message){
		return new JoinPoint();
	}
}