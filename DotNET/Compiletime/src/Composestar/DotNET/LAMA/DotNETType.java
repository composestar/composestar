/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LAMA.UnitResult;

/**
 * Corresponds to the Type class in the .NET framework. For more information on 
 * the methods and their meaning please refer to the microsoft documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemtypeclasstopic.asp
 * note that the name may be redundant, but should be consistent with the name of 
 * the concern.
 */
public class DotNETType extends Type {
    
	private static final long serialVersionUID = 5652622506200113401L;
	
	public int HashCode;
    public boolean IsAbstract;
    public boolean IsAnsiClass;
    public boolean IsArray;
    public boolean IsAutoClass;
    public boolean IsAutoLayout;
    public boolean IsByRef;
    public boolean IsClass;
    public boolean IsContextful;
    public boolean IsEnum;
    public boolean IsImport;
    public boolean IsInterface;
    public boolean IsMarshalByRef;
    public boolean IsNestedAssembly;
    public boolean IsNestedFamOrAssem;
    public boolean IsPointer;
    public boolean IsPrimitive;
    public boolean IsPublic;
    public boolean IsSealed;
    public boolean IsSerializable;
    public boolean IsValueType;
    public boolean IsNotPublic;
    
    public ArrayList ImplementedInterfaceNames; // List of Strings
    private ArrayList ImplementedInterfaces; // List of DotNETTypes
    public DotNETType theDotNETType;
    public DotNETModule Assembly;
    public String NameSpace;
    public String AssemblyQualifiedName;
    private DotNETType BaseType;
    public DotNETModule Module;
    private DotNETType UnderlyingType;
    public String UnderlyingTypeString;
    public String BaseTypeString;
 
	// private sets
    private ProgramElement parentNS; // Added by the Language Model; this relation can be used in logic queries
    private HashSet childTypes; // Added by the Language Model; this relation contains links to sub-types of this type.
    
    private HashSet parameterTypes; // Added by the Language Model; this relation contains links to parameters of this type.
    private HashSet methodReturnTypes; // Added by the Language Model; this relation contains links to methods that return this type.
    private HashSet fieldTypes; // Added by the Language Model; this relation contains links to fields of this type.
    private HashSet implementedBy; // Added by the Language Model; this relation exists for interfaces and points to the Types that implement this interface
    
	public String fromDLL; // Added by TypeHarvester and TypeCollector for incremental type collecting

    /**
     * @roseuid 4028E9FF026C
     */
    public DotNETType()
    {
    	super();
        ImplementedInterfaceNames = new ArrayList();
        ImplementedInterfaces = null;
        childTypes = new HashSet();
        parameterTypes = new HashSet();
        methodReturnTypes = new HashSet();
        fieldTypes = new HashSet();
        implementedBy = new HashSet();  
	 }
    
    /**
     * @return java.lang.String
     * @roseuid 401B84CF01D8
     */
    public String assemblyQualifiedName() {
        return AssemblyQualifiedName;     
    }
    
    /**
     * @param name
     * @roseuid 4029F59702DC
     */
    public void setAssemblyQualifedName(String name) {
        AssemblyQualifiedName = name;     
    }
    
    /**
     * @return Composestar.dotnet.LAMA.DotNETType
     * @roseuid 401B84CF01D9
     */
    public DotNETType baseType() {
        if( BaseType == null ) {
            TypeMap map = TypeMap.instance();
			BaseType = (DotNETType)map.getType( BaseTypeString );
		}
        return BaseType;     
    }
    
