/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Binding.java,v 1.2 2006/02/16 12:51:22 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;

public class Binding extends ContextRepositoryEntity {
  public SelectorReference selector;


  /**
   * @roseuid 401FAA5602B8
   */
  public Binding() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReferenc
   *         e
   *
   * @roseuid 401FAA5602B9
   */
  public SelectorReference getSelector() {
    return selector;
  }


  /**
   * @param selectorValue
   * @roseuid 401FAA5602C2
   */
  public void setSelector(SelectorReference selectorValue) {
    this.selector = selectorValue;
  }
}
