package Composestar.Repository.LanguageModel;

/**
 * Summary description for AttributeElement.
 */
public class AttributeElement {
	private String _parentType;
	private int _parentId;
	
	private String _type; // attribute type
	private String _value; // attribute value

	public AttributeElement(String parentType, int parentId) 
	{
		_parentType = parentType;
		_parentId = parentId;	
	}
	
	/** @property */
	public String get_parentType() 
	{
	    return _parentType; 
	}
	
	/** @property */
	public int get_parentId() 
	{
	    return _parentId; 
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
	public String get_Value()
	{
		return _value;
	}

	/** @property */
	public void set_Value(String value)
	{
		_value = value;
	}
	
}
