package Composestar.Repository.LanguageModel;

/**
 * Summary description for MethodElement.
 */
public class MethodElement
{
	private int _id;
	private int _parentTypeId;
	private String _name;
	private String _returnType;
	private MethodBody _methodBody;

	public MethodElement()
	{
		_id = this.hashCode();
		_methodBody = new MethodBody(_id); 
	}

	/** @property */
	public int get_Id() { return _id; }
	
	/** @property */
	public void set_ParentTypeId(int value)
	{
	    _parentTypeId = value; 
	}
	
	/** @property */
	public int get_ParentTypeId() 
	{
	    return _parentTypeId; 
	}
	
	/** @property */
	public String get_Name()
	{
		return _name;
	}

	/** @property */
	public void set_Name(String value)
	{
		_name = value;
	}

	/** @property */
	public String get_ReturnType()
	{
		return _returnType;
	}

	/** @property */
	public void set_ReturnType(String value)
	{
		_returnType = value;
	}	

	/** @property */
	public MethodBody get_MethodBody()
	{
		return _methodBody;
	}

	/** @property */
	public void set_MethodBody(MethodBody value)
	{
		_methodBody = value;
	}

}
