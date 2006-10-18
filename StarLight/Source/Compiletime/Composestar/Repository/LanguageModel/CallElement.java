package Composestar.Repository.LanguageModel;


/**
 * Summary description for CallElement.
 */
public class CallElement
{

	private String _parentMethodBodyId;

	/** @property */
	public String get_ParentMethodBodyId()
	{
		return _parentMethodBodyId;
	}

	/** @property */
	public void set_ParentMethodBodyId(String value)
	{
		_parentMethodBodyId = value;
	}

	private String _methodReference;

	/** @property */
	public String get_MethodReference()
	{
		return _methodReference;
	}

	/** @property */
	public void set_MethodReference(String value)
	{
		_methodReference = value;
	}


	private String _outputFilter;

	/** @property */
	public String get_OutputFilter()
	{
		return _outputFilter;
	}

	/** @property */
	public void set_OutputFilter(String value)
	{
		_outputFilter = value;
	}

	public String toString(){
		String s = _methodReference;
		if ( _outputFilter != null ){
			s += "\n" + _outputFilter.toString();
		}
		return s;
	}
}
