package Composestar.RuntimeCore.FLIRT.Filters;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Message.Message;

public abstract class Filter {
	protected ComposeStarAction _acceptAction;
	protected ComposeStarAction _rejectAction;
	
	public abstract boolean accept(Message message);
	
	public ComposeStarAction getAction(Message message){
		if(accept(message)){
			return _acceptAction;
		}
		else{
			return _rejectAction;
		}
	}
}
