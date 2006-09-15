package Composestar.Repository.LanguageModel;

public class FieldElement {
	private int _parentTypeId;
	private String _name = "";
	private String _type = "";
	private boolean _isPrivate;  
	private boolean _isPublic; 
	private boolean _isStatic;
	
	public FieldElement()
	{	
	}
	
	public FieldElement(int parentTypeId)
	{
		super();
		_parentTypeId = parentTypeId;
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
	public String get_Type()
	{
		return _type;
	}

	/** @property */
	public void set_Type(String value)
	{
		_type = value;
	}

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
	public boolean get_IsPrivate()
	{
		return _isPrivate;
	}

	/** @property */
	public void set_IsPrivate(boolean value)
	{
		_isPrivate = value;
	}
	
	/** @property */
	public boolean get_IsPublic()
	{
		return _isPublic;
	}

	/** @property */
	public void set_IsPublic(boolean value)
	{
		_isPublic = value;
	}
	
	/** @property */
	public boolean get_IsStatic()
	{
		return _isStatic;
	}

	/** @property */
	public void set_IsStatic(boolean value)
	{
		_isStatic = value;
	}
}
