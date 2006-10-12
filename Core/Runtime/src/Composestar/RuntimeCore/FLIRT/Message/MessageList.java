package Composestar.RuntimeCore.FLIRT.Message;

import Composestar.RuntimeCore.FLIRT.Actions.SendAction;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchAction;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchToInnerAction;
import Composestar.RuntimeCore.Utils.ResponseBuffer;
import Composestar.RuntimeCore.Utils.Debug;

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
public class MessageList
{
    
	/**
	 *  Some stuff to indicate the direction of the message
	 **/
	public static int OUTGOING = 0;
	public static int INCOMING = 1;
	

	private int orgMessagePointer;
	private LinkedList messages;

	public MessageList(Message m)
	{
		messages = new LinkedList();
		messages.add( m );
		orgMessagePointer = messages.size() - 1;
		
		this.copyFromMessage( m );
		m.setMessageList( this );
	}
    
	public MessageList(MessageList ml)
	{
		copyFromMessageList( ml );
	}

	public void copyFromMessageList( MessageList ml ) 
	{
		orgMessagePointer = ml.getOrgMessagePointer();

		messages = new LinkedList();
		LinkedList othermessages = ml.getMessages();
		for( int i = 0; i < othermessages.size(); i++ ) 
		{
			Message newMessage = new Message( (Message) othermessages.get( i ) );
			newMessage.setMessageList( this );
			messages.add( newMessage );
		}

		this.setSender( ml.getSender() );
		this.setServer( ml.getServer() );
		this.setInner( ml.getInner() );
		this.setArguments( ml.getArguments() );
		this.setInternals( ml.getInternals() );
		this.setExternals( ml.getExternals() );
		this.setFilterParameters( ml.getFilterParameters() );
		this.setDirection( ml.getDirection() );
	}

	public void copyFromMessage( Message m ) 
	{
		// clear the messagelist to get the message's own properties
		MessageList ml = m.getMessageList();
		m.setMessageList( null );

		this.setSender( m.getSender() );
		this.setServer( m.getServer() );
		this.setInner( m.getInner() );
		this.setArguments( m.getArguments() );
		this.setInternals( m.getInternals() );
		this.setExternals( m.getExternals() );
		this.setFilterParameters( m.getFilterParameters() );
		this.setDirection( m.getDirection() );

		// reset the messagelist
		m.setMessageList( ml );
	}
    
