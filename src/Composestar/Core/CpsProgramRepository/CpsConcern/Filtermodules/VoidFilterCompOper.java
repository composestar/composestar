/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;


public class VoidFilterCompOper extends FilterCompOper {

  /**
   * @roseuid 404C4B6B009F
   */
  public VoidFilterCompOper() {
    super();
  }


  /**
   * raise exception (should not be invoked on this element)
   *
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
   *
   * @roseuid 402AA90E0222
   */
  public Filter getRightArgument() {
    return null;
  }


  /**
   * raise exception (should not be invoked on this element)
   *
   * @param filter
   * @roseuid 402AA91102D0
   */
  public void setRightArgument(Filter filter) {

  }
}
