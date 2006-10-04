package Composestar.Java.LAMA;

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Utils.StringConverter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class JavaMethodInfo extends MethodInfo 
{

	private static final long serialVersionUID = 308590098089260675L;
	
	public Method theMethod;
	
	public JavaMethodInfo() 
	{
		super();
	}
	
	public JavaMethodInfo(Method m) 
	{
		super();
		this.theMethod = m;
	}
	
	public boolean isDeclaredHere() 
	{
		if((Parent.fullName()).equals(theMethod.getDeclaringClass().getName()))
			return true;
		return false;
	}
	
	/**
     * This method should make a clone of the MethodInfo with the name and
     * parentType changed to the given name and actualParent. The parameters and
     * return type should stay the same. 
     */
    public MethodInfo getClone( String name, Type actualParent ) 
    {
    	JavaMethodInfo mi = new JavaMethodInfo();
    	mi.setName(name);
    	
    	//set MethodInfo variables
    	mi.Parent = actualParent;
    	mi.Parameters = this.Parameters;
    	mi.ReturnType = this.ReturnType;
    	mi.ReturnTypeString = this.ReturnTypeString;
    	
    	//set JavaMethodInfo variables
    	mi.theMethod = this.theMethod;
    	
    	return mi;
    }
	
	/** Stuff for LOLA **/
	
	private HashSet toHashSet(Collection c) 
	{
		HashSet result = new HashSet();
		Iterator iter = c.iterator();
		while (iter.hasNext())
			result.add(iter.next());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see Composestar.core.LAMA.ProgramElement#getUnitAttributes()
	 */
	public Collection getUnitAttributes() 
	{
		HashSet result = new HashSet();
		Iterator modifierIt = StringConverter.stringToStringList( Modifier.toString(theMethod.getModifiers()), " " );
		while(modifierIt.hasNext())
		{
			result.add(modifierIt.next());
		}
		return result;
	}
    
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	public UnitResult getUnitRelation(String argumentName) 
	{
		if (argumentName.equals("ParentClass") && Parent.getUnitType().equals("Class"))
			return new UnitResult(Parent);
		else if (argumentName.equals("ParentInterface") && Parent.getUnitType().equals("Interface"))
			return new UnitResult(Parent);      
		else if (argumentName.equals("ChildParameters"))
			return new UnitResult(toHashSet(Parameters));
		else if (argumentName.equals("ReturnClass") && returnType().getUnitType().equals("Class"))
			return new UnitResult(returnType());
		else if (argumentName.equals("ReturnInterface") && returnType().getUnitType().equals("Interface"))
			return new UnitResult(returnType());
		else if (argumentName.equals("ReturnAnnotation") && returnType().getUnitType().equals("Annotation"))
			return new UnitResult(returnType());
		else if (argumentName.equals("Annotations")) 
		{
			Iterator i = getAnnotations().iterator();
			HashSet res = new HashSet();
			while (i.hasNext())
				res.add(((JavaAnnotation)i.next()).getType());
			return new UnitResult(res);
		}        
      
		return null;
	}
    
	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		// Nothing yet
	}
	
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		// Nothing yet
	}
}

