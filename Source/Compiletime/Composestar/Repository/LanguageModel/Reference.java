package Composestar.Repository.LanguageModel;

/**
 * Summary description for Reference.
 */
public class Reference
{
	public final static String INNER_TARGET = "inner";
	public final static String SELF_TARGET = "self";

	public Reference()
	{
		//
		// TODO: Add Constructor Logic here
		//
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




	private String _target;

	/** @property */
	public String get_Target()
	{
		return _target;
	}

	/** @property */
	public void set_Target(String value)
	{
		_target = value;
	}




	private String _selector;

	/** @property */
	public String get_Selector()
	{
		return _selector;
	}

	/** @property */
	public void set_Selector(String value)
	{
		_selector = value;
	}

}
