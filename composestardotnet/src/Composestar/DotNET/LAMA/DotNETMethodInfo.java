/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DotNETMethodInfo.java,v 1.3 2006/02/16 12:58:05 composer Exp $
 */

//
package Composestar.DotNET.LAMA;

import Composestar.Core.LAMA.*;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Corresponds to the MethodInfo class in the .NET framework. For more information 
 * on the methods and their meaning please refer to the microsoft documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemreflectionmethodinfoclasstopic.asp
 */
public class DotNETMethodInfo extends MethodInfo /*implements SerializableRepositoryEntity, LanguageUnit*/ 
{
    /**
	 * 
	 */
	public int CallingConvention;
    private int HashCode;
    // moved to supertype Method: public String Name;
    
    //moved to supertype Method: public String ReturnTypeString;
    //moved to supertype Method: private DotNETType ReturnType;
    //moved to supertype Method: private ArrayList Parameters;
    //moved to supertype Method: private DotNETType Parent;
    
    /**
     * @roseuid 401B84CF0212
     */
    public DotNETMethodInfo() {
       
    	//moved to supertype Method
    	//Parameters = new ArrayList();
    	
    	UnitRegister.instance().registerLanguageUnit(this);       
    }
    
    /**
     * @return int
     * @roseuid 401B84CF0201
     */
    public int callingConvention() {
		return CallingConvention;     
    }
    
