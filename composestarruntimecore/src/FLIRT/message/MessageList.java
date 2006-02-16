package Composestar.RuntimeCore.FLIRT.Message;

import Composestar.RuntimeCore.FLIRT.Actions.SendAction;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchAction;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchToInnerAction;
import Composestar.RuntimeCore.Utils.ResponseBuffer;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessageList;

import java.util.*;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: MessageList.java,v 1.3 2006/02/15 11:46:23 composer Exp $
 * 
 * Models the Message as it is being Filtered
 * Keeps the name and arguments of the message. It also keeps some of the
 * pseudo variables necessary during filtering (for example internals and
 * externals). Finally it is responsible for producing a Reified message of
 * itself for the ACT's in the meta filter.
 */
public class MessageList implements DebuggableMessageList
{
    
	/**
	 *  Some stuff to indicate the direction of the message
	 **/
	public static int OUTGOING = 0;
	public static int INCOMING = 1;
	
	//public int direction;

	//public int getDirection()
	//{
	//	return this.direction;
	//}
	//public void setDirection(int direction)
	//{
	//	this.direction = direction;
	//}

	/**
	 *  This buffer is used to lock execution of the MessageHandlingFacility 
	 *  until a return value or an executable action is written to the same buffer
	 * 
	 */
	//ResponseBuffer responseBuffer;

	/**
	 * Original Sender of the message -as os this version this is not used
	 * due to constraints of the message interception layer
	 */
	//private Object sender;
    
	/**
	 * Object to whom the message is directed
	 */
	//private Object target;
    
	/**
	 * Inner
	 */
	//private Object inner;
	//private Object server;
    
	/**
	 * Name of the message
	 */
	//private String selector;
    
	/**
	 * Arguments of the message
	 */
	//private Object args[];

	/**
	 * Internals of the current FilterModule
	 */
	//private Dictionary internals = new Hashtable();
    
	/**
	 * Externals of the current FilterModule
	 */
	//private Dictionary externals = new Hashtable();
	//private Dictionary filterParams = new Hashtable();

	/*
	private int STATE = 0;
	public static final int MESSAGE_NONSTATIC_NONSTATIC_VOID   = 1; // A message from non static to a non static method
	public static final int MESSAGE_NONSTATIC_NONSTATIC_RETURN = 2; // A message from non static to a non static method with return
	
	public static final int MESSAGE_STATIC_NONSTATIC_VOID      = 3; // A message from static to a non static method
	public static final int MESSAGE_STATIC_NONSTATIC_RETURN    = 4; // A message from static to a non static method with return
	
	public static final int MESSAGE_NONSTATIC_STATIC_VOID      = 5; // A message from non static to a static method
	public static final int MESSAGE_NONSTATIC_STATIC_RETURN	   = 6; // A message from non static to a static method with return
	
	public static final int MESSAGE_STATIC_STATIC_VOID         = 7; // A message from static to a static method
	public static final int MESSAGE_STATIC_STATIC_RETURN       = 8; // A message from static to a static method with return

	public static final int MESSAGE_CONSTRUCTOR				   = 9; // A construction message
	*/
  
	//private Message getOrgMessage();
	private int orgMessagePointer;
	private LinkedList messages;

	public MessageList(Message m)
	{
		Message newMessage = new Message( m );
		
		messages = new LinkedList();
		messages.add( newMessage );
		orgMessagePointer = messages.size() - 1;
		
		/* testing...
		if( m.getSelector().equals( "getLevelData" ) ) 
		{
			Message x = new Message( "sayHello" );
			x.STATE = x.MESSAGE_NONSTATIC_NONSTATIC_VOID;
			x.setTarget( m.getInternals().get( "hello" ) );
			messages.add( x );
		}
		*/
	}
    
	public MessageList(MessageList ml)
	{
		//getOrgMessage() = new Message( ml.getOrgMessage() );
		orgMessagePointer = ml.getOrgMessagePointer();
		//this.STATE = ml.STATE;

		messages = new LinkedList();
		LinkedList othermessages = ml.getMessages();
		for( int i = 0; i < othermessages.size(); i++ ) 
		{
			messages.add( new Message( (Message) othermessages.get( i ) ) );
		}
	}
    
	/**
	 * Reifies this message.
	 * It produces a version of this message that is fit for manipulation
	 * inside a ACT.
	 * @return Reified version of this message
	 * @roseuid 3F36532702FC
	 */
	public ReifiedMessage reify() 
	{
		return new ReifiedMessage(this);     
	}
    
	/**
	 * Returns the object bound to inner
	 * @return java.lang.Object
	 * @roseuid 3F36532702FD
	 */
	public Object getInner() 
	{
		return getOrgMessage().getInner();     
	}
    
	/**
	 * Binds the object to the inner pseudo var.
	 * @param inner new inner
	 * @roseuid 3F3653270306
	 */
	public void setInner(Object inner) 
	{
		getOrgMessage().setInner( inner );     
	}
    
