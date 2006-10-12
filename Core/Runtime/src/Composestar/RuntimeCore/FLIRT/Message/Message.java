package Composestar.RuntimeCore.FLIRT.Message;

import Composestar.RuntimeCore.FLIRT.Actions.SendAction;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchAction;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchToInnerAction;
import Composestar.RuntimeCore.FLIRT.Actions.ErrorAction;
import Composestar.RuntimeCore.Utils.ResponseBuffer;

import java.util.*;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Models the Message as it is being Filtered
 * Keeps the name and arguments of the message. It also keeps some of the
 * pseudo variables necessary during filtering (for example internals and
 * externals). Finally it is responsible for producing a Reified message of
 * itself for the ACT's in the meta filter.
 */
public class Message
{
	/**
	 * If messageList is not null, some of the properties of the message are
	 * delegated to the list.
	 */
	private MessageList messageList = null;
    
	public void setMessageList (MessageList ml ) 
	{
		messageList = ml;
	}

	public MessageList getMessageList() 
	{
		return messageList;
	}

	/**
	 *  Some stuff to indicate the direction of the message
	 **/
	public static final int OUTGOING = 0;
	public static final int INCOMING = 1;

	private int direction;
	
	public int getDirection()
	{
		if( messageList != null )
			return messageList.getDirection();
		else
			return this.direction;
	}
	public void setDirection(int direction)
	{
		if( messageList != null )
			messageList.setDirection( direction );
		else
			this.direction = direction;
	}

	/**
	 *  This buffer is used to lock execution of the MessageHandlingFacility 
	 *  until a return value or an executable action is written to the same buffer
	 * 
	 */
	ResponseBuffer responseBuffer;

    /**
     * Original Sender of the message -as os this version this is not used
     * due to constraints of the message interception layer
     */
    private Object sender;
    
    /**
     * Object to whom the message is directed
     */
    private Object target;
    
    /**
     * Inner
     */
    private Object inner;
    private Object server;
    
    /**
     * Name of the message
     */
    private String selector;
    
    /**
     * Arguments of the message
     */
    private Object args[];

	/**
     * Internals of the current FilterModule
     */
    private Dictionary internals = new Hashtable();
    
    /**
     * Externals of the current FilterModule
     */
    private Dictionary externals = new Hashtable();
    private Dictionary filterParams = new Hashtable();

	public int STATE = 0;
	public static final int MESSAGE_NONSTATIC_NONSTATIC_VOID   = 1; // A message from non static to a non static method
	public static final int MESSAGE_NONSTATIC_NONSTATIC_RETURN = 2; // A message from non static to a non static method with return
	
	public static final int MESSAGE_STATIC_NONSTATIC_VOID      = 3; // A message from static to a non static method
	public static final int MESSAGE_STATIC_NONSTATIC_RETURN    = 4; // A message from static to a non static method with return
	
	public static final int MESSAGE_NONSTATIC_STATIC_VOID      = 5; // A message from non static to a static method
	public static final int MESSAGE_NONSTATIC_STATIC_RETURN	   = 6; // A message from non static to a static method with return
	
	public static final int MESSAGE_STATIC_STATIC_VOID         = 7; // A message from static to a static method
	public static final int MESSAGE_STATIC_STATIC_RETURN       = 8; // A message from static to a static method with return

	public static final int MESSAGE_CONSTRUCTOR				   = 9; // A construction message
	    
    /**
     * Construts a message with arguments
     * @param selector selector of the message
     * @param args arguments of the message
     * @roseuid 3F36532702F2
     */
    public Message(String selector, Object[] args) 
	{
        this(selector);
		this.args = args;
    }
    
    /**
     * Constructs a Message from its selector
     * It is supposed that no arguments are set.
     * @param selector selector of the message
     * @roseuid 3F36532702E9
     */
    public Message(String selector) {
		this.responseBuffer = new ResponseBuffer();
		this.selector     = selector;
        args              = null;
        filterParams      = new Hashtable();
		this.direction    = OUTGOING;
    }

	public Message(Message m)
	{
		// clear the messagelist to get the message's own properties
		MessageList ml = m.getMessageList();
		m.setMessageList( null );

		this.sender = m.getSender();
		this.server = m.getServer();
		this.inner = m.getInner();
		this.selector = m.getSelector();
		this.target = m.getTarget();
		this.args = m.getArguments();
		this.internals = m.getInternals();
		this.externals = m.getExternals();
		this.filterParams = m.getFilterParameters();
	    this.STATE = m.STATE;
		this.responseBuffer = m.getResponseBuffer();
		this.direction = m.getDirection();
		this.messageList = ml;

		// reset the messagelist
		m.setMessageList( ml );
	}
    
    /**
     * Reifies this message.
     * It produces a version of this message that is fit for manipulation
     * inside a ACT.
     * @return Reified version of this message
     * @roseuid 3F36532702FC
     */
    public ReifiedMessage reify() {
        return new ReifiedMessage(this);     
    }
    
    /**
     * Returns the object bound to inner
     * @return java.lang.Object
     * @roseuid 3F36532702FD
     */
    public Object getInner() {
		if( messageList != null )
			return messageList.getInner();
		else
			return inner;     
    }
    
