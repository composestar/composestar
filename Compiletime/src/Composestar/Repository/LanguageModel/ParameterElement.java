package Composestar.Repository.LanguageModel;

/**
 * Summary description for ParameterElement.
 */
public class ParameterElement
{
	private int _parentMethodId;
	private String _name;

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


}
