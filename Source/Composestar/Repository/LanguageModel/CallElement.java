package Composestar.Repository.LanguageModel;

import Composestar.Repository.LanguageModel.Inlining.*;    

/**
 * Summary description for CallElement.
 */
public class CallElement
{

	private String _parentMethodBodyId;

	/** @property */
	public String get_ParentMethodBodyId()
	{
		return _parentMethodBodyId;
	}

	/** @property */
	public void set_ParentMethodBodyId(String value)
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


	private InlineInstruction _outputFilter;

	/** @property */
	public InlineInstruction get_OutputFilter()
	{
		return _outputFilter;
	}

	/** @property */
	public void set_OutputFilter(InlineInstruction value)
	{
		_outputFilter = value;
	}

}
