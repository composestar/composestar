package Composestar.Repository.LanguageModel;

/**
 * Summary description for MethodElement.
 */
public class MethodElement
{
	private int _id;
	private int _parentTypeId;
	private String _name = "";
	private String _returnType = "";
	private boolean _isAbstract;
	private boolean _isConstructor;
	private boolean _isPrivate;
	private boolean _isPublic;
	private boolean _isStatic;
	private boolean _isVirtual;
	private MethodBody _methodBody;
	
	public MethodElement()
	{
		_id = this.hashCode();
		_methodBody = new MethodBody(_id); 
	}

	/** @property */
	public int get_Id() { return _id; }
	
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
	public String get_ReturnType()
	{
		return _returnType;
	}

	/** @property */
	public void set_ReturnType(String value)
	{
		_returnType = value;
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
	public boolean get_IsConstructor()
	{
		return _isConstructor;
	}

	/** @property */
	public void set_IsConstructor(boolean value)
	{
		_isConstructor = value;
	}
	
	/** @property */
	public boolean get_IsPrivate()
	{
		return _isPrivate;
	}

	/** @property */
	public void set_IsPrivate(boolean value)
	{
		_isPrivate = value;
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
	public boolean get_IsStatic()
	{
		return _isStatic;
	}

	/** @property */
	public void set_IsStatic(boolean value)
	{
		_isStatic = value;
	}
	
	/** @property */
	public boolean get_IsVirtual()
	{
		return _isVirtual;
	}

	/** @property */
	public void set_IsVirtual(boolean value)
	{
		_isVirtual = value;
	}

	/** @property */
	public MethodBody get_MethodBody()
	{
		return _methodBody;
	}

	/** @property */
	public void set_MethodBody(MethodBody value)
	{
		_methodBody = value;
	}

}
