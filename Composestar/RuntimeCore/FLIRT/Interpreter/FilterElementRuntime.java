package Composestar.RuntimeCore.FLIRT.Interpreter;

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
 * $Id: FilterElementRuntime.java 514 2006-09-06 08:05:15Z reddog33hummer $
 */
public class FilterElementRuntime extends ReferenceEntityRuntime implements Interpretable 
{
    public FilterElementCompositionOperatorRuntime rightOperator;
    public ConditionExpressionRuntime conditionpart;
    public ArrayList matchingPatterns = null;
    public EnableOperatorTypeRuntime theEnableOperatorTypeRuntime;
	public FilterRuntime theFilter = null;
    
    /**
     * @roseuid 40DDFC7202D8
     */
    public FilterElementRuntime() {

		matchingPatterns = new ArrayList();
     
    }
    
    /**
     * @param compositionOperator
     * @param operatorType
     * @param conditionexpr
     * @param matchingPatterns
     * @roseuid 40DD59C5029B
     */
    public FilterElementRuntime(FilterElementCompositionOperatorRuntime compositionOperator, EnableOperatorTypeRuntime operatorType, ConditionExpressionRuntime conditionexpr, ArrayList matchingPatterns) {
    	this.rightOperator = compositionOperator;
    	this.conditionpart = conditionexpr;
    	this.matchingPatterns = matchingPatterns;
    	this.theEnableOperatorTypeRuntime = operatorType;     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD62BE023A
     */
    public boolean interpret(MessageList m, Dictionary context) {
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\tInterpreting FilterElementCompositionOperatorRuntime...");
    	boolean returnvalue = true;
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tCondition: "+this.conditionpart.GetType());
    	if(this.conditionpart.interpret(m,context))
    	{
    		boolean matches = false;
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tCondition OK, checking "+this.matchingPatterns.size() + " pattern(s)...");
			for(int i=0; i<this.matchingPatterns.size(); i++)
			{
				MatchingPatternRuntime mpr = (MatchingPatternRuntime)this.matchingPatterns.get(i);
				matches = matches || mpr.interpret(m, context);
			}
    		if(this.theEnableOperatorTypeRuntime instanceof EnableOperatorRuntime) // =>
    		{
    			returnvalue = matches;
    		}
    		else if(this.theEnableOperatorTypeRuntime instanceof DisableOperatorRuntime) // ~>
    		{
    			returnvalue = !matches;
    		}
    	}
    	else
    	{
    		returnvalue = false;
    	}
    	/* Now check if we need to go to the next because we did not match!!
    	if(!returnvalue && rightOperator.getRightArgument() != null)
    	{
    		returnvalue = rightOperator.getRightArgument().interpret(m,context);
    	}*/
    	return returnvalue;     
    }
    
    /**
     * @return 
     * Composestar.Runtime.FLIRT.interpreter.FilterElementCompositionOperatorRuntime
     * @roseuid 40DE9E4002D4
     */
    public FilterElementCompositionOperatorRuntime getNextFilterElementCompositionOperator() {
    	return this.rightOperator;     
    }
}
