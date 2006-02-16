/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ConditionLiteral.java,v 1.2 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;

/**
 * name of 1 condition (leafs in the composite pattern tree):
 * this is just a wrapper for References.ConditionReference, necessary to deal
 * with typing (i.e. we must inherit from ConditionExpression, and
 * References.ConditionReference is in a Reference inheritance tree
 */
public class ConditionLiteral extends ConditionExpression {
  public ConditionReference condition;

  /**
   * @deprecated Temporary fix (the concernreference should be used)
   */
  public String name;


  /**
   * @roseuid 404DDA6F01F6
   */
  public ConditionLiteral() {
    super();
  }


  /**
   * should be a reference?
   *
   * @roseuid 401FAA650364
   */
  public void Literal() {

  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConditionReferen
   *         ce
   *
   * @roseuid 401FAA650365
   */
  public ConditionReference getCondition() {
    return condition;
  }


  /**
   * @param conditionValue
   * @roseuid 401FAA650366
   */
  public void setCondition(ConditionReference conditionValue) {
    this.condition = conditionValue;
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADD15600A7
   */
  public boolean isBinary() {
    return (false);
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADD15C0091
   */
  public boolean isUnary() {
    return (false);
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADD164011F
   */
  public boolean isLiteral() {
    return (true);
  }


  /**
   * @return java.lang.String
   *
   * @roseuid 40ADD1FD0364
   * @deprecated Use getCondition.getName
   */
  public String getName() {
    return (name);
  }


  /**
   * @param n
   * @roseuid 40ADD2140303
   * @deprecated Use getCondition.setName
   */
  public void setName(String n) {
    name = n;
  }
}
