package Composestar.Java.LAMA;

import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Utils.StringConverter;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class JavaParameterInfo extends ParameterInfo {

	private static final long serialVersionUID = -6839040829288333952L;
	
	private Class theParameter;	
	public JavaMethodInfo Parent;
	
	public JavaParameterInfo(Class p) {
		super();
		this.theParameter = p;
	}
	
	/** Stuff for LOLA **/
	
	/* (non-Javadoc)
     * @see Composestar.core.LAMA.ProgramElement#getUnitAttributes()
     */
    public Collection getUnitAttributes() {
    	return new HashSet();
    }
    
    /* (non-Javadoc)
     * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
     */
    public UnitResult getUnitRelation(String argumentName)
    {
      if (argumentName.equals("ParentMethod"))
        return new UnitResult(Parent);
      else if (argumentName.equals("Class") && parameterType().getUnitType().equals("Class"))
        return new UnitResult(parameterType());
      else if (argumentName.equals("Interface") && parameterType().getUnitType().equals("Interface"))
        return new UnitResult(parameterType());
      else if (argumentName.equals("Annotation") && parameterType().getUnitType().equals("Annotation"))
        return new UnitResult(parameterType());
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
}
