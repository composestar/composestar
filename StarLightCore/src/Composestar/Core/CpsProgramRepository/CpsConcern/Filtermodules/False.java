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

public class False extends ConditionExpression {

  public False() {
    super();
  }


  public boolean isBinary() {
    return (false);
  }


  public boolean isUnary() {
    return (false);
  }


  public boolean isLiteral() {
    return (false);
  }
}
