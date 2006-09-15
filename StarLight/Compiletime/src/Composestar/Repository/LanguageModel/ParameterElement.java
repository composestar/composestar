package Composestar.Repository.LanguageModel;

/**
 * Summary description for ParameterElement.
 */
public class ParameterElement
{
	private int _parentMethodId;
	private String _name;
	private boolean _isIn;
	private boolean _isOptional;
	private boolean _isOut;
	private boolean _isRetVal;
	
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
	public String get_Name()
	{
		return _name;
	}

	/** @property */
	public void set_Name(String value)
	{
		_name = value;
	}
	private short _ordinal;

	/** @property */
	public short get_Ordinal()
	{
		return _ordinal;
	}

	/** @property */
	public void set_Ordinal(short value)
	{
		_ordinal = value;
	}

	private String _parameterType;

	/** @property */
	public String get_ParameterType()
	{
		return _parameterType;
	}

	/** @property */
	public void set_ParameterType(String value)
	{
		_parameterType = value;
	}

	private String _parameterAttributes;

	/** @property */
	public String get_ParameterAttributes()
	{
		return _parameterAttributes;
	}

	/** @property */
	public void set_ParameterAttributes(String value)
	{
		_parameterAttributes = value;
	}
	
	/** @property */
	public boolean get_IsIn()
	{
		return _isIn;
	}

	/** @property */
	public void set_IsIn(boolean value)
	{
		_isIn = value;
	}
	
	/** @property */
	public boolean get_IsOptional()
	{
		return _isOptional;
	}

	/** @property */
	public void set_IsOptional(boolean value)
	{
		_isOptional = value;
	}

	/** @property */
	public boolean get_IsOut()
	{
		return _isOut;
	}

	/** @property */
	public void set_IsOut(boolean value)
	{
		_isOut = value;
	}
	
	/** @property */
	public boolean get_IsRetVal()
	{
		return _isRetVal;
	}

	/** @property */
	public void set_IsRetVal(boolean value)
	{
		_isRetVal = value;
	}

}
