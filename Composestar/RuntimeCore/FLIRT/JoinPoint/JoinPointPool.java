package Composestar.RuntimeCore.FLIRT.JoinPoint;

import java.util.HashMap;

import Composestar.RuntimeCore.FLIRT.Filters.FilterModule;
import Composestar.RuntimeCore.FLIRT.Message.*;

public class JoinPointPool {
	protected static HashMap _map = new HashMap(); 
	
	public synchronized static JoinPoint getJoinPoint(Message message){
		JoinPoint result = (JoinPoint)_map.get(message.getTarget());
		if(result == null){
			FilterModule[] modules = {}; //TODO getFilterModules
			result = new JoinPoint(modules);
			_map.put(message.getTarget(),result);
		}
		return result;
	}
}