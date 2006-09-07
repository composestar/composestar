/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: MatchingPart.java,v 1.1 2006/02/16 23:03:50 pascal_durr Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;


public class MatchingPartAST extends AbstractPattern {
  public MatchingType matchType;


  /**
   * @roseuid 401FAA66001C
   */
  public MatchingPartAST() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingType
   *
   * @roseuid 401FAA66001D
   */
  public MatchingType getMatchType() {
    return matchType;
  }


  /**
   * @param matchTypeValue
   * @roseuid 401FAA660030
   */
  public void setMatchType(MatchingType matchTypeValue) {
    this.matchType = matchTypeValue;
  }
}
