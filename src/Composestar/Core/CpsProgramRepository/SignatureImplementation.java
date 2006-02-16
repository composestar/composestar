/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: SignatureImplementation.java,v 1.2 2006/02/16 12:51:20 composer Exp $
 */
package Composestar.Core.CpsProgramRepository;

import Composestar.Core.LAMA.*;
import Composestar.Core.RepositoryImplementation.*;

/**
 * Holds the relationship between a method and its implementation.
 */
public class SignatureImplementation implements SerializableRepositoryEntity {
  public int RelationType;
  public static int NORMAL = 1;
  public static int ADDED = 2;
  public static int REMOVED = 4;
  public static int ALL = 255;
  private MethodInfo DotNETPresentation;


  /**
   * @roseuid 404C4B670107
   */
  public SignatureImplementation() {
    super();
  }


  /**
   * @return Composestar.CTAdaption.TYM.TypeCollector.DotNETTypes.DotNETMethodInfo
   *
   * @roseuid 40504E940259
   */
  public MethodInfo implementation() {
    return null;
  }


  /**
   * @return int
   *
   * @roseuid 4050504F0289
   */
  public int getRelationType() {
    return 0;
  }
}
