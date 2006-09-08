package Composestar.RuntimeCore.FLIRT.Message;

public class Message {
	/**
	 *  Direction of a message
	 **/
	public static final int OUTGOING = 0; //Outgoing is sender side
	public static final int INCOMING = 1; //Outgoing is receiver side
	
	protected int _direction = OUTGOING;
	
	protected Object _sender;
	protected Object _target;
	protected String _selector;
	protected Object[] _arguments;
	protected Object _response;
	
	protected int _messageKind = MESSAGE_UNKNOWN;
	
	public static final int MESSAGE_UNKNOWN 				   = 0; // Unknown TYPE 
	
	public static final int MESSAGE_NONSTATIC_NONSTATIC_VOID   = 1; // A message from non static to a non static method
	public static final int MESSAGE_NONSTATIC_NONSTATIC_RETURN = 2; // A message from non static to a non static method with return
	
	public static final int MESSAGE_STATIC_NONSTATIC_VOID      = 3; // A message from static to a non static method
	public static final int MESSAGE_STATIC_NONSTATIC_RETURN    = 4; // A message from static to a non static method with return
	
	public static final int MESSAGE_NONSTATIC_STATIC_VOID      = 5; // A message from non static to a static method
	public static final int MESSAGE_NONSTATIC_STATIC_RETURN	   = 6; // A message from non static to a static method with return
	
	public static final int MESSAGE_STATIC_STATIC_VOID         = 7; // A message from static to a static method
	public static final int MESSAGE_STATIC_STATIC_RETURN       = 8; // A message from static to a static method with return

	public static final int MESSAGE_CONSTRUCTOR				   = 9; // A construction message
	
	public String toString(){
		return (_direction == OUTGOING? "Outgoing" : "Incomming") + "Message, sender(" +this.getSender() + "), target(" + this.getTarget() + "::" + this.getSelector() + ')'; 
	}

	public Message(Object sender, Object target, String selector, Object[] arguments, int messageKind)
	{
		this._sender = sender;
		this._target = target;
		this._selector = selector;
		this._arguments = arguments; 
		this._messageKind = messageKind;
		
		this._direction = OUTGOING;
	}
	
	public Object[] getArguments() {
		return this._arguments;
	}

	public void setArguments(Object[] arguments) {
		this._arguments = arguments;
	}

	public int getDirection() {
		return this._direction;
	}

	public void setDirection(int direction) {
		this._direction = direction;
	}

	public String getSelector() {
		return this._selector;
	}

	public void setSelector(String selector) {
		this._selector = selector;
	}

	public Object getSender() {
		return this._sender;
	}

	public void setSender(Object sender) {
		this._sender = sender;
	}

	public Object getTarget() {
		return this._target;
	}

	public void setTarget(Object target) {
		this._target = target;
	}

	public int getMessageKind() {
		return this._messageKind;
	}

	public void setMessageKind(int kind) {
		this._messageKind = kind;
	}
	
	public Object getResponse()
	{
		return _response;
	}
	
	public void setResponse(Object response)
	{
		this._response = response;
	}
	
	public Message copy(){
		return MessagePool.getMessage(_sender, _target, _selector, _arguments, _messageKind);
	}
	
	public void returnMessage(){
		MessagePool.returnMessage(this);
	}
}
