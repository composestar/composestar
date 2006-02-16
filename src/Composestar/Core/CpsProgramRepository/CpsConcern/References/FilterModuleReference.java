/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: FilterModuleReference.java,v 1.2 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;

/**
 * reference to a filtermodule
 */
public class FilterModuleReference extends ConcernElementReference {
  private FilterModule ref;


  /**
   * @roseuid 401FAA640376
   */
  public FilterModuleReference() {
    super();
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
}
