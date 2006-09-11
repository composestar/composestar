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


public class MatchingPart extends AbstractPattern {
  //public MatchingType matchType; -> get from MatchingPartAST
  public MatchingPartAST mpa;

  /**
   * @roseuid 401FAA66001C
   * @deprecated
   */
  public MatchingPart() {
    super();
  }
  
  public MatchingPart(MatchingPartAST abstr) {
	super();
	mpa = abstr;
	target = mpa.getTarget();
	selector = new MessageSelector(mpa.getSelector());
  }

  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingType
   *
   * @roseuid 401FAA66001D
   */
  public MatchingType getMatchType() {
    return mpa.getMatchType();
  }


  /**
   * @param matchTypeValue
   * @roseuid 401FAA660030
   */
  public void setMatchType(MatchingType matchTypeValue) {
    mpa.setMatchType(matchTypeValue);
  }

public void Pattern() {
	mpa.Pattern();
}

public MatchingPartAST getMpa() {
	return mpa;
}
}
