package Composestar.Core.FILTH.Core;

/*
 * Created on 11-sep-2003
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
public class SoftCondRule extends Rule {

	public SoftCondRule(Parameter left, Parameter right){
		super(left, right);
		identifier="cond_soft";
	}

	/* (non-Javadoc)
	 * @see Rule#apply()
	 */
	public void apply() {

		System.out.print(" *applying: SoftCond <"+_left + ',' +_right +">*\n");
		if ( (_left.evaluate()!=null) &&
			 (((Action)_left).isExecuted()) && 
			 (!_left.evaluate().booleanValue()) )
						((Action)_right).setExecutable(false);	

	}

}