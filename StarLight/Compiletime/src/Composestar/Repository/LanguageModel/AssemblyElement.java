package Composestar.Repository.LanguageModel;

/**
 * Summary description for AssemblyElement.
 */
public class AssemblyElement
{
	private String _name;
	private String _fileName;
	private long _timestamp;

	/** @property
	  */
	public String get_Name()
	{
		return _name;
	}

	/** @property
	  */
	public void set_Name(String value)
	{
		_name = value;
	}

	/** @property
	  */
	public String get_FileName()
	{
		return _fileName;
	}

	/** @property
	  */
	public void set_FileName(String value)
	{
		_fileName = value;
	}

	/** @property
    */
	public long get_Timestamp()
	{
		return _timestamp;
	}

	/** @property
	  */
	public void set_Timestamp(long value)
	{
		_timestamp = value;
	}

}
