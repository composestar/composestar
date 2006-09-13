package Composestar.Repository.LanguageModel;

import Composestar.Repository.LanguageModel.Inlining.*;    

/**
 * Summary description for CallElement.
 */
public class CallElement
{

	private MethodBody _referencedMethodBody;

	/** @property */
	public MethodBody get_ParentMethodBody()
	{
		return _referencedMethodBody;
	}

	/** @property */
	public void set_ParentMethodBody(MethodBody value)
	{
		_referencedMethodBody = value;
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
