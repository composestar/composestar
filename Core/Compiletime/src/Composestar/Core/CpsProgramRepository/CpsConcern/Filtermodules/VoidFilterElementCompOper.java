/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: VoidFilterElementCompOper.java,v 1.2 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;


/**
 * used as the rightHandOperator of the last FilterElement in a filter.
 */
public class VoidFilterElementCompOper extends FilterElementCompOper {

  /**
   * @roseuid 404C4B6B00F9
   */
  public VoidFilterElementCompOper() {
    super();
  }


  /**
   * raise exception (should not be invoked on this element)
   *
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
   *
   * @roseuid 402AA8640295
   */
  public FilterElement getRightArgument() {
    return null;
  }


  /**
   * raise exception (should not be invoked on this element)
   *
   * @param element
   * @roseuid 402AA8CF0362
   */
  public void setRightArgument(FilterElement element) {

  }
}