	/**
	 * Returns the selector of this message
	 * @return current selector
	 * @roseuid 3F3653270310
	 */
	public String getSelector() 
	{
		return getOrgMessage().getSelector();     
	}
    
	/**
	 * Sets  the selector of this message.
	 * @param new selector.
	 * @param s
	 * @roseuid 3F3653270311
	 */
	public void setSelector(String s) 
	{
		getOrgMessage().setSelector( s );
	}
    
	/**
	 * Returns the arguments of this message
	 * @return an array of objects containing the arguments
	 * @roseuid 3F365327031A
	 */
	public Object[] getArguments() 
	{
		return getOrgMessage().getArguments();     
	}

	public void setArguments(Object[] args) 
	{
		getOrgMessage().setArguments( args );
	}
    
	/**
	 * Sets a new filter parameter
	 * @param messageElement the name of the parameter
	 * @param identifier the value of the parameter.
	 * @roseuid 3F365327031B
	 */
	public void addFilterParameter(String messageElement, String identifier) 
	{
		getOrgMessage().addFilterParameter( messageElement, identifier );
	}
    
	/**
	 * Returns the value associated with the message element.
	 * @param messageElement key
	 * @return the identifier associated with it
	 * @roseuid 3F3653270326
	 */
	public String getFilterParameter(String messageElement) 
	{
		return (String) getOrgMessage().getFilterParameter( messageElement );
	}

	public Dictionary getFilterParameters()
	{
		return getOrgMessage().getFilterParameters();
	}
    
	/**
	 * Returns an internal by its name.
	 * @param name name of the internal
	 * @return the object bound to that name.
	 * @roseuid 3F365327032F
	 */
	public Object getInternal(String name) 
	{
		return getOrgMessage().getInternal(name);     
	}
    
	/**
	 * Returns a dictionary containing all the internals.
	 * @return all the internals
	 * @roseuid 3F3653270338
	 */
	public Dictionary getInternals() 
	{
		return getOrgMessage().getInternals();     
	}
    
	/**
	 * Sets (replaces) the internals of the message
	 * @param internals new set of internals
	 * @roseuid 3F3653270339
	 */
	public void setInternals(Dictionary internals) 
	{
		getOrgMessage().setInternals( internals );
	}
    
	/**
	 * Returns an external by its name
	 * @param name name of the desired external
	 * @return the object bound to the name.
	 * @roseuid 3F3653270343
	 */
	public Object getExternal(String name) 
	{
		return getOrgMessage().getExternal( name );
	}
    
	/**
	 * Returns a dictionary containing all the externals
	 * @return the current externals
	 * @roseuid 3F365327034D
	 */
	public Dictionary getExternals() 
	{
		return getOrgMessage().getExternals();
	}
    
	/**
	 * Replaces all the externals.
	 * @param externals new set of externals.
	 * @param extenals
	 * @roseuid 3F3653270356
	 */
	public void setExternals(Dictionary externals) 
	{
		getOrgMessage().setExternals( externals );
	}
    
	/**
	 * @return java.lang.Object
	 * @roseuid 411B57170266
	 */
	public Object getSender() 
	{
		return getOrgMessage().getSender();
	}
    
	/**
	 * @param obj
	 * @roseuid 411B571E01BC
	 */
	public void setSender(Object obj) 
	{
		getOrgMessage().setSender( obj );
	}

	public void setServer(Object server)
	{
		getOrgMessage().setServer( server );
	}

	public Object getServer()
	{
		return getOrgMessage().getServer();
	}

	public void setTarget(Object target)
	{
		getOrgMessage().setTarget( target );
	}

	public Object getTarget()
	{
		return getOrgMessage().getTarget();
	}


	public ResponseBuffer getResponseBuffer()
	{
		return getOrgMessage().getResponseBuffer();
	}

	public void setResponse(Object o)
	{
		getOrgMessage().setResponse( o );
		
	}

	public Object getResponse()
	{
		return getOrgMessage().getResponse();
		/*
		Object response = this.getResponseBuffer().consume();

		if( response instanceof DispatchAction )
			response = ((DispatchAction)response).execute();
		else if( response instanceof DispatchToInnerAction )
			response = ((DispatchToInnerAction)response).execute();
		else if( response instanceof SendAction )
			response = ((SendAction)response).execute();

		return response;
		*/
	}

	public Message getOrgMessage() 
	{
		return (Message) messages.get( orgMessagePointer );
	}

	public int getOrgMessagePointer() 
	{
		return orgMessagePointer;
	}

	public LinkedList getMessages() 
	{
		return messages;
	}

	public int getDirection() 
	{
		return getOrgMessage().getDirection();
	}

	public void setDirection(int direction) 
	{
		getOrgMessage().setDirection( direction );
	}

	public Iterator getIterator() 
	{
		return messages.iterator();
	}
	
	public String toString() 
	{
		String ret = "MessageList containing "+messages.size()+" Messages:";
		for( int i = 0; i<messages.size(); i++ ) 
		{
			ret += "\n" + ((Message)messages.get(i)).toString();
		}
		return ret;
	}
}