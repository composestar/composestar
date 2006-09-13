package Composestar.Repository.LanguageModel;

import Composestar.Repository.LanguageModel.Inlining.*;

/**
 * Contains information about the method body.
 */
public class MethodBody
{

	private Instruction _inputFilter;

	/** @property */
	public Instruction get_InputFilter()
	{
		return _inputFilter;
	}

	/** @property */
	public void set_InputFilter(Instruction value)
	{
		_inputFilter = value;
	} 



}
