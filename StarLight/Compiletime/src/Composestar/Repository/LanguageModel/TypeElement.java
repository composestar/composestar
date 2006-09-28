package Composestar.Repository.LanguageModel;


/**
 * Summary description for Class1
 */
public class TypeElement implements IRepositoryElement
{
	private String _id;
	private String _name = "";
	private String _fullName = "";
	private String _assembly = "";
	private String _baseType = "";
	private String _namespace = "";
	private boolean _isAbstract;
	private boolean _isInterface;
	private boolean _isSealed;
	private boolean _isValueType;
	private boolean _isEnum;
	private boolean _isClass;
	private boolean _isNotPublic;
	private boolean _isPrimitive;
	private boolean _isPublic;
	private boolean _isSerializable;
	private String module; // TODO: object ???????
	private String _fromDLL = "";
	
	public TypeElement(String id) 
	{
		_id = id;
	}

	private String _implementedInterface;

	/** @property */
	public String get_ImplementedInterface()
	{
		return _implementedInterface;
	}

	/** @property */
	public void set_ImplementedInterface(String value)
	{
		_implementedInterface = value;
	} 


	/** @property */
	public String get_Id() { return _id; }

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
	public String get_Assembly()
	{
		return _assembly;
	}

	/** @property */
	public void set_Assembly(String value)
	{
		_assembly = value;
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
	//public AssemblyElement get_AssemblyElement()
	//{
	//	return _assemblyElement;
	//}

	/** @property */
	//public void set_AssemblyElement(AssemblyElement value)
	//{
	//	_assemblyElement = value;
	//}
	
	/** @property */
	public String get_Namespace()
	{
		return _namespace;
	}

	/** @property */
	public void set_Namespace(String value)
	{
		_namespace = value;
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

	/** @property */
	public boolean get_IsClass()
	{
		return _isClass;
	}

	/** @property */
	public void set_IsClass(boolean value)
	{
		_isClass = value;
	}

	/** @property */
	public boolean get_IsNotPublic()
	{
		return _isNotPublic;
	}

	/** @property */
	public void set_IsNotPublic(boolean value)
	{
		_isNotPublic = value;
	}

	/** @property */
	public boolean get_IsPrimitive()
	{
		return _isPrimitive;
	}

	/** @property */
	public void set_IsPrimitive(boolean value)
	{
		_isPrimitive = value;
	}

	/** @property */
	public boolean get_IsPublic()
	{
		return _isPublic;
	}

	/** @property */
	public void set_IsPublic(boolean value)
	{
		_isPublic = value;
	}

	/** @property */
	public boolean get_IsSerializable()
	{
		return _isSerializable;
	}

	/** @property */
	public void set_IsSerializable(boolean value)
	{
		_isSerializable = value;
	}
	
	/** @property */
	public String get_FromDLL()
	{
		return _fromDLL;
	}

	/** @property */
	public void set_FromDLL(String value)
	{
		_fromDLL = value;
	}


}