    /**
     * @param type
     * @roseuid 4029F5FC0205
     */
    public void setBaseType(String type) {
        BaseTypeString = type;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01DB
     */
    public boolean isAbstract() {
        return IsAbstract;     
    }
    
    /**
     * @param isAbstract
     * @roseuid 4029F63A0092
     */
    public void setIsAbstract(boolean isAbstract) {
        IsAbstract = isAbstract;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01DC
     */
    public boolean isAnsiClass() {
        return IsAnsiClass;     
    }
    
    /**
     * @param isAnsi
     * @roseuid 4029F6580153
     */
    public void setIsAnsiClass(boolean isAnsi) {
        IsAnsiClass = isAnsi;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01DD
     */
    public boolean isArray() {
        return IsArray;     
    }
    
    /**
     * @param isArray
     * @roseuid 4029F66A031C
     */
    public void setIsArray(boolean isArray) {
        IsArray = isArray;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01DE
     */
    public boolean isAutoClass() {
        return IsAutoClass;     
    }
    
    public void setIsAutoClass(boolean isAuto) {
        IsAutoClass = isAuto;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01DF
     */
    public boolean isAutoLayout() {
        return IsAutoLayout;     
    }
    
    /**
     * @param isAutoLayout
     * @roseuid 4029F6850108
     */
    public void setIsAutoLayout(boolean isAutoLayout) {
        IsAutoLayout = isAutoLayout;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E0
     */
    public boolean isByRef() {
        return IsByRef;     
    }
    
    /**
     * @param isByRef
     * @roseuid 4029F69E01FE
     */
    public void setIsByRef(boolean isByRef) {
        IsByRef = isByRef;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E1
     */
    public boolean isClass() {
        return IsClass;     
    }
    
    /**
     * @param isClass
     * @roseuid 4029F6E001F9
     */
    public void setIsClass(boolean isClass) {
        IsClass = isClass;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E2
     */
    public boolean isContextful() {
        return IsContextful;     
    }
    
    /**
     * @param isContextful
     * @roseuid 4029F6EF02D7
     */
    public void setIsContextful(boolean isContextful) {
        IsContextful = isContextful;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E3
     */
    public boolean isEnum() {
        return IsEnum;     
    }
    
    /**
     * @param isEnum
     * @roseuid 4029F6FF01B7
     */
    public void setIsEnum(boolean isEnum) {
        IsEnum = isEnum;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E4
     */
    public boolean isImport() {
        return IsImport;     
    }
    
    /**
     * @param isImport
     * @roseuid 4029F70D0117
     */
    public void setIsImport(boolean isImport) {
        IsImport = isImport;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E5
     */
    public boolean isInterface() {
        return IsInterface;     
    }
    
    /**
     * @param isInterface
     * @roseuid 4029F728012A
     */
    public void setIsInterface(boolean isInterface) {
        IsInterface = isInterface;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E6
     */
    public boolean isMarshalByRef() {
        return IsMarshalByRef;     
    }
    
    /**
     * @param isMarshal
     * @roseuid 4029F75E0286
     */
    public void setIsMarshalByRef(boolean isMarshal) {
        IsMarshalByRef = isMarshal;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E7
     */
    public boolean isNestedAssembly() {
        return IsNestedAssembly;     
    }
    
    /**
     * @param isNestedAsm
     * @roseuid 4029F76E02A7
     */
    public void setIsNestedAssembly(boolean isNestedAsm) {
        IsNestedAssembly = isNestedAsm;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01E8
     */
    public boolean isNestedFamOrAssem() {
        return IsNestedFamOrAssem;     
    }
    
    /**
     * @param isNested
     * @roseuid 4029F77B0166
     */
    public void setIsNestedFamOrAssem(boolean isNested) {
        IsNestedFamOrAssem = isNested;     
    }
    
    /**
     * @param isNested
     * @roseuid 4029F794018A
     */
    public void setIsNestedPrivate(boolean isNested) {
        IsNestedPrivate = isNested;     
    }
    
    /**
     * @param isNested
     * @roseuid 4029F7A1012E
     */
    public void setIsNestedPublic(boolean isNested) {
        IsNestedPublic = isNested;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01EB
     */
    public boolean isNotPublic() {
        return IsNotPublic;     
    }
    
    /**
     * @param isPublic
     * @roseuid 4029F7AB03BD
     */
    public void setIsNotPublic(boolean isPublic) {
        IsNotPublic = isPublic;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01EC
     */
    public boolean isPointer() {
        return IsPointer;     
    }
    
    /**
     * @param isPointer
     * @roseuid 4029F7B8005F
     */
    public void setIsPointer(boolean isPointer) {
        IsPointer = isPointer;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01ED
     */
    public boolean isPrimitive() {
        return IsPrimitive;     
    }
    
    /**
     * @param isPrim
     * @roseuid 4029F7C902EE
     */
    public void setIsPrimitive(boolean isPrim) {
        IsPrimitive = isPrim;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01EE
     */
    public boolean isPublic() {
        return IsPublic;     
    }
    
    /**
     * @param isPublic
     * @roseuid 4029F7D40380
     */
    public void setIsPublic(boolean isPublic) {
        IsPublic = isPublic;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01EF
     */
    public boolean isSealed() {
        return IsSealed;     
    }
    
    /**
     * @param isSealed
     * @roseuid 4029F7DE02A8
     */
    public void setIsSealed(boolean isSealed) {
        IsSealed = isSealed;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01F0
     */
    public boolean isSerializable() {
        return IsSerializable;     
    }
    
    /**
     * @param isSer
     * @roseuid 4029F7E803CF
     */
    public void setIsSerializable(boolean isSer) {
        IsSerializable = isSer;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF01F1
     */
    public boolean isValueType() {
        return IsValueType;     
    }
    
    /**
     * @param isValueT
     * @roseuid 4029F7F800BB
     */
    public void setIsValueType(boolean isValueT) {
        IsValueType = isValueT;     
    }
    
    /**
     * @return Composestar.DotNET.LAMA.DotNETModule
     * @roseuid 401B84CF01F2
     */
    public DotNETModule module() {
        return Module;     
    }
    
    /**
     * @param mod
     * @roseuid 4029F80E0348
     */
    public void setModule(DotNETModule mod) {
        Module = mod;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 401B84CF01F4
     */
    public String namespace() {
        return NameSpace;     
    }
    
    /**
     * @param space
     * @roseuid 4029F878028C
     */
    public void setNamespace(String space) {
        NameSpace = space;     
    }
    
    /**
     * @return Composestar.DotNET.LAMA.DotNETType
     * @roseuid 401B84CF01F5
     */
    public DotNETType underlyingSystemType() {
        if( UnderlyingType == null ) {
            TypeMap map = TypeMap.instance();
            UnderlyingType = (DotNETType)map.getType( UnderlyingTypeString );
        }
        return UnderlyingType;     
    }
    
    /**
     * @param type
     * @roseuid 4029F8890114
     */
    public void setunderlyingSystemType(String type) {
        UnderlyingTypeString = type;     
    }
    
    /**
     * @param types
     * @return Composestar.DotNET.LAMA.DotNETMethodInfo
     * @roseuid 401B84CF01F6
     */
    public DotNETMethodInfo getConstructor(String[] types) {
        DotNETMethodInfo method = null;
        for( ListIterator iter = Methods.listIterator(); iter.hasNext(); /* nop */ ) 
		{
			method = (DotNETMethodInfo)iter.next();
            if( method.isConstructor() && method.hasParameters( types ) ) 
			{
                return method;
            }
        }
        return null;     
    }
    
    /**
     * @return java.util.List
     * @roseuid 401B84CF01F7
     */
    public List getConstructors() {
        DotNETMethodInfo method = null;
        List constructors = new ArrayList();
        for( ListIterator iter = Methods.listIterator(); iter.hasNext(); /* nop */ ) 
		{
			method = (DotNETMethodInfo)iter.next();
            if( method.isConstructor() ) 
			{
                constructors.add( method );
            }
        }
        return constructors;     
    }
    
    /**
     * @return int
     * @roseuid 401B84CF01F8
     */
    public int getHashCode() {
        return HashCode;     
    }
    
    /**
     * @param code
     * @roseuid 4029F8A400AE
     */
    public void setHashCode(int code) {
        HashCode = code;     
    }
    
    public DotNETFieldInfo getField(String name) {
		DotNETFieldInfo field = null;
		for (ListIterator iter = Fields.listIterator(); iter.hasNext(); /* nop */ ) 
		{
			field = (DotNETFieldInfo)iter.next();
			if (field.name().equals(name))
			{
				return field;
			}
		}
		return null;
    }
    
    public void addImplementedInterface(String iface) {
      ImplementedInterfaceNames.add(iface);
    }
    
    public List getImplementedInterfaces() {
      if (null == ImplementedInterfaces)
      {
        ImplementedInterfaces = new ArrayList();
        Iterator iter = ImplementedInterfaceNames.iterator();
        TypeMap map = TypeMap.instance();
        while (iter.hasNext())
        {
          DotNETType iface = (DotNETType)map.getType( (String)iter.next() );
          if (null != iface)
            ImplementedInterfaces.add(iface);
        }
      }
      return ImplementedInterfaces;
    }
    
    /**
     * @return java.lang.String
     * @roseuid 40FD2B88026A
     */
    public String assemblyName() {
		String name = this.assemblyQualifiedName();
		StringTokenizer st = new StringTokenizer(name,",",false);
		//System.out.println("Name: "+st.countTokens());
		if(st.hasMoreTokens())
		{
			if(st.countTokens() == 6) // Really ugly hack for this case: System.Int16[,] this throws of the assemblyname resulting in errors during assembly transformer
			{
				st.nextToken();
			}
			if(st.hasMoreTokens())
				st.nextToken();
			if(st.hasMoreTokens())
				name = st.nextToken();
		}
		return name;   
    } 
    
    
    public String useArrayType(String type){
    	if(type.endsWith("[]")){
    		return "[]";
    	}
    	else return "";
    }
    
    
    /**
     * Converts a .net type to IL data type as defined at
     * http://www.codeproject.com/dotnet/ilassembly.asp
     * 
     * @return an IL datatype
     */ 
    public String ilType() {
    	String fullName = this.FullName;
    	String arrayPart = "";
    	if(fullName.endsWith("[]")){
    		fullName = fullName.substring(0,fullName.length()-2);
    		arrayPart = "[]";
    	}
		if( fullName.equals("System.Void"))
			return "void" + arrayPart;
		else if (fullName.equals("System.Boolean")) 
			return "bool" + arrayPart;
		else if (fullName.equals("System.Char")) 
			return "Char" + arrayPart;
		else if (fullName.equals("System.SByte")) 
			return "int8" + arrayPart;
		else if (fullName.equals("System.Int16")) 
			return "int16" + arrayPart;
		else if (fullName.equals("System.Int32")) 
			return "int32" + arrayPart;
		else if (fullName.equals("System.64")) 
			return "int64" + arrayPart;
		else if (fullName.equals("System.IntPtr")) 
			return "native int" + arrayPart;
		else if (fullName.equals("System.Byte")) 
			return "unsigned int8" + arrayPart;
		else if (fullName.equals("System.UInt16")) 
			return "unsigned int16" + arrayPart;
		else if (fullName.equals("System.UInt32")) 
			return "unsigned int32" + arrayPart;
		else if (fullName.equals("System.UInt64")) 
			return "unsigned int64" + arrayPart;
		else if (fullName.equals("System.UIntPtr")) 
			return "native unsigned int" + arrayPart;
		else if (fullName.equals("System.Single")) 
			return "Float32" + arrayPart;
		else if (fullName.equals("System.Double")) 
			return "Float64" + arrayPart;
		else if (fullName.equals("System.Object")) 
			return "object" + arrayPart;
		else if (fullName.equals("System.IntPtr")) 
			return "*" + arrayPart;
		else if (fullName.equals("System.Array")) 
			return "Array" + arrayPart;
		else if (fullName.equals("System.String")) 
			return "string" + arrayPart;
		else
			return "class [" + this.assemblyName() + "]"+ fullName + arrayPart; 
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
        if (obj instanceof DotNETMethodInfo)
        {
          if (((DotNETMethodInfo)obj).isDeclaredHere())
          	out.add(obj);
        }
        else if (obj instanceof DotNETFieldInfo)
        {
          if (((DotNETFieldInfo)obj).isDeclaredHere())
            out.add(obj);
        }
        else
          out.add(obj); // No filtering on other kinds of objects
      }
      return out;
    }
    
    public UnitResult getUnitRelationForClass(String argumentName)
    {
      if (argumentName.equals("ParentNamespace"))
        return new UnitResult(parentNS);
      else if (argumentName.equals("ParentClass"))
        return new UnitResult(baseType()); // can be null!
      else if (argumentName.equals("ChildClasses"))
        return new UnitResult(childTypes);
      else if (argumentName.equals("ChildMethods"))
        return new UnitResult(filterDeclaredHere(Methods));
      else if (argumentName.equals("ChildFields"))
        return new UnitResult(filterDeclaredHere(Fields));
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
          res.add(((Annotation)i.next()).getType());
        return new UnitResult(res);
      }

      return null;      
    }
    
    public UnitResult getUnitRelationForInterface(String argumentName)
    {
      if (argumentName.equals("ParentNamespace"))
        return new UnitResult(parentNS);
      else if (argumentName.equals("ParentInterface"))
        return new UnitResult(baseType()); // can be null!
      else if (argumentName.equals("ChildInterfaces"))
        return new UnitResult(childTypes);
      else if (argumentName.equals("ChildMethods"))
        return new UnitResult(filterDeclaredHere(Methods));
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
          res.add(((Annotation)i.next()).getType());
        return new UnitResult(res);
      }
        
      return null;      
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
	    ProgramElement unit = ((Annotation)i.next()).getTarget();
	    if (unit instanceof DotNETType)
	    {
	      DotNETType type = (DotNETType)unit;
	      if (type.isInterface())
	        resInterfaces.add(type);
	      else if (type.isClass())
	        resClasses.add(type);
	    }
	    else if (unit instanceof DotNETMethodInfo)
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
    
    /*** Extra method for storing the parent namespace; called by DotNETLanguageModel::completeModel() **/
    public void setParentNamespace(ProgramElement parentNS)
    {
      this.parentNS = parentNS;
    }
    
    /*** Extra method for adding links to child types of this type */
    public void addChildType(ProgramElement childType)
    {
	    this.childTypes.add(childType);
	}

    /*** Extra method for adding links to parameters of this type */
    public void addParameterType(ProgramElement paramType)
    {
      this.parameterTypes.add(paramType);
    }

    /*** Extra method for adding links to methods that return this type */
    public void addMethodReturnType(ProgramElement returnType)
    {
      this.methodReturnTypes.add(returnType);
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
    
    /* (non-Javadoc)
     * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
     */
    public String getUnitType()
    {
	  if (isInterface())
        return "Interface";
      else if (isAttribute())
        return "Annotation";
      else
        return "Class";
    }

    /** Stuff for annotations **/
    
    // A class is an annotation (attribute) type, if it inherits from System.Attribute.
    public boolean isAttribute()
    {
    	DotNETType baseType = this.baseType();
    	while (baseType != null)
    	{
    		String unitName = baseType.getUnitName();    		
    		if ("System.Attribute".equals(unitName))
    			return true;
    		
    		baseType = baseType.baseType();
    	}
    	return false;
    	//return (0!=attributeInstances.size());
    }
    
    /**
     * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
     */
    public Collection getUnitAttributes()
    {
      HashSet result = new HashSet();
      if (isPublic())
        result.add("public");
      return result;
    }	

	public void reset()
	{
		childTypes = new HashSet();
		parameterTypes = new HashSet();
		methodReturnTypes = new HashSet();
		fieldTypes = new HashSet();
		implementedBy = new HashSet();
		UnitRegister.instance().registerLanguageUnit(this);  
		
		// register fields
		Iterator fiter = Fields.iterator();
		while (fiter.hasNext())
		{
			DotNETFieldInfo field = (DotNETFieldInfo)fiter.next();
			if (null != field)
				UnitRegister.instance().registerLanguageUnit(field); 
		}

		// register methods and its parameters
		Iterator miter = Methods.iterator();
		while (miter.hasNext())
		{
			DotNETMethodInfo method = (DotNETMethodInfo)miter.next();
			if (null != method)
				UnitRegister.instance().registerLanguageUnit(method);
 
			Iterator piter = method.getParameters().iterator();
			while (piter.hasNext())
			{
				DotNETParameterInfo param = (DotNETParameterInfo)piter.next();
				if (null != param)
					UnitRegister.instance().registerLanguageUnit(param);
			}
		}
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		HashCode = in.readInt();
		IsAbstract = in.readBoolean();
		IsAnsiClass = in.readBoolean();
		IsArray = in.readBoolean();
		IsAutoClass = in.readBoolean();
		IsAutoLayout = in.readBoolean();
		IsByRef = in.readBoolean();
		IsClass = in.readBoolean();
		IsContextful = in.readBoolean();
		IsEnum = in.readBoolean();
		IsImport = in.readBoolean();
		IsInterface = in.readBoolean();
		IsMarshalByRef = in.readBoolean();
		IsNestedAssembly = in.readBoolean();
		IsNestedFamOrAssem = in.readBoolean();
		IsPointer = in.readBoolean();
		IsPrimitive = in.readBoolean();
		IsPublic = in.readBoolean();
		IsSealed = in.readBoolean();
		IsSerializable = in.readBoolean();
		IsValueType = in.readBoolean();
		IsNotPublic = in.readBoolean();
		ImplementedInterfaceNames = (ArrayList)in.readObject();
		theDotNETType = (DotNETType)in.readObject();
		Assembly = (DotNETModule)in.readObject();
		NameSpace = in.readUTF();
		if(NameSpace.equals(""))
			NameSpace = null;
		AssemblyQualifiedName = in.readUTF();
		Module = (DotNETModule)in.readObject();
		UnderlyingTypeString = in.readUTF();
		BaseTypeString = in.readUTF();
		if(BaseTypeString.equals(""))
			BaseTypeString = null;
		fromDLL = in.readUTF();
		annotationInstances = (ArrayList)in.readObject();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeBoolean(IsAbstract);
		out.writeBoolean(IsAnsiClass);
		out.writeBoolean(IsArray);
		out.writeBoolean(IsAutoClass);
		out.writeBoolean(IsAutoLayout);
		out.writeBoolean(IsByRef);
		out.writeBoolean(IsClass);
		out.writeBoolean(IsContextful);
		out.writeBoolean(IsEnum);
		out.writeBoolean(IsImport);
		out.writeBoolean(IsInterface);
		out.writeBoolean(IsMarshalByRef);
		out.writeBoolean(IsNestedAssembly);
		out.writeBoolean(IsNestedFamOrAssem);
		out.writeBoolean(IsPointer);
		out.writeBoolean(IsPrimitive);
		out.writeBoolean(IsPublic);
		out.writeBoolean(IsSealed);
		out.writeBoolean(IsSerializable);
		out.writeBoolean(IsValueType);
		out.writeBoolean(IsNotPublic);
		out.writeObject(ImplementedInterfaceNames);
		out.writeObject(theDotNETType);
		out.writeObject(Assembly);
		if(NameSpace!=null)
			out.writeUTF(NameSpace);
		else
			out.writeUTF("");
		out.writeUTF(AssemblyQualifiedName);
		out.writeObject(Module);
		out.writeUTF(UnderlyingTypeString);
			
		if(BaseTypeString!=null)
			out.writeUTF(BaseTypeString);
		else
			out.writeUTF("");

		out.writeUTF(fromDLL);
		out.writeObject(annotationInstances);
	}
}
