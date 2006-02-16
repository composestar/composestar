/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: FilterCompOper.java,v 1.2 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;

/**
 * the composition operator between two filters, currently this is always the ';'
 * (or the VoidFilterElementOperator for the last filter element)
 */
public abstract class FilterCompOper extends ContextRepositoryEntity {
  public Filter rightArgument;


  /**
   * @roseuid 404C4B6A03D3
   */
  public FilterCompOper() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
   *
   * @roseuid 402AA817028B
   */
  public Filter getRightArgument() {
    return rightArgument;
  }


  /**
   * @param rightArgumentValue
   * @roseuid 402AA822004C
   */
  public void setRightArgument(Filter rightArgumentValue) {
    this.rightArgument = rightArgumentValue;
  }
}
