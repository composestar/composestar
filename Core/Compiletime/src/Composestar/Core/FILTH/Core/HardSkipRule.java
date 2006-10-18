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
public class HardSkipRule extends SkipRule {
	
	public HardSkipRule(Parameter left, Parameter right, Parameter newValue){
		super(left, right, newValue);
		identifier="skip_hard";
	}

	/* (non-Javadoc)
	 * @see Rule#apply()
	 */
	public void apply() {

		//System.out.print(" *applying: HardSkip <"+(Action)_left+","+(Action)_right+">*\n");
		if ( (_left.evaluate()!=null) &&
			 (_left.evaluate().booleanValue()) ){
				((Action)_right).setExecutable(false);
				((Action)_right).setReturnValue( _newValue.evaluate() );
				((Action)_right).setExecuted();
				//System.out.print(" * skipping>> "+(Action)_right+" with "+_newValue.evaluate()+"*\n");
					
			 }

	}

}