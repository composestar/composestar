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
  public final static String WAIT = "Wait";
  public final static String DISPATCH = "Dispatch";
  public final static String ERROR = "Error";
  public final static String META = "Meta";
  public final static String SUBSTITUTION = "Substitution";
  public final static String CUSTOM = "Custom";
  public final static String SEND	  = "Send";
  public final static String PREPEND = "Prepend";
  public final static String APPEND  = "Append";

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
