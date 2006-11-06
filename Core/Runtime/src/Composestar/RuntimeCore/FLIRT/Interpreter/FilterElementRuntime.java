package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.List;
import java.util.ArrayList;
import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.Utils.Debug;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * FilterElementRuntime.java 2506 2006-11-03 13:08:36Z elmuerte $
 */
public class FilterElementRuntime extends ReferenceEntityRuntime implements Interpretable
{
	public FilterElementCompositionOperatorRuntime rightOperator;

	public ConditionExpressionRuntime conditionpart;

	public MatchingPatternRuntime matchingPattern;

	protected EnableOperatorTypeRuntime theEnableOperatorTypeRuntime;

	public FilterRuntime theFilter = null;

	/**
	 * @roseuid 40DDFC7202D8
	 */
	public FilterElementRuntime()
	{}

	public FilterElementRuntime(FilterElementCompositionOperatorRuntime compositionOperator,
			EnableOperatorTypeRuntime operatorType, ConditionExpressionRuntime conditionexpr,
			MatchingPatternRuntime matchingPattern)
	{
		this.rightOperator = compositionOperator;
		this.conditionpart = conditionexpr;
		this.matchingPattern = matchingPattern;
		this.theEnableOperatorTypeRuntime = operatorType;
		this.matchingPattern.oper = operatorType;
	}

	public void setEnableOperatorTypeRuntime(EnableOperatorTypeRuntime operatorType)
	{
		this.theEnableOperatorTypeRuntime = operatorType;
		this.matchingPattern.oper = operatorType;
	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD62BE023A
	 */
	public boolean interpret(MessageList m, Dictionary context)
	{
		if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT",
				"\t\tInterpreting FilterElementCompositionOperatorRuntime...");
		boolean returnvalue = true;
		if (this.conditionpart.interpret(m, context))
		{
			// boolean matches = false;
			/*
			 * if(Debug.SHOULD_DEBUG)
			 * Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tCondition OK,
			 * checking "+this.matchingPatterns.size() + " pattern(s)...");
			 * for(int i=0; i<this.matchingPatterns.size(); i++) {
			 * MatchingPatternRuntime mpr =
			 * (MatchingPatternRuntime)this.matchingPatterns.get(i); matches =
			 * matches || mpr.interpret(m, context); }
			 */
			if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT",
					"\t\t\tCondition OK, checking pattern...");

			returnvalue = matchingPattern.interpret(m, context);

			/*
			 * // this is now done by matchingPattern
			 * if(this.theEnableOperatorTypeRuntime instanceof
			 * EnableOperatorRuntime) // => { returnvalue = matches; } else
			 * if(this.theEnableOperatorTypeRuntime instanceof
			 * DisableOperatorRuntime) // ~> { returnvalue = !matches; }
			 */
		}
		else
		{
			returnvalue = false;
		}
		/*
		 * Now check if we need to go to the next because we did not match!!
		 * if(!returnvalue && rightOperator.getRightArgument() != null) {
		 * returnvalue = rightOperator.getRightArgument().interpret(m,context); }
		 */
		return returnvalue;
	}

	/**
	 * @return Composestar.Runtime.FLIRT.interpreter.FilterElementCompositionOperatorRuntime
	 * @roseuid 40DE9E4002D4
	 */
	public FilterElementCompositionOperatorRuntime getNextFilterElementCompositionOperator()
	{
		return this.rightOperator;
	}
}
