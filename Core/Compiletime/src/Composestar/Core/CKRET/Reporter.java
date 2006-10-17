/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2005-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Reporter {

	public void open();
	public void close();
	public void openConcern(Concern concern);
	public void closeConcern();
	public void reportOrder(FilterModuleOrder order, FilterSetAnalysis analysis, boolean selected, boolean incremental);
	
}
