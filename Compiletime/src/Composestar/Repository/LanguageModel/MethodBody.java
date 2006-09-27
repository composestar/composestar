package Composestar.Repository.LanguageModel;

import Composestar.Repository.LanguageModel.Inlining.*;

/**
 * Contains information about the method body.
 */
public class MethodBody
{
	private int _id;
	private String _parentMethodId;
	private InlineInstruction _inputFilter;
		
	public MethodBody(String parentId)
	{
		_parentMethodId = parentId;
	}
	
	/** @property */
	public void set_ParentMethodId(String value)
	{
	    _parentMethodId = value; 
	}
	
	/** @property */
	public String get_ParentMethodId() 
	{
	    return _parentMethodId; 
	}
	
	/** @property */
	public int get_Id()
	{
		return _id;
	}

	/** @property */
	public InlineInstruction get_InputFilter()
	{
		return _inputFilter;
	}

	/** @property */
	public void set_InputFilter(InlineInstruction value)
	{
		_inputFilter = value;
	} 



}
