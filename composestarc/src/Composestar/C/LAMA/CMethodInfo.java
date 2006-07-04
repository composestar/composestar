/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CMethodInfo.java,v 1.1 2006/03/16 14:08:54 johantewinkel Exp $
 */

//
package Composestar.C.LAMA;

import Composestar.Core.LAMA.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Corresponds to the MethodInfo class in the .NET framework. For more information 
 * on the methods and their meaning please refer to the microsoft documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemreflectionmethodinfoclasstopic.asp
 */
public class CMethodInfo extends MethodInfo /*implements SerializableRepositoryEntity, LanguageUnit*/ 
{
    
	private static final long serialVersionUID = 5652622506200113201L;
    public int CallingConvention;
    private int HashCode;
    
    /**@johan
    *Elements of Class Function of TreeParser from WeaveC
    *private String name = null;
    *
    *private ParameterType retParameter = null;
    *private Vector returnPoints = new Vector(); // vector with ReturnPoints
    *private Vector inParameters = new Vector(); // vector with Parameters
    *private TNode toInsertBefore = null; // node113 of a function
    *public TNode mainNode = null;
    *private boolean bVoid = false;
    *private boolean noInputParameters = false;
    *
    */
    
    
    /**
     * @roseuid 401B84CF0212
     */
    public CMethodInfo() {   
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
    
    public MethodInfo getClone(String n, Type actualParent){
    	CMethodInfo mi = new CMethodInfo();
    	mi.setName(n);
    	//set MethodInfo variables
    	//mi.Parent = this.Parent;
    	mi.Parent = actualParent;
    	mi.Parameters = this.Parameters;
    	mi.ReturnType = this.ReturnType;
    	mi.ReturnTypeString = this.ReturnTypeString;
      	mi.CallingConvention = this.CallingConvention;
    	return mi;
    }
    
    /**
     * @param code
     * @roseuid 402A0319000E
     */
    public void setHashCode(int code) {
       HashCode = code;     
    }
    
    /**
     * @param param
     * @roseuid 402A032A02F7
     */
    public void addParameter(ParameterInfo param) {
       Parameters.add( param );
    }
        
    /**
     * @param parent
     * @roseuid 405068A401B0
     */
    public void setParent(Type parent) {
     	Parent = parent;     
    }
    
    public UnitResult getUnitRelation(String argumentName)
    {
      if (argumentName.equals("ParentClass")) //moet parent file worden
        return new UnitResult(Parent);
      return null;
    }
    public Collection getUnitAttributes()
    {
      HashSet result = new HashSet();
      result.add("public");
      return result;
    }
    
    public void setName(String name){
    	this.Name = name;
    }

    /**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		CallingConvention = in.readInt();
		HashCode = in.readInt();
		Name = in.readUTF();
		ReturnTypeString = in.readUTF();
		Parameters = (ArrayList)in.readObject();   
		Parent = (Type)in.readObject();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(CallingConvention);
		out.writeInt(HashCode);	
		out.writeUTF(Name);
		out.writeUTF(ReturnTypeString);
		out.writeObject(Parameters);
		out.writeObject(Parent);
	}
}
