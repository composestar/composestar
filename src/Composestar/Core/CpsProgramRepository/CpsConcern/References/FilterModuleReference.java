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

import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;

/**
 * reference to a filtermodule
 */
public class FilterModuleReference extends ConcernElementReference {
  private FilterModule ref;
  private Vector args;

  /**
   * @roseuid 401FAA640376
   */
  public FilterModuleReference() {
    super();
    args = new Vector();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule
   *
   * @roseuid 401FAA640377
   */
  public FilterModule getRef() {
    return ref;
  }


  /**
   * @param refValue
   * @roseuid 40503C2401EE
   */
  public void setRef(FilterModule refValue) {
    this.ref = refValue;
  }
  
  public void setArgs(Vector a){
	  args = a;
  }
  
  public Vector getArgs(){
	  return args;
  }
  
  public void addArg(Object o){
	  args.add(o);
  }
}
