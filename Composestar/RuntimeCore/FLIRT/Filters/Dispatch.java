package Composestar.RuntimeCore.FLIRT.Filters;

import Composestar.RuntimeCore.FLIRT.Actions.ContinueFilterEvaluationAction;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchAction;
import Composestar.RuntimeCore.FLIRT.Message.Message;

public class Dispatch extends Filter {	
	
	public Dispatch(String target, String selector)
	{
		_acceptAction = new DispatchAction(target,selector);
		_rejectAction = ContinueFilterEvaluationAction.NOP;
	}
	
	public boolean accept(Message message) {
		// TODO Auto-generated method stub
		return false;
	}

}
