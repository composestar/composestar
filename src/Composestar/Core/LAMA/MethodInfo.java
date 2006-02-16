package Composestar.Core.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

import java.util.*;

public class MethodInfo extends ProgramElement implements SerializableRepositoryEntity
{
	
	public String Name;
	public String ReturnTypeString;
    
	public Type ReturnType;
	public ArrayList Parameters;
	public Type Parent;

	public boolean IsAbstract;
	public boolean IsAssembly;
	public boolean IsConstructor;
	public boolean IsFamily;
	public boolean IsFamilyAndAssembly;
	public boolean IsFamilyOrAssembly;
	public boolean IsFinal;
	public boolean IsHideBySig;
	public boolean IsPrivate;
	public boolean IsPublic;
	public boolean IsStatic;
	public boolean IsVirtual;
	public boolean IsDeclaredHere;

	public MethodInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
		Parameters = new ArrayList();
	}

	/****** Implementation of Language Unit interface **********/
    
	protected HashSet toHashSet(Collection c)
	{
		HashSet result = new HashSet();
		Iterator iter = c.iterator();
		while (iter.hasNext())
			result.add(iter.next());
		return result;
	}

	/* (non-Javadoc)
	 * @see Composestar.CTCommon.LogicLang.metamodel.LanguageUnit#getUnitRelation(java.lang.String)
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
				res.add(((Annotation)i.next()).getType());
			return new UnitResult(res);
		}        
      
		return null;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF020A
	 */
	public boolean isPrivate() 
	{
		return IsPrivate;     
	}
    
	/**
	 * @param isPrivate
	 * @roseuid 402A0240034B
	 */
	public void setIsprivate(boolean isPrivate) 
	{
		IsPrivate = isPrivate;     
	}
    
	/**
	 * @return boolean
	 * @roseuid 401B84CF020B
	 */
	public boolean isPublic() 
	{
		return IsPublic;     
	}
    
	/**
	 * @param isPublic
	 * @roseuid 402A024C0280
	 */
	public void setIsPublic(boolean isPublic) 
	{
		IsPublic = isPublic;     
	}
    
	/**
	 * @return boolean
	 * @roseuid 401B84CF020C
	 */
	public boolean isStatic() 
	{
		return IsStatic;     
	}
    
	/**
	 * @param isStatic
	 * @roseuid 402A026E02BB
	 */
	public void setIsStatic(boolean isStatic) 
	{
		IsStatic = isStatic;     
	}
    
	/**
	 * @return boolean
	 * @roseuid 401B84CF020D
	 */
	public boolean isVirtual() 
	{
		return IsVirtual;     
	}
    
	/**
	 * @param isVirtual
	 * @roseuid 402A027A03E4
	 */
	public void setIsVirtual(boolean isVirtual) 
	{
		IsVirtual = isVirtual;     
	}

	/* (non-Javadoc)
	 * @see Composestar.CTCommon.LOLA.metamodel.LanguageUnit#getUnitAttributes()
	 */
	public Collection getUnitAttributes()
	{
		HashSet result = new HashSet();
		if (isPublic())
			result.add("public");
		if (isPrivate())
			result.add("private");
		/*if (isProtected())
		 result.add("protected");*/
		if (isStatic())
			result.add("static");
		if (isFinal())
			result.add("final");
		return result;
	}

	/**
	 * @return boolean
	 * @roseuid 401B84CF0208
	 */
	public boolean isFinal() 
	{
		return IsFinal;     
	}
    
	/**
	 * @param isFinal
	 * @roseuid 402A01FB02A1
	 */
	public void setIsFinal(boolean isFinal) 
	{
		IsFinal = isFinal;     
	}

	/**
	 * @return java.lang.String
	 * @roseuid 401B84CF020E
	 */
	public String name() 
	{
		return Name;     
    }
	
	/**
	 * @param name
	 * @roseuid 402A028601CF
	 */
	public void setName(String name) 
	{
		Name = name;     
	}
	
	/**
	 * @return java.util.List
	 * @roseuid 401B84CF0211
	 */
	public List getParameters() 
	{
		return Parameters;     
	}
	
	/**
	 * @param types Check if the methods has these types
	 * @return true if there is a signature match. False otherwise
	 * @roseuid 402C9CE401C5
	 */
	public boolean hasParameters(String[] types) 
	{
		return true;
	}
	
	/**
	 * @return Composestar.Core.LAMA.Type
	 * @roseuid 4050689303BD
	 */
	public Type parent() 
	{
		return Parent;     
	}
	
	/**
	 * @return Composestar.Core.LAMA.Type
	 * @roseuid 401B84CF020F
	 */
	public Type returnType() 
	{
        if( ReturnType == null ) {
            TypeMap map = TypeMap.instance();
			ReturnType = map.getType( ReturnTypeString );
        }
        return ReturnType;
	} 
	
	/** Stuff for LOLA **/
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
    {
      return Name;
    }
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	public String getUnitType()
    {
      return "Method";
    }
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#hasAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
    {
      return getUnitAttributes().contains(attribute);
    }

}
