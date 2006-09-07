package Composestar.Core.FILTH.Core;
/*
 * Created on 12-sep-2003
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
public abstract class SkipRule extends Rule {
	protected Parameter _newValue;
	
	public SkipRule(Parameter left, Parameter right, Parameter newValue){
		super(left, right);
		_newValue=newValue;
	}
	
	public Parameter getNewValue(){
		return _newValue;
	}
	/* (non-Javadoc)
	 * @see Rule#apply()
	 */
	public abstract void apply(); 
		// TODO Auto-generated method stub

}
