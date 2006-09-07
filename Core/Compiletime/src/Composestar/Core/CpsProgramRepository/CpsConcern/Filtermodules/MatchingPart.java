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

public MessageSelector getSelector() {
	return mpa.getSelector();
}

public Target getTarget() {
	return mpa.getTarget();
}

public void Pattern() {
	mpa.Pattern();
}

public void setSelector(MessageSelector selectorValue) {
	mpa.setSelector(selectorValue);
}

public void setTarget(Target targetValue) {
	mpa.setTarget(targetValue);
}

public MatchingPartAST getMpa() {
	return mpa;
}
}
