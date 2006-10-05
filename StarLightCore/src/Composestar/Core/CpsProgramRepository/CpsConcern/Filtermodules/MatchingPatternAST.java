/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: MatchingPatternAST.java
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Vector;
import Composestar.Utils.*;

import Composestar.Core.RepositoryImplementation.*;

public class MatchingPatternAST extends ContextRepositoryEntity {
	
  public Vector matchingParts;
  public Vector substitutionParts;


  /**
   * @roseuid 404C4B6B0167
   */
  public MatchingPatternAST() {
    super();
	matchingParts = new Vector();
	substitutionParts = new Vector();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart
   *
   * @roseuid 401FAA65006B
   */
  public Vector getMatchingParts() {
   return matchingParts;
  }

  public java.util.Iterator getMatchingPartsIterator() {
	return new CPSIterator( matchingParts );
  }


  /**
   * @param matchingPartValue
   * @roseuid 401FAA65007E
   */
  public void addMatchingPart(MatchingPartAST matchingPartValue) {
    this.matchingParts.addElement( matchingPartValue );
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionP
   *         art
   *
   * @roseuid 401FAA65008A
   */
  public Vector getSubstitutionParts() {
    return substitutionParts;
  }

  public java.util.Iterator getSubstitutionPartsIterator() {
    return new CPSIterator( substitutionParts );
  }


  /**
   * @param substitutionPartValue
   * @roseuid 401FAA65009D
   */
  public void addSubstitutionPart(SubstitutionPartAST substitutionPartValue) {
    this.substitutionParts.addElement( substitutionPartValue );
  }
}