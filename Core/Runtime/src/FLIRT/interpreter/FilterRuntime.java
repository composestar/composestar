package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Actions.WaitAction;
import Composestar.RuntimeCore.FLIRT.Exception.FilterException;
import Composestar.RuntimeCore.FLIRT.Filtertypes.FilterTypeRuntime;
import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 */
public class FilterRuntime extends ReferenceEntityRuntime implements Interpretable
{
    private ComposeStarAction acceptAction;
    private ComposeStarAction rejectAction;
    public FilterCompositionOperatorRuntime rightArgument;
    private ArrayList filterElements = null;
    public FilterTypeRuntime theFilterTypeRuntime;
    
	public boolean isDummy()
	{
		return reference.repositoryKey.equalsIgnoreCase("CpsDefaultInnerDispatchConcern.CpsDefaultInnerDispatchFilterModule.CpsDefaultInnerDispatchFilter");
	}

    /**
     * @roseuid 40DEA09803A0
     */
    public FilterRuntime() {
		this.filterElements = new ArrayList();
    }
    
    /**
     * @param filterElements
     * @param rightOperator
     * @roseuid 40DD5751034C
     */
    public FilterRuntime(ArrayList filterElements, FilterCompositionOperatorRuntime rightOperator) {
    	this.filterElements = filterElements;
    	this.rightArgument = rightOperator;     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD624102DA
     */
    public boolean interpret(MessageList m, Dictionary context) {
    	if(!this.filterElements.isEmpty())
    	{
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\tInterpreting FilterRuntime...");
    		// Get the first element and call it's operator to interpret this message.
    		FilterElementRuntime fer = (FilterElementRuntime)this.filterElements.get(0);
    		return fer.getNextFilterElementCompositionOperator().interpret(m,context);
    	}
    	return false;     
    }
    
    /**
     * @return Composestar.Runtime.FLIRT.interpreter.FilterCompositionOperatorRuntime
     * @roseuid 40DE9E220169
     */
    public FilterCompositionOperatorRuntime getNextFilterCompositionOperator() {
    	return this.rightArgument;     
    }
    
    /**
     * @param message
     * @param context
     * @return boolean
     * @roseuid 40F2960B0002
     */
    public boolean canAccept(MessageList message, Dictionary context) {
		return this.interpret(message,context);     
    }
    
    /**
     * @param message
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40F2962E0142
     */
    public ComposeStarAction getAcceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context) {
		return this.theFilterTypeRuntime.acceptAction(originalMessage, modifiedMessage, context);
    }
    
    /**
     * @param message
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40F2963C03C3
     */
    public ComposeStarAction getRejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context) {
		return this.theFilterTypeRuntime.rejectAction(originalMessage, modifiedMessage, context);
    }

	public void addFilterElement(FilterElementRuntime filterElement)
	{
		filterElements.add(filterElement);
	}

	public String getDeclerationFileName()
	{
		return getReference().getDescriptionFileName();
	}

	public int getDeclerationLineNumber()
	{
		return getReference().getDescriptionLineNumber();
	}
}
