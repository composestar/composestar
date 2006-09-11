package Composestar.RuntimeCore.FLIRT.Filters;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueFilterEvaluationAction;
import Composestar.RuntimeCore.FLIRT.Message.Message;

public class FilterModule{
	protected Filter[] _filterSet;
	
	public FilterModule(Filter[] filterSet){
		this._filterSet = filterSet;
	}
	
	public ComposeStarAction getAction(Message message){
		ComposeStarAction action = ContinueFilterEvaluationAction.NOP;
		int index = 0;
		while(action instanceof ContinueFilterEvaluationAction && index < _filterSet.length)
		{
			action = _filterSet[index].getAction(message);
			action.execute(message);
		}
		return action;
	}
}
