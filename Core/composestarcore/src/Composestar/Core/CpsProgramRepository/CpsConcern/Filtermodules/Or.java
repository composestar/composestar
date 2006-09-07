/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Or.java,v 1.2 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;


public class Or extends ConditionExpression {
  private ConditionExpression left;
  private ConditionExpression right;


  /**
   * @roseuid 401FAA670031
   */
  public Or() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
   *         ession
   *
   * @roseuid 401FAA67003B
   */
  public ConditionExpression getLeft() {
    return left;
  }


  /**
   * @param leftValue
   * @roseuid 401FAA670045
   */
  public void setLeft(ConditionExpression leftValue) {
    this.left = leftValue;
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
   *         ession
   *
   * @roseuid 401FAA670059
   */
  public ConditionExpression getRight() {
    return right;
  }


  /**
   * @param rightValue
   * @roseuid 401FAA670063
   */
  public void setRight(ConditionExpression rightValue) {
    this.right = rightValue;
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADDA6902BA
   */
  public boolean isBinary() {
    return (true);
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADDA6C0051
   */
  public boolean isUnary() {
    return (false);
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADDA6D03BA
   */
  public boolean isLiteral() {
    return (false);
  }
}
