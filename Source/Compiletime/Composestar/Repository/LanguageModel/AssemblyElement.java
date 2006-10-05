package Composestar.Repository.LanguageModel;

/**
 * Summary description for AssemblyElement.
 */
public class AssemblyElement
{
	private String _name;
	private String _fileName;
	private long _timestamp;

	/** @attribute com.db4o.Transient() */ 
	private TypeElement[] _typeElements;
	
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

	/** @property
    */
	public TypeElement[] get_TypeElements()
	{
		return _typeElements;
	}

	/** @property
	  */
	public void set_TypeElements(TypeElement[] value)
	{
		_typeElements = value;
	}
}
