/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: FilterElement.java,v 1.1 2006/02/16 23:03:50 pascal_durr Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Utils.*;

import java.util.*;

/**
 * @modelguid {5976EB5C-BFC4-46BF-995B-AD41D42C48C7}
 */
public class FilterElement extends RepositoryEntity {
  public FilterElementAST filterElementAST;
  //public ConditionExpression conditionPart; -> delegate to FilterElementAST
  //public EnableOperatorType enableOperatorType; -> delegate to FilterElementAST
  //public FilterElementCompOper rightOperator; -> delegate to FilterElementAST
  //public Vector matchingPatterns;

  /**
   * @modelguid {51317331-0B65-4CD1-AE8A-20C987FD14D0}
   * @roseuid 401FAA63027A
   * @deprecated
   */
  public FilterElement() {
    super();
    //matchingPatterns = new Vector();
  }
  
  public FilterElement(FilterElementAST ast){
	  filterElementAST = ast;
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
   *         ession
   *
   * @roseuid 401FAA630284
   */
  public ConditionExpression getConditionPart() {
    return filterElementAST.conditionPart;
  }


  /**
   * @param conditionValue
   * @roseuid 401FAA63028E
   */
  public void setConditionPart(ConditionExpression conditionValue) {
	  filterElementAST.setConditionPart(conditionValue);
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperato
   *         rType
   *
   * @roseuid 401FAA6302A3
   */
  public EnableOperatorType getEnableOperatorType() {
    return filterElementAST.getEnableOperatorType();
  }


  /**
   * @param enableOperatorTypeValue
   * @roseuid 401FAA6302B6
   */
  public void setEnableOperatorType(EnableOperatorType enableOperatorTypeValue) {
	  filterElementAST.setEnableOperatorType(enableOperatorTypeValue);
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
	  filterElementAST.addMatchingPattern(filterObject);
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
    return filterElementAST.removeMatchingPattern(index);
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {A8E915D5-89DB-477F-9CED-C27F0FA8AE1A}
   * @roseuid 401FAA630312
   */
  public Iterator getMatchingPatternIterator() {
    return filterElementAST.getMatchingPatternIterator();
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPatte
   *         rn
   *
   * @roseuid 402AB4E5039D
   */
  public MatchingPattern getMatchingPattern(int index) {
    return filterElementAST.getMatchingPattern(index);
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
   *         CompOper
   *
   * @roseuid 402AB5780268
   */
  public FilterElementCompOper getRightOperator() {
    return filterElementAST.getRightOperator();
  }


  /**
   * @param rightOperatorValue
   * @roseuid 402AB57C02DB
   */
  public void setRightOperator(FilterElementCompOper rightOperatorValue) {
	  filterElementAST.setRightOperator(rightOperatorValue);
  }

public FilterElementAST getFilterElementAST() {
	return filterElementAST;
}
}
