package Composestar.Core.FILTH.Core;

/*
 * Created on 15-sep-2003
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
public class HardPreRule extends Rule {
	
	public HardPreRule(Parameter left, Parameter right){
		super(left, right);
		identifier="pre_hard";
	}
	/* (non-Javadoc)
	 * @see Rule#apply()
	 */
	public void apply() {
		//System.out.print(" *applying: HardPre <"+(Action)_left+","+(Action)_right+">*\n");
		if (
                !((Action) _left).isExecuted()
			  )
						((Action)_right).setExecutable(false);		}

}
