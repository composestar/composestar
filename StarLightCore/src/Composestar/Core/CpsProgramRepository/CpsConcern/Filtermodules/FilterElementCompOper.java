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

import Composestar.Core.RepositoryImplementation.*;

/**
 * the composition operator between two filterelements, currently this is always
 * the ',' (or the void operator for the last filter element)
 */
public abstract class FilterElementCompOper extends ContextRepositoryEntity {
  public FilterElement rightArgument;


  /**
   * @roseuid 404C4B6B0045
   */
  public FilterElementCompOper() {
    super();
  }


  /**
   * @param rightArgumentValue
   * @roseuid 402AA867024A
   */
  public void setRightArgument(FilterElement rightArgumentValue) {
    this.rightArgument = rightArgumentValue;
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
   *
   * @roseuid 402AA8CC0169
   */
  public FilterElement getRightArgument() {
    return rightArgument;
  }
}
