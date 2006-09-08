package Composestar.RuntimeCore.FLIRT.Message;

import java.util.*;


/*
 * MessageList is the first message.
 * so if a filter doesn't handle a MessageList it get's processed like a normal message
 */
public class MessageList extends Message {
	public static final int SET_ALL_POLICY = 0;
	public static final int SET_FIRST_POLICY = 1;
	
	protected int _setPolicy = SET_ALL_POLICY;
	protected ArrayList _otherMessages = new ArrayList();
	
	public MessageList(Object sender, Object target, String selector, Object[] arguments, int messageKind){
		super(sender,target, selector, arguments, messageKind);
	}
	
	public void addMessage(Message message){
		if(message instanceof MessageList){
			this._otherMessages.addAll(((MessageList)message)._otherMessages);
		}
		else
		{
			this._otherMessages.add(message);	
		}
	}
	
	public int getMessagesSize(){
		return _otherMessages.size() + 1;
	}
	
	public void setTarget(Object target){
		super.setTarget(target);
		if(_setPolicy == SET_ALL_POLICY){
			Iterator iterator = _otherMessages.iterator();
			while(iterator.hasNext()){
				((Message)iterator.next()).setTarget(target);
			}
		}
	}
	
	public void setSender(Object sender){
		super.setSender(sender);
		if(_setPolicy == SET_ALL_POLICY){
			Iterator iterator = _otherMessages.iterator();
			while(iterator.hasNext()){
				((Message)iterator.next()).setSender(sender);
			}
		}
	}
	
	public void setSelector(String selector){
		super.setSelector(selector);
		if(_setPolicy == SET_ALL_POLICY){
			Iterator iterator = _otherMessages.iterator();
			while(iterator.hasNext()){
				((Message)iterator.next()).setSelector(selector);
			}
		}
	}
	
	public void setArguments(Object[] arguments){
		super.setArguments(arguments);
		if(_setPolicy == SET_ALL_POLICY){
			Iterator iterator = _otherMessages.iterator();
			while(iterator.hasNext()){
				((Message)iterator.next()).setArguments(arguments);
			}
		}
	}
	
	public void setMessageKind(int kind){
		super.setMessageKind(kind);
		if(_setPolicy == SET_ALL_POLICY){
			Iterator iterator = _otherMessages.iterator();
			while(iterator.hasNext()){
				((Message)iterator.next()).setMessageKind(kind);
			}
		}
	}

	/*
	 * Sets if setting the message changes the first message or them all
	 */
	public void setSetPolicy(int setPolicy)
	{
		_setPolicy = setPolicy;
	}

	/*
	 * Returns the setting policy
	 */
	public int getSetPolicy(){
		return _setPolicy;
	}

}
