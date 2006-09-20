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

import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SelectorReference extends ConcernElementReference 
{
  private SelectorDefinition ref;


  /**
   * @roseuid 401FAA68017D
   */
  public SelectorReference() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDef
   *         inition
   *
   * @roseuid 401FAA68017E
   */
  public SelectorDefinition getRef() {
    return ref;
  }


  /**
   * @param refValue
   * @roseuid 40503C3500F4
   */
  public void setRef(SelectorDefinition refValue) {
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
