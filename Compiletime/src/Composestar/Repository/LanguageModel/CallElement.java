package Composestar.Repository.LanguageModel;

import Composestar.Repository.LanguageModel.Inlining.*;    

/**
 * Summary description for CallElement.
 */
public class CallElement
{

	private int _parentMethodBodyId;

	/** @property */
	public int get_ParentMethodBodyId()
	{
		return _parentMethodBodyId;
	}

	/** @property */
	public void set_ParentMethodBodyId(int value)
	{
		_parentMethodBodyId = value;
	}

	private String _methodReference;

	/** @property */
	public String get_MethodReference()
	{
		return _methodReference;
	}

	/** @property */
	public void set_MethodReference(String value)
	{
		_methodReference = value;
	}


	private Instruction _outputFilter;

	/** @property */
	public Instruction get_OutputFilter()
	{
		return _outputFilter;
	}

	/** @property */
	public void set_OutputFilter(Instruction value)
	{
		_outputFilter = value;
	}

}
