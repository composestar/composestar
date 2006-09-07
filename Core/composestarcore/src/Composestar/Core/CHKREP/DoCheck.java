/*
* This file is part of Composestar project [http://composestar.sf.net].
* Copyright (C) 2005 University of Twente.
*
* Licensed under LGPL v2.1 or (at your option) any later version.
* [http://www.fsf.org/copyleft/lgpl.html]
*
* $Id: DoCheck.java,v 1.1 2006/02/16 23:03:48 pascal_durr Exp $
*/
package Composestar.Core.CHKREP;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Utils.*;
import Composestar.Core.Exception.*;

/**
 * Starts all the checks.
 */
public class DoCheck {
  public static DataStore ds = null; //the repository
  public static boolean debug = true; //display debugging information?

  /**
   * Initializes all variables so we can start parsing a (new) cpsfile
   */
  private void init() {
  }


  /**
   * Starts doing all the necessary steps
   */
  public void go(DataStore d) throws ModuleException {
//    try {
	    init();

      //Debug.setMode(3); <<< ONLY USE THIS ONCE IN MASTER WHEN READING CONFIG FILE
      if (d == null) {
        Debug.out(Debug.MODE_WARNING, "CHKREP", "invalid repository");
        return;
      }
      ds = d;
      
      //Testbench
      NotUsedInternals nui = new NotUsedInternals();
      nui.check(ds);
      
      NotUsedExternals nue = new NotUsedExternals();
      nue.check(ds);
      
      NotUsedCondition nuc = new NotUsedCondition();
      nuc.check(ds);
      
      NotUsedSelector nus = new NotUsedSelector();
      nus.check(ds);
      
      //Allready checked by REXREF
      //ExistFilterModule efm = new ExistFilterModule();
      //efm.check(ds);
      
      //Allready checked by REXREF
      //ExistSelector es = new ExistSelector();
      //es.check(ds);
      
      ExistCondition ec = new ExistCondition();
      ec.check(ds);
      
//  /} catch (Exception e) {
//      System.err.println(e.getMessage());
//      /System.exit(-1);
//    }
  }


}
