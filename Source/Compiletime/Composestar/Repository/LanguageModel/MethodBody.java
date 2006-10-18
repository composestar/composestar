package Composestar.Repository.LanguageModel;


/**
 * Contains information about the method body.
 */
public class MethodBody
{
	private String _id;
	private String _parentMethodId;
	private String _inputFilter;

	/** @attribute com.db4o.Transient() */
	private CallElement[] _callElements;
		
	public MethodBody(String id, String parentId)
	{
		_id = id;
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
	public String get_Id()
	{
		return _id;
	}

	/** @property */
	public String get_InputFilter()
	{
		return _inputFilter;
	}

	/** @property */
	public void set_InputFilter(String value)
	{
		_inputFilter = value;
	}

	/** @property
	*/
	public CallElement[] get_CallElements()
	{
		return _callElements;
	}

	/** @property
	  */
	public void set_CallElements(CallElement[] value)
	{
		_callElements = value;
	}

}
