/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: BooleanOclExpression.java,v 1.2 2006/02/16 12:51:20 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;

/**
 * @modelguid {298702D8-779F-4FE0-B6A7-6417F2AEAAC1}
 */
public class BooleanOclExpression extends ContextRepositoryEntity {

  /**
   * @modelguid {ADCB919B-FD9B-4B52-9ABE-B132818A98DC}
   */
  private String source;


  /**
   * @modelguid {F96261F0-D84A-4456-8CDF-F5A9502D9E2C}
   * @roseuid 401FAA56033C
   */
  public BooleanOclExpression() {
    super();
  }


  /**
   * @return java.lang.String
   *
   * @modelguid {CC85EBB0-12E2-4C02-8822-594CEE27A65C}
   * @roseuid 401FAA560344
   */
  public String getSource() {
    return source;
  }


  /**
   * @param sourceValue
   * @modelguid {9B1EF1E0-50EA-44B2-B0ED-50A97A0DEDEB}
   * @roseuid 401FAA560345
   */
  public void setSource(String sourceValue) {
    this.source = sourceValue;
  }
}
