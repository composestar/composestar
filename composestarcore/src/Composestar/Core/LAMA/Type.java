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
	
    public List getFields() {
        return Fields;
    }
    
	/**
	 * @return java.lang.String
	 * @roseuid 401B84CF01DA
	 */
	public String fullName() 
	{
		return FullName;     
	}
	
	/**
	 * @param name
	 * @roseuid 4029F62E03B6
	 */
	public void setFullName(String name) 
	{
		FullName = name;     
    }
	
	/**
	 * @param name
	 * @param types
	 * @return Composestar.dotnet.LAMA.DotNETMethodInfo
	 * @roseuid 401B84CF01F9
	 */
	public MethodInfo getMethod(String name, String[] types) 
	{
        MethodInfo method = null;
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
	 * @roseuid 401B84CF01FA
	 */
	public List getMethods() 
	{
		return Methods;     
	}
	
	/**
	 * @return boolean
	 * @roseuid 401B84CF01E9
	 */
	public boolean isNestedPrivate() 
	{
		return IsNestedPrivate;     
    }
	
	/**
	 * @return boolean
	 * @roseuid 401B84CF01EA
	 */
	public boolean isNestedPublic() 
	{
		return IsNestedPublic;     
    }
	
	/**
	 * @return java.lang.String
	 * @roseuid 401B84CF01F3
	 */
	public String name() 
	{
		return Name;     
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
	
	public String getUnitName()
    {
      return FullName;
    }
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#hasUnitAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
    {
      return getUnitAttributes().contains(attribute);
    }
}
