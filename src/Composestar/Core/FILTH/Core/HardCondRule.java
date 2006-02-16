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

public class HardCondRule extends Rule{
	public HardCondRule(Parameter left, Parameter right){
		super(left, right);
		identifier="cond_hard";
	}
	
	public void apply(){
		//System.out.print(" *applying: HardCond <"+(Action)_left+","+(Action)_right+">*\n");
		if ( (_left.evaluate()==null) ||
			 (((Action)_left).isExecuted()==false) || 
			 (_left.evaluate().booleanValue()==false) )
						((Action)_right).setExecutable(false);	
	}
}
