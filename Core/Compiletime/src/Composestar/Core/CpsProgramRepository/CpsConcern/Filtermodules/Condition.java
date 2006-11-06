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
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;

/**
 * @modelguid {BBEF58A4-13F3-4D33-8CAA-F77A11B5E453}
 */
public class Condition extends DeclaredRepositoryEntity {

  /**
   * @modelguid {87B0B644-6B80-4544-8BCA-9E2AE2244340}
   */
  //public String name; // declared in parent class
  public BooleanOclExpression ocl;
  public Reference shortref;
  public FilterModuleElementReference longref;



  /**
   * @modelguid {53F220EC-C531-4B22-923B-0A7C997FDF41}
   * @roseuid 401FAA570309
   */
  public Condition() {
    super();
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BooleanOclExp
   *         ression
   *
   * @modelguid {234C75EC-E80B-43EF-9FF3-93DE2332FA23}
   * @roseuid 401FAA57031E
   */
  public BooleanOclExpression getOCL() {
    return ocl;
  }


  /**
   * @param oCL
   * @modelguid {3CC7CDC6-7FA8-4EA3-9DD6-82740C1E1411}
   * @roseuid 401FAA570327
   */
  public void setOCL(BooleanOclExpression inocl) {
    this.ocl = inocl;
  }


  public Reference getShortref() {
    return shortref;
  }


  public void setShortref(Reference shortref) {
    this.shortref = shortref;
  }


  public void setLongref(FilterModuleElementReference longref) {
    this.longref = longref;
  }


  public FilterModuleElementReference getLongref() {
    return longref;
  }
}
