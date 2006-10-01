package Composestar.Repository.LanguageModel;

/**
 * Summary description for Condition.
 */
public class Condition
{
	public Condition()
	{
		//
		// TODO: Add Constructor Logic here
		//
	}

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


	private Reference _reference;

	/** @property */
	public Reference get_Reference()
	{
		return _reference;
	}

	/** @property */
	public void set_Reference(Reference value)
	{
		_reference = value;
	}


	private String _parentTypeId;

	/** @property */
	public void set_ParentTypeId(String value)
	{
		_parentTypeId = value;
	}

	/** @property */
	public String get_ParentTypeId()
	{
		return _parentTypeId;
	}
}
