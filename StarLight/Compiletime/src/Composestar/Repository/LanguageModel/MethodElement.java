package Composestar.Repository.LanguageModel;

/**
 * Summary description for MethodElement.
 */
public class MethodElement implements IRepositoryElement
{
	private String _id;
	private String _parentTypeId;
	private String _name = "";
	private String _returnType = "";
	private boolean _isAbstract;
	private boolean _isConstructor;
	private boolean _isPrivate;
	private boolean _isPublic;
	private boolean _isStatic;
	private boolean _isVirtual;
	private MethodBody _methodBody;
	private String _signature;

	public MethodElement(String id)
	{
		_id = id;
			
	}

	/** @property */
	public String get_Signature()
	{
		return _signature;
	}

	/** @property */
	public void set_Signature(String value)
	{
		_signature = value;
	} 


	/** @property */
	public String get_Id() { return _id; }
	
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
		if (_methodBody == null) _methodBody = new MethodBody(_id); 

		return _methodBody;
	}

	/** @property */
	public void set_MethodBody(MethodBody value)
	{
		_methodBody = value;
	}

	/** @property */
	public boolean get_HasMethodBody()
	{
		if (_methodBody != null) return true;

		return false;
	}

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append( "Method:" );
        buffer.append( _signature );
        buffer.append( "\nInputfilters:\n" );
        buffer.append( _methodBody.get_InputFilter().toString() );
        return buffer.toString();
    }
}
