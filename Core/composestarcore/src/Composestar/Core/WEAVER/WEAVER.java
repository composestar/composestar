
package Composestar.Core.WEAVER;

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: WEAVER.java,v 1.1 2006/02/13 11:16:58 pascal Exp $
 */

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;

public interface WEAVER extends CTCommonModule {

	public abstract void run(CommonResources resources) throws ModuleException;  
    public abstract void main(String[] args);
   
}
