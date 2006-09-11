package Composestar.RuntimeCore.FLIRT.JoinPoint;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueFilterEvaluationAction;
import Composestar.RuntimeCore.FLIRT.Actions.ResetFilterEvaluationAction;
import Composestar.RuntimeCore.FLIRT.Filters.FilterModule;
import Composestar.RuntimeCore.FLIRT.Message.*;

public class JoinPoint {
	protected FilterModule[] _modules;
	
	public JoinPoint(FilterModule[] filterModules){
		_modules = filterModules;
	}
	
	public ComposeStarAction getAction(Message message){
		ComposeStarAction action = ContinueFilterEvaluationAction.NOP;
		int index = 0;
		while(action instanceof ContinueFilterEvaluationAction && index < _modules.length)
		{
			action = _modules[index].getAction(message);
			action.execute(message);
		}
		return action;
	}
	
	public static void evaluateMessage(Message message){
		JoinPoint joinPoint = JoinPointPool.getJoinPoint(message);
		ComposeStarAction action = joinPoint.getAction(message);
		while(!(action instanceof ResetFilterEvaluationAction)) {
			JoinPointPool.getJoinPoint(message);
			action = joinPoint.getAction(message);
		}
	}
}