    /**
     * @param cv
     * @roseuid 402A018403CD
     */
    public void setCallingConvention(int cv) {
       CallingConvention = cv;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF0202
     */
    public boolean isAbstract() {
    return IsAbstract;     
    }
    
    /**
     * @param isAbstract
     * @roseuid 402A0195034F
     */
    public void setIsAbstract(boolean isAbstract) {
       IsAbstract = isAbstract;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF0203
     */
    public boolean isAssembly() {
       return IsAssembly;     
    }
    
    /**
     * @param isAssembly
     * @roseuid 402A01AC00F9
     */
    public void setIsAssembly(boolean isAssembly) {
       IsAssembly = isAssembly;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF0204
     */
    public boolean isConstructor() {
    return IsConstructor;     
    }
    
    /**
     * @param isConstructor
     * @roseuid 402A01BB026D
     */
    public void setIsConstructor(boolean isConstructor) {
       IsConstructor = isConstructor;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF0205
     */
    public boolean isFamily() {
    return IsFamily;     
    }
    
    /**
     * @param isFamily
     * @roseuid 402A01CB018A
     */
    public void setIsFamily(boolean isFamily) {
       IsFamily = isFamily;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF0206
     */
    public boolean isFamilyAndAssembly() {
    return IsFamilyAndAssembly;     
    }
    
    /**
     * @param isFamAndAsm
     * @roseuid 402A01D70000
     */
    public void setIsFamilyAndAssembly(boolean isFamAndAsm) {
       IsFamilyAndAssembly = isFamAndAsm;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF0207
     */
    public boolean isFamilyOrAssembly() {
    return IsFamilyOrAssembly;     
    }
    
    /**
     * @param isFamOrAsm
     * @roseuid 402A01EF0164
     */
    public void setIsFamilyOrAssembly(boolean isFamOrAsm) {
       IsFamilyOrAssembly = isFamOrAsm;     
    }
    

    
    /**
     * @return boolean
     * @roseuid 401B84CF0209
     */
    public boolean isHideBySig() {
    return IsHideBySig;     
    }
    
    /**
     * @param isHide
     * @roseuid 402A0213006B
     */
    public void setIsHideBySig(boolean isHide) {
       IsHideBySig = isHide;     
    }

    
    /**
     * @return java.lang.String
     * @roseuid 401B84CF020E
     */
    //moved to supertype Method
    /*public String name() {
    return Name;     
    }*/
    
    /**
     * @param name
     * @roseuid 402A028601CF
     */
    
    //moved to supertype Method
    /*public void setName(String name) {
    		Name = name;     
    }*/
    
    /**
     * @return Composestar.CTAdaption.TYM.TypeCollector.DotNETTypes.DotNETType
     * @roseuid 401B84CF020F
     */
    //moved to supertype MethodInfo
    /*public DotNETType returnType() {
        if( ReturnType == null ) {
            TypeMap map = TypeMap.instance();
			ReturnType = map.getType( ReturnTypeString );
        }
        return ReturnType;     
    }*/
    
    /**
     * @param type
     * @roseuid 402A02D60328
     */
    public void setReturnType(String type) {
       ReturnTypeString = type;     
    }
    
    /**
     * @return int
     * @roseuid 401B84CF0210
     */
    public int getHashCode() {
    return HashCode;     
    }
    
    /**
     * @param code
     * @roseuid 402A0319000E
     */
    public void setHashCode(int code) {
       HashCode = code;     
    }
    
    /**
     * @return java.util.List
     * @roseuid 401B84CF0211
     */
    //moved to supertype Parameters
    /*public List getParameters() {
    	return Parameters;     
    }*/
    
    /**
     * @param param
     * @roseuid 402A032A02F7
     */
    public void addParameter(DotNETParameterInfo param) {
       Parameters.add( param );
       //param.setParent(this);
    }
    
    /**
     * @param types Check if the methods has these types
     * @return true if there is a signature match. False otherwise
     * @roseuid 402C9CE401C5
     */
    //moved to supertype Method
    /*public boolean hasParameters(String[] types) {
     return true;
    }*/
    
    /**
     * @return Composestar.CTAdaption.TYM.TypeCollector.DotNETTypes.DotNETType
     * @roseuid 4050689303BD
     */
    //moved to supertype Method
    /*public DotNETType parent() {
     	return Parent;     
    }*/
    
    /**
     * @param parent
     * @roseuid 405068A401B0
     */
    public void setParent(DotNETType parent) {
     	Parent = parent;     
    }
    
    /**
     * This class represents the CallingConventions Enum in the .NET framework.
     */
    
	/*
	public class DotNETCallingConventions {
        public static final int Standard = 1;
        public static final int VarArgs = 2;
        public static final int Any = 3;
        public static final int HasThis = 32;
        public static final int ExplicitThis = 64;
        
        /**
         * @roseuid 402A33A201F8
         */
        /*
		public DotNETCallingConventions() {
         
        }
    }
	*/


    
    /* (non-Javadoc)
     * @see Composestar.CTCommon.LogicLang.metamodel.LanguageUnit#getUnitName()
     */
    
    //moved to supertype MethodInfo
    /*public String getUnitName()
    {
      return Name;
    }*/

    /* (non-Javadoc)
     * @see Composestar.CTCommon.LOLA.metamodel.LanguageUnit#getUnitType()
     */
    //moved to supertype MethodInfo
    /*public String getUnitType()
    {
      return "Method";
    }*/    
	
    public boolean isDeclaredHere()
    {
      return IsDeclaredHere;
    }

    public void setIsDeclaredHere(boolean declaredHere)
    {
      IsDeclaredHere = declaredHere;
    }

    /* (non-Javadoc)
     * @see Composestar.CTCommon.LOLA.metamodel.LanguageUnit#hasAttribute(java.lang.String)
     */
    //moved to supertype MethodInfo
    /*public boolean hasUnitAttribute(String attribute)
    {
      return getUnitAttributes().contains(attribute);
    }*/

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		CallingConvention = in.readInt();
		HashCode = in.readInt();
		IsAbstract = in.readBoolean();
		IsAssembly = in.readBoolean();
		IsConstructor = in.readBoolean();
		IsFamily = in.readBoolean();
		IsFamilyAndAssembly = in.readBoolean();
		IsFamilyOrAssembly = in.readBoolean();
		IsFinal = in.readBoolean();
		IsHideBySig = in.readBoolean();
		IsPrivate = in.readBoolean();
		IsPublic = in.readBoolean();
		IsStatic = in.readBoolean();
		IsVirtual = in.readBoolean();
		IsDeclaredHere = in.readBoolean();
		Name = in.readUTF();
		ReturnTypeString = in.readUTF();
		Parameters = (ArrayList)in.readObject();   
		Parent = (DotNETType)in.readObject();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(CallingConvention);
		out.writeInt(HashCode);
		out.writeBoolean(IsAbstract);
		out.writeBoolean(IsAssembly);
		out.writeBoolean(IsConstructor);
		out.writeBoolean(IsFamily);
		out.writeBoolean(IsFamilyAndAssembly);
		out.writeBoolean(IsFamilyOrAssembly);
		out.writeBoolean(IsFinal);
		out.writeBoolean(IsHideBySig);
		out.writeBoolean(IsPrivate);
		out.writeBoolean(IsPublic);
		out.writeBoolean(IsStatic);
		out.writeBoolean(IsVirtual);
		out.writeBoolean(IsDeclaredHere);
		out.writeUTF(Name);
		out.writeUTF(ReturnTypeString);
		out.writeObject(Parameters);
		out.writeObject(Parent);
	}
}