	public void copyToMessage( Message m ) 
	{
		// clear the messagelist to get the message's own properties
		MessageList ml = m.getMessageList();
		m.setMessageList( null );

		m.setSender( this.getSender() );
		m.setServer( this.getServer() );
		m.setInner( this.getInner() );
		m.setArguments( this.getArguments() );
		m.setInternals( this.getInternals() );
		m.setExternals( this.getExternals() );
		m.setFilterParameters( this.getFilterParameters() );
		m.setDirection( this.getDirection() );

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
	public ReifiedMessage reify() 
	{
		return new ReifiedMessage(this);     
	}
    

	private Object inner;

	/**
	 * Returns the object bound to inner
	 * @return java.lang.Object
	 * @roseuid 3F36532702FD
	 */
	public Object getInner() 
	{
		return inner;     
	}
    
	/**
	 * Binds the object to the inner pseudo var.
	 * @param inner new inner
	 * @roseuid 3F3653270306
	 */
	public void setInner(Object inner) 
	{
		this.inner = inner;     
	}
    
	/**
	 * Returns the selector of this message
	 * @return current selector
	 * @roseuid 3F3653270310
	 */
	/*
	public String getSelector() 
	{
		return getOrgMessage().getSelector();     
	}
    */

	/**
	 * Sets  the selector of this message.
	 * @param new selector.
	 * @param s
	 * @roseuid 3F3653270311
	 */
	/*
	public void setSelector(String s) 
	{
		getOrgMessage().setSelector( s );
	}
    */

	/**
	 * Arguments of the message
	 */
	private Object args[];

	/**
	 * Returns the arguments of this message
	 * @return an array of objects containing the arguments
	 * @roseuid 3F365327031A
	 */
	public Object[] getArguments() 
	{
		return args;     
	}

	public void setArguments(Object[] args) 
	{
		this.args = args;
	}
    

	private Dictionary filterParams = new Hashtable();

	/**
	 * Sets a new filter parameter
	 * @param messageElement the name of the parameter
	 * @param identifier the value of the parameter.
	 * @roseuid 3F365327031B
	 */
	public void addFilterParameter(String messageElement, String identifier) 
	{
		filterParams.put( messageElement, identifier );
	}
    
	/**
	 * Returns the value associated with the message element.
	 * @param messageElement key
	 * @return the identifier associated with it
	 * @roseuid 3F3653270326
	 */
	public String getFilterParameter(String messageElement) 
	{
		return (String) filterParams.get(messageElement);
	}

	public Dictionary getFilterParameters()
	{
		return filterParams;
	}
    
	public void setFilterParameters( Dictionary d )
	{
		filterParams = d;
	}
    

	/**
	 * Internals of the current FilterModule
	 */
	private Dictionary internals = new Hashtable();

	/**
	 * Returns an internal by its name.
	 * @param name name of the internal
	 * @return the object bound to that name.
	 * @roseuid 3F365327032F
	 */
	public Object getInternal(String name) 
	{
		return internals.get(name);     
	}
    
	/**
	 * Returns a dictionary containing all the internals.
	 * @return all the internals
	 * @roseuid 3F3653270338
	 */
	public Dictionary getInternals() 
	{
		return internals;     
	}
    
	/**
	 * Sets (replaces) the internals of the message
	 * @param internals new set of internals
	 * @roseuid 3F3653270339
	 */
	public void setInternals(Dictionary internals) 
	{
		this.internals = internals;
	}
    

	/**
	 * Externals of the current FilterModule
	 */
	private Dictionary externals = new Hashtable();

	/**
	 * Returns an external by its name
	 * @param name name of the desired external
	 * @return the object bound to the name.
	 * @roseuid 3F3653270343
	 */
	public Object getExternal(String name) 
	{
		return externals.get( name );
	}
    
	/**
	 * Returns a dictionary containing all the externals
	 * @return the current externals
	 * @roseuid 3F365327034D
	 */
	public Dictionary getExternals() 
	{
		return externals;
	}
    
	/**
	 * Replaces all the externals.
	 * @param externals new set of externals.
	 * @roseuid 3F3653270356
	 */
	public void setExternals(Dictionary externals) 
	{
		this.externals = externals;
	}
    

	/**
	 * Original Sender of the message -as os this version this is not used
	 * due to constraints of the message interception layer
	 */
	private Object sender;

	/**
	 * @return java.lang.Object
	 * @roseuid 411B57170266
	 */
	public Object getSender() 
	{
		return sender;
	}
    
	/**
	 * @param obj
	 * @roseuid 411B571E01BC
	 */
	public void setSender(Object obj) 
	{
		this.sender = obj;
	}


	private Object server;

	public void setServer(Object server)
	{
		this.server = server;
	}

	public Object getServer()
	{
		return server;
	}
/*
	public void setTarget(Object target)
	{
		getOrgMessage().setTarget( target );
	}

	public Object getTarget()
	{
		return getOrgMessage().getTarget();
	}
*/
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

	private int direction;

	public int getDirection() 
	{
		return direction;
	}

	public void setDirection(int direction) 
	{
		this.direction = direction;
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

	public String toShortString() 
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("#( ");
		String separator = "";
		for( int i = 0; i<messages.size(); i++ ) 
		{
			buffer.append(separator);
			buffer.append(((Message)messages.get(i)).getTarget().getClass());
			buffer.append('.');
			buffer.append(((Message)messages.get(i)).getSelector());
			separator = ", ";
		}
		buffer.append(" )");
		return buffer.toString();
	}

	public void prepend( Message m ) 
	{
		m.setMessageList( this );
		messages.addFirst( m );
		orgMessagePointer++;
	}

	public void append( Message m ) 
	{
		m.setMessageList( this );
		messages.addLast( m );
	}

	public void replace( MessageList ml ) 
	{
		int firstmatch = getFirstMatchIndex();
		int lastmatch  = getLastMatchIndex();
		
		if( firstmatch == -1 || lastmatch == -1 )
			throw new Composestar.RuntimeCore.FLIRT.Exception.ComposestarRuntimeException( "There was no match" );


		//if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","Firstmatch: " + firstmatch + "\nLastmatch: " + lastmatch );
		//if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","This: " + this.toShortString() );
		//if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","Other: " + ml.toShortString() );
	

		// first remove the old messages
		for( int i = firstmatch; i <= lastmatch; i++ ) 
		{
			messages.remove( firstmatch );
		}

		//if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","This after removal: " + this.toShortString() );

		// insert new messages
		int insertat = firstmatch;
		for( int i = 0; i < ml.getMessages().size(); i++ ) 
		{
			Message m = (Message) ml.getMessages().get( i );
			m.setMessageList( this );
			messages.add( insertat, m );
			insertat++;
		}

		//if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","This after insertion: " + this.toShortString() );
	}

	private int getFirstMatchIndex() 
	{
		for( int i = 0; i < messages.size(); i++ ) 
		{
			if( ((Message)messages.get( i )).isMatched() )
				return i;
		}
		return -1;
	}

	private int getLastMatchIndex()
	{
		for( int i = messages.size()-1; i >= 0; i-- ) 
		{
			if( ((Message)messages.get( i )).isMatched() )
				return i;
		}
		return -1;
	}

	public Message getFirstMessage() 
	{
		if(messages.isEmpty() )
			return null;
		else
			return (Message) messages.get( 0 );
	}

	public boolean hasWildcards() 
	{
		for( int i = 0; i < messages.size(); i++ ) 
		{
			Message m = (Message) messages.get( i );
			if( m.getTarget().equals( "*" ) || m.getSelector().equals( "*" ) )
				return true;
		}
		return false;
	}

	public void resetMatches() 
	{
		for( int i = 0; i < messages.size(); i++ ) 
		{
			((Message) messages.get( i )).setMatched( false );
		}
	}

	public void matchAll() 
	{
		for( int i = 0; i < messages.size(); i++ ) 
		{
			((Message) messages.get( i )).setMatched( true );
		}
	}

	private MessageList originalMessageList = null;

	public void setOriginalMessageList( MessageList ml ) 
	{
		originalMessageList = ml;
	}

	public MessageList getOriginalMessageList() 
	{
		return originalMessageList;
	}

	public Message getMessageAfterOutputFilter() 
	{
		Message m = new Message( getOrgMessage() );
		m.setMessageList( null );
		copyToMessage( m );
		return m;
	}

	public Message reduceToOne() 
	{
		Message m1 = (Message) messages.get( 0 );
		messages = new LinkedList();
		messages.add( m1 );
		return m1;
	}

	public Message duplicateOne()
	{
		Message m2 = new Message( (Message) messages.get( 0 ) );
		messages.add( m2 );
		return m2;
	}

}