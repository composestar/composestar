/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DotNETType.java,v 1.1 2006/02/16 23:10:59 pascal_durr Exp $
 */

package Composestar.C.LAMA;

import Composestar.Core.LAMA.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Corresponds to the Type class in the .NET framework. For more information on 
 * the methods and their meaning please refer to the microsoft documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemtypeclasstopic.asp
 * note that the name may be redundant, but should be consistent with the name of 
 * the concern.
 */
public class CFile extends Type 
{
    /**
    * File in C and as a hack this is also used as a annotation
    */
    private static final long serialVersionUID = 5652622506200113401L;
    public int HashCode;
    public boolean isAnnotation;
   
    /**
     * @roseuid 4028E9FF026C
     */
    public CFile()
    {
    	super();
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
    
    public void addMethod(CMethodInfo method) {
        Methods.add( method );
        method.setParent(this);     
    }
    
    public void addField(CVariable field) {
      Fields.add(field);
      field.setParent(this);
    }
    
    public void setName(String Name){
    	this.Name=Name;
    }
    
    public boolean isAnnotation(){
    	return isAnnotation;
    }
    
    public void setAnnotation(boolean isAnnotation){
    	this.isAnnotation=isAnnotation;
    }
    
    
    /****** Implementation of Language Unit interface **********/
    
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
          out.add(obj);
          //System.out.println(((ProgramElement)obj).getUnitName());
      }
      return out;
    }
   
    public UnitResult getUnitRelation(String argumentName)
    {
    	if (getUnitType().equals("Class")){
    		if (argumentName.equals("ChildMethods"))
    			return new UnitResult(filterDeclaredHere(Methods));
    		else if (argumentName.equals("ChildFields"))
    			return new UnitResult(filterDeclaredHere(Fields));
    	}
    	else if(getUnitType().equals("Annotation"))
    	{
    		HashSet resMethods = new HashSet();
    		Iterator i = getAnnotationInstances().iterator();
    		while (i.hasNext())
    		{
    		   ProgramElement unit = ((CAnnotation)i.next()).getTarget();
    		   if (unit instanceof CMethodInfo)
    			      resMethods.add(unit);
    		}
    		if (argumentName.equals("AttachedMethods"))
    		    return new UnitResult(resMethods);
    		//System.out.println("Annotation found it works quit good in CFILE");
    	}
    	return null; // Should never happen!
    }
  
    public String getUnitType()
    {
    	if (isAnnotation())
            return "Annotation";
    	return "Class";
    }

    public Collection getUnitAttributes()
    {
      HashSet result = new HashSet();
      result.add("public");
      return result;
    }	
    
    
  	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		HashCode = in.readInt();
		Methods = (ArrayList)in.readObject();
		Fields = (ArrayList)in.readObject();
		isAnnotation = in.readBoolean();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeObject(Methods);
		out.writeObject(Fields);
		out.writeBoolean(isAnnotation);
	}
}
