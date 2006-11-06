package Composestar.Core.FILTH.Core;
/*
 * Created on 9-sep-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author nagyist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.util.*;

public class ExecutionManager {
	LinkedList _order;
	Graph _graph;
	
	public ExecutionManager(LinkedList order, Graph g){
		_order=order;_graph=g;
	}
	
	public void execute(){
		_order.removeFirst();//be careful the root is the first!
		
		Node currentNode; 
		Action currentAction; 
		LinkedList rules; Rule crule;
		
		while (!_order.isEmpty()){	
			currentNode=(Node)_order.getFirst(); 
			currentAction=(Action)currentNode.getElement(); 			

			//get all of its rules and apply them
			rules = currentAction.getRules();
			//step 1. order the rules according to the preferences of operators...
			rules=setPreferences(rules);
			detectConflict(rules);
			//step 2. detect conflicts, like skip-skip
			for (Iterator i=rules.iterator(); i.hasNext(); )
			{
				if ( currentAction.isExecutable() ){
					crule=(Rule)i.next();
					crule.apply();
				}
			}
			
			//if the current action is still executable...
			if ( currentAction.isExecutable() ){
				//System.out.println("executed>> "+currentAction);
				currentAction.execute();
			}//else
			//	System.out.println("NOT executed>> "+currentAction);
			
			_order.removeFirst();
			//if the execution is not executable, go on the nexr action in the order
			
				/* check its rules ~ edges, evaluate the necessery members, and do the semantics*/
        }
	}
	
	/* ordering the rules of an action according to the pref. table */
	private LinkedList setPreferences(LinkedList rules){
		String prefix;
		Rule r;
		
		LinkedList preflist=new LinkedList();
	
		/* currently the preferencetable of the operators is hard-wired & all operators must be listed */
		preflist.addLast("skip_hard");
		preflist.addLast("skip_soft");
		
		preflist.addLast("pre_hard");

		preflist.addLast("cond_hard");
		preflist.addLast("cond_soft");

		preflist.addLast("pre_soft");

		/* end of pref. table */
		
		LinkedList newRules=new LinkedList();
		for (Iterator j=preflist.iterator();j.hasNext(); ){
			prefix=(String)j.next();
			for (Iterator i=rules.iterator();i.hasNext(); ){
				r=(Rule)i.next();
				if (r.getIdentifier().startsWith(prefix)){					
					newRules.addLast(r);
				}
			}
		}
		//System.out.println(rules.size()+"-"+newRules.size());
		return newRules; 
	}
	
	/* detecting conflicts */
	private void detectConflict(LinkedList rules){
		/* currently only one rule (skip)is defined & implemented,
		 * which  
		 * */
		SkipRule r1,r2;
		LinkedList crules=new LinkedList();
		/* collect the skip rules */
		Rule r;
		for (Iterator i=rules.iterator();i.hasNext(); ){
			r=(Rule)i.next();
			if (r.getIdentifier().startsWith("skip"))
			{
				crules.add(r);
			}
		}
		/* evaluate the parameters of skip rules and their skipping value if there is a conflict*/
		try{
			if (crules.size()>1){
			
				for (Iterator i=crules.iterator();i.hasNext(); ){
					r1=(SkipRule)i.next();
					for (Iterator j=crules.iterator();j.hasNext(); ){
						r2=(SkipRule)i.next();
						/* i	f the skip rules have the same skip value */
						if ( ((r1.getNewValue()==null) && (r2.getNewValue()==null)) ||
				     	 	(r1.getNewValue().evaluate().booleanValue()!=r2.getNewValue().evaluate().booleanValue()) ){ 
				     	
							//and both should be skipped
							if (
						   		( ((r1.getLeft().evaluate()!=null) && (r2.getLeft().evaluate()!=null)) &&
						     	((r1.getLeft().evaluate().booleanValue()) && (r2.getLeft().evaluate().booleanValue()))
						     	) ||
				     	   		( ((r1.getLeft().evaluate()!=null) && (r1.getLeft().evaluate().booleanValue())) &&
				     	     	((r2.getLeft().evaluate()==null) && (r2 instanceof SoftSkipRule))
				     	    	) ||
						   		( ((r2.getLeft().evaluate()!=null) && (r2.getLeft().evaluate().booleanValue())) &&
							 	((r1.getLeft().evaluate()==null) && (r1 instanceof SoftSkipRule))
								)  )
							{
								throw new RuntimeException("Conflict between two skips");
							}
				     	
				     	}
					}
				}
				
			}
		}catch(NullPointerException e){		
			//System.out.println("detectConflict - null");
		}
	}
}
