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

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FILTH.FilterModuleOrder;

/**
 * 
 */
public interface Reporter
{

	void open();

	void close();

	void openConcern(Concern concern);

	void closeConcern();

	void reportOrder(FilterModuleOrder order, FilterSetAnalysis analysis, boolean selected, boolean incremental);

}
