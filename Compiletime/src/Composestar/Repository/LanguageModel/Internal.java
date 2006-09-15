package Composestar.Repository.LanguageModel;

/**
 * Summary description for Internal.
 */
public class Internal
{
	public Internal()
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


	private String _nameSpace;

	/** @property */
	public String get_NameSpace()
	{
		return _nameSpace;
	}

	/** @property */
	public void set_NameSpace(String value)
	{
		_nameSpace = value;
	}




	private String _concern;

	/** @property */
	public String get_Concern()
	{
		return _concern;
	}

	/** @property */
	public void set_Concern(String value)
	{
		_concern = value;
	}



	private int _parentTypeId;

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
}
