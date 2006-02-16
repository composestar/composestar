/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: MatchingPart.java,v 1.2 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;


public class MatchingPart extends AbstractPattern {
  public MatchingType matchType;


  /**
   * @roseuid 401FAA66001C
   */
  public MatchingPart() {
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
