/*
* This file is part of Composestar project [http://composestar.sf.net].
* Copyright (C) 2003 University of Twente.
*
* Licensed under LGPL v2.1 or (at your option) any later version.
* [http://www.fsf.org/copyleft/lgpl.html]
*
* $Id: DoubleNames.java,v 1.1 2006/02/13 11:16:53 pascal Exp $
*/
package Composestar.Core.CHKREP;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Core.Exception.*;

public class DoubleNames implements BaseChecker {

  public boolean performCheck() {
  	return true;
  }

  public void check(DataStore ds) throws ModuleException {
    
  }
}
