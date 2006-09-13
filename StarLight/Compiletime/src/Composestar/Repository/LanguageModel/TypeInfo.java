package Composestar.Repository.LanguageModel;


/**
 * Summary description for Class1
 */
public class TypeInfo
{
	private int _id;
	private String _name;
	private String _fullName;
	private String _baseType;
	private AssemblyInfo _assemblyInfo;
	private boolean _isAbstract;
	private boolean _isInterface;
	private boolean _isSealed;
	private boolean _isValueType;
	private boolean _isEnum;
	
	public TypeInfo()
	{
		_id = this.hashCode();
	}
	
	public int get_id() { return _id; }

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

	/** @property */
	public String get_FullName()
	{
		return _fullName;
	}

	/** @property */
	public void set_FullName(String value)
	{
		_fullName = value;
	}

	/** @property */
	public String get_BaseType()
	{
		return _baseType;
	}

	/** @property */
	public void set_BaseType(String value)
	{
		_baseType = value;
	}
	
	/** @property */
	public AssemblyInfo get_AssemblyInfo()
	{
		return _assemblyInfo;
	}

	/** @property */
	public void set_AssemblyInfo(AssemblyInfo value)
	{
		_assemblyInfo = value;
	}

	/** @property */
	public boolean get_IsAbstract()
	{
		return _isAbstract;
	}

	/** @property */
	public void set_IsAbstract(boolean value)
	{
		_isAbstract = value;
	}

	/** @property */
	public boolean get_IsInterface()
	{
		return _isInterface;
	}

	/** @property */
	public void set_IsInterface(boolean value)
	{
		_isInterface = value;
	}

	/** @property */
	public boolean get_IsSealed()
	{
		return _isSealed;
	}

	/** @property */
	public void set_IsSealed(boolean value)
	{
		_isSealed = value;
	}

	/** @property */
	public boolean get_IsValueType()
	{
		return _isValueType;
	}

	/** @property */
	public void set_IsValueType(boolean value)
	{
		_isValueType = value;
	}

	/** @property */
	public boolean get_IsEnum()
	{
		return _isEnum;
	}

	/** @property */
	public void set_IsEnum(boolean value)
	{
		_isEnum = value;
	}



}
