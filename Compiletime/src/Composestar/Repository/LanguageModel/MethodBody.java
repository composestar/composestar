package Composestar.Repository.LanguageModel;

import Composestar.Repository.LanguageModel.Inlining.*;

/**
 * Contains information about the method body.
 */
public class MethodBody
{
	private int _id;
	private int _parentMethodId;
	private InlineInstruction _inputFilter;
	
	public MethodBody()
	{
		_id = this.hashCode();
	}
	
	public MethodBody(int parentId)
	{
		this();
		_parentMethodId = parentId;
	}
	
	/** @property */
	public void set_ParentMethodId(int value)
	{
	    _parentMethodId = value; 
	}
	
	/** @property */
	public int get_ParentMethodId() 
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
