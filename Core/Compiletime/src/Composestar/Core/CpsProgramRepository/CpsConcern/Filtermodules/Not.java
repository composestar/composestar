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


public class Not extends ConditionExpression {

  /**
   * Unary operator
   */
  public ConditionExpression operand;


  /**
   * @roseuid 401FAA660353
   */
  public Not() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
   *         ession
   *
   * @roseuid 401FAA66035B
   */
  public ConditionExpression getOperand() {
    return operand;
  }


  /**
   * @param operandValue
   * @roseuid 401FAA660365
   */
  public void setOperand(ConditionExpression operandValue) {
    this.operand = operandValue;
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADDA04012E
   */
  public boolean isBinary() {
    return (false);
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADDA13019E
   */
  public boolean isUnary() {
    return (true);
  }


  /**
   * @return boolean
   *
   * @roseuid 40ADDA1B0018
   */
  public boolean isLiteral() {
    return (false);
  }
}
