/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import Composestar.Core.FIRE.ActionNode;
import Composestar.Core.FIRE.FilterActionNode;
import Composestar.Core.FIRE.FilterReasoningEngine;
import Composestar.Core.FIRE.SignatureActionNode;
import Composestar.Core.FIRE.SubstituteActionNode;
import Composestar.Core.FIRE.Symbol;
import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Staijen
 *
 * @TODO remove
 * UNUSED FOR NOW (UNTIL FIRE IS USED) 
 */
public class StateAnalysis {

	private Concern concern;
	private ActionNode node;
	
	public StateAnalysis(Concern concern, ActionNode node)
	{
		this.concern = concern;
		this.node = node;
	}

	public void analyze()
	{
		System.out.print('\n' + this.concern.getName() + ": analyzing ");
		//CKRET.printState(this.node);

		//table = new SemanticTable(this.node.getTarget().getName(), this.node.getSelector().getName());
		process(this.node);
	}
	
	
	public void process(ActionNode node)
	{
		System.out.println(node);
		if( node instanceof SignatureActionNode )
		{
			if( node.getSelector().getName().equals("*") || ((SignatureActionNode)node).isSignatureMatch(null, node.getSelector().getName(), this.concern.getName()) != FilterReasoningEngine.FALSE )
			{
				System.out.println("MATCH");
				process((ActionNode) node.getChild(0));
			}
			else
			{
				process((ActionNode)node.getChild(1));
			}
		}
		else if( node instanceof SubstituteActionNode )
		{
			if( node.numberOfChildren() != 1 )
				System.out.println("WHOOOPS, " + node.numberOfChildren() + " children, taking the first one..");
			process((ActionNode) node.getChild(0));
		}
		else if( node instanceof FilterActionNode )
		{
			FilterActionNode fan = (FilterActionNode) node;
			String type = fan.getFilterName();
			if( type.equals("Meta"))
			{
				if( node.numberOfChildren() == 1 )
					process((ActionNode) node.getChild(0));
				else
					System.out.println("More then 1 child under meta");
			}
			else if( type.equals("dispatch"))
			{
				System.out.println("Dispatching, analysis complete.");
			}
			else if( type.equals("EndOfSet"))
			{
				System.out.println("EndOfSet, faulthy path !!!!!");
			}
			else
			{
				if( node.numberOfChildren() == 1 )
					process((ActionNode) node.getChild(0));
			}
		}
	}
	
	public String[] getState()
	{
		Symbol[] symbols = this.node.getConditions();
		String[] conditions = new String[symbols.length];
		for( int i = 0; i < symbols.length; i++ )
		{
			conditions[i] = symbols[i].getName();
		}
		return conditions;
	}
	
}
