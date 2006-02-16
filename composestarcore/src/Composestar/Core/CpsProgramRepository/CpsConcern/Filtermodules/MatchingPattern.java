/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: TypedInstance.java,v 1.3 2004/02/02 19:33:23 vinkes Exp
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;

public class MatchingPattern extends ContextRepositoryEntity {
  public MatchingPart matchingPart;
  public SubstitutionPart substitutionPart;


  /**
   * @roseuid 404C4B6B0167
   */
  public MatchingPattern() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart
   *
   * @roseuid 401FAA65006B
   */
  public MatchingPart getMatchingPart() {
    return matchingPart;
  }


  /**
   * @param matchingPartValue
   * @roseuid 401FAA65007E
   */
  public void setMatchingPart(MatchingPart matchingPartValue) {
    this.matchingPart = matchingPartValue;
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionP
   *         art
   *
   * @roseuid 401FAA65008A
   */
  public SubstitutionPart getSubstitutionPart() {
    return substitutionPart;
  }


  /**
   * @param substitutionPartValue
   * @roseuid 401FAA65009D
   */
  public void setSubstitutionPart(SubstitutionPart substitutionPartValue) {
    this.substitutionPart = substitutionPartValue;
  }
}
