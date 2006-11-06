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

/**
 * @modelguid {86ABF788-6260-4FAD-9691-88814BEEC0EF}
 * This represents the '=>' (Enabling) or '~>' (Disabling) operator
 * we explicitly represent these two operators as subclasses of EnableOperatorType
 */
public abstract class EnableOperatorType extends ContextRepositoryEntity {

  /**
   * @modelguid {75F90E43-9F74-4907-BE97-537D6C99ED9E}
   */
  private String type;


  /**
   * @roseuid 404DDA6C0050
   */
  public EnableOperatorType() {
    super();
  }

  /**
   * @return java.lang.String
   *
   * @roseuid 401FAA65010B
   */
  public String getType() {
    return type;
  }


  /**
   * @param typeValue
   * @roseuid 401FAA65010C
   */
  public void setType(String typeValue) {
    this.type = typeValue;
  }
}
