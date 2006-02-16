/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Main.java,v 1.1 2006/02/13 11:16:53 pascal Exp $
 */
/**
 * Main class
 */
package Composestar.Core.CHKREP;

import Composestar.Core.Master.*;
import Composestar.Core.RepositoryImplementation.*;
import Composestar.Core.Exception.*;

/**
 * Main class used to run the parser
 */
public class Main implements CTCommonModule {
  /**
   * Constructor
   */
  public Main() {
  }


  /**
   * Run methods called by master
   *
   * @param resources Resources received from master (such as the repository)
   */
  public void run(CommonResources resources) throws ModuleException {
    DoCheck dc = new DoCheck();
    dc.go((DataStore) resources.getResource("TheRepository"));
  }
}
