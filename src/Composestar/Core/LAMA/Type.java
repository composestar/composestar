package Composestar.Core.LAMA; 

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public abstract class Type extends ProgramElement{

	public ArrayList Methods;
	public ArrayList Fields;
		
	public String Name;
	public String FullName;
	
	public boolean IsNestedPrivate;
    public boolean IsNestedPublic;
    
    public Type()
    {
    	UnitRegister.instance().registerLanguageUnit(this);
    	Methods = new ArrayList();
    	Fields = new ArrayList();
    }
    
    /**
     * @param field
     */
    public void addField(FieldInfo field) 
    {
        Fields.add(field);
        field.setParent(this);
    }
    
    /**
     * @param method
     */
    public void addMethod(MethodInfo method) 
    {
        Methods.add( method );
        method.setParent(this);     
    }
	
    /**
	 * @return java.util.List
	 */
    public List getFields() 
    {
        return Fields;
    }
    
	/**
	 * @return java.lang.String
	 */
	public String fullName() 
	{
		return FullName;     
	}
	
	/**
	 * @param name
	 */
	public void setFullName(String name) 
	{
		FullName = name;     
    }
	
	/**
	 * @param name
	 * @param types
	 * @return Composestar.dotnet.LAMA.DotNETMethodInfo
	 */
	public MethodInfo getMethod(String name, String[] types) 
	{
        MethodInfo method;
        for( ListIterator iter = Methods.listIterator(); iter.hasNext(); ) 
        {
        	method = (MethodInfo)iter.next();
            // if same name && param length
            if( method.name().equals( name ) && method.hasParameters( types ) ) {
                return method;
            }
        }
        return null;
	}    
	
	/**
	 * @return java.util.List
	 */
	public List getMethods() 
	{
		return Methods;     
	}
	
	/**
	 * @return boolean
	 */
	public boolean isNestedPrivate() 
	{
		return IsNestedPrivate;     
    }
	
	/**
	 * @return boolean
	 */
	public boolean isNestedPublic() 
	{
		return IsNestedPublic;     
    }
	
	/**
	 * @return java.lang.String
	 */
	public String name() 
	{
		return Name;     
    }
	
	/**
     * @param name
     * @roseuid 4029F83F0366
     */
    public void setName(String name) {
        Name = name;     
    }
	
	/** Stuff for annotations **/

	public ArrayList annotationInstances = new ArrayList();
	
	public void addAnnotationInstance(Annotation annotation)
    {
    	this.annotationInstances.add(annotation);
    }
	
	public void removeAnnotationInstance(Annotation annotation)
    {
    	this.annotationInstances.remove(annotation);
    }
	
	public List getAnnotationInstances()
	{
		return this.annotationInstances;
	}
	
	/** Stuff for LOLA **/
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
    {
      return fullName();
    }
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#hasUnitAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
    {
      return getUnitAttributes().contains(attribute);
    }
}
