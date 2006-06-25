/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: FilterType.java,v 1.1 2006/02/16 23:03:50 pascal_durr Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.CpsProgramRepository.*;

/**
 * @modelguid {FAFD0EAB-6BB2-4634-835C-74619506D1A1}
 * This is a wrapper for the implementation of a filter; here, specific properties
 * of a filter type (such as required by FIRE and SECRET) can be added. The
 * implementations of the predefined filters will be PrimitiveConcerns; this may
 * also be user-defined filter types that are implemented as a ComposeStarConcern.
 * In the future, add specific attributes
 * here that apply to all filter types, create subclasses on demand.
 * we might or might not be able to reason about the implementation here
 * (depending on whether it is a CpsConcern or PrimitiveConcern).
 */
public class FilterType extends Concern {
  public String type;
  public static String WAIT = "Wait";
  public static String DISPATCH = "Dispatch";
  public static String ERROR = "Error";
  public static String META = "Meta";
  public static String SUBSTITUTION = "Substitution";
  public static String CUSTOM = "Custom";
  public static String SEND	  = "Send";
  public static String PREPEND = "Prepend";
  public static String APPEND  = "Append";

  /**
   * @modelguid {6E580DFE-8DED-49DC-A73D-EFAAE97C4827}
   * @roseuid 401FAA650205
   */
  public FilterType() 
  {
    super();
  }


  /**
   * @return java.lang.String
   *
   * @roseuid 401FAA650206
   */
  public String getType() {
    return type;
  }


  /**
   * @param typeValue
   * @roseuid 401FAA65020F
   */
  public void setType(String typeValue) {
    this.type = typeValue;
  }
}
