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

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.CPSIterator;

/**
 * @modelguid {5976EB5C-BFC4-46BF-995B-AD41D42C48C7}
 */
public class FilterElement extends ContextRepositoryEntity {
  public FilterElementAST filterElementAST;
  //public ConditionExpression conditionPart; -> delegate to FilterElementAST
  //public EnableOperatorType enableOperatorType; -> delegate to FilterElementAST
  //public FilterElementCompOper rightOperator; -> delegate to FilterElementAST
  
  /** @deprecated */
  public Vector matchingPatterns = new Vector();
  
  public MatchingPattern matchingPattern;

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
	  /*
	  matchingPatterns = new Vector();
	  
	  Iterator it = filterElementAST.getMatchingPatternIterator();
	  while (it.hasNext()){
		  MatchingPattern mp = new MatchingPattern((MatchingPatternAST) it.next());
		  mp.setParent(this);
		  matchingPatterns.add(mp);
		  DataStore.instance().addObject(mp);
	  }
	  */	  
	  
	  if (filterElementAST.getMatchingPattern() != null) {
		  matchingPattern = new MatchingPattern(filterElementAST.getMatchingPattern());
		  matchingPattern.setParent(this);		  
		  DataStore.instance().addObject(matchingPattern);
		  matchingPatterns.add(matchingPattern);
	  }
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
   * @deprecated
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
   * @deprecated
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
   * @deprecated
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
   * @deprecated
   */
  public MatchingPattern getMatchingPattern(int index) {
	  return ((MatchingPattern) matchingPatterns.elementAt(index));
  }
  
  public MatchingPattern getMatchingPattern() {
	  return matchingPattern;
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
