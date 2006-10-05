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


public class And extends ConditionExpression {
  public ConditionExpression left;
  public ConditionExpression right;


  /**
   * @roseuid 401FAA5600C3
   */
  public And() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
   *         ession
   *
   * @roseuid 401FAA5600C4
   */
  public ConditionExpression getLeft() {
    return left;
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
   *         ession
   *
   * @roseuid 401FAA5600EB
   */
  public ConditionExpression getRight() {
    return right;
  }


  /**
   * @param leftValue
   * @roseuid 401FAA5600CD
   */
  public void setLeft(ConditionExpression leftValue) {
    this.left = leftValue;
  }


  /**
   * @param rightValue
   * @roseuid 401FAA5600F5
   */
  public void setRight(ConditionExpression rightValue) {
    this.right = rightValue;
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADCE3303C3
   */
  public boolean isBinary() {
    return (true);
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADCE480110
   */
  public boolean isLiteral() {
    return (false);
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADCE500357
   */
  public boolean isUnary() {
    return (false);
  }
}
