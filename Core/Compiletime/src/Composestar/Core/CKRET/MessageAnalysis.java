/*
 * Created on 3-dec-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.FIRE.ActionNode;
import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Staijen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MessageAnalysis {

    private Concern concern;
	
	private List states;
	
	
	public MessageAnalysis(Concern concern, String selector)
	{
        String selector1 = selector;
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
