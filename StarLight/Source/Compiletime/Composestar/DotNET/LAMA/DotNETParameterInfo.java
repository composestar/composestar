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

import Composestar.Core.LAMA.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Corresponds to the ParameterInfo class in the .NET framework. For more 
 * information on the methods and their meaning please refer to the microsoft 
 * documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemreflectionparameterinfoclasstopic.asp
 */
public class DotNETParameterInfo extends ParameterInfo {
    /**
	 * 
	 */
	private static final long serialVersionUID = -527486572514730318L;
	private int HashCode;
    public int Position;
    private boolean IsIn;    
    private boolean IsOptional;
    private boolean IsOut;
    private boolean IsRetval;
    
    /**
     * @roseuid 401B84CF0220
     */
    public DotNETParameterInfo() 
	{      
		super();
	}
    
    /**
     * @return boolean
     * @roseuid 401B84CF0217
     */
    public boolean isIn() {
        return IsIn;     
    }
    
    /**
     * @param isln
     * @roseuid 402A06EE014F
     */
    public void setIsln(boolean isln) {
        IsIn = isln;     
    }
    
  
    
    /**
     * @return boolean
     * @roseuid 401B84CF0219
     */
    public boolean isOptional() {
        return IsOptional;     
    }
    
    /**
     * @param isOptional
     * @roseuid 402A070A020D
     */
    public void setIsOptional(boolean isOptional) {
        IsOptional = isOptional;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF021A
     */
    public boolean isOut() {
        return IsOut;     
    }
    
    /**
     * @param isOut
     * @roseuid 402A07140077
     */
    public void setIsOut(boolean isOut) {
        IsOut = isOut;     
    }
    
    /**
     * @return boolean
     * @roseuid 401B84CF021B
     */
    public boolean isRetval() {
        return IsRetval;     
    }
    
    /**
     * @param isRetval
     * @roseuid 402A071C000A
     */
    public void setIsRetVal(boolean isRetval) {
        IsRetval = isRetval;     
    }
    
    /**
     * @return int
     * @roseuid 401B84CF021E
     */
    public int position() {
        return Position;     
    }
    
    /**
     * @param pos
     * @roseuid 402A074A0061
     */
    public void setPosition(int pos) {
        Position = pos;     
    }
    
    /**
     * @return int
     * @roseuid 401B84CF021F
     */
    public int getHashCode() {
        return HashCode;     
    }
    
    /**
     * @param code
     * @roseuid 402A075602A3
     */
    public void setHashCode(int code) {
        HashCode = code;     
    }

    
    /** Stuff for LOLA **/  
    
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
          res.add(((Annotation)i.next()).getType());
        return new UnitResult(res);
      }
      
      return null;
    }

    /* (non-Javadoc)
     * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
     */
    public Collection getUnitAttributes()
    {
      return new HashSet();
    }   
 
	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		HashCode = in.readInt();
		Position = in.readInt();
		IsIn = in.readBoolean();
		IsOptional = in.readBoolean();
		IsOut = in.readBoolean();
		IsRetval = in.readBoolean();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeInt(Position);
		out.writeBoolean(IsIn);
		out.writeBoolean(IsOptional);
		out.writeBoolean(IsOut);
		out.writeBoolean(IsRetval);
	}
}
