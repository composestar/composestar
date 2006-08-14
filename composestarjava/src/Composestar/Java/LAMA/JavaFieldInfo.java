package Composestar.Java.LAMA;

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Utils.StringConverter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class JavaFieldInfo extends FieldInfo 
{

	private static final long serialVersionUID = -3329314904670932060L;
	
	public Field theField;
	
	public JavaFieldInfo( Field f ) 
	{
		super();
		this.theField = f;
	}
	
	public boolean isDeclaredHere() 
	{
		if((Parent.fullName()).equals(theField.getDeclaringClass().getName()))
			return true;
		return false;
	}
	
	/** Stuff for LOLA **/
	
	/* (non-Javadoc)
	 * @see Composestar.core.LAMA.ProgramElement#getUnitAttributes()
	 */
	public Collection getUnitAttributes() 
	{
		HashSet result = new HashSet();
		Iterator modifierIt = StringConverter.stringToStringList( Modifier.toString(theField.getModifiers()), " " );
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
	    
		if ("ParentType".equals(argumentName))
			return new UnitResult(Parent);
		else if ("Class".equals(argumentName) && "Class".equals(fieldType().getUnitType()))
			return new UnitResult(fieldType());
		else if ("Interface".equals(argumentName) && "Interface".equals(fieldType().getUnitType()))
			return new UnitResult(fieldType());
		else if ("Annotation".equals(argumentName) && "Annotation".equals(fieldType().getUnitType()))
			return new UnitResult(fieldType());
		else if ("Annotations".equals(argumentName)) 
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