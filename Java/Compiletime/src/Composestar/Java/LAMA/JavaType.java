package Composestar.Java.LAMA;

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Utils.StringConverter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class JavaType extends Type {
	
	private static final long serialVersionUID = 3739255608747710756L;
	public Class theClass;
	public boolean isAnnotation;
	private JavaType superClass; 
	public ArrayList ImplementedInterfaceNames; // List of Strings
    private ArrayList ImplementedInterfaces; // List of JavaTypes
		
	private ProgramElement parentNS; // Added by the Language Model; this relation can be used in logic queries
	private HashSet childTypes; // Added by the Language Model; this relation contains links to sub-types of this type.
	private HashSet parameterTypes; // Added by the Language Model; this relation contains links to parameters of this type.
	private HashSet methodReturnTypes; // Added by the Language Model; this relation contains links to methods that return this type.
	private HashSet fieldTypes; // Added by the Language Model; this relation contains links to fields of this type.
	private HashSet implementedBy; // Added by the Language Model; this relation exists for interfaces and points to the Types that implement this interface
	
	public JavaType(Class c) {
		super();
		this.theClass = c;
		ImplementedInterfaceNames = new ArrayList();
        ImplementedInterfaces = null;
		childTypes = new HashSet();
		parameterTypes = new HashSet();
		methodReturnTypes = new HashSet();
        fieldTypes = new HashSet();
        implementedBy = new HashSet();
        isAnnotation = false;
    }
	
	public Class getclass() {
		return theClass;
	}
	
	public void addImplementedInterface(String iface) {
	      ImplementedInterfaceNames.add(iface);
	}
	
	public List getImplementedInterfaces() {
		
		if (null == ImplementedInterfaces) {
			ImplementedInterfaces = new ArrayList();
		    Iterator iter = ImplementedInterfaceNames.iterator();
		    TypeMap map = TypeMap.instance();
		    while (iter.hasNext()) {
		    	JavaType iface = (JavaType)map.getType( (String)iter.next() );
		        if (null != iface)
		        	ImplementedInterfaces.add(iface);
		    }
		}
		return ImplementedInterfaces;
	}
	
	public String namespace() {
		if(theClass.getPackage()==null)
			return null;
		return theClass.getPackage().getName();     
    }
	
	public boolean isAnnotation() {
		//TODO: this is 1.5..
		//return theClass.isAnnotation();
		return isAnnotation;
	}
	
	public void setIsAnnotation(boolean annotation) {
		this.isAnnotation = annotation;
	}
	
	public boolean isInterface() {
		return theClass.isInterface();
	}
	
	public JavaType superClass() {
		if( superClass == null ) {
            Class superclass = theClass.getSuperclass();
            if(null != superclass) {
            	TypeMap map = TypeMap.instance();
            	superClass = (JavaType)map.getType( theClass.getSuperclass().getName() );
            }	
        }
		return superClass;
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
	
	private HashSet filterDeclaredHere(Collection in)
    {
      HashSet out = new HashSet();
      Iterator iter = in.iterator();
      while (iter.hasNext())
      {
        Object obj = iter.next();
        if (obj instanceof JavaMethodInfo)
        {
          if (((JavaMethodInfo)obj).isDeclaredHere())
          	out.add(obj);
        }
        else if (obj instanceof JavaFieldInfo)
        {
          if (((JavaFieldInfo)obj).isDeclaredHere())
            out.add(obj);
        }
        else
          out.add(obj); // No filtering on other kinds of objects
      }
      return out;
    }
	
	/* (non-Javadoc)
     * @see Composestar.core.LAMA.ProgramElement#getUnitAttributes()
     */
	public Collection getUnitAttributes()
    {
		HashSet result = new HashSet();
    	Iterator modifierIt = StringConverter.stringToStringList( Modifier.toString(theClass.getModifiers()), " " );
		while(modifierIt.hasNext()){
			result.add(modifierIt.next());
		}
		return result;
    }	
	
	/* (non-Javadoc)
     * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
     */
    public UnitResult getUnitRelation(String argumentName)
    {
      if (getUnitType().equals("Class"))
        return getUnitRelationForClass(argumentName);
      else if (getUnitType().equals("Interface"))
        return getUnitRelationForInterface(argumentName);
      else if (getUnitType().equals("Annotation"))
        return getUnitRelationForAnnotation(argumentName);
      return null; // Should never happen!
    }
	
    public UnitResult getUnitRelationForAnnotation(String argumentName)
	{
	  HashSet resClasses = new HashSet();
	  HashSet resInterfaces = new HashSet();
	  HashSet resMethods = new HashSet();
	  HashSet resFields = new HashSet();
	  HashSet resParameters = new HashSet();
	  
	  Iterator i = getAnnotationInstances().iterator();
	  while (i.hasNext())
	  {
	    ProgramElement unit = ((JavaAnnotation)i.next()).getTarget();
	    if (unit instanceof JavaType)
	    {
	      JavaType type = (JavaType)unit;
	      if (type.isInterface())
	        resInterfaces.add(type);
	      else 
	        resClasses.add(type);
	    }
	    else if (unit instanceof JavaMethodInfo)
	      resMethods.add(unit);
	    else if (unit instanceof FieldInfo)
	      resFields.add(unit);
	    else if (unit instanceof ParameterInfo)
	      resParameters.add(unit);
	  }
	  
	  if (argumentName.equals("AttachedClasses"))
	    return new UnitResult(resClasses);
	  else if (argumentName.equals("AttachedInterfaces"))
	    return new UnitResult(resInterfaces);
	  else if (argumentName.equals("AttachedMethods"))
	    return new UnitResult(resMethods);
	  else if (argumentName.equals("AttachedFields"))
	    return new UnitResult(resFields);
	  else if (argumentName.equals("AttachedParameters"))
	    return new UnitResult(resParameters);
	  
	  return null;
	}
    
    public UnitResult getUnitRelationForClass(String argumentName)
    {
      if (argumentName.equals("ParentNamespace"))
        return new UnitResult(parentNS);
      else if (argumentName.equals("ParentClass"))
        return new UnitResult(superClass()); // can be null!
      else if (argumentName.equals("ChildClasses"))
        return new UnitResult(childTypes);
      else if (argumentName.equals("ChildMethods"))
        return new UnitResult(filterDeclaredHere(this.m_methods));
      else if (argumentName.equals("ChildFields"))
        return new UnitResult(filterDeclaredHere(this.m_fields));
      else if (argumentName.equals("ParameterClass"))
        return new UnitResult(parameterTypes);
      else if (argumentName.equals("MethodReturnClass"))
        return new UnitResult(methodReturnTypes);
      else if (argumentName.equals("FieldClass"))
        return new UnitResult(fieldTypes);
      else if (argumentName.equals("Implements"))
        return new UnitResult(toHashSet(getImplementedInterfaces()));
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
    
    public UnitResult getUnitRelationForInterface(String argumentName)
    {
      if (argumentName.equals("ParentNamespace"))
        return new UnitResult(parentNS);
      else if (argumentName.equals("ParentInterface"))
        return new UnitResult(superClass()); // can be null!
      else if (argumentName.equals("ChildInterfaces"))
        return new UnitResult(childTypes);
      else if (argumentName.equals("ChildMethods"))
        return new UnitResult(filterDeclaredHere(this.m_methods));
      else if (argumentName.equals("ParameterInterface"))
        return new UnitResult(parameterTypes);
      else if (argumentName.equals("MethodReturnInterface"))
        return new UnitResult(methodReturnTypes);
      else if (argumentName.equals("FieldInterface"))
        return new UnitResult(fieldTypes);
      else if (argumentName.equals("ImplementedBy"))
        return new UnitResult(implementedBy);
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
    
	/* (non-Javadoc)
     * @see Composestar.core.LAMA.ProgramElement#getUnitType()
     */
    public String getUnitType()
    {
	  if (isInterface())
        return "Interface";
      else if (isAnnotation())
        return "Annotation";
      else
        return "Class";
    }
    
    /*** Extra method for adding links to child types of this type */
    public void addChildType(ProgramElement childType)
    {
	    this.childTypes.add(childType);
	}
    
    /*** Extra method for adding links to methods that return this type */
    public void addFieldType(ProgramElement fieldType)
    {
      this.fieldTypes.add(fieldType);
    }
    
    /*** Extra method for adding links to classes that implement this interface */
    public void addImplementedBy(ProgramElement aClass)
    {
      implementedBy.add(aClass);
    }
    
    /*** Extra method for adding links to methods that return this type */
    public void addMethodReturnType(ProgramElement returnType)
    {
      this.methodReturnTypes.add(returnType);
    }
    
    /*** Extra method for adding links to parameters of this type */
    public void addParameterType(ProgramElement paramType)
    {
      this.parameterTypes.add(paramType);
    }
    
    /*** Extra method for storing the parent namespace; called by JavaLanguageModel::completeModel() **/
    public void setParentNamespace(ProgramElement parentNS)
    {
      this.parentNS = parentNS;
    }
}
