/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Utils.*;

import java.util.*;

public class StarCondition extends FilterModuleElementReference {

  /**
   * contains conditionReferences
   */
  private Vector conditionSet;


  /**
   * contains conditionReferences
   *
   * @roseuid 401FAA680354
   */
  public StarCondition() {
    super();
    conditionSet = new Vector();
  }


  /**
   * conditionSet
   *
   * @param c
   * @return boolean
   *
   * @roseuid 401FAA680355
   */
  public boolean addCondition(ConditionReference c) {
    conditionSet.addElement(c);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConditionReferen
   *         ce
   *
   * @roseuid 401FAA680369
   */
  public ConditionReference removeCondition(int index) {
    Object o = conditionSet.elementAt(index);
    conditionSet.removeElementAt(index);
    return ((ConditionReference) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConditionReferen
   *         ce
   *
   * @roseuid 401FAA680373
   */
  public ConditionReference getCondition(int index) {
    return ((ConditionReference) conditionSet.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @roseuid 401FAA680387
   */
  public Iterator getConditionIterator() {
    return (new CPSIterator(conditionSet));
  }
}
