package Composestar.Repository.LanguageModel;

/**
 * Summary description for ParameterElement.
 */
public class ParameterElement
{

	private String _name;

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

	private MethodElement _referencedMethodElement;

	/** @property */
	public MethodElement get_ParentMethodElement()
	{
		return _referencedMethodElement;
	}

	/** @property */
	public void set_ParentMethodElement(MethodElement value)
	{
		_referencedMethodElement = value;
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
