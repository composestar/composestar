/*
 * DotNETFieldInfo.java - Created on 18-okt-2004 by havingaw
 *
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 */

package Composestar.C.LAMA;

import Composestar.Core.LAMA.*;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;
import Composestar.Core.LAMA.FieldInfo;

import java.util.Collection;
import java.util.HashSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author havingaw
 *
 * Contains the .Net reflection information of a Field
 * See documentation of .NET System.Reflection.FieldInfo (on MSDN).
 */

public class CVariable extends FieldInfo implements SerializableRepositoryEntity
{
  
  private static final long serialVersionUID = 235924601234730641L;
  public int HashCode;                
  public String FieldTypeString;      
  private Type FieldType;       
  private CFile Parent;
  public boolean IsStatic;   
  private boolean isPointer;
  private boolean isGlobal;
    
  public CVariable()
  {
    UnitRegister.instance().registerLanguageUnit(this);    
  }
  
  public int getHashCode()
  {
    return this.HashCode;
  }
  
  public void setHashCode(int hashcode)
  {
    this.HashCode = hashcode;
  }
  
  public Type fieldType()
  {
   if (this.FieldType == null)
    { // We don't do this in setFieldType because at that point
      // the DotNETType of the field may not be registered yet (!)
      TypeMap map = TypeMap.instance();
      this.FieldType = (Type)map.getType( FieldTypeString );
    }
    return FieldType;
  }
  
  public void setFieldType(String fieldtype)
  {
    this.FieldTypeString = fieldtype;
  }
  
  public boolean isStatic()
  {
    return this.IsStatic;     
  }
  
  public void setIsStatic(boolean isStatic)
  {
     this.IsStatic = isStatic;     
  }
  
  public boolean isPointer(){
	  return this.isPointer;
  }
  
  public void setIsPointer(boolean isPointer){
	  this.isPointer=isPointer;
  }
  
  public boolean isGlobal(){
	  return this.isGlobal;
  }
  
  public void setIsGlobal(boolean isGlobal){
	  this.isGlobal=isGlobal;
  }
  
  public String name()
  {
    return this.Name;
  }
  
  public void setName(String name)
  {
    this.Name = name;
  }

  public void setParent(CFile parent)
  {
    this.Parent = parent;
  }
  
  //public CFile parent()
  //{
  //  return this.Parent;
  //}

  /* (non-Javadoc)
   * @see Composestar.CTCommon.LogicLang.metamodel.LanguageUnit#getUnitRelation(java.lang.String)
   */
  public UnitResult getUnitRelation(String argumentName)
  {
    if ("ParentType".equals(argumentName))
      return new UnitResult(Parent);
    else if ("Class".equals(argumentName) && "Class".equals(fieldType().getUnitType()))
      return new UnitResult(fieldType());
    return null;
  }

  public Collection getUnitAttributes()
  {
    HashSet result = new HashSet();
    if (isStatic())
      result.add("static");
    return result;
  }

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		HashCode = in.readInt();
		FieldTypeString = in.readUTF();
		IsStatic = in.readBoolean();
		isGlobal = in.readBoolean();
		isPointer = in.readBoolean();
		Name = in.readUTF();
		Parent = (CFile)in.readObject();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeUTF(FieldTypeString);
		out.writeBoolean(IsStatic);
		out.writeBoolean(isGlobal);
		out.writeBoolean(isPointer);
		out.writeUTF(Name);
		out.writeObject(Parent);
	}
}
