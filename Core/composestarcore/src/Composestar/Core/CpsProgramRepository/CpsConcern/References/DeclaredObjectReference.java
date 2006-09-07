/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DeclaredObjectReference.java,v 1.2 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

import Composestar.Core.RepositoryImplementation.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This is used to refer to an internal or external variable; the name will be
 * specified, and after searching the scope (including bindings), a reference to
 * the (typed) declaration of the internal or external object is obtained.
 */
public class DeclaredObjectReference extends FilterModuleElementReference 
{
  private TypedDeclaration ref;


  /**
   * @roseuid 402CBC860172
   */
  public DeclaredObjectReference() {
    super();
  }


  /**
   * @return Composestar.Core.RepositoryImplementation.TypedDeclaration
   *
   * @roseuid 402CBC8D01C2
   */
  public TypedDeclaration getRef() {
    return ref;
  }


  /**
   * @param refValue
   * @roseuid 40503C9303A4
   */
  public void setRef(TypedDeclaration refValue) {
    this.ref = refValue;
  }

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		// nothing yet
	}
	
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		// nothing yet
	}
}
