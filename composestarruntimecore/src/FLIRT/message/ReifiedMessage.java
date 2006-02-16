package Composestar.RuntimeCore.FLIRT.Message;

import Composestar.RuntimeCore.FLIRT.ObjectManager;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.SyncBuffer;
import Composestar.RuntimeCore.Utils.Invoker;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ReifiedMessage.java,v 1.5 2006/02/16 14:09:46 composer Exp $
 */
public class ReifiedMessage implements Runnable
{
	public static final int REIFIED = 0;
	public static final int RESPONDED = 1;
	public static final int PROCEEDED = 2;
	public static final int FORKED = 3;

	private int state = 0;

	protected Object returnValue;
    
	private Message message;
    
	private Object actObject;
	private String actMethod;
	private Object[] actArgs;
	private SyncBuffer continueBuffer;
	
	// while this is true, when producing the continuebuffer
	// the next filter should be evaluated..
	// otherwise there should be a returnvalue
	private boolean shouldContinue = true;

	public void setActMethodInfo(Object act, String method, Object[] args) 
	{
		this.actObject = act;
		this.actMethod = method;
		this.actArgs   = args;
		this.continueBuffer = new SyncBuffer();
	}

	public SyncBuffer getContinueBuffer()
	{
		return this.continueBuffer;
	}

	public  synchronized  void run()
	{
		Invoker.getInstance().invoke(this.actObject, this.actMethod, this.actArgs);

		switch(this.state)
		{
			case REIFIED:
				resume();
				break;
			case PROCEEDED:
				resume();
				break;
			case RESPONDED:
				proceed();
				resume();
				break;
			case FORKED:
				break;
		}
	}

	/**
	 * @param m
	 * @roseuid 404C4B5D0199
	 */
	public ReifiedMessage(Message m) 
	{
		this.message = m;     
	}

	public ReifiedMessage( MessageList m )
	{
		this.message = m.getOrgMessage();
		// TODO WM: Do something useful here
	}
    
	/**
	 * @roseuid 40EA9DD10250
	 */
	public  synchronized void resume() 
	{
		if( this.state == REIFIED )
		{
			this.continueBuffer.produce(null);
			this.state = FORKED;
		}
		else if( this.state == PROCEEDED )
		{
			message.getResponseBuffer().produce(this.getReturnValue());
			this.state = FORKED;
		}
	}
    
	public boolean shouldContinue()
	{
		return this.shouldContinue;
	}

	public synchronized  void reply() 
	{
		if( this.state == REIFIED )
		{
			this.shouldContinue = false;
			this.continueBuffer.produce(null);
		}
		else if( this.state == RESPONDED )
		{
			this.shouldContinue = false;
			this.continueBuffer.produce(null);
		}
	}

	public void reply(Object o)
	{
		this.setReturnValue(o);
		reply();
	}

	public void respond() 
	{
		respond(null);
	}

	public void respond(Object dummy)
	{
		this.message.getResponseBuffer().produceFirst(dummy);
	}
    
	/**
	 * @roseuid 40EAA183019C
	 */
	public synchronized void proceed() 
	{
		if( this.state == REIFIED || this.state == RESPONDED )
		{
			// wrap the responsebuffer so a response
			// will be set in a new buffer and not in the
			// original one
			this.message.getResponseBuffer().wrap();
			// make the filterset continue...
			this.continueBuffer.produce(null);
			// the call below waits for a response being set in the wrapped message
			this.returnValue = this.message.getResponse();
			// unwrap the responsebuffer
			// another response will be set in the original buffer
			this.message.getResponseBuffer().unwrap();

			this.state = PROCEEDED;
		}
	}

	public Object send(Object target)
	{
		ObjectManager om = ObjectManager.getObjectManagerFor(target,MessageHandlingFacility.datastore);
		Message m = new Message(this.message.getSelector(),this.message.getArguments());
		m.setSender(this.message.getSender());
		m.setServer(target);
		m.setDirection(Message.INCOMING);
		return om.deliverIncomingMessage(m.getSender(),target,m);
	}

	/**
	 * @return int
	 * @roseuid 40EA9F6F02AF
	 */
	public int getState() 
	{
		return this.state;     
	}
    
	/**
	 * @return java.lang.String
	 * @roseuid 40EA9F7903CC
	 */
	public String getSelector() 
	{
		return this.message.getSelector();     
	}
    
	/**
	 * @param s
	 * @roseuid 40EA9F8100C0
	 */
	public void setTarget(Object obj) 
	{
		this.message.setTarget(obj);     
	}

	public Object getTarget() 
	{
		return this.message.getTarget();     
	}
    
	/**
	 * @param s
	 * @roseuid 40EA9F8100C0
	 */
	public void setSelector(String s) 
	{
		this.message.setSelector(s);     
	}
    
	/**
	 * @return java.lang.Object[]
	 * @roseuid 40EA9FA903DF
	 */
	public Object[] getArgs() 
	{
		return this.message.getArguments();     
	}

	public void setArgs(Object[] args) 
	{
		this.message.setArguments(args);
	}
    
	public Object getArg(int index) 
	{
		return this.getArgs()[index];
	}

	public void setArg(int index, Object arg)
	{
		Object[] args = this.message.getArguments();
		args[index] = arg;
		this.message.setArguments(args);
	}

	/**
	 * @return java.lang.Object
	 * @roseuid 40EA9FB70277
	 */
	public Object getReturnValue() 
	{
		return this.returnValue;     
	}

	public void setReturnValue(Object retval)
	{
		this.returnValue = retval;
	}

	public Object getSender()
	{
		return this.message.getSender();
	}
}
