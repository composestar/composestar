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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.FIRE.ActionNode;
import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Staijen
 */
public class MessageAnalysis {

    private Concern concern;
	
	private List states;
	
	
	public MessageAnalysis(Concern concern, String selector)
	{
        //String selector1 = selector;
        this.concern = concern;
		this.states = new ArrayList();
	}
	
	public void addState(ActionNode node)
	{
		StateAnalysis state = new StateAnalysis(this.concern, node);
		this.states.add(state);
	}
	
	public void analyze()
	{
		for( Iterator i = this.states.iterator(); i.hasNext(); )
		{
			((StateAnalysis)i.next()).analyze();
		}
	}
	
}
