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

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Utils.*;

import java.util.*;

/**
 * extra klasse toegevoegd, niet in rose
 *
 * @modelguid {1260B89D-250A-4C2C-8602-77D8B44B9E11}
 */
public class InternalAST extends TypedDeclaration {
  public Vector valueExpressions;


  /**
   * @modelguid {5C63E872-AABD-4585-89FE-4B87E692DB03}
   * @roseuid 401FAA6502C3
   */
  public InternalAST() {
    super();
    valueExpressions = new Vector();
    type = null;
  }


  /**
   * valueExpressions
   *
   * @param valexp
   * @return boolean
   *
   * @modelguid {AF18E2B5-C471-47DC-83E4-7F44C95BDFDE}
   * @roseuid 401FAA6502C4
   */
  public boolean addValueExpression(ValueExpression valexp) {
    valueExpressions.addElement(valexp);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ValueExpressi
   *         on
   *
   * @modelguid {DC7912C1-6175-4805-BCE8-19E0E251E6AD}
   * @roseuid 401FAA6502CE
   */
  public ValueExpression removeValueExpression(int index) {
    Object o = valueExpressions.elementAt(index);
    valueExpressions.removeElementAt(index);
    return ((ValueExpression) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ValueExpressi
   *         on
   *
   * @modelguid {93AE7522-8EC8-4920-B2DC-E8B70129EFB5}
   * @roseuid 401FAA6502E2
   */
  public ValueExpression getValueExpression(int index) {
    return ((ValueExpression) valueExpressions.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {5DEB1C94-050E-4527-A4CA-38E39BA43D5B}
   * @roseuid 401FAA6502F5
   */
  public Iterator getValueExpressionIterator() {
    return (new CPSIterator(valueExpressions));
  }
}