    /**
     * Binds the object to the inner pseudo var.
     * @param inner new inner
     * @roseuid 3F3653270306
     */
    public void setInner(Object inner) {
		if( messageList != null )
			messageList.setInner( inner );
		else
			this.inner = inner;     
    }
    
    /**
     * Returns the selector of this message
     * @return current selector
     * @roseuid 3F3653270310
     */
    public String getSelector() {
        return selector;     
    }
    
    /**
     * Sets  the selector of this message.
     * @param new selector.
     * @param s
     * @roseuid 3F3653270311
     */
    public void setSelector(String s) {
        selector = s;     
    }
    
    /**
     * Returns the arguments of this message
     * @return an array of objects containing the arguments
     * @roseuid 3F365327031A
     */
    public Object[] getArguments() {
		if( messageList != null )
			return messageList.getArguments();
		else
			return args;     
    }

	public void setArguments(Object[] args) 
	{
		if( messageList != null )
			messageList.setArguments( args );
		else
			this.args = args;
	}
    
    /**
     * Sets a new filter parameter
     * @param messageElement the name of the parameter
     * @param identifier the value of the parameter.
     * @roseuid 3F365327031B
     */
    public void addFilterParameter(String messageElement, String identifier) {
		if( messageList != null )
			messageList.getFilterParameters().put( messageElement, identifier );
		else
			filterParams.put(messageElement, identifier);     
    }
    
    /**
     * Returns the value associated with the message element.
     * @param messageElement key
     * @return the identifier associated with it
     * @roseuid 3F3653270326
     */
    public String getFilterParameter(String messageElement) {
		if( messageList != null )
			return messageList.getFilterParameter( messageElement );
		else
			return (String) filterParams.get(messageElement);     
    }

	public Dictionary getFilterParameters()
	{
		if( messageList != null )
			return messageList.getFilterParameters();
		else
			return this.filterParams;
	}
    
	public void setFilterParameters( Dictionary d )
	{
		if( messageList != null )
			messageList.setFilterParameters( d );
		else
			filterParams = d;
	}

	/**
     * Returns an internal by its name.
     * @param name name of the internal
     * @return the object bound to that name.
     * @roseuid 3F365327032F
     */
    public Object getInternal(String name) 
	{
		if( messageList != null )
			return messageList.getInternal( name );
		else
			return internals.get(name);     
    }
    
    /**
     * Returns a dictionary containing all the internals.
     * @return all the internals
     * @roseuid 3F3653270338
     */
    public Dictionary getInternals() {
		if( messageList != null )
			return messageList.getInternals();
		else
			return internals;     
    }
    
    /**
     * Sets (replaces) the internals of the message
     * @param internals new set of internals
     * @roseuid 3F3653270339
     */
    public void setInternals(Dictionary internals) {
		if( messageList != null )
			messageList.setInternals( internals );
		else
			this.internals = internals;     
    }
    
    /**
     * Returns an external by its name
     * @param name name of the desired external
     * @return the object bound to the name.
     * @roseuid 3F3653270343
     */
    public Object getExternal(String name) {
		if( messageList != null )
			return messageList.getExternal( name );
		else
			return externals.get(name);     
    }
    
    /**
     * Returns a dictionary containing all the externals
     * @return the current externals
     * @roseuid 3F365327034D
     */
    public Dictionary getExternals() {
		if( messageList != null )
			return messageList.getExternals();
		else
			return this.externals;     
    }
    
    /**
     * Replaces all the externals.
     * @param externals new set of externals.
     * @param extenals
     * @roseuid 3F3653270356
     */
    public void setExternals(Dictionary externals) {
		if( messageList != null )
			messageList.setExternals( externals );
		else
			this.externals = externals;
    }
    
    /**
     * @return java.lang.Object
     * @roseuid 411B57170266
     */
    public Object getSender() {
		if( messageList != null )
			return messageList.getSender();
		else
			return this.sender;
    }
    
    /**
     * @param obj
     * @roseuid 411B571E01BC
     */
    public void setSender(Object obj) {
		if( messageList != null )
			messageList.setSender( obj );
		else
			this.sender = obj;
    }

	public void setServer(Object server)
	{
		if( messageList != null )
			messageList.setServer( server );
		else
			this.server = server;
	}

	public Object getServer()
	{
		if( messageList != null )
			return messageList.getServer();
		else
			return this.server;
	}

	public void setTarget(Object target)
	{
		this.target = target;
	}

	public Object getTarget()
	{
		return this.target;
	}


	public ResponseBuffer getResponseBuffer()
	{
		return this.responseBuffer;
	}

	public void setResponse(Object o)
	{
		this.getResponseBuffer().produce(o);
		
	}

	public Object getResponse()
	{
		Object response = this.getResponseBuffer().consume();

		if( response instanceof DispatchAction )
			response = ((DispatchAction)response).execute();
		else if( response instanceof DispatchToInnerAction )
			response = ((DispatchToInnerAction)response).execute();
		else if( response instanceof SendAction )
			response = ((SendAction)response).execute();
		else if( response instanceof ErrorAction )
			response = ((ErrorAction)response).execute();

		return response;
	}

	public String toString()
	{
		return "Message,caller(" + this.getSender() + "),target(" + this.getTarget() + "::" + this.getSelector() + ')';
	}

	private boolean matched;
	public boolean isMatched() 
	{
		return matched;
	}

	public void setMatched( boolean m ) 
	{
		matched = m;
	}
}
