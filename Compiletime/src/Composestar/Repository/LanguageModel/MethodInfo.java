package Composestar.Repository.LanguageModel;

/**
 * Summary description for MethodInfo.
 */
public class MethodInfo
{
	private int _id;
	private int _typeId;
	private String _name;
	private String _returnType;
	private MethodBody _methodBody;

	public MethodInfo()
	{
		//_parameters = new java.util.ArrayList();
		_id = this.hashCode();
		_methodBody = new MethodBody(); 
	}

	/** @property */
	public int get_Id() { return _id; }
	
	/** @property */
	public void set_TypeId(int value)
	{
		_typeId = value; 
	}
	
	/** @property */
	public int get_TypeId() 
	{
		return _typeId; 
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
