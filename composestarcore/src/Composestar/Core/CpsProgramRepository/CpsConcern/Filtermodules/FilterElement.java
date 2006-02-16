/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: FilterElement.java,v 1.3 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Utils.*;

import java.util.*;

/**
 * @modelguid {5976EB5C-BFC4-46BF-995B-AD41D42C48C7}
 */
public class FilterElement extends RepositoryEntity {

  /**
   * @modelguid {6783CA89-B3A5-4BAC-959E-31BB7590D241}
   */
  public ConditionExpression conditionPart;

  /**
   * @modelguid {0B16FE4E-FD0E-4CE7-A05C-714E74CAC427}
   */
  public EnableOperatorType enableOperatorType;
  public FilterElementCompOper rightOperator;
  public Vector matchingPatterns;

  /**
   * @modelguid {51317331-0B65-4CD1-AE8A-20C987FD14D0}
   * @roseuid 401FAA63027A
   */
  public FilterElement() {
    super();
    matchingPatterns = new Vector();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
   *         ession
   *
   * @roseuid 401FAA630284
   */
  public ConditionExpression getConditionPart() {
    return conditionPart;
  }


  /**
   * @param conditionValue
   * @roseuid 401FAA63028E
   */
  public void setConditionPart(ConditionExpression conditionValue) {
    this.conditionPart = conditionValue;
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperato
   *         rType
   *
   * @roseuid 401FAA6302A3
   */
  public EnableOperatorType getEnableOperatorType() {
    return enableOperatorType;
  }


  /**
   * @param enableOperatorTypeValue
   * @roseuid 401FAA6302B6
   */
  public void setEnableOperatorType(EnableOperatorType enableOperatorTypeValue) {
    this.enableOperatorType = enableOperatorTypeValue;
  }


  /**
   * objectSet
   *
   * @param filterObject
   * @return boolean
   *
   * @modelguid {B83FCFDA-49F8-4E83-9519-1A110C32EE86}
   * @roseuid 401FAA6302CA
   */
  public boolean addMatchingPattern(MatchingPattern filterObject) {
    matchingPatterns.addElement(filterObject);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPatte
   *         rn
   *
   * @modelguid {03C4435A-96E5-49B1-A4E2-6961355E6DFD}
   * @roseuid 401FAA6302E9
   */
  public MatchingPattern removeMatchingPattern(int index) {
    Object o = matchingPatterns.elementAt(index);
    matchingPatterns.removeElementAt(index);
    return ((MatchingPattern) o);
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {A8E915D5-89DB-477F-9CED-C27F0FA8AE1A}
   * @roseuid 401FAA630312
   */
  public Iterator getMatchingPatternIterator() {
    return (new CPSIterator(matchingPatterns));
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPatte
   *         rn
   *
   * @roseuid 402AB4E5039D
   */
  public MatchingPattern getMatchingPattern(int index) {
    return ((MatchingPattern) matchingPatterns.elementAt(index));
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
   *         CompOper
   *
   * @roseuid 402AB5780268
   */
  public FilterElementCompOper getRightOperator() {
    return rightOperator;
  }


  /**
   * @param rightOperatorValue
   * @roseuid 402AB57C02DB
   */
  public void setRightOperator(FilterElementCompOper rightOperatorValue) {
    this.rightOperator = rightOperatorValue;
  }
}
