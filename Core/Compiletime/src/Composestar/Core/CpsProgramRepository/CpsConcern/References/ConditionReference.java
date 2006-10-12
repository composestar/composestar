/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * reference to a condition
 */
public class ConditionReference extends FilterModuleElementReference 
{
  private Condition ref;


  /**
   * @roseuid 401FAA580170
   */
  public ConditionReference() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
   *
   * @roseuid 401FAA580171
   */
  public Condition getRef() {
  	return ref;
  }


  /**
   * @param refValue
   * @roseuid 40503CA002BA
   */
  public void setRef(Condition refValue) {
    this.ref = refValue;
  }

	/**
	 * Custom deserialization of this object
     * @param in
     */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		this.ref = (Condition)in.readObject();
	}
	
	/**
	 * Custom serialization of this object
     * @param out
     */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(this.ref);
	} 
}
