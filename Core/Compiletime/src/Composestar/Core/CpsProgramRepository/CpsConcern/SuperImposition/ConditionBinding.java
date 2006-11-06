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

/**
 * @modelguid {0BCB4921-FE88-4B0F-AB82-7D9BBACFC29B}
 */
public class ConditionBinding extends Binding {

  /**
   * ConditionReferences and StarConditions
   */
  private Vector conditionSet;


  /**
   * ConditionReferences and StarConditions
   *
   * @modelguid {69D8E044-0E49-462F-833F-1B923754E373}
   * @roseuid 401FAA5703B6
   */
  public ConditionBinding() {
    super();
    conditionSet = new Vector();
  }


  /**
   * conditionSet
   *
   * @param c
   * @return boolean
   *
   * @modelguid {DA47F6E4-66F9-4B9C-951F-374194250ED1}
   * @roseuid 401FAA5703BE
   */
  public boolean addCondition(ConditionReference c) {
    conditionSet.addElement(c);
    return true;
  }


  /**
   * @param c
   * @return boolean
   *
   * @roseuid 401FAA5703DD
   */
  public boolean addCondition(StarCondition c) {
    conditionSet.addElement(c);
    return true;
  }


  /**
   * @param index
   * @return java.lang.Object
   *
   * @modelguid {6C475B1E-2519-43A8-80A7-5DB14D803AAB}
   * @roseuid 401FAA580013
   */
  public Object removeCondition(int index) { //fixme: could be star or single object
    Object o = conditionSet.elementAt(index);
    conditionSet.removeElementAt(index);
    return o;
  }


  /**
   * @param index
   * @return java.lang.Object
   *
   * @modelguid {92EF76B9-DC2E-432E-9288-BEDB552A94CC}
   * @roseuid 401FAA58001C
   */
  public Object getCondition(int index) { //fixme: could be star or single object
    return conditionSet.elementAt(index);
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {6D02CEA1-40B8-489A-A401-EA2A9C60F633}
   * @roseuid 401FAA58003A
   */
  public Iterator getConditionIterator() {
    return new CPSIterator(conditionSet);
  }


  /**
   * @roseuid 4023C2400133
   */
  public void resolveStar() {

  }
}
