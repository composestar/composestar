package Composestar.RuntimeCore.FLIRT.Message;

import Composestar.RuntimeCore.Utils.Settings;

public abstract class MessagePool {
	protected static int _top = -1;
	protected static Message[] _pool = new Message[Settings.MAX_MESSAGEPOOL_SIZE];

	private static final boolean isEmpty()
	{
		return _top == -1;
	}
	
	public synchronized static void returnMessage(Message message)
	{
		_top++;
		if(_top == _pool.length){
			_top--; //Drop Message from pool
		}
		else{
			_pool[_top] = message;
		}
	}
	
	public synchronized static Message getMessage(Object sender, Object target, String selector, Object[] arguments, int kind){
		if(isEmpty())
		{
			return new Message(sender, target, selector, arguments, kind);
		}
		else
		{
			Message result = _pool[_top];
			result.setSender(sender);
			result.setTarget(target);
			result.setSelector(selector);
			result.setArguments(arguments);
			result.setMessageKind(kind);
			return result;
		}
	}
	
	public void returnMessage(){
		//Message are used to little to be recycled.
	}
}
